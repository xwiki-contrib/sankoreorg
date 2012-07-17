package org.xwiki.sankore.internal;

import com.xpn.xwiki.XWikiException;

/**
 * Exception when try get {@link SuperDocument} that does not exist.
 * 
 * @version $Id: f8b54438bf790d2415c3532d5a692bbcd1eb38b3 $
 * @since Application Manager 1.0RC1
 */
public class XObjectDocumentDoesNotExistException extends XWikiException
{
    /**
     * Create new instance of {@link XObjectDocumentDoesNotExistException}.
     * 
     * @param message the error message.
     */
    public XObjectDocumentDoesNotExistException(String message)
    {
        super(XWikiException.MODULE_XWIKI_DOC, XWikiException.ERROR_XWIKI_DOES_NOT_EXIST, message);
    }
}
