package org.curriki.xwiki.plugin.asset.attachment;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.doc.XWikiAttachment;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.objects.BaseObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.curriki.xwiki.plugin.asset.Asset;
import org.curriki.xwiki.plugin.asset.Constants;
import org.curriki.xwiki.plugin.asset.DefaultAssetManager;
import org.curriki.xwiki.plugin.asset.UniversalNamespaceResolver;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathException;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by Flaviusx on 12/12/13.
 */
public class SankoreAssetManager extends AttachmentAssetManager {

    private static final Log LOG = LogFactory.getLog(SankoreAssetManager.class);

    public static String CATEGORY_NAME = Constants.ASSET_CATEGORY_SANKORE;
    public static  Class<? extends Asset> ASSET_CLASS = AttachmentAsset.class;

    public String getCategory() {
        return CATEGORY_NAME;
    }

    public Class<? extends Asset> getAssetClass() {
        return ASSET_CLASS;
    }

    public void updateSubAssetClass(XWikiDocument assetDoc, String filetype, String category, XWikiAttachment attachment, XWikiContext context)
    {
        if (filetype.equals("ubz") || filetype.equals("ubw")) {
            String contents = new String();
            ZipEntry ze = null;
            try {
                ZipInputStream zin = new ZipInputStream(attachment.getContentInputStream(context));
                while ((ze = zin.getNextEntry()) != null) {
                    if (ze.getName().equals("metadata.rdf")) {
                        byte[] buffer = new byte[8192];
                        int len;
                        while ((len = zin.read(buffer)) != -1) {
                            String buffersz = new String(buffer, 0, len, "UTF-8");
                            contents += buffersz;
                        }
                        break;
                    }
                }
                zin.close();
                DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = domFactory.newDocumentBuilder();
                Document dDoc = builder.parse(new ByteArrayInputStream(contents.getBytes("UTF8")));
                XPath xPath = XPathFactory.newInstance().newXPath();
                xPath.setNamespaceContext(new UniversalNamespaceResolver(dDoc));
                assetDoc.setTitle(xPath.evaluate("RDF/Description/sessionTitle", dDoc));
                assetDoc.setTags(xPath.evaluate("RDF/Description/sessionKeywords", dDoc), context);
                BaseObject assetObject = assetDoc.getObject(Constants.ASSET_CLASS);
                BaseObject licenceObject = assetDoc.getObject(Constants.ASSET_LICENCE_CLASS);
                assetObject.setLargeStringValue("description", xPath.evaluate("RDF/Description/sessionObjectives", dDoc));
                assetObject.setStringValue("keywords", xPath.evaluate("RDF/Description/sessionKeywords", dDoc));
                assetObject.setStringValue("language", "fr");
                assetObject.setStringValue("education_system", "AssetMetadata.FranceEducation");
                String xvalue = xPath.evaluate("RDF/Description/sessionGradeLevel", dDoc);
                if (!xvalue.isEmpty() && context.getWiki().exists("SankoreCode.GradeLevelMapping", context)) {
                    String mappingString = context.getWiki().getDocument("SankoreCode.GradeLevelMapping", context).getContent();
                    Scanner scanner = new Scanner(mappingString);
                    while (scanner.hasNextLine()) {
                        String line = scanner.nextLine();
                        String[] pair = line.split("=");
                        String key = pair[0];
                        if (key.equals(xvalue)) {
                            xvalue = pair[1];
                            assetObject.setDBStringListValue("educational_level", Arrays.asList(xvalue.split(",")));
                            break;
                        }
                    }
                }
                xvalue = xPath.evaluate("RDF/Description/sessionSubjects", dDoc);
                if (!xvalue.isEmpty() && context.getWiki().exists("SankoreCode.SubjectsMapping", context)) {
                    String mappingString = context.getWiki().getDocument("SankoreCode.SubjectsMapping", context).getContent();
                    Scanner scanner = new Scanner(mappingString);
                    while (scanner.hasNextLine()) {
                        String line = scanner.nextLine();
                        String[] pair = line.split("=");
                        String key = pair[0];
                        if (key.equals(xvalue)) {
                            xvalue = pair[1];
                            assetObject.setDBStringListValue("fw_items", Arrays.asList(xvalue.split(",")));
                            break;
                        }
                    }
                }
                xvalue = xPath.evaluate("RDF/Description/sessionType", dDoc);
                if (!xvalue.isEmpty() && context.getWiki().exists("SankoreCode.TypeMapping", context)) {
                    String mappingString = context.getWiki().getDocument("SankoreCode.TypeMapping", context).getContent();
                    Scanner scanner = new Scanner(mappingString);
                    while (scanner.hasNextLine()) {
                        String line = scanner.nextLine();
                        String[] pair = line.split("=");
                        String key = pair[0];
                        if (key.equals(xvalue)) {
                            xvalue = pair[1];
                            assetObject.setDBStringListValue("instructional_component", Arrays.asList(xvalue.split(",")));
                            break;
                        }
                    }
                }
                xvalue = xPath.evaluate("RDF/Description/sessionLicence", dDoc);
                if (!xvalue.isEmpty() && context.getWiki().exists("SankoreCode.LicenseMapping", context)) {
                    String mappingString = context.getWiki().getDocument("SankoreCode.LicenseMapping", context).getContent();
                    Scanner scanner = new Scanner(mappingString);
                    while (scanner.hasNextLine()) {
                        String line = scanner.nextLine();
                        String[] pair = line.split("=");
                        String key = pair[0];
                        if (key.equals(xvalue)) {
                            xvalue = pair[1];
                            licenceObject.setStringValue("licenseType", xvalue);
                            break;
                        }
                    }
                }
                licenceObject.setStringValue("rightsHolder", xPath.evaluate("RDF/Description/sessionAuthors", dDoc));
            } catch (IOException ioe) {
                LOG.error("Unexpected exception ", ioe);
            } catch (ParserConfigurationException pce) {
                LOG.error("Unexpected exception ", pce);
            } catch (SAXException saxe) {
                LOG.error("Unexpected exception ", saxe);
            } catch (XPathException xpe) {
                LOG.error("Unexpected exception ", xpe);
            } catch (XWikiException xe) {
                LOG.error("Unexpected exception ", xe);
            }
        }
    }
}
