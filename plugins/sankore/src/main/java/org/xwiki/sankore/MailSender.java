package org.xwiki.sankore;

import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;

import org.apache.velocity.VelocityContext;
import org.xwiki.component.annotation.ComponentRole;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.rendering.converter.ConversionException;

import com.xpn.xwiki.api.Attachment;
import com.xpn.xwiki.plugin.mailsender.MailConfiguration;

@ComponentRole
public interface MailSender
{
    public static final String PROTOCOL_SMTP = "smtp";

    public static final String DEFAULT_ENCODING = "UTF-8";

    public boolean sendMail(Mail mail) throws MessagingException, Exception;
    public boolean sendMailFromTemplate(DocumentReference templateReference, String from, String to, String cc, String bcc, Map<String, Object> parameters)
            throws Exception;
}
