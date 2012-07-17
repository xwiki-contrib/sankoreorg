package org.xwiki.sankore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.xwiki.component.annotation.Component;
import org.xwiki.model.reference.AttachmentReference;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.model.reference.DocumentReferenceResolver;
import org.xwiki.script.service.ScriptService;

import com.xpn.xwiki.api.Attachment;
import com.xpn.xwiki.web.Utils;

@Component
@Named("mailsender")
@Singleton
public class MailSenderScriptService implements ScriptService
{
    @Inject
    private MailSender mailSender;

    @SuppressWarnings("unchecked")
    private DocumentReferenceResolver<String> currentStringDocumentReferenceResolver = Utils.getComponent(
            DocumentReferenceResolver.class, "current");

    public boolean sendMessageFromTemplate(String templateFullName, String from, String to, Map<String, Object> parameters)
    {
        return sendMessageFromTemplate(templateFullName, from, to, StringUtils.EMPTY, StringUtils.EMPTY, parameters);
    }

    public boolean sendMessageFromTemplate(String templateFullName, String from, String to, String cc, String bcc, Map<String, Object> parameters)
    {
        DocumentReference templateReference = currentStringDocumentReferenceResolver.resolve(templateFullName);
        boolean sent = false;
        try {
            sent = mailSender.sendMailFromTemplate(templateReference, from, to, cc, bcc, parameters);
        } catch (Exception ex) {
        }
        return sent;
    }

    public boolean sendTextMessage(String from, String to, String subject, String messsage)
    {
        return sendHtmlMessage(from, to, StringUtils.EMPTY, StringUtils.EMPTY, subject, StringUtils.EMPTY, messsage, null);
    }

    public boolean sendTextMessage(String from, String to, String cc, String bcc, String subject, String message)
    {
        return sendHtmlMessage(from, to, cc, bcc, subject, StringUtils.EMPTY, message, null);
    }

    public boolean sendTextMessage(String from, String to, String cc, String bcc, String subject, String message, List<Attachment> attachments)
    {
        return sendHtmlMessage(from, to, cc, bcc, subject, StringUtils.EMPTY, message, attachments);
    }

    public boolean sendHtmlMessage(String from, String to, String subject, String messageHtml, String messageText)
    {
        return sendHtmlMessage(from, to, StringUtils.EMPTY, StringUtils.EMPTY, subject, messageHtml, messageText, null);
    }

    public boolean sendHtmlMessage(String from, String to, String cc, String bcc, String subject, String messageHtml, String messageText)
    {
        return sendHtmlMessage(from, to, cc, bcc, subject, messageHtml, messageText, null);
    }

    public boolean sendHtmlMessage(String from, String to, String cc, String bcc, String subject, String messageHtml, String messageText, List<Attachment> attachments)
    {
        Mail mail = new Mail(from, to, cc, bcc, subject, messageText, messageHtml);
        if (CollectionUtils.isNotEmpty(attachments)) {
            List<AttachmentReference> attachmentReferences = new ArrayList<AttachmentReference>(attachments.size());
            for (Attachment attachment : attachments) {
                attachmentReferences.add(new AttachmentReference(attachment.getFilename(), attachment.getDocument().getDocumentReference()));
            }
            mail.setAttachmentReferences(attachmentReferences);
        }
        boolean sent = false;
        try {
            sent = mailSender.sendMail(mail);
        } catch (Exception ex) {
        }
        return sent;
    }

    public MailConfiguration getMailConfiguration() {
        return mailSender.getMailConfiguration();
    }
}
