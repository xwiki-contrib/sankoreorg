package org.xwiki.sankore;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.xwiki.component.annotation.Component;
import org.xwiki.model.EntityType;
import org.xwiki.model.reference.DocumentReferenceResolver;
import org.xwiki.model.reference.EntityReference;
import org.xwiki.sankore.internal.ClassManager;
import org.xwiki.sankore.internal.UserProfileObjectDocument;
import org.xwiki.script.service.ScriptService;

import com.xpn.xwiki.XWikiException;

@Component
@Named("userprofilesmgr")
@Singleton
public class UserProfilesManagerScriptService implements ScriptService
{
    @Inject
    @Named("UserProfileClass")
    ClassManager<UserProfileObjectDocument> userProfileClass;

    @Inject
    @Named("current/reference")
    DocumentReferenceResolver<EntityReference> currentReferenceDocumentReferenceResolver;


    public UserProfileObjectDocument getUserProfile(String spaceName, String userName)
            throws XWikiException
    {
        return userProfileClass.getDocumentObject(
                currentReferenceDocumentReferenceResolver.resolve(
                        new EntityReference(userName, EntityType.DOCUMENT,
                                new EntityReference(spaceName, EntityType.SPACE))), 0);
    }

    public UserProfileObjectDocument newUserProfile(String spaceName, String userName)
            throws XWikiException
    {
        return userProfileClass.newDocumentObject(
                currentReferenceDocumentReferenceResolver.resolve(
                        new EntityReference(userName, EntityType.DOCUMENT,
                                new EntityReference(spaceName, EntityType.SPACE))));
    }

    public List<UserProfileObjectDocument> searchUserProfilesByField(String fieldName, String fieldValue)
            throws XWikiException
    {
        return userProfileClass.searchDocumentObjectsByField(fieldName, fieldValue);
    }

    public List<UserProfileObjectDocument> searchUserProfilesByFields(Map<String, Object> fields)
            throws XWikiException
    {
        return userProfileClass.searchDocumentObjectsByFields(fields);
    }

    public void saveUserProfile(UserProfileObjectDocument userProfileObjectDocument)
            throws XWikiException
    {
        this.userProfileClass.saveDocumentObject(userProfileObjectDocument);
    }
}
