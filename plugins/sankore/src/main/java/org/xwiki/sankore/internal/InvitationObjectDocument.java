package org.xwiki.sankore.internal;

import java.util.Date;

import org.xwiki.model.reference.DocumentReference;
import org.xwiki.model.reference.ObjectReference;
import org.xwiki.rendering.block.XDOM;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.api.*;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.objects.BaseObject;

public class InvitationObjectDocument extends DefaultXObjectDocument
{
    public InvitationObjectDocument(XObjectDocumentClass<InvitationObjectDocument> cls, XWikiDocument doc, BaseObject obj, XWikiContext context)
            throws XWikiException
    {
        super(cls, doc, obj, context);
    }

    /**
     * @return the prettyname of the space.
     */
    public Date getRequestDate()
    {
        return getDateValue(InvitationClass.FIELD_REQUESTDATE);
    }

    /**
     * Modify the prettyname of the space.
     *
     * @param requestDate the new owner of the wiki.
     */
    public void setRequestDate(Date requestDate)
    {
        setDateValue(InvitationClass.FIELD_REQUESTDATE, requestDate);
    }

    /**
     * @return the description od the wiki.
     */
    public Date getResponseDate()
    {
        return getDateValue(InvitationClass.FIELD_RESPONSEDATE);
    }

    /**
     * Modify the description of the wiki.
     *
     * @param responseDate the new description of the wiki.
     */
    public void setResponseDate(Date responseDate)
    {
        setDateValue(InvitationClass.FIELD_RESPONSEDATE, responseDate);
    }

    /**
     * @return the type of the space.
     */
    public String getText()
    {
        return getLargeStringValue(InvitationClass.FIELD_TEXT);
    }

    /**
     * Modify type of the space.
     *
     * @param text the new domain name of the wiki.
     */
    public void setText(String text)
    {
        setLargeStringValue(InvitationClass.FIELD_TEXT, text);
    }

    /**
     * @return the type of the space.
     */
    public String getMap()
    {
        return getLargeStringValue(InvitationClass.FIELD_MAP);
    }

    /**
     * Modify type of the space.
     *
     * @param map the new domain name of the wiki.
     */
    public void setMap(String map)
    {
        setLargeStringValue(InvitationClass.FIELD_MAP, map);
    }

    /**
     * @return the type of the space.
     */
    public String getStatus()
    {
        return getStringValue(InvitationClass.FIELD_STATUS);
    }

    /**
     * Modify type of the space.
     *
     * @param status the new domain name of the wiki.
     */
    public void setStatus(String status)
    {
        setStringValue(InvitationClass.FIELD_STATUS, status);
    }

    /**
     * @return the type of the space.
     */
    public String getRoles()
    {
        return getStringValue(InvitationClass.FIELD_ROLES);
    }

    /**
     * Modify type of the space.
     *
     * @param roles the new domain name of the wiki.
     */
    public void setRoles(String roles)
    {
        setStringValue(InvitationClass.FIELD_ROLES, roles);
    }

    /**
     * @return the type of the space.
     */
    public String getGroup()
    {
        return getStringValue(InvitationClass.FIELD_GROUP);
    }

    /**
     * Modify type of the space.
     *
     * @param group the new domain name of the wiki.
     */
    public void setGroup(String group)
    {
        setStringValue(InvitationClass.FIELD_GROUP, group);
    }

    /**
     * @return the type of the space.
     */
    public String getInvitee()
    {
        return getStringValue(InvitationClass.FIELD_INVITEE);
    }

    /**
     * Modify type of the space.
     *
     * @param invitee the new domain name of the wiki.
     */
    public void setInvitee(String invitee)
    {
        setStringValue(InvitationClass.FIELD_INVITEE, invitee);
    }

    /**
     * @return the type of the space.
     */
    public String getInviter()
    {
        return getStringValue(InvitationClass.FIELD_INVITER);
    }

    /**
     * Modify type of the space.
     *
     * @param inviter the new domain name of the wiki.
     */
    public void setInviter(String inviter)
    {
        setStringValue(InvitationClass.FIELD_INVITER, inviter);
    }

    /**
     * @return the type of the space.
     */
    public String getKey()
    {
        return getStringValue(InvitationClass.FIELD_KEY);
    }

    /**
     * Modify type of the space.
     *
     * @param key the new domain name of the wiki.
     */
    public void setKey(String key)
    {
        setStringValue(InvitationClass.FIELD_KEY, key);
    }
}
