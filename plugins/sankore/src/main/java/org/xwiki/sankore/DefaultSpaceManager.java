package org.xwiki.sankore;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.manager.ComponentLookupException;
import org.xwiki.component.manager.ComponentManager;
import org.xwiki.context.Execution;
import org.xwiki.context.ExecutionContext;
import org.xwiki.model.EntityType;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.model.reference.DocumentReferenceResolver;
import org.xwiki.model.reference.EntityReference;
import org.xwiki.model.reference.SpaceReference;
import org.xwiki.query.QueryException;
import org.xwiki.query.QueryManager;
import org.xwiki.sankore.internal.SpaceClass;
import org.xwiki.sankore.internal.SpaceXObjectDocument;

import com.xpn.xwiki.XWiki;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.doc.XWikiDocument;

@Component
@Singleton
public class DefaultSpaceManager implements SpaceManager
{
    private static final String DEFAULT_RESOLVER_HINT = "currentmixed/reference";

    @Inject
    private Logger logger;

    @Inject
    private Execution execution;

    @Inject
    private QueryManager queryManager;

    @Inject
    private ComponentManager componentManager;

    public Space getSpace(SpaceReference spaceReference) throws XWikiException
    {
        return new Space(spaceReference, execution.getContext());
    }

    public Space createSpace(SpaceReference spaceReference) throws XWikiException
    {
        Space space = new Space(spaceReference, execution.getContext());
        if (space.isDirty()) {
            return null;
        }
        return space;
    }

    public Space createSpaceFromRequest()
    {
        return null;
    }

    public Space updateSpaceFromRequest()
    {
        return null;
    }

    public void copySpace(SpaceReference spaceReference, SpaceReference targetSpaceReference, boolean doUpdateParents) throws XWikiException
    {
        List<String>  results = null;
        try {
            results =
                    this.queryManager.createQuery("select distinct doc.name from XWikiDocument as doc where doc.space='" + spaceReference.getName() + "'", "xwql")
                            .execute();
        } catch (QueryException qe) {

        }

        XWikiContext xwikiContext = ContextUtils.getXWikiContext(execution.getContext());
        XWiki xwiki = ContextUtils.getXWiki(execution.getContext());
        for(String docName : results)
        {
            XWikiDocument xwikiDocument = xwiki.getDocument(new DocumentReference(docName, spaceReference),
                    xwikiContext);
            XWikiDocument newDocument = xwikiDocument.copyDocument(new DocumentReference(xwikiDocument.getDocumentReference().getName(), targetSpaceReference), xwikiContext);
            if(doUpdateParents && newDocument.getParentReference() != null && newDocument.getParentReference().getLastSpaceReference().getName().equals(spaceReference.getName()))
            {
                DocumentReference newParentReference = new DocumentReference(newDocument.getParentReference().getName(), targetSpaceReference);
                newDocument.setParentReference(newParentReference);
            }
            xwiki.saveDocument(newDocument, xwikiContext);
        }
    }

    public Space createSpaceFromTemplate(SpaceReference spaceReference, SpaceReference templateReference) throws XWikiException
    {
        if (this.countDocuments(spaceReference) > 0)
            return null;

        this.copySpace(templateReference, spaceReference, true);
        Space space = new Space(spaceReference, execution.getContext());
        if (space.isDirty()) {
            space.save();
        }

        return space;
    }

    @SuppressWarnings("unchecked")
    public SpaceReference createSpaceReference(String wikiName, String spaceName)
    {

        EntityReference reference = null;
        if (!StringUtils.isEmpty(wikiName)) {
            reference = new EntityReference(wikiName, EntityType.WIKI);
        }

        if (!StringUtils.isEmpty(spaceName)) {
            reference = new EntityReference(spaceName, EntityType.SPACE, reference);
        }

        reference = new EntityReference(SpaceClass.PREFERENCES_NAME, EntityType.DOCUMENT, reference);

        DocumentReference preferencesReference;
        try {
            preferencesReference =
                    this.componentManager.lookup(DocumentReferenceResolver.class, this.DEFAULT_RESOLVER_HINT)
                            .resolve(reference);
        } catch (ComponentLookupException e) {
            preferencesReference = null;
        }

        return preferencesReference.getLastSpaceReference();
    }

    public int countDocuments(SpaceReference spaceReference)
    {
        int count = 0;
        try {
            count  = Integer.parseInt(
                    this.queryManager.createQuery("select count(doc.fullName) from XWikiDocument as doc where doc.space='" + spaceReference.getName() + "'", "xwql")
                            .execute().get(0).toString());
        } catch (QueryException qe) {

        }

        return count;
    }

    public List<String> searchDocumentsNames(SpaceReference spaceReference, int nb, int start)
    {
        List<String> results = new ArrayList<String>();

        try {
            results = this.queryManager.createQuery("select distinct doc.fullName from XWikiDocument as doc where doc.space='" + spaceReference.getName() + "'", "xwql")
                            .setLimit(nb)
                            .setOffset(start)
                            .execute();
        } catch (QueryException qe) {

        }

        return results;
    }

    public List<String> searchDocumentsNames(SpaceReference spaceReference, String whereHql, int nb, int start)
    {
        List<String> results = new ArrayList<String>();
        try {
            results = this.queryManager.createQuery("select distinct doc.fullName from XWikiDocument as doc where doc.space='" + spaceReference.getName() + "' " + whereHql, "xwql")
                    .setLimit(nb)
                    .setOffset(start)
                    .execute();
        } catch (QueryException qe) {

        }

        return results;
    }
}