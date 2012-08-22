package org.xwiki.sankore.internal;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.objects.BaseObject;

public class MemberGroupObjectDocument extends DefaultXObjectDocument
{
    public MemberGroupObjectDocument(XObjectDocumentClass<MemberGroupObjectDocument> cls, XWikiDocument doc,
            BaseObject obj, XWikiContext context)
            throws XWikiException
    {
        super(cls, doc, obj, context);
    }

    /**
     * @return the prettyname of the space.
     */
    public String getRole()
    {
        return getStringValue(MemberGroupClass.FIELD_ROLE);
    }

    /**
     * Modify the prettyname of the space.
     *
     * @param role the new owner of the wiki.
     */
    public void setRole(String role)
    {
        setStringValue(MemberGroupClass.FIELD_ROLE, role);
    }
}
