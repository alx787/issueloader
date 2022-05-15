package ru.segezhagroup.issueloader.webwork;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.atlassian.jira.web.action.JiraWebActionSupport;

public class LoadersetupAction extends JiraWebActionSupport
{
    private static final Logger log = LoggerFactory.getLogger(LoadersetupAction.class);

    @Override
    public String execute() throws Exception {
        return super.execute(); //returns SUCCESS
    }

    public String doDefault() throws Exception {
        return SUCCESS;
    }
}
