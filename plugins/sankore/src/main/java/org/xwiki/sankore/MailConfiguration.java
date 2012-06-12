package org.xwiki.sankore;

import java.util.Properties;

import org.xwiki.component.annotation.ComponentRole;


@ComponentRole
public interface MailConfiguration
{
    public void setHost(String host);
    public String getHost();

    public void setPort(int port);
    public int getPort();

    public void setFrom(String from);
    public String getFrom();

    public void setSmtpUsername(String smtpUsername);
    public String getSmtpUsername();

    public void setSmtpPassword(String smtpPassword);
    public String getSmtpPassword();

    public boolean usesAuthentication();

    public void setExtraProperties(String extraPropertiesString);

    public void appendExtraPropertiesTo(Properties externalProperties, boolean overwrite);

}
