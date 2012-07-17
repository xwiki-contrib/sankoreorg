package org.xwiki.sankore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.VelocityContext;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.xwiki.bridge.DocumentAccessBridge;
import org.xwiki.bridge.DocumentModelBridge;
import org.xwiki.component.annotation.Component;
import org.xwiki.context.Execution;
import org.xwiki.model.reference.AttachmentReference;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.rendering.converter.Converter;
import org.xwiki.rendering.syntax.Syntax;
import org.xwiki.velocity.VelocityManager;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.web.ExternalServletURLFactory;

@Component
@Singleton
public class DefaultMailSender implements MailSender
{
    private static final String[][] MIME_TYPES ={
            {"application/mac-binhex40", "hqx"}, {"application/mac-compactpro", "cpt"},
            {"application/msword", "doc"}, {"application/octet-stream", "bin"},
            {"application/octet-stream", "dms"}, {"application/octet-stream", "lha"},
            {"application/octet-stream", "lzh"}, {"application/octet-stream", "exe"},
            {"application/octet-stream", "class"}, {"application/oda", "oda"},
            {"application/pdf", "pdf"}, {"application/postscript", "ai"},
            {"application/postscript", "eps"}, {"application/postscript", "ps"},
            {"application/powerpoint", "ppt"}, {"application/rtf", "rtf"},
            {"application/x-bcpio", "bcpio"}, {"application/x-cdlink", "vcd"},
            {"application/x-compress", "Z"}, {"application/x-cpio", "cpio"},
            {"application/x-csh", "csh"}, {"application/x-director", "dcr"},
            {"application/x-director", "dir"}, {"application/x-director", "dxr"},
            {"application/x-dvi", "dvi"}, {"application/x-gtar", "gtar"},
            {"application/x-gzip", "gz"}, {"application/x-hdf", "hdf"},
            {"application/x-httpd-cgi", "cgi"}, {"application/x-koan", "skp"},
            {"application/x-koan", "skd"}, {"application/x-koan", "skt"},
            {"application/x-koan", "skm"}, {"application/x-latex", "latex"},
            {"application/x-mif", "mif"}, {"application/x-netcdf", "nc"},
            {"application/x-netcdf", "cdf"}, {"application/x-sh", "sh"},
            {"application/x-shar", "shar"}, {"application/x-stuffit", "sit"},
            {"application/x-sv4cpio", "sv4cpio"}, {"application/x-sv4crc", "sv4crc"},
            {"application/x-tar", "tar"}, {"application/x-tcl", "tcl"}, {"application/x-tex", "tex"},
            {"application/x-texinfo", "texinfo"}, {"application/x-texinfo", "texi"},
            {"application/x-troff", "t"}, {"application/x-troff", "tr"},
            {"application/x-troff", "roff"}, {"application/x-troff-man", "man"},
            {"application/x-troff-me", "me"}, {"application/x-troff-ms", "ms"},
            {"application/x-ustar", "ustar"}, {"application/x-wais-source", "src"},
            {"application/zip", "zip"}, {"audio/basic", "au"}, {"audio/basic", "snd"},
            {"audio/mpeg", "mpga"}, {"audio/mpeg", "mp2"}, {"audio/mpeg", "mp3"},
            {"audio/x-aiff", "aif"}, {"audio/x-aiff", "aiff"}, {"audio/x-aiff", "aifc"},
            {"audio/x-pn-realaudio", "ram"}, {"audio/x-pn-realaudio-plugin", "rpm"},
            {"audio/x-realaudio", "ra"}, {"audio/x-wav", "wav"}, {"chemical/x-pdb", "pdb"},
            {"chemical/x-pdb", "xyz"}, {"image/gif", "gif"}, {"image/ief", "ief"},
            {"image/jpeg", "jpeg"}, {"image/jpeg", "jpg"}, {"image/jpeg", "jpe"},
            {"image/png", "png"}, {"image/tiff", "tiff"}, {"image/tiff", "tif"},
            {"image/x-cmu-raster", "ras"}, {"image/x-portable-anymap", "pnm"},
            {"image/x-portable-bitmap", "pbm"}, {"image/x-portable-graymap", "pgm"},
            {"image/x-portable-pixmap", "ppm"}, {"image/x-rgb", "rgb"}, {"image/x-xbitmap", "xbm"},
            {"image/x-xpixmap", "xpm"}, {"image/x-xwindowdump", "xwd"}, {"text/html", "html"},
            {"text/html", "htm"}, {"text/plain", "txt"}, {"text/richtext", "rtx"},
            {"text/tab-separated-values", "tsv"}, {"text/x-setext", "etx"}, {"text/x-sgml", "sgml"},
            {"text/x-sgml", "sgm"}, {"video/mpeg", "mpeg"}, {"video/mpeg", "mpg"},
            {"video/mpeg", "mpe"}, {"video/quicktime", "qt"}, {"video/quicktime", "mov"},
            {"video/x-msvideo", "avi"}, {"video/x-sgi-movie", "movie"},
            {"x-conference/x-cooltalk", "ice"}, {"x-world/x-vrml", "wrl"}, {"x-world/x-vrml", "vrml"}};



