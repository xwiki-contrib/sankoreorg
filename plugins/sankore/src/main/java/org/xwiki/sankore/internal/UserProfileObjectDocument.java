package org.xwiki.sankore.internal;

import org.xwiki.model.reference.DocumentReference;
import org.xwiki.model.reference.ObjectReference;
import org.xwiki.rendering.block.XDOM;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.api.Document;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.objects.BaseObject;

public class UserProfileObjectDocument extends DefaultXObjectDocument
{
    protected BaseObject obj;

    public UserProfileObjectDocument(XObjectDocumentClass<UserProfileObjectDocument> cls,  XWikiDocument doc, BaseObject obj, XWikiContext context)
            throws XWikiException
    {
        super(cls, doc, obj, context);
    }

    public String getProfile()
    {
        return getStringValue(UserProfileClass.FIELD_PROFILE);
    }

    public void setProfile(String profile)
    {
        setStringValue(UserProfileClass.FIELD_PROFILE, profile);
    }

    public boolean getAllowNotifications()
    {
        return getBooleanValue(UserProfileClass.FIELD_ALLOW_NOTIFICATIONS);
    }

    public void setAllowNotifications(boolean allowNotifications)
    {
        setBooleanValue(UserProfileClass.FIELD_ALLOW_NOTIFICATIONS, allowNotifications);
    }

    public boolean getAllowNotificationsFromSelf()
    {
        return getBooleanValue(UserProfileClass.FIELD_ALLOW_NOTIFICATIONS_FROM_SELF);
    }

    public void setAllowNotificationsFromSelf(boolean allowNotificationsFromSelf)
    {
        setBooleanValue(UserProfileClass.FIELD_ALLOW_NOTIFICATIONS_FROM_SELF, allowNotificationsFromSelf);
    }
}
