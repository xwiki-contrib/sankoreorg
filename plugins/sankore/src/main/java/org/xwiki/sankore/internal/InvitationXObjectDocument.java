package org.xwiki.sankore.internal;

import java.util.Date;
import java.util.List;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.objects.BaseObject;
import com.xpn.xwiki.objects.BaseProperty;
import com.xpn.xwiki.objects.classes.PropertyClass;
import com.xpn.xwiki.plugin.applicationmanager.core.doc.objects.classes.DefaultXObjectDocument;

/**
 * Created with IntelliJ IDEA. User: XWIKI Date: 6/1/12 Time: 1:56 PM To change this template use File | Settings | File
 * Templates.
 */
public class InvitationXObjectDocument extends DefaultXObjectDocument
{
    /**
     * Create new XWikiSpace managing provided XWikiDocument.
     *
     * @param xWikiContext the encapsulated XWikiDocument.
     * @param objectId the id of the XWiki object included in the document to manage.
     * @param xWikiDocument the XWiki context.
     * @throws com.xpn.xwiki.XWikiException error
     */
    public InvitationXObjectDocument(XWikiDocument xWikiDocument, int objectId, XWikiContext xWikiContext)
            throws XWikiException
    {
        super(InvitationClass.getInstance(xWikiContext), xWikiDocument, objectId, xWikiContext);
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
        return getStringValue(InvitationClass.FIELD_TEXT);
    }

    /**
     * Modify type of the space.
     *
     * @param text the new domain name of the wiki.
     */
    public void setText(String text)
    {
        setStringValue(InvitationClass.FIELD_TEXT, text);
    }

    /**
     * @return the type of the space.
     */
    public String getMap()
    {
        return getStringValue(InvitationClass.FIELD_MAP);
    }

    /**
     * Modify type of the space.
     *
     * @param map the new domain name of the wiki.
     */
    public void setMap(String map)
    {
        setStringValue(InvitationClass.FIELD_MAP, map);
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

    @Override
    public void setStringValue(String fieldName, String value)
    {
        BaseObject obj = getBaseObject(false);

        if (obj != null) {
            PropertyClass pclass = (PropertyClass) this.sclass.getBaseClass().get(fieldName);

            if (pclass != null) {
                BaseProperty prop = (BaseProperty) obj.safeget(fieldName);
                prop = pclass.fromString(value);
                if (prop != null) {
                    obj.safeput(fieldName, prop);
                }
            }
        }
    }

    @Override
    public void setStringListValue(String fieldName, List<String> value)
    {
        BaseObject obj = getBaseObject(false);

        if (obj != null) {
            obj.setStringListValue(fieldName, value);
        }
    }

    @Override
    public void setIntValue(String fieldName, int value)
    {
        BaseObject obj = getBaseObject(false);

        if (obj != null) {
            obj.setIntValue(fieldName, value);
        }
    }

    public Date getDateValue(String fieldName)
    {
        BaseObject obj = getBaseObject(false);

        if (obj == null) {
            return null;
        }

        return obj.getDateValue(fieldName);
    }

    public void setDateValue(String fieldName, Date value)
    {
        BaseObject obj = getBaseObject(false);

        if (obj != null) {
            obj.setDateValue(fieldName, value);
        }
    }

    @Override
    public String toString()
    {
        return getSpace();
    }
}
