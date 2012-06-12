package org.xwiki.sankore;

import com.xpn.xwiki.web.XWikiRequest;

import org.apache.velocity.VelocityContext;
import org.xwiki.context.ExecutionContext;

import com.xpn.xwiki.XWiki;
import com.xpn.xwiki.XWikiContext;

public class ContextUtils
{

    public static final String XWIKICONTEXT_KEY = "xwikicontext";
    public static final String VCONTEXT_KEY = "vcontext";

    public static XWikiContext getXWikiContext(ExecutionContext executionContext)
    {
        return (XWikiContext) executionContext.getProperty(XWIKICONTEXT_KEY);
    }

    public static VelocityContext getVelocityContext(XWikiContext xWikiContext)
    {
        return (VelocityContext) xWikiContext.get(VCONTEXT_KEY);
    }

    public static XWikiContext putVelocityContext(XWikiContext xWikiContext, VelocityContext velocityContext)
    {
        xWikiContext.put(VCONTEXT_KEY, velocityContext);
        return xWikiContext;
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
