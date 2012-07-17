package org.xwiki.sankore;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.xwiki.component.annotation.Component;
import org.xwiki.model.reference.DocumentReferenceResolver;
import org.xwiki.model.reference.EntityReference;
import org.xwiki.sankore.internal.ClassManager;
import org.xwiki.sankore.internal.UserObjectDocument;
import org.xwiki.script.service.ScriptService;

import com.xpn.xwiki.XWikiException;

@Component
@Named("usersmgr")
@Singleton
public class UsersManagerScriptService implements ScriptService
{
    @Inject
    @Named("UsersClass")
    ClassManager<UserObjectDocument> usersClass;

    @Inject
    @Named("current/reference")
    DocumentReferenceResolver<EntityReference> currentReferenceDocumentReferenceResolver;

    @Inject
    DocumentReferenceResolver<String> stringDocumentReferenceResolver;

    public UserObjectDocument getUser(String userName)
            throws XWikiException
    {
        return usersClass.getDocumentObject(stringDocumentReferenceResolver.resolve(userName));
    }

    public UserObjectDocument newUser(String userName)
            throws XWikiException
    {
        return usersClass.newDocumentObject(stringDocumentReferenceResolver.resolve(userName));
    }

    public List<UserObjectDocument> searchUserProfilesByField(String fieldName, String fieldValue)
            throws XWikiException
    {
        return usersClass.searchDocumentObjectsByField(fieldName, fieldValue);
    }

    public List<UserObjectDocument> searchUserProfilesByFields(Map<String, Object> fields)
            throws XWikiException
    {
        return usersClass.searchDocumentObjectsByFields(fields);
    }

    public void saveUser(UserObjectDocument userObjectDocument)
            throws XWikiException
    {
        this.usersClass.saveDocumentObject(userObjectDocument);
    }
}
