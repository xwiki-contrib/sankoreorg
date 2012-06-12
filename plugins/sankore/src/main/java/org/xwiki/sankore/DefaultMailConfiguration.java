package org.xwiki.sankore;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.phase.Initializable;
import org.xwiki.configuration.ConfigurationSource;


import com.xpn.xwiki.web.Utils;

@Component
@Singleton
public class DefaultMailConfiguration implements MailConfiguration, Initializable
{
    private int port;

    private String host;

    private String from;

    private String smtpUsername;

    private String smtpPassword;

    private Properties extraProperties;

    @Inject
    private Logger logger;

    @Inject
    @Named("wiki")
    ConfigurationSource wikiPreferences;

    private static final String DEFAULT_SMTP_SERVER = "localhost";
    private static final int DEFAULT_SMTP_PORT = 25;

    private static final String SMTP_SERVER = "smtp_server";
    private static final String SMTP_PORT = "smtp_port";
    private static final String SMTP_FROM = "smtp_from";
    private static final String SMTP_SERVER_USERNAME = "smtp_server_username";
    private static final String SMTP_SERVER_PASSWORD = "smtp_server_password";
    private static final String JAVAMAIL_EXTRA_PROPS = "javamail_extra_props";


    public void DefaultMailConfiguration()
    {
        this.port = DEFAULT_SMTP_PORT;
        this.host = DEFAULT_SMTP_SERVER;

        this.initialize();
    }

    public void initialize()
    {
        String smtpServer = wikiPreferences.getProperty(SMTP_SERVER, String.class);
        if (StringUtils.isNotBlank(smtpServer)) {
            setHost(smtpServer);
        }

        String smtpPort = wikiPreferences.getProperty(SMTP_PORT, String.class);
        if (StringUtils.isNotBlank(smtpPort)) {
            setPort(Integer.parseInt(smtpPort));
        }

        String smtpFrom = wikiPreferences.getProperty(SMTP_FROM, String.class);
        if (StringUtils.isNotBlank(smtpFrom)) {
            setFrom(smtpFrom);
        }

        String smtpUsername = wikiPreferences.getProperty(SMTP_SERVER_USERNAME, String.class);
        String smtpPassword = wikiPreferences.getProperty(SMTP_SERVER_PASSWORD, String.class);
        if (StringUtils.isNotBlank(smtpUsername) && StringUtils.isNotBlank(smtpPassword)) {
            setSmtpUsername(smtpUsername);
            setSmtpPassword(smtpPassword);
        }

        String javaMailExtraProps = wikiPreferences.getProperty(JAVAMAIL_EXTRA_PROPS, String.class);
        if (StringUtils.isNotBlank(javaMailExtraProps)) {
            setExtraProperties(javaMailExtraProps);
        }
    }

    public void setHost(String host)
    {
        this.host = host;
    }

    public String getHost()
    {
        return this.host;
    }

    public void setPort(int port)
    {
        this.port = port;
    }

    public int getPort()
    {
        return this.port;
    }

    public void setFrom(String from)
    {
        this.from = from;
    }

    public String getFrom()
    {
        return this.from;
    }

    public void setSmtpUsername(String smtpUsername)
    {
        this.smtpUsername = smtpUsername;
    }

    public String getSmtpUsername()
    {
        return this.smtpUsername;
    }

    public void setSmtpPassword(String smtpPassword)
    {
        this.smtpPassword = smtpPassword;
    }

    public String getSmtpPassword()
    {
        return this.smtpPassword;
    }

    public boolean usesAuthentication()
    {
        return !StringUtils.isEmpty(getSmtpUsername()) && !StringUtils.isEmpty(getSmtpPassword());
    }

    public void setExtraProperties(String extraPropertiesString)
    {
        if (StringUtils.isEmpty(extraPropertiesString)) {
            this.extraProperties = null;
        } else {
            InputStream is = new ByteArrayInputStream(extraPropertiesString.getBytes());
            this.extraProperties = new Properties();
            try {
                this.extraProperties.load(is);
            } catch (IOException e) {
                // Shouldn't ever occur...
                throw new RuntimeException("Error configuring mail connection.", e);
            }
        }

    }

    public void appendExtraPropertiesTo(Properties externalProperties, boolean overwrite)
    {
        // sanity check
        if (externalProperties == null) {
            throw new IllegalArgumentException("externalProperties can't be null");
        }

        if (this.extraProperties != null && this.extraProperties.size() > 0) {
            for (Map.Entry<Object, Object> e : this.extraProperties.entrySet()) {
                String propName = (String) e.getKey();
                String propValue = (String) e.getValue();
                if (overwrite || externalProperties.getProperty(propName) == null) {
                    externalProperties.setProperty(propName, propValue);
                }
            }
        }
    }

    @Override
    public String toString()
    {
        StringBuffer buffer = new StringBuffer();

        if (getHost() != null) {
            buffer.append("Host [" + getHost() + "]");
        }

        if (getFrom() != null) {
            buffer.append(", From [" + getFrom() + "]");
        }

        buffer.append(", Port [" + getPort() + "]");

        if (usesAuthentication()) {
            buffer.append(", Username [" + getSmtpUsername() + "]");
            buffer.append(", Password [*****]");
        }

        return buffer.toString();
    }
}
