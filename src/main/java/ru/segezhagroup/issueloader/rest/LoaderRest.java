package ru.segezhagroup.issueloader.rest;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.project.Project;
import com.atlassian.plugins.rest.common.multipart.FilePart;
import com.atlassian.plugins.rest.common.multipart.MultipartFormParam;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.segezhagroup.issueloader.jira.ProcessTools;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.util.List;

/**
 * A resource of message.
 */
@Path("/load")
public class LoaderRest {


    private static final Logger log = LoggerFactory.getLogger(LoaderRest.class);


    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/testhello")
    public Response getMessage()
    {
        return Response.ok("{\"status\":\"ok\", \"description\":\"Hello World\"}").build();
    }



    @POST
////    @AnonymousAllowed
////    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    @Path("/jsonfile")
    public Response loadJsonFile(@MultipartFormParam("project-id") FilePart projectId, @MultipartFormParam("issues-file-upload") List<FilePart> filePartList)
    {

        boolean isError = false;
        String msg = "";

        InputStream is = null;

        StringWriter writer = new StringWriter();

        // переменные
        String tProjectId = "";
        File jsonFile = null;
        Project project = null;

        ///////////////////////////////
        // ид проекта
        ///////////////////////////////
        try {
            is = projectId.getInputStream();
            IOUtils.copy(is, writer, "UTF-8");
            tProjectId = writer.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }


        if (tProjectId.isEmpty()) {
            isError = true;
            msg = msg + "project id is empty";
        }

        project = ComponentAccessor.getProjectManager().getProjectObjByKey(tProjectId);

        if (project == null) {
            isError = true;
            msg = msg + "project not found";
        }


//        log.warn("project id: " + tProjectId);

        ///////////////////////////////
        // файл
        ///////////////////////////////
        if (filePartList == null) {
            isError = true;
            msg = msg + "fail is null";
        }

        if (filePartList.size() == 0) {
            isError = true;
            msg = msg + "file was not received";
        }



        if (isError)
            return Response.ok("{\"status\":\"error\", \"description\":\"" + msg + "\"}").build();



        try {
//            log.warn("file size: " + String.valueOf(filePartList.size()));

            is = filePartList.get(0).getInputStream();

            jsonFile = File.createTempFile("issue_load", "json");

            FileOutputStream outputStream = new FileOutputStream(jsonFile);

            int read;
            byte[] bytes = new byte[1024];
            while ((read = is.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }


        } catch (IOException e) {
            e.printStackTrace();

            isError = true;
            msg = msg + "cant save file";

        }


        if (jsonFile == null) {
            isError = true;
            msg = msg + "error processing file";
        }


        if (isError)
            return Response.ok("{\"status\":\"error\", \"description\":\"" + msg + "\"}").build();


//        if (jsonFile != null) {
//            log.warn("file name: " + jsonFile.getPath() + " - " + jsonFile.getName());
//        }


        List<String> badNumbers = ProcessTools.ProcessingFile(project, jsonFile.getPath());

        if (jsonFile != null) {
            jsonFile.delete();
        }

        // подготовим ответ
        msg = "ошибок: " + String.valueOf(badNumbers.size());

        if (badNumbers.size() > 0) {
            msg = msg + "\r\n\r\n";
            for (String oneBadNum : badNumbers) {
                msg = msg + oneBadNum + "\r\n\r\n";
            }
        }

        JsonObject jsonRestAnswer = new JsonObject();
        jsonRestAnswer.addProperty("status", "ok");
        jsonRestAnswer.addProperty("description", msg);


        Gson gson = new Gson();
        return Response.ok(gson.toJson(jsonRestAnswer)).build();

    }
}