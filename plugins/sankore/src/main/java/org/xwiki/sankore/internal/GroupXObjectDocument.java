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

public class GroupXObjectDocument extends DefaultXObjectDocument
{
    public GroupXObjectDocument(SpaceReference spaceReference, ExecutionContext executionContext) throws XWikiException
    {
        this(ContextUtils.getXWikiContext(executionContext).getWiki().getDocument(new DocumentReference(GroupClass.PREFERENCES_NAME, spaceReference), ContextUtils.getXWikiContext(executionContext)),
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
    public GroupXObjectDocument(XWikiDocument xwikiDocument, int objectId, XWikiContext xwikiContext) throws XWikiException
    {
        super(GroupClass.getInstance(xwikiContext), xwikiDocument, objectId, xwikiContext);
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
     * @param prettyname the new owner of the wiki.
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
    public List<String> getEducationalLevel()
    {
        return getStringListValue(GroupClass.FIELD_EDUCATIONAL_LEVEL);
    }

    /**
     * Modify url educational level of the space.
     *
     * @param educationalLevel
     */
    public void setEducationalLevel(List<String> educationalLevel)
    {
        setStringListValue(GroupClass.FIELD_EDUCATIONAL_LEVEL, educationalLevel);
    }

    /**
     * @return the disciplines of the group.
     */
    public List getDisciplines()
    {
        return getStringListValue(GroupClass.FIELD_DISCIPLINES);
    }

    /**
     * Modify disciplines of the group.
     *
     * @param disciplines the new domain name of the wiki.
     */
    public void setDisciplines(List<String> disciplines)
    {
        setStringListValue(GroupClass.FIELD_DISCIPLINES, disciplines);
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
