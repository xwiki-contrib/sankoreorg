package org.curriki.xwiki.plugin.asset.attachment;

import com.xpn.xwiki.XWiki;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.doc.XWikiAttachment;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.objects.BaseObject;
import org.curriki.xwiki.plugin.asset.DefaultAssetManager;
import org.curriki.xwiki.plugin.asset.Constants;
import org.curriki.xwiki.plugin.asset.Asset;
import org.curriki.xwiki.plugin.asset.UniversalNamespaceResolver;
import org.curriki.xwiki.plugin.asset.external.ExternalAsset;
import com.xpn.xwiki.XWikiException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by IntelliJ IDEA.
 * User: ludovic
 * Date: 10 d�c. 2008
 * Time: 19:42:43
 * To change this template use File | Settings | File Templates.
 */
public class AttachmentAssetManager extends DefaultAssetManager {
    public static String CATEGORY_NAME = Constants.ASSET_CATEGORY_ATTACHMENT;
    public static  Class<? extends Asset> ASSET_CLASS = AttachmentAsset.class;

    public String getCategory() {
         return CATEGORY_NAME;
     }
        
    public Class<? extends Asset> getAssetClass() {
        return ASSET_CLASS;
    }



}
