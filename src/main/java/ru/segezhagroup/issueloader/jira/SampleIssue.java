package ru.segezhagroup.issueloader.jira;

import java.util.List;

public class SampleIssue {

    private String number;
    private String service;
    private String component;
    private String category;
    private String createdate;
    private String summary;
    private String description;
    private String decision;
    private String status;
    private String reporter;
    private String reporteremail;
    private String assignee;
    private String assigneeemail;
    private String assigneegroup;
    private String assigneeunit;
    private String operator;
    private String operatoremail;
    private List<SampleWatcher> watchers;
    private List<SampleAttachment> attachments;
    private List<SampleMessage> messages;

    public SampleIssue(String number, String service, String component, String category, String createdate, String summary, String description, String decision, String status, String reporter, String reporteremail, String assignee, String assigneeemail, String assigneegroup, String assigneeunit, String operator, String operatoremail, List<SampleWatcher> watchers, List<SampleAttachment> attachments, List<SampleMessage> messages) {
        this.number = number;
        this.service = service;
        this.component = component;
        this.category = category;
        this.createdate = createdate;
        this.summary = summary;
        this.description = description;
        this.decision = decision;
        this.status = status;
        this.reporter = reporter;
        this.reporteremail = reporteremail;
        this.assignee = assignee;
        this.assigneeemail = assigneeemail;
        this.assigneegroup = assigneegroup;
        this.assigneeunit = assigneeunit;
        this.operator = operator;
        this.operatoremail = operatoremail;
        this.watchers = watchers;
        this.attachments = attachments;
        this.messages = messages;
    }

    public void addMessage(SampleMessage oneMessage) {
        this.messages.add(oneMessage);
    }


    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCreatedate() {
        return createdate;
    }

    public void setCreatedate(String createdate) {
        this.createdate = createdate;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDecision() {
        return decision;
    }

    public void setDecision(String decision) {
        this.decision = decision;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReporter() {
        return reporter;
    }

    public void setReporter(String reporter) {
        this.reporter = reporter;
    }

    public String getReporteremail() {
        return reporteremail;
    }

    public void setReporteremail(String reporteremail) {
        this.reporteremail = reporteremail;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public String getAssigneeemail() {
        return assigneeemail;
    }

    public void setAssigneeemail(String assigneeemail) {
        this.assigneeemail = assigneeemail;
    }

    public String getAssigneegroup() {
        return assigneegroup;
    }

    public void setAssigneegroup(String assigneegroup) {
        this.assigneegroup = assigneegroup;
    }

    public String getAssigneeunit() {
        return assigneeunit;
    }

    public void setAssigneeunit(String assigneeunit) {
        this.assigneeunit = assigneeunit;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getOperatoremail() {
        return operatoremail;
    }

    public void setOperatoremail(String operatoremail) {
        this.operatoremail = operatoremail;
    }

    public List<SampleWatcher> getWatchers() {
        return watchers;
    }

    public void setWatchers(List<SampleWatcher> watchers) {
        this.watchers = watchers;
    }

    public List<SampleAttachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<SampleAttachment> attachments) {
        this.attachments = attachments;
    }

    public List<SampleMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<SampleMessage> messages) {
        this.messages = messages;
    }

    @Override
    public String toString() {
        return "SampleIssue{" +
                "number='" + number + '\'' +
                ", service='" + service + '\'' +
                ", component='" + component + '\'' +
                ", category='" + category + '\'' +
                ", createdate='" + createdate + '\'' +
                ", summary='" + summary + '\'' +
                ", status='" + status + '\'' +
                ", reporter='" + reporter + '\'' +
                ", reporteremail='" + reporteremail + '\'' +
                ", assignee='" + assignee + '\'' +
                ", assigneeemail='" + assigneeemail + '\'' +
                ", assigneegroup='" + assigneegroup + '\'' +
                ", assigneeunit='" + assigneeunit + '\'' +
                ", operator='" + operator + '\'' +
                ", operatoremail='" + operatoremail + '\'' +
                '}';
    }
}
