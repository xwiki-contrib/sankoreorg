package org.xwiki.sankore.internal;

import java.util.List;

import org.xwiki.model.reference.DocumentReference;
import org.xwiki.model.reference.ObjectReference;
import org.xwiki.rendering.block.XDOM;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.api.Document;
import com.xpn.xwiki.cache.api.XWikiCacheNeedsRefreshException;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.objects.BaseObject;

public class GroupObjectDocument extends DefaultXObjectDocument
{
    public GroupObjectDocument(XObjectDocumentClass<GroupObjectDocument> cls, XWikiDocument doc, BaseObject obj, XWikiContext context)
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

    /**
     * @return the prettyname of the space.
     */
    public String getLogo()
    {
        return getStringValue(GroupClass.FIELD_LOGO);
    }

    /**
     * Modify the prettyname of the space.
     *
     * @param logo the new owner of the wiki.
     */
    public void setLogo(String logo)
    {
        setStringValue(GroupClass.FIELD_LOGO, logo);
    }

    /**
     * @return the description od the wiki.
     */
    public String getPolicy()
    {
        return getStringValue(GroupClass.FIELD_POLICY);
    }

    /**
     * Modify the description of the group.
     *
     * @param policy the new description of the group.
     */
    public void setPolicy(String policy)
    {
        setStringValue(GroupClass.FIELD_POLICY, policy);
    }

    /**
     * @return the type of the space.
     */
    public String getAccessLevel()
    {
        return getStringValue(GroupClass.FIELD_ACCESS_LEVEL);
    }

    /**
     * Modify type of the space.
     *
     * @param accessLevel the new domain name of the wiki.
     */
    public void setAccessLevel(String accessLevel)
    {
        setStringValue(GroupClass.FIELD_ACCESS_LEVEL, accessLevel);
    }

    /**
     * @return the type of the space.
     */
    public String getLanguage()
    {
        return getStringValue(GroupClass.FIELD_LANGUAGE);
    }

    /**
     * Modify type of the space.
     *
     * @param language the new domain name of the wiki.
     */
    public void setLanguage(String language)
    {
        setStringValue(GroupClass.FIELD_LANGUAGE, language);
    }

    /**
     * @return the url shortcut of the space.
     */
    public String getEducationSystem()
    {
        return getStringValue(GroupClass.FIELD_EDUCATION_SYSTEM);
    }

    /**
     * Modify url shortcut of the space.
     *
     * @param educationSystem the new domain name of the wiki.
     */
    public void setEducationSystem(String educationSystem)
    {
        setStringValue(GroupClass.FIELD_EDUCATION_SYSTEM, educationSystem);
    }

    /**
     * @return the url shortcut of the space.
     */
    public List getEducationalLevel()
    {
        return getListValue(GroupClass.FIELD_EDUCATIONAL_LEVEL);
    }

    /**
     * Modify url educational level of the space.
     *
     * @param educationalLevel
     */
    public void setEducationalLevel(List educationalLevel)
    {
        setListValue(GroupClass.FIELD_EDUCATIONAL_LEVEL, educationalLevel);
    }

    /**
     * @return the disciplines of the group.
     */
    public List getDisciplines()
    {
        return getListValue(GroupClass.FIELD_DISCIPLINES);
    }

    /**
     * Modify disciplines of the group.
     *
     * @param disciplines the new domain name of the wiki.
     */
    public void setDisciplines(List<String> disciplines)
    {
        setListValue(GroupClass.FIELD_DISCIPLINES, disciplines);
    }

    /**
     * @return the url shortcut of the space.
     */
    public String getLicense()
    {
        return getStringValue(GroupClass.FIELD_LICENSE);
    }

    /**
     * Modify url shortcut of the space.
     *
     * @param license the new domain name of the wiki.
     */
    public void setLicense(String license)
    {
        setStringValue(GroupClass.FIELD_LICENSE, license);
    }
}