    @Inject
    private Logger logger;

    @Inject
    private Execution execution;

    @Inject
    private Converter converter;

    @Inject
    private DocumentAccessBridge documentAccessBridge;

    @Inject
    private VelocityManager velocityManager;

    @Inject
    private MailConfiguration mailConfiguration;

    public boolean sendMail(Mail mail)
            throws MessagingException, Exception
    {
        boolean sent = false;
        Session session;
        Transport transport = null;
        try {
            Properties properties = initProperties(mailConfiguration);
            session = Session.getInstance(properties, null);
            transport = session.getTransport(MailSender.PROTOCOL_SMTP);
            if (!mailConfiguration.usesAuthentication()) {
                // no auth info - typical 127.0.0.1 open relay scenario
                transport.connect();
            } else {
                // auth info present - typical with external smtp server
                transport.connect(mailConfiguration.getSmtpUsername(), mailConfiguration.getSmtpPassword());
            }

            try {
                MimeMessage message = createMimeMessage(mail, session);
                if (message == null) {
                    return false;
                }

                transport.sendMessage(message, message.getAllRecipients());
                sent = true;

            } catch (SendFailedException ex) {
                logger.error("SendFailedException has occured.", ex);
                logger.error("Detailed email information" + mail.toString());
                throw ex;
            } catch (MessagingException ex) {
                logger.error("MessagingException has occured.", ex);
                logger.error("Detailed email information" + mail.toString());
                throw ex;
            } catch (IOException ex) {
                logger.error("IOException has occured.", ex);
            }
        } catch (MessagingException ex) {
            logger.error("MessagingException has occured.", ex);
        } catch (Exception ex) {
            logger.error("Exception has occured.", ex);
        } finally {
            try {
                if (transport != null) {
                    transport.close();
                }
            } catch (MessagingException ex) {
                logger.error("MessagingException has occured.", ex);
            }
        }

         return sent;
    }

    private Map<String, String> renderTemplate(DocumentReference documentReference, String from, String to, String cc, String bcc, Map<String, Object> parameters)
            throws XWikiException
    {
        Map<String, String> rendered = new HashMap<String, String>();
        XWikiContext xWikiContext = ContextUtils.getXWikiContext(execution.getContext());
        VelocityContext velocityContext = ContextUtils.getVelocityContext(xWikiContext);

        velocityContext.put("from.name", from);
        velocityContext.put("from.address", from);
        velocityContext.put("to.name", to);
        velocityContext.put("to.address", to);
        velocityContext.put("to.cc", cc);
        velocityContext.put("to.bcc", bcc);
        velocityContext.put("bounce", from);

        if (parameters != null) {
            for (String key : parameters.keySet()) {
                velocityContext.put(key, parameters.get(key));
            }
        }
        // put the new variables
        ContextUtils.putVelocityContext(xWikiContext, velocityContext);

        ExternalServletURLFactory externalServletURLFactory = new ExternalServletURLFactory(xWikiContext);
        xWikiContext.setURLFactory(externalServletURLFactory);

        XWikiDocument xWikiDocument = xWikiContext.getWiki().getDocument(documentReference, xWikiContext).getTranslatedDocument(xWikiContext);
        String content = xWikiDocument.getTranslatedContent(xWikiContext);
        String renderedContent = xWikiDocument.getRenderedContent(content, xWikiDocument.getSyntax().toIdString(), xWikiContext);
        String renderedTitle = xWikiDocument.getRenderedTitle(Syntax.PLAIN_1_0, xWikiContext);

        rendered.put("subject", renderedTitle);
        rendered.put("html", renderedContent);
        rendered.put("text", Jsoup.parseBodyFragment(renderedContent).text());

        return  rendered;
    }

    public boolean sendMailFromTemplate(DocumentReference templateReference, String from, String to, String cc, String bcc, Map<String, Object> parameters)
            throws XWikiException
    {
        Map<String, String> mailTemplate = renderTemplate(templateReference, from, to, cc, bcc, parameters);

        Mail mail = new Mail(from, to, cc, bcc, mailTemplate.get("subject"), mailTemplate.get("text"), mailTemplate.get("html"));
        /*List<AttachmentReference> attachmentReferences = documentAccessBridge.getAttachmentReferences(templateReference);
        List<Attachment> attachments = new ArrayList<Attachment>(attachmentReferences.size());
        for (AttachmentReference attachmentReference : attachmentReferences) {
            attachments = documentAccessBridge.getAttachmentContent(attachmentReference);
        }
        mail.setAttachments();*/

        boolean sent = false;
        try {
            sent = sendMail(mail);
        } catch (Exception e) {
            logger.error("sendEmailFromTemplate: " + templateReference, e);
        }

        return sent;
    }

