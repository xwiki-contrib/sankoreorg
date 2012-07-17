package org.xwiki.sankore;

import com.xpn.xwiki.XWikiException;

public class MailSenderException extends XWikiException
{
    public static final int MODULE_COMPONENT_MAILSENDER = 101;

    public static final int ERROR_DOCUMENT_ACCESS_BRIDGE = 101001;

    public MailSenderException() {
    }

    public MailSenderException(int module, int code, String message) {
        super(module, code, message);
    }

    public MailSenderException(int module, int code, String message, Exception e) {
        super(module, code, message, e);
    }

    public MailSenderException(XWikiException xe) {
        super();
        setModule(xe.getModule());
        setCode(xe.getCode());
        setException(xe.getException());
        setArgs(xe.getArgs());
        setMessage(xe.getMessage());
    }
}
