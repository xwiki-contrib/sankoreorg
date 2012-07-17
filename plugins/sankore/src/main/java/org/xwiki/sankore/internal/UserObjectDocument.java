package org.xwiki.sankore.internal;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.objects.BaseObject;

public class UserObjectDocument extends DefaultXObjectDocument
{
    public UserObjectDocument(XObjectDocumentClass<UserObjectDocument> cls, XWikiDocument doc, BaseObject obj, XWikiContext context)
            throws XWikiException
    {
        super(cls, doc, obj, context);
    }

    /**
     * @return the first name of the user.
     */
    public String getFirstName()
    {
        return getStringValue(UserClass.FIELD_FIRST_NAME);
    }

    /**
     * Modify the first name of the user.
     *
     * @param firstName the new owner of the wiki.
     */
    public void setFirstName(String firstName)
    {
        setStringValue(UserClass.FIELD_FIRST_NAME, firstName);
    }

    /**
     * @return the description od the wiki.
     */
    public String getLastName()
    {
        return getStringValue(UserClass.FIELD_LAST_NAME);
    }

    /**
     * Modify the description of the wiki.
     *
     * @param lastName the new description of the wiki.
     */
    public void setLastName(String lastName)
    {
        setStringValue(UserClass.FIELD_LAST_NAME, lastName);
    }

    /**
     * @return the type of the space.
     */
    public String getMemberType()
    {
        return getStringValue(UserClass.FIELD_MEMBER_TYPE);
    }

    /**
     * Modify type of the space.
     *
     * @param memberType the new domain name of the wiki.
     */
    public void setMemberType(String memberType)
    {
        setStringValue(UserClass.FIELD_MEMBER_TYPE, memberType);
    }

    /**
     * @return the url shortcut of the space.
     */
    public String getEmail()
    {
        return getStringValue(UserClass.FIELD_EMAIL);
    }

    /**
     * Modify url shortcut of the space.
     *
     * @param email the new domain name of the wiki.
     */
    public void setEmail(String email)
    {
        setStringValue(UserClass.FIELD_EMAIL, email);
    }
}