    private MimeMessage createMimeMessage(Mail mail, Session session)
            throws MessagingException, IOException, Exception
    {
        // this will also check for email error
        InternetAddress from = new InternetAddress(mail.getFrom());
        String recipients = mail.getHeader("To");
        if (StringUtils.isBlank(recipients)) {
            recipients = mail.getTo();
        } else {
            recipients = mail.getTo() + "," + recipients;
        }
        List<InternetAddress> to = toInternetAddresses(recipients);

        recipients = mail.getHeader("Cc");
        if (StringUtils.isBlank(recipients)) {
            recipients = mail.getCc();
        } else {
            recipients = mail.getCc() + "," + recipients;
        }
        List<InternetAddress> cc = toInternetAddresses(recipients);

        recipients = mail.getHeader("Bcc");
        if (StringUtils.isBlank(recipients)) {
            recipients = mail.getBcc();
        } else {
            recipients = mail.getBcc() + "," + recipients;
        }
        List<InternetAddress> bcc = toInternetAddresses(recipients);

        if (CollectionUtils.isEmpty(to) && CollectionUtils.isEmpty(cc) && CollectionUtils.isEmpty(bcc)) {
            logger.info("No recipient -> skipping this email");
            return null;
        }

        MimeMessage message = new MimeMessage(session);
        message.setSentDate(new Date());
        message.setFrom(from);

        if (CollectionUtils.isNotEmpty(to)) {
            message.setRecipients(javax.mail.Message.RecipientType.TO, (Address[])to.toArray(new Address[to.size()]));
        }

        if (CollectionUtils.isNotEmpty(cc)) {
            message.setRecipients(javax.mail.Message.RecipientType.CC, (Address[])cc.toArray(new Address[to.size()]));
        }

        if (CollectionUtils.isNotEmpty(bcc)) {
            message.setRecipients(javax.mail.Message.RecipientType.BCC, (Address[])bcc.toArray(new Address[to.size()]));
        }

        message.setSubject(mail.getSubject(), DEFAULT_ENCODING);

        for (Map.Entry<String, String> header : mail.getHeaders().entrySet()) {
            message.setHeader(header.getKey(), header.getValue());
        }

        if (StringUtils.isNotEmpty(mail.getHtmlPart()) || CollectionUtils.size(mail.getAttachmentReferences()) > 0) {
            Multipart multipart = createMimeMultipart(mail);
            message.setContent(multipart);
        } else {
            message.setText(mail.getTextPart());
        }

        message.setSentDate(new Date());
        message.saveChanges();
        return message;
    }

