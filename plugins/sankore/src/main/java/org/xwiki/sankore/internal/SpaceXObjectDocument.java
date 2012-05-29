package org.xwiki.sankore.internal;

import java.util.List;

import org.xwiki.context.ExecutionContext;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.model.reference.SpaceReference;
import org.xwiki.sankore.ContextUtils;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.objects.BaseObject;
import com.xpn.xwiki.objects.BaseProperty;
import com.xpn.xwiki.objects.classes.PropertyClass;
import com.xpn.xwiki.plugin.applicationmanager.core.doc.objects.classes.DefaultXObjectDocument;

public class SpaceXObjectDocument extends DefaultXObjectDocument
{
    public SpaceXObjectDocument(SpaceReference spaceReference, ExecutionContext executionContext) throws XWikiException {

        this(ContextUtils.getXWikiContext(executionContext).getWiki().getDocument(new DocumentReference(SpaceClass.PREFERENCES_NAME, spaceReference), ContextUtils.getXWikiContext(executionContext)),
                SpaceClass.OBJECTID,
                ContextUtils.getXWikiContext(executionContext));
    }
    /**
     * Create new XWikiSpace managing provided XWikiDocument.
     *
     * @param xwikiDocument the encapsulated XWikiDocument.
     * @param objectId the id of the XWiki object included in the document to manage.
     * @param xwikiContext the XWiki context.
     * @throws com.xpn.xwiki.XWikiException error
     */
    public SpaceXObjectDocument(XWikiDocument xwikiDocument, int objectId, XWikiContext xwikiContext) throws XWikiException
    {
        super(SpaceClass.getInstance(xwikiContext), xwikiDocument, objectId, xwikiContext);
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
        return getStringValue(SpaceClass.FIELD_DESCRIPTION);
    }

    /**
     * Modify the description of the wiki.
     *
     * @param description the new description of the wiki.
     */
    public void setDescription(String description)
    {
        setStringValue(SpaceClass.FIELD_DESCRIPTION, description);
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
