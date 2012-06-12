package org.xwiki.sankore.internal;

import java.util.List;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.objects.BaseObject;
import com.xpn.xwiki.objects.BaseProperty;
import com.xpn.xwiki.objects.classes.PropertyClass;
import com.xpn.xwiki.plugin.applicationmanager.core.doc.objects.classes.DefaultXObjectDocument;

/**
 * Created with IntelliJ IDEA. User: XWIKI Date: 6/1/12 Time: 2:35 PM To change this template use File | Settings | File
 * Templates.
 */
public class MembershipRequestXObjectDocument extends DefaultXObjectDocument
{
    /**
     * Create new XWikiSpace managing provided XWikiDocument.
     *
     * @param xwikiDocument the encapsulated XWikiDocument.
     * @param objectId the id of the XWiki object included in the document to manage.
     * @param xwikiContext the XWiki context.
     * @throws com.xpn.xwiki.XWikiException error
     */
    public MembershipRequestXObjectDocument(XWikiDocument xwikiDocument, int objectId, XWikiContext xwikiContext) throws
            XWikiException
    {
        super(InvitationClass.getInstance(xwikiContext), xwikiDocument, objectId, xwikiContext);
    }

    /**
     * @return the prettyname of the space.
     */
    public String getRequestDate()
    {
        return getStringValue(MembershipRequestClass.FIELD_REQUESTDATE);
    }

    /**
     * Modify the prettyname of the space.
     *
     * @param requestDate the new owner of the wiki.
     */
    public void setRequestDate(String requestDate)
    {
        setStringValue(MembershipRequestClass.FIELD_REQUESTDATE, requestDate);
    }

    /**
     * @return the description od the wiki.
     */
    public String getResponseDate()
    {
        return getStringValue(MembershipRequestClass.FIELD_RESPONSEDATE);
    }

    /**
     * Modify the description of the wiki.
     *
     * @param responseDate the new description of the wiki.
     */
    public void setResponseDate(String responseDate)
    {
        setStringValue(MembershipRequestClass.FIELD_RESPONSEDATE, responseDate);
    }

    /**
     * @return the type of the space.
     */
    public String getText()
    {
        return getStringValue(MembershipRequestClass.FIELD_TEXT);
    }

    /**
     * Modify type of the space.
     *
     * @param text the new domain name of the wiki.
     */
    public void setText(String text)
    {
        setStringValue(MembershipRequestClass.FIELD_TEXT, text);
    }

    /**
     * @return the type of the space.
     */
    public String getMap()
    {
        return getStringValue(MembershipRequestClass.FIELD_MAP);
    }

    /**
     * Modify type of the space.
     *
     * @param map the new domain name of the wiki.
     */
    public void setMap(String map)
    {
        setStringValue(MembershipRequestClass.FIELD_MAP, map);
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

    @Override
    public String toString()
    {
        return getSpace();
    }
}
