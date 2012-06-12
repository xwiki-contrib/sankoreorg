package org.xwiki.sankore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.xwiki.model.reference.AttachmentReference;

public class Mail
{
    private String from;

    private String to;

    private String cc;

    private String bcc;

    private String subject;

    private String textPart;

    private String htmlPart;

    private List<AttachmentReference> attachmentReferences;

    private Map<String, String> headers;

    public Mail()
    {
        this.headers = new TreeMap<String, String>();
    }

    public Mail(String from, String to, String cc, String bcc, String subject, String textPart, String htmlPart)
    {
        this();
        setFrom(from);
        setTo(to);
        setCc(cc);
        setBcc(bcc);
        setSubject(subject);
        setTextPart(textPart);
        setHtmlPart(htmlPart);
        setAttachmentReferences(attachmentReferences);
    }

    public List<AttachmentReference> getAttachmentReferences()
    {
        return this.attachmentReferences == null ? new ArrayList<AttachmentReference>() : this.attachmentReferences;
    }

    public void setAttachmentReferences(List<AttachmentReference> attachmentReferences)
    {
        this.attachmentReferences = attachmentReferences;
    }

    public String getFrom()
    {
        return this.from == null ? StringUtils.EMPTY : this.from;
    }

    public void setFrom(String from)
    {
        this.from = from;
    }

    public String getTo()
    {
        return this.to == null ? StringUtils.EMPTY : this.to;
    }

    public void setTo(String to)
    {
        this.to = to;
    }

    public String getCc()
    {
        return this.cc  == null ? StringUtils.EMPTY : this.cc;
    }

    public void setCc(String cc)
    {
        this.cc = cc;
    }

    public String getBcc()
    {
        return this.bcc == null ? StringUtils.EMPTY : this.bcc;
    }

    public void setBcc(String bcc)
    {
        this.bcc = bcc;
    }

    public String getSubject()
    {
        return this.subject == null ? StringUtils.EMPTY : this.subject;
    }

    public void setSubject(String subject)
    {
        this.subject = subject;
    }

    public String getTextPart()
    {
        return this.textPart == null ? StringUtils.EMPTY : this.textPart;
    }

    public void setTextPart(String message)
    {
        this.textPart = message;
    }

    @Override
    public String toString()
    {
        StringBuffer buffer = new StringBuffer();

        if (StringUtils.isNotEmpty(getFrom())) {
            buffer.append("From [" + getFrom() + "]");
        }

        if (StringUtils.isNotEmpty(getTo())) {
            buffer.append(", To [" + getTo() + "]");
        }

        if (StringUtils.isNotEmpty(getCc())) {
            buffer.append(", Cc [" + getCc() + "]");
        }

        if (StringUtils.isNotEmpty(getBcc())) {
            buffer.append(", Bcc [" + getBcc() + "]");
        }

        if (StringUtils.isNotEmpty(getSubject())) {
            buffer.append(", Subject [" + getSubject() + "]");
        }

        if (StringUtils.isNotEmpty(getTextPart())) {
            buffer.append(", Text [" + getTextPart() + "]");
        }

        if (StringUtils.isNotEmpty(getHtmlPart())) {
            buffer.append(", HTML [" + getHtmlPart() + "]");
        }

        if (!getHeaders().isEmpty()) {
            buffer.append(", Headers [" + toStringHeaders() + "]");
        }

        return buffer.toString();
    }

    private String toStringHeaders()
    {
        StringBuffer buffer = new StringBuffer();
        for (Map.Entry<String, String> header : getHeaders().entrySet()) {
            buffer.append("[" + header.getKey() + "] = [" + header.getValue() + "]");
        }
        return buffer.toString();
    }

    public String getHtmlPart()
    {
        return this.htmlPart == null ? StringUtils.EMPTY : this.htmlPart;
    }

    public void setHtmlPart(String htmlPart)
    {
        this.htmlPart = htmlPart;
    }

    public void setHeader(String header, String value)
    {
        this.headers.put(header, value);
    }

    public String getHeader(String header)
    {
        return getHeaders().get(header);
    }

    public void setHeaders(Map<String, String> headers)
    {
        this.headers = headers;
    }

    public Map<String, String> getHeaders()
    {
        return this.headers == null ? new TreeMap<String, String>() : this.headers;
    }
}
