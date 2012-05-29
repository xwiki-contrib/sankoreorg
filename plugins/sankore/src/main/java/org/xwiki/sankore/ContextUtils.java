package org.xwiki.sankore;

import com.xpn.xwiki.web.XWikiRequest;
import org.xwiki.context.ExecutionContext;

import com.xpn.xwiki.XWiki;
import com.xpn.xwiki.XWikiContext;

public class ContextUtils
{

    public static XWikiContext getXWikiContext(ExecutionContext executionContext)
    {
        return (XWikiContext) executionContext.getProperty(XWikiContext.EXECUTIONCONTEXT_KEY);
    }

    public static XWiki getXWiki(ExecutionContext executionContext)
    {
        return ContextUtils.getXWikiContext(executionContext).getWiki();
    }

    public static XWikiRequest getXWikiRequest(ExecutionContext executionContext)
    {
        return ContextUtils.getXWikiContext(executionContext).getRequest();
    }
}
