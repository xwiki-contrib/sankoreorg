package org.xwiki.sankore;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;
import org.xwiki.context.Execution;
import org.xwiki.model.EntityType;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.model.reference.DocumentReferenceResolver;
import org.xwiki.model.reference.EntityReference;
import org.xwiki.model.reference.SpaceReference;
import org.xwiki.query.QueryException;
import org.xwiki.query.QueryManager;
import org.xwiki.sankore.internal.ClassManager;
import org.xwiki.sankore.internal.SpaceClass;
import org.xwiki.sankore.internal.SpaceObjectDocument;

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
    @Named("SpaceClass")
    ClassManager<SpaceObjectDocument> spaceClass;

    @Inject
    @Named("current/reference")
    DocumentReferenceResolver<EntityReference> currentReferenceDocumentReferenceResolver;

    @Inject
    private QueryManager queryManager;

    public SpaceObjectDocument getSpace(SpaceReference spaceReference) throws XWikiException
    {
        return this.spaceClass.getDocumentObject(new DocumentReference(SpaceClass.PREFERENCES_NAME, spaceReference));
    }

    public SpaceObjectDocument createSpace(SpaceReference spaceReference) throws XWikiException
    {
        return spaceClass.newDocumentObject(new DocumentReference(SpaceClass.PREFERENCES_NAME, spaceReference));
    }

    public SpaceObjectDocument createSpaceFromRequest()
    {
        return null;
    }

    public SpaceObjectDocument updateSpaceFromRequest()
    {
        return null;
    }

    public void copySpace(SpaceReference spaceReference, SpaceReference targetSpaceReference, boolean doUpdateParents)
            throws XWikiException
    {
        XWikiContext context = ContextUtils.getXWikiContext(execution.getContext());

        List<DocumentReference> references = context.getWiki().getStore()
                .searchDocumentReferences("where doc.space='" + spaceReference.getName() + "'", context);

        if (references != null) {
            for(DocumentReference reference : references) {
                context.getWiki().copyDocument(reference,
                        new DocumentReference(reference.getName(), targetSpaceReference),
                        null,
                        true,
                        true,
                        true,
                        context);
            }
        } else {
            logger.info("copySpace() no documents found in space: " + spaceReference.getName());
        }
    }

    public SpaceObjectDocument createSpaceFromTemplate(SpaceReference spaceReference, SpaceReference templateReference)
            throws XWikiException
    {
        if (this.countDocuments(spaceReference) > 0) {
            return null;
        }

        this.copySpace(templateReference, spaceReference, true);

        return this.spaceClass.newDocumentObject(new DocumentReference(SpaceClass.PREFERENCES_NAME, spaceReference));
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

        DocumentReference documentReference = currentReferenceDocumentReferenceResolver.resolve(reference);

        return documentReference.getLastSpaceReference();
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