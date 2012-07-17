package org.xwiki.sankore.internal;

import java.util.Date;

import org.xwiki.model.reference.DocumentReference;
import org.xwiki.model.reference.ObjectReference;
import org.xwiki.rendering.block.XDOM;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.api.Document;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.objects.BaseObject;

public class MembershipRequestObjectDocument extends DefaultXObjectDocument
{
    public MembershipRequestObjectDocument(XObjectDocumentClass<MembershipRequestObjectDocument> cls, XWikiDocument doc, BaseObject obj, XWikiContext context)
            throws XWikiException
    {
        super(cls, doc, obj, context);
    }

    /**
     * @return the prettyname of the space.
     */
    public Date getRequestDate()
    {
        return getDateValue(MembershipRequestClass.FIELD_REQUESTDATE);
    }

    /**
     * Modify the prettyname of the space.
     *
     * @param requestDate the new owner of the wiki.
     */
    public void setRequestDate(Date requestDate)
    {
        setDateValue(MembershipRequestClass.FIELD_REQUESTDATE, requestDate);
    }

    /**
     * @return the description od the wiki.
     */
    public Date getResponseDate()
    {
        return getDateValue(MembershipRequestClass.FIELD_RESPONSEDATE);
    }

    /**
     * Modify the description of the wiki.
     *
     * @param responseDate the new description of the wiki.
     */
    public void setResponseDate(Date responseDate)
    {
        setDateValue(MembershipRequestClass.FIELD_RESPONSEDATE, responseDate);
    }

    /**
     * @return the type of the space.
     */
    public String getText()
    {
        return getLargeStringValue(MembershipRequestClass.FIELD_TEXT);
    }

    /**
     * Modify type of the space.
     *
     * @param text the new domain name of the wiki.
     */
    public void setText(String text)
    {
        setLargeStringValue(MembershipRequestClass.FIELD_TEXT, text);
    }

    /**
     * @return the type of the space.
     */
    public String getMap()
    {
        return getLargeStringValue(MembershipRequestClass.FIELD_MAP);
    }

    /**
     * Modify type of the space.
     *
     * @param map the new domain name of the wiki.
     */
    public void setMap(String map)
    {
        setLargeStringValue(MembershipRequestClass.FIELD_MAP, map);
    }

    /**
     * @return the type of the space.
     */
    public String getStatus()
    {
        return getStringValue(MembershipRequestClass.FIELD_STATUS);
    }

    /**
     * Modify type of the space.
     *
     * @param status the new domain name of the wiki.
     */
    public void setStatus(String status)
    {
        setStringValue(MembershipRequestClass.FIELD_STATUS, status);
    }

    /**
     * @return the type of the space.
     */
    public String getGroup()
    {
        return getStringValue(MembershipRequestClass.FIELD_GROUP);
    }

    /**
     * Modify type of the space.
     *
     * @param group the new domain name of the wiki.
     */
    public void setGroup(String group)
    {
        setStringValue(MembershipRequestClass.FIELD_GROUP, group);
    }

    /**
     * @return the type of the space.
     */
    public String getRequester()
    {
        return getStringValue(MembershipRequestClass.FIELD_REQUESTER);
    }

    /**
     * Modify type of the space.
     *
     * @param requester the new domain name of the wiki.
     */
    public void setRequester(String requester)
    {
        setStringValue(MembershipRequestClass.FIELD_REQUESTER, requester);
    }

    /**
     * @return the type of the space.
     */
    public String getResponder()
    {
        return getStringValue(MembershipRequestClass.FIELD_RESPONDER);
    }

    /**
     * Modify type of the space.
     *
     * @param responder the new domain name of the wiki.
     */
    public void setResponder(String responder)
    {
        setStringValue(MembershipRequestClass.FIELD_RESPONDER, responder);
    }
}
