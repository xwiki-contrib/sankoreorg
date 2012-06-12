package org.xwiki.sankore.internal;

import java.util.List;

import org.xwiki.model.EntityType;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.model.reference.EntityReference;
import org.xwiki.model.reference.SpaceReference;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.objects.BaseObject;
import com.xpn.xwiki.objects.BaseProperty;
import com.xpn.xwiki.objects.classes.PropertyClass;
import com.xpn.xwiki.plugin.applicationmanager.core.doc.objects.classes.DefaultXObjectDocument;


public class UserXObjectDocument extends DefaultXObjectDocument
{
    /**
     * Create new XWikiSpace managing provided XWikiDocument.
     *
     * @param xWikiDocument the encapsulated XWikiDocument.
     * @param objectId the id of the XWiki object included in the document to manage.
     * @param xWikiContext the XWiki context.
     * @throws com.xpn.xwiki.XWikiException error
     */
    public UserXObjectDocument(XWikiDocument xWikiDocument, int objectId, XWikiContext xWikiContext) throws
            XWikiException
    {
        super(XWikiUsersClass.getInstance(xWikiContext), xWikiDocument, XWikiUsersClass.OBJECTID, xWikiContext);
    }

    /**
     * @return the first name of the user.
     */
    public String getFirstName()
    {
        return getStringValue(XWikiUsersClass.FIELD_FIRST_NAME);
    }

    /**
     * Modify the first name of the user.
     *
     * @param firstName the new owner of the wiki.
     */
    public void setFirstName(String firstName)
    {
        setStringValue(XWikiUsersClass.FIELD_FIRST_NAME, firstName);
    }

    /**
     * @return the description od the wiki.
     */
    public String getLastName()
    {
        return getStringValue(XWikiUsersClass.FIELD_LAST_NAME);
    }

    /**
     * Modify the description of the wiki.
     *
     * @param lastName the new description of the wiki.
     */
    public void setLastName(String lastName)
    {
        setStringValue(XWikiUsersClass.FIELD_LAST_NAME, lastName);
    }

    /**
     * @return the type of the space.
     */
    public String getMemberType()
    {
        return getStringValue(XWikiUsersClass.FIELD_MEMBER_TYPE);
    }

    /**
     * Modify type of the space.
     *
     * @param memberType the new domain name of the wiki.
     */
    public void setMemberType(String memberType)
    {
        setStringValue(XWikiUsersClass.FIELD_MEMBER_TYPE, memberType);
    }

    /**
     * @return the url shortcut of the space.
     */
    public String getEmail()
    {
        return getStringValue(XWikiUsersClass.FIELD_EMAIL);
    }

    /**
     * Modify url shortcut of the space.
     *
     * @param email the new domain name of the wiki.
     */
    public void setEmail(String email)
    {
        setStringValue(XWikiUsersClass.FIELD_EMAIL, email);
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
