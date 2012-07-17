package org.xwiki.sankore.internal;

import org.xwiki.model.reference.DocumentReference;
import org.xwiki.model.reference.ObjectReference;
import org.xwiki.rendering.block.XDOM;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.api.Document;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.objects.BaseObject;

public class SpaceObjectDocument extends DefaultXObjectDocument
{
    public SpaceObjectDocument(XObjectDocumentClass<SpaceObjectDocument> cls, XWikiDocument doc, BaseObject obj, XWikiContext context)
            throws XWikiException
    {
        super(cls, doc, obj, context);
    }

    /**
     * @return the prettyname of the space.
     */
    public String getTitle()
    {
        return getStringValue(SpaceClass.FIELD_TITLE);
    }

    /**
     * Modify the prettyname of the space.
     *
     * @param title the new owner of the wiki.
     */
    public void setTitle(String title)
    {
        setStringValue(SpaceClass.FIELD_TITLE, title);
    }

    /**
     * @return the description od the wiki.
     */
    public String getDescription()
    {
        return getLargeStringValue(SpaceClass.FIELD_DESCRIPTION);
    }

    /**
     * Modify the description of the wiki.
     *
     * @param description the new description of the wiki.
     */
    public void setDescription(String description)
    {
        setLargeStringValue(SpaceClass.FIELD_DESCRIPTION, description);
    }

    /**
     * @return the type of the space.
     */
    public String getType()
    {
        return getStringValue(SpaceClass.FIELD_TYPE);
    }

    /**
     * Modify type of the space.
     *
     * @param type the new domain name of the wiki.
     */
    public void setType(String type)
    {
        setStringValue(SpaceClass.FIELD_TYPE, type);
    }

    /**
     * @return the url shortcut of the space.
     */
    public String getUrlShortcut()
    {
        return getStringValue(SpaceClass.FIELD_URLSHORTCUT);
    }

    /**
     * Modify url shortcut of the space.
     *
     * @param urlshortcut the new domain name of the wiki.
     */
    public void setUrlShortcut(String urlshortcut)
    {
        setStringValue(SpaceClass.FIELD_URLSHORTCUT, urlshortcut);
    }
}
