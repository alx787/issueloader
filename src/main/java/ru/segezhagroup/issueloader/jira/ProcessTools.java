package ru.segezhagroup.issueloader.jira;

import com.atlassian.crowd.embedded.api.Group;
import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.bc.user.ApplicationUserBuilder;
import com.atlassian.jira.bc.user.UserService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.event.type.EventDispatchOption;
import com.atlassian.jira.exception.AddException;
import com.atlassian.jira.exception.CreateException;
import com.atlassian.jira.exception.PermissionException;
import com.atlassian.jira.issue.*;
import com.atlassian.jira.issue.attachment.CreateAttachmentParamsBean;
import com.atlassian.jira.issue.comments.CommentManager;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.watchers.WatcherManager;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.UserDetails;
import com.atlassian.jira.user.UserUtils;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.jira.user.util.UserUtil;
import com.atlassian.jira.util.ErrorCollection;
import com.atlassian.jira.util.WarningCollection;
import com.atlassian.jira.web.util.AttachmentException;
import com.google.gson.Gson;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ProcessTools {

    private static final Logger log = LoggerFactory.getLogger(ProcessTools.class);


    public static List<String> ProcessingFile(Project project, String jsonFileName) {
        // создание задач этапы
        // 1 - получить значения всех основных реквизитов
        // 2 - создать задачу с этими реквизитами
        // 3 - получить вложения, добавить их к задаче
        // 4 - получить переписку, добавить ее к задаче


        // тут закомментарено чтение сразу в массив json

//        Path path = Paths.get(jsonFileName);
//        Reader reader = null;
//        try {
//            reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);
//        } catch (IOException e) {
//            e.printStackTrace();
//
//            return 0;
//        }
//
//
//        JsonParser parser = new JsonParser();
//        JsonElement tree = parser.parse(reader);
//
//        JsonArray array = tree.getAsJsonArray();

        Gson gson = new Gson();

        SampleIssue[] sampleIssuesArray = null;

        try {
            // reader
//            Reader reader = new FileReader(jsonFileName);
            Reader reader = Files.newBufferedReader(Paths.get(jsonFileName), StandardCharsets.UTF_8);
            // parse JSON from file path to Customer Class
            sampleIssuesArray = gson.fromJson(reader, SampleIssue[].class);

            for (SampleIssue oneSampleIssue : sampleIssuesArray) {

                // get first_name of the file
                log.warn(oneSampleIssue.getSummary());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        if (sampleIssuesArray == null) {
            return new ArrayList<String>();
        }


        // номера не созданных
        List<String> badNumbers = new ArrayList<String>();


        for (SampleIssue oneSampleIssue : sampleIssuesArray) {
            // создадим задачу
            MutableIssue newIssue = createIssue(project, oneSampleIssue);
            if (newIssue != null) {
                // наблюдатели
                addWatchers(newIssue, oneSampleIssue.getWatchers());
                // сообщения переписка
                // если у задачи есть решение то добавим его отдельным комментарием
                if (!oneSampleIssue.getDecision().isEmpty()) {
                    oneSampleIssue.addMessage(new SampleMessage("", "", "", "", "РЕШЕНИЕ:" + "\r\n\r\n" + oneSampleIssue.getDecision()));
                }
                addComments(newIssue, oneSampleIssue.getMessages());
                // вложения
                addAttachments(newIssue, oneSampleIssue.getAttachments());
            } else {
                badNumbers.add(oneSampleIssue.getNumber());
            }
        }

        return badNumbers;
    }


    /////////////////////////////////////////
    // создание задачи
    /////////////////////////////////////////
    private static MutableIssue createIssue(Project project, SampleIssue sampleIssue) {

        IssueService issueService = ComponentAccessor.getIssueService();
        IssueInputParameters issueInputParameters = issueService.newIssueInputParameters();
        IssueManager issueManager = ComponentAccessor.getIssueManager();
//        issueInputParameters.setSkipScreenCheck(true);

        JiraAuthenticationContext jAC = ComponentAccessor.getJiraAuthenticationContext();
        ApplicationUser currUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser();


        issueInputParameters.setProjectId(project.getId());
//        issueInputParameters.setIssueTypeId("10000");

        log.warn("== project: " + project.getName());

        // для разных установок джиры могут быть различные идентификаторы типов задач и возможно статусов тоже
        // 10511 - запрос на изменение
        // 10510 - запрос на обслуживание
        if (sampleIssue.getCategory().equals("Доработка функционала")) {
            issueInputParameters.setIssueTypeId("10511");
        } else {
            issueInputParameters.setIssueTypeId("10510");
        }
//        issueInputParameters.setIssueTypeId("10100");


//        issueInputParameters.setStatusId("10101");
        issueInputParameters.setStatusId("10113"); // требуется поддержка

        issueInputParameters.setPriorityId("3"); // medium

        issueInputParameters.setSummary(sampleIssue.getSummary());

        log.warn("== summary: " + sampleIssue.getSummary());

//    issueInputParameters.setDescription(issue.getDescription())
//        issueInputParameters.setDescription("обращение " + sampleIssue.getNumber() + " от " + sampleIssue.getCreatedate() + "\r\n\r\n" +  sampleIssue.getDescription() + "\r\n\r\n" + sampleIssue.getDecision());
        issueInputParameters.setDescription("обращение " + sampleIssue.getNumber() + " от " + sampleIssue.getCreatedate() + "\r\n\r\n" +  sampleIssue.getDescription());

        log.warn("== description: " + "обращение " + sampleIssue.getNumber() + " от " + sampleIssue.getCreatedate() + "\r\n\r\n" +  sampleIssue.getDescription());


        ApplicationUser user = null;
        user = createFindUser(sampleIssue.getReporteremail(), sampleIssue.getReporter());
        log.warn("== reporter: " + user.getDisplayName());
//        issueInputParameters.setReporterId(user.getKey());
        issueInputParameters.setReporterId(user.getUsername());

//        исполнитель не назначается на этапе создания задачи !!!!
//
//        user = createFindUser(sampleIssue.getAssigneeemail(), sampleIssue.getAssignee());
//        log.warn("== assignee: " + user.getDisplayName());
//        issueInputParameters.setAssigneeId(user.getUsername());

//        CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager();
//        CustomField tgtField = customFieldManager.getCustomFieldObject("customfield_10001");
//        com.atlassian.servicedesk.internal.customfields.origin.VpOrigin requestType = (com.atlassian.servicedesk.internal.customfields.origin.VpOrigin) tgtField.getCustomFieldType().getSingularObjectFromString("IT help");
        issueInputParameters.addCustomFieldValue("customfield_10001", "SG1C", "1C: Request for change");



        //jAC.setLoggedInUser(currUser);

        IssueService.CreateValidationResult createValidationResult = issueService.validateCreate(jAC.getLoggedInUser(), issueInputParameters);
//        IssueService.CreateValidationResult createValidationResult = issueService.validateCreate(currUser, issueInputParameters);

        MutableIssue createdIssue = null;

        if (createValidationResult.isValid()) {
            log.warn("==================== createValidationResult");

            IssueService.IssueResult createResult = issueService.create(jAC.getLoggedInUser(), createValidationResult);
            if (!createResult.isValid()) {
                log.warn("==================== Error while creating the issue.");

                ErrorCollection creationErrors = createResult.getErrorCollection();

                log.warn("********* some errors while creation new issue *************\n");
                log.info(String.valueOf(creationErrors.getErrorMessages().size()));
                for(String error : creationErrors.getErrorMessages()){
                    log.warn(error);
                }

                WarningCollection creationWarnings = createResult.getWarningCollection();
                log.warn("********* some warnings while creation new issue *************\n");
                log.warn(String.valueOf(creationWarnings.getWarnings().size()));
                for(String warning : creationWarnings.getWarnings()){
                    log.warn(warning);
                }

                return null;
            } else {
                createdIssue = createResult.getIssue();
                log.warn(" ==================== Create new issue  ");
                log.warn(createdIssue.toString());
            }
        } else {
            log.warn("==================== not valid createValidationResult");


            ErrorCollection errors = createValidationResult.getErrorCollection();
            log.warn("********* some errors while validating new issue *************\n");
            log.info(String.valueOf(errors.getErrorMessages().size()));
            for(String error : errors.getErrorMessages()){
                log.warn(error);
            }


            WarningCollection warnings = createValidationResult.getWarningCollection();
            log.warn("********* some warnings while validating new issue *************\n");
            log.warn(String.valueOf(warnings.getWarnings().size()));
            for(String warning : warnings.getWarnings()){
                log.warn(warning);
            }

            return null;
        }


        user = createFindUser(sampleIssue.getAssigneeemail(), sampleIssue.getAssignee());
        log.warn("== assignee: " + user.getDisplayName());
        if (user != null) {
            createdIssue.setAssignee(user);
            issueManager.updateIssue(currUser, createdIssue, EventDispatchOption.DO_NOT_DISPATCH, false);
        }


        // установим дату создания
        if (createdIssue != null && !sampleIssue.getCreatedate().isEmpty()) {

            DateTimeFormatter formatDateTime = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
            LocalDateTime localDateTime = LocalDateTime.from(formatDateTime.parse(sampleIssue.getCreatedate()));
            Timestamp tsCreateDate = Timestamp.valueOf(localDateTime);

            createdIssue.setCreated(tsCreateDate);
            issueManager.updateIssue(currUser, createdIssue, EventDispatchOption.DO_NOT_DISPATCH, false);

            log.warn("== creation date: " + sampleIssue.getCreatedate() + " convert to " + String.valueOf(tsCreateDate));
        }

//        issueManager.updateIssue(currUser, createdIssue, EventDispatchOption.ISSUE_UPDATED, false);
//        issueManager.updateIssue(currUser, createdIssue, EventDispatchOption.DO_NOT_DISPATCH, false);

        return createdIssue;
    }


    /////////////////////////////////////////
    // watchers
    /////////////////////////////////////////
    private static void addWatchers(Issue issue, List<SampleWatcher> sampleWatcherList) {

        WatcherManager watcherManager = ComponentAccessor.getWatcherManager();

        for (SampleWatcher oneWatcher : sampleWatcherList) {
            ApplicationUser user = createFindUser(oneWatcher.getEmail(), oneWatcher.getName());
            if (user != null) {
                watcherManager.startWatching(user, issue);
            }
        }
    }


    /////////////////////////////////////////
    // comments
    /////////////////////////////////////////
    private static void addComments(Issue issue, List<SampleMessage> sampleMessageList) {
        CommentManager commentManager = ComponentAccessor.getCommentManager();
        ApplicationUser user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser();

        for (SampleMessage oneMessage : sampleMessageList) {
//            ApplicationUser user = createFindUser(oneMessage.getAuthoremail(), oneMessage.getAuthor());
            if (user != null) {
                String msg = "";
                if (oneMessage.getDirection().equals("in")){
                    msg = "Входящее сообщение" + "\r\n\r\n";
                }

                msg = oneMessage.getDate() + "\r\n\r\n"
                        + oneMessage.getAuthor() + "\r\n"
                        + oneMessage.getAuthoremail() + "\r\n\r\n"
                        + oneMessage.getBody();

                commentManager.create(issue, user, msg, false);
            }
        }
    }


    /////////////////////////////////////////
    // attachments
    /////////////////////////////////////////
    private static void addAttachments(Issue issue, List<SampleAttachment> sampleAttachmentList) {

        AttachmentManager attachmentManager = ComponentAccessor.getAttachmentManager();
        ApplicationUser user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser();

        File tmpFile = null;

        for (SampleAttachment oneAttachment : sampleAttachmentList) {
            byte[] data = Base64.decodeBase64(oneAttachment.getBody());

            try {
                tmpFile = File.createTempFile("attach_load", "bin");
                OutputStream stream = new FileOutputStream(tmpFile.getPath());
                stream.write(data);
            } catch (IOException e) {
                e.printStackTrace();

                log.warn("error save file in tmp");

                if (tmpFile != null) {
                    tmpFile.delete();
                }

                continue;
            }



            // прицепим файл во вложение
            CreateAttachmentParamsBean.Builder builder = new CreateAttachmentParamsBean.Builder();
            builder.file(tmpFile);
            builder.filename(oneAttachment.getName());
            // builder.contentType("image/png");
            builder.author(user);
            builder.issue(issue);
            builder.copySourceFile(true);

            CreateAttachmentParamsBean bean = builder.build();
            try {
                attachmentManager.createAttachment(bean);
            } catch (AttachmentException e) {
                e.printStackTrace();

                log.warn("error copy file to attachment");

//                if (tmpFile != null) {
//                    tmpFile.delete();
//                }
//
//                continue;

            }

            tmpFile.delete();
        }


    }

    // ищем пользователя, если не находим то возвращаем текущего
    private static ApplicationUser createFindUser(String email, String name) {
        ApplicationUser currentUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser();
        ApplicationUser user = null;

        if (email.isEmpty())
            return currentUser;

        user = UserUtils.getUserByEmail(email);

        if (user == null) {
            return currentUser;
        }

        return user;
    }

    // найти пользователя или создать нового
    private static ApplicationUser createFindUser_not_used(String email, String name) {

        if (email.isEmpty())
            return null;

        ApplicationUser user = UserUtils.getUserByEmail(email);

        if (user == null) {

//            UserService userService = (UserService) ComponentAccessor.getComponent(UserService.class);
//            ApplicationUser currUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser();
//
//            UserService.CreateUserRequest newCreateRequest = UserService.CreateUserRequest.withUserDetails(currUser, name, "234567890", email, name).sendNotification(false);
//
//            UserService.CreateUserValidationResult createValidationResult = userService.validateCreateUser(newCreateRequest);
//
//            if (createValidationResult.isValid()) {
//                try {
//                    userService.createUser(createValidationResult);
//                } catch (PermissionException e) {
//                    e.printStackTrace();
//                } catch (CreateException e) {
//                    e.printStackTrace();
//                }
//            }

            UserManager userManager = ComponentAccessor.getUserManager();
            UserDetails userDetails = new UserDetails(email, name).withDirectory(1L);

            try {
                user = userManager.createUser(userDetails);
            } catch (CreateException e) {
                e.printStackTrace();
            } catch (PermissionException e) {
                e.printStackTrace();
            }

            // проапдейтим имя пользователя и адрес
            if (user != null) {
                UserService userService = ComponentAccessor.getComponent(UserService.class);
                ApplicationUserBuilder applicationUserBuilder = userService.newUserBuilder(user);
                applicationUserBuilder.displayName(name);
                applicationUserBuilder.emailAddress(email);
                ApplicationUser userForValidation = applicationUserBuilder.build();

                UserService.UpdateUserValidationResult updateUserValidationResult = userService.validateUpdateUser(userForValidation);
                if (updateUserValidationResult.isValid()) {
                    userService.updateUser(updateUserValidationResult);
                }

            }


        }

        // добавим пользователя в группу
        UserUtil userUtil = ComponentAccessor.getUserUtil();

        Group group = ComponentAccessor.getGroupManager().getGroup("jira-servicedesk-users");
        if (group != null) {
            if (!ComponentAccessor.getGroupManager().getGroupsForUser(user).contains(group)) {
                try {
                    userUtil.addUserToGroup(group, user);
                } catch (PermissionException e) {
                    e.printStackTrace();
                } catch (AddException e) {
                    e.printStackTrace();
                }
            }
        }


        return user;
    }

}