    private Multipart createMimeMultipart(Mail mail)
            throws IOException, MessagingException, Exception
    {
        Multipart multipart;
        List<AttachmentReference> attachmentReferences = mail.getAttachmentReferences();

        if (StringUtils.isNotEmpty(mail.getHtmlPart()) && !CollectionUtils.isEmpty(attachmentReferences)) {
            multipart = new MimeMultipart("mixed");

            // Create the text part of the email
            BodyPart textPart = new MimeBodyPart();
            textPart.setContent(mail.getTextPart(), "text/plain; charset=" + DEFAULT_ENCODING);
            multipart.addBodyPart(textPart);

            // Add attachments to the main multipart
            for (AttachmentReference attachmentReference : attachmentReferences) {
                multipart.addBodyPart(createAttachmentBodyPart(attachmentReference));
            }
        } else {
            multipart = new MimeMultipart("mixed");
            List<AttachmentReference> attachments = new ArrayList<AttachmentReference>();
            List<AttachmentReference> embeddedImages = new ArrayList<AttachmentReference>();

            // Create the text part of the email
            BodyPart textPart;
            textPart = new MimeBodyPart();
            textPart.setText(mail.getTextPart());

            // Create the HTML part of the email, define the html as a multipart/related in case there are images
            Multipart htmlMultipart = new MimeMultipart("related");
            BodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(mail.getHtmlPart(), "text/html; charset=" + DEFAULT_ENCODING);
            htmlPart.setHeader("Content-Disposition", "inline");
            htmlPart.setHeader("Content-Transfer-Encoding", "quoted-printable");
            htmlMultipart.addBodyPart(htmlPart);

            // Find images used with src="cid:" in the email HTML part
            Pattern cidPattern =
                    Pattern.compile("src=('|\")cid:([^'\"]*)('|\")", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
            Matcher matcher = cidPattern.matcher(mail.getHtmlPart());
            List<String> foundEmbeddedImages = new ArrayList<String>();
            while (matcher.find()) {
                foundEmbeddedImages.add(matcher.group(2));
            }

            // Loop over the attachments of the email, add images used from the HTML to the list of attachments to be
            // embedded with the HTML part, add the other attachements to the list of attachments to be attached to the
            // email.
            for (AttachmentReference attachmentReference : attachmentReferences) {
                if (foundEmbeddedImages.contains(attachmentReference.getName())) {
                    embeddedImages.add(attachmentReference);
                } else {
                    attachments.add(attachmentReference);
                }
            }

            // Add the images to the HTML multipart (they should be hidden from the mail reader attachment list)
            for (AttachmentReference attachmentReference : embeddedImages) {
                htmlMultipart.addBodyPart(createAttachmentBodyPart(attachmentReference));
            }

            // Wrap the HTML and text parts in an alternative body part and add it to the main multipart
            Multipart alternativePart = new MimeMultipart("alternative");
            BodyPart alternativeMultipartWrapper = new MimeBodyPart();
            BodyPart htmlMultipartWrapper = new MimeBodyPart();
            alternativePart.addBodyPart(textPart);
            htmlMultipartWrapper.setContent(htmlMultipart);
            alternativePart.addBodyPart(htmlMultipartWrapper);
            alternativeMultipartWrapper.setContent(alternativePart);
            multipart.addBodyPart(alternativeMultipartWrapper);

            // Add attachments to the main multipart
            for (AttachmentReference attachmentReference : attachments) {
                multipart.addBodyPart(createAttachmentBodyPart(attachmentReference));
            }
        }

        return multipart;
    }

    private BodyPart createAttachmentBodyPart(AttachmentReference attachmentReference)
            throws IOException, MessagingException, Exception
    {
        String filename = attachmentReference.getName();
        InputStream stream = documentAccessBridge.getAttachmentContent(attachmentReference);
        File temp = File.createTempFile("tmpfile", ".tmp");
        FileOutputStream fos = new FileOutputStream(temp);
        byte[] buf = new byte[1024];
        int len;
        while ((len = stream.read(buf)) > 0) {
            fos.write(buf, 0, len);
        }
        fos.close();
        DataSource source = new FileDataSource(temp);
        MimeBodyPart part = new MimeBodyPart();
        String mimeType = getMimeTypeFromFilename(filename);

        part.setDataHandler(new DataHandler(source));
        part.setHeader("Content-Type", mimeType);
        part.setFileName(filename);
        part.setContentID("<" + filename + ">");
        part.setDisposition("inline");

        temp.deleteOnExit();

        return part;
    }

    private static List<InternetAddress> toInternetAddresses(String str)
            throws AddressException
    {
        String[] strings = StringUtils.stripAll(str.split(","));
        List<String> emails = new ArrayList<String>(strings.length);
        for (String sz : strings) {
            if (!StringUtils.isEmpty(sz)) {
                emails.add(sz);
            }
        }

        List<InternetAddress> addresses = new ArrayList<InternetAddress>(emails.size());
        for (String email : emails) {
            addresses.add(new InternetAddress(email));
        }

        return addresses;
    }

    private Properties initProperties(MailConfiguration mailConfiguration)
    {
        Properties properties = new Properties();

        // Note: The full list of available properties that we can set is defined here:
        // http://java.sun.com/products/javamail/javadocs/com/sun/mail/smtp/package-summary.html

        properties.put("mail.smtp.port", Integer.toString(mailConfiguration.getPort()));
        properties.put("mail.smtp.host", mailConfiguration.getHost());
        properties.put("mail.smtp.localhost", "localhost");
        properties.put("mail.host", "localhost");
        properties.put("mail.debug", "false");

        if (mailConfiguration.getFrom() != null) {
            properties.put("mail.smtp.from", mailConfiguration.getFrom());
        }

        if (mailConfiguration.usesAuthentication()) {
            properties.put("mail.smtp.auth", "true");
        }

        mailConfiguration.appendExtraPropertiesTo(properties, true);

        return properties;
    }

    private String getMimeTypeFromFilename(String filename)
    {
        int index = filename.lastIndexOf(".");
        String extension = filename;
        if (index != -1) {
            if (index == filename.length()) {
                return ("application/octet-stream");
            } else {
                extension = filename.substring(index + 1);
            }
        }
        String type = null;
        for (String[] MIME_TYPE : MIME_TYPES) {
            if (MIME_TYPE[1].equals(extension)) {
                type = MIME_TYPE[0];
                break;
            }
        }
        if (type == null) {
            return ("application/octet-stream");
        } else {
            return (type);
        }
    }

    public MailConfiguration getMailConfiguration()
    {
        return mailConfiguration;
    }
}