package org.xwiki.sankore;

import java.lang.String;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.commons.lang.StringUtils;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.manager.ComponentLookupException;
import org.xwiki.component.manager.ComponentManager;
import org.xwiki.model.EntityType;
import org.xwiki.model.internal.scripting.ModelScriptService;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.model.reference.DocumentReferenceResolver;
import org.xwiki.model.reference.EntityReference;
import org.xwiki.model.reference.EntityReferenceResolver;
import org.xwiki.model.reference.SpaceReference;
import org.xwiki.sankore.internal.SpaceClass;
import org.xwiki.script.service.ScriptService;

import com.xpn.xwiki.XWikiException;

@Component
@Named("spacemgr")
@Singleton
public class SpaceManagerScriptService implements ScriptService
{
    @Inject
    private SpaceManager spaceManager;

    /**
     * Used to dynamically look up component implementations based on a given hint.
     */
    @Inject
    private ComponentManager componentManager;

    public SpaceReference createSpaceReference(String wikiName, String spaceName)
    {
        return this.spaceManager.createSpaceReference(wikiName, spaceName);
    }

    public Space getSpace(SpaceReference reference) throws XWikiException
    {
        return spaceManager.getSpace(reference);
    }

    public Space createSpaceFromTemplate(SpaceReference spaceReference, SpaceReference templateReference)
    {
        try {
            this.spaceManager.createSpaceFromTemplate(spaceReference, templateReference);
        } catch (XWikiException xe) {

        }

        Space space = null;
        try {
            space = this.spaceManager.getSpace(spaceReference);
        } catch (XWikiException xe) {

        }

        return space;
    }

    public int countDocuments(SpaceReference spaceReference)
    {
        return this.spaceManager.countDocuments(spaceReference);
    }

    public List<String> searchDocuments(SpaceReference spaceReference, int nb, int start)
    {
        return this.spaceManager.searchDocumentsNames(spaceReference, nb, start);
    }
}