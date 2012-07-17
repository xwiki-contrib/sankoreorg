package org.xwiki.sankore;

import java.lang.String;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.xwiki.component.annotation.Component;
import org.xwiki.model.reference.SpaceReference;
import org.xwiki.sankore.internal.SpaceObjectDocument;
import org.xwiki.script.service.ScriptService;

import com.xpn.xwiki.XWikiException;

@Component
@Named("spacemgr")
@Singleton
public class SpaceManagerScriptService implements ScriptService
{
    @Inject
    private SpaceManager spaceManager;

    public SpaceReference createSpaceReference(String wikiName, String spaceName)
    {
        return this.spaceManager.createSpaceReference(wikiName, spaceName);
    }

    public SpaceObjectDocument getSpace(SpaceReference reference) throws XWikiException
    {
        return spaceManager.getSpace(reference);
    }

    public SpaceObjectDocument createSpaceFromTemplate(SpaceReference spaceReference, SpaceReference templateReference)
    {
        try {
            this.spaceManager.createSpaceFromTemplate(spaceReference, templateReference);
        } catch (XWikiException xe) {

        }

        SpaceObjectDocument space = null;
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