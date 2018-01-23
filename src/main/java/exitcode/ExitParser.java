package exitcode;

import org.xml.sax.*;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import java.security.*;
import java.util.ArrayList;
import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.lang.Exception;
import java.util.zip.*;

// Internal dependancies: Logger, Main

public class ExitParser {

    public static Document getDocument(String docString) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setIgnoringComments(true);
            factory.setIgnoringElementContentWhitespace(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(new InputSource(docString));
        }
        catch(Exception ex) {
            Logger.error(ex.getMessage());
        }
        return null;
    }

    public static void createNewSave(File pathToFile) throws IOException {
        pathToFile.getParentFile().mkdirs();
        InputStream stream = null;
        OutputStream resStreamOut = null;
        String jarFolder;
        try {
            stream = ExitParser.class.getResourceAsStream("/resources/save.ecsave");//note that each / is a directory down in the "jar tree" been the jar the root of the tree
            if(stream == null) {
                throw new Exception("Cannot get resource \"/resources/save.ecsave\" from Jar file.");
            }
            int readBytes;
            byte[] buffer = new byte[4096];
            resStreamOut = new FileOutputStream(pathToFile);
            while ((readBytes = stream.read(buffer)) > 0) {
                resStreamOut.write(buffer, 0, readBytes);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            stream.close();
            resStreamOut.close();
        }
    }

    public static String getBuildDefaultSystem(String hostname) {
        String system = String.format("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n\n" +
                "<system>\n" +
                "    <info>\n" +
                "        <system name=\"PiXElos\"/>\n" +
                "        <host name=\"%s\"/>\n" +
                "        <owner uid=\"1000\"/>\n" +
                "        <top dir=\"/\"/>\n" +
                "    </info>\n" +
                "\n" +
                "    <hardware>\n" +
                "        <type name=\"Laptop\"/>\n" +
                "        <cpu ghz=\"1.0\" cores=\"1\" type=\"1\" upgradeable=\"false\"/>\n" +
                "        <ram mb=\"500\" max_mb=\"500\" upgradeable=\"false\"/>\n" +
                "        <storage mb=\"1000\" type=\"1\" upgradeable=\"false\"/>\n" +
                "    </hardware>\n" +
                "\n" +
                "    <files>\n" +
                "        <dir group=\"-\" groupp=\"r-x\" name=\"root\" otherp=\"r-x\" owner=\"0\" ownerp=\"r-x\">\n" +
                "            <dir group=\"-\" groupp=\"r-x\" name=\"Desktop\" otherp=\"r-x\" owner=\"0\" ownerp=\"r-x\">\n" +
                "            </dir>\n" +
                "            <dir group=\"-\" groupp=\"r-x\" name=\"Downloads\" otherp=\"r-x\" owner=\"0\" ownerp=\"r-x\">\n" +
                "            </dir>\n" +
                "            <dir group=\"-\" groupp=\"r-x\" name=\"Documents\" otherp=\"r-x\" owner=\"0\" ownerp=\"r-x\">\n" +
                "            </dir>\n" +
                "        </dir>\n" +
                "        <dir group=\"0\" groupp=\"r-x\" name=\"home\" otherp=\"r-x\" owner=\"0\" ownerp=\"rwx\">\n" +
                "        </dir>\n" +
                "        <dir group=\"0\" groupp=\"r-x\" name=\"bin\" otherp=\"r-x\" owner=\"0\" ownerp=\"rwx\">\n" +
                "        </dir>\n" +
                "    </files>\n" +
                "</system>", hostname);
        return system;
    }

    public static void createNewSystem(String inzipfile, String data) {
        Path zipFilePath = Paths.get(Main.savefile.toString());
        try ( java.nio.file.FileSystem fileSystem = FileSystems.newFileSystem(zipFilePath, null) ) {
            Path fileInsideZipPath = fileSystem.getPath(inzipfile);
            Logger.message(inzipfile);
            File file = new File("saves/.temp");
            try( PrintWriter out = new PrintWriter(file) ){
                out.println(data);
            } catch (Exception ex) { ex.printStackTrace(); }
            Path myFilePath = Paths.get("saves/.temp");
            if (Files.exists(fileInsideZipPath)) {
                Files.delete(fileInsideZipPath);
            }
            Files.copy(myFilePath, fileInsideZipPath); // --
            fileSystem.close();
            file.delete();
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    /**
     * Returns Document from file.zip:system.xml
     *
     * Grabs file.zip location from Main.savefile
     *
     * @param inzipfile The system.xml file within the zip to open.
     *
     * @return Document
     */
    public static Document getDocFromZip(String inzipfile) {
        try {
            ZipFile zipFile = new ZipFile(Main.savefile);
            ZipEntry entry = zipFile.getEntry(inzipfile);
            InputStream stream = zipFile.getInputStream(entry);
            Document doc = readXml(stream);
            stream.close();
            zipFile.close();
            return doc;
        } catch (Exception ex) {
            Logger.error(ex.getMessage());
        }
        return null;
    }

    public static void writeDocToZip(Document document, String inzipfile) {
        Path zipFilePath = Paths.get(Main.savefile.toString());
        try ( java.nio.file.FileSystem fileSystem = FileSystems.newFileSystem(zipFilePath, null) ) {
            Path fileInsideZipPath = fileSystem.getPath(inzipfile);
            writeXml(document, "saves/.temp");
            File file = new File("saves/.temp");
            Path myFilePath = Paths.get("saves/.temp");
            if (Files.exists(fileInsideZipPath)) {
                Files.delete(fileInsideZipPath);
            }
            Files.copy(myFilePath, fileInsideZipPath);
            fileSystem.close();
            file.delete();
        } catch (Exception ex) {
            if (ex.getMessage().contains("FileSystemException")) {
                // TODO -- Notifacation
            } else {
                ex.printStackTrace();
            }
        }
    }

    public static NodeList getNextSet(NodeList data, String attrName) {
        try {
            for(int i=0;  i < data.getLength(); i++){
                Element dataElement = (Element)data.item(i);
                return dataElement.getElementsByTagName(attrName);
            }
        } catch (Exception ex) {
            Logger.error(ex.getMessage());
        }
        return null;
    }

    public static Node getNextSetAttr(Node data, String attrName) {
        try {
            Element dataElement = (Element) data;
            Node childNode = dataElement.getFirstChild();
            while( childNode.getNextSibling()!=null ){
                childNode = childNode.getNextSibling();
                if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                    if (childNode.getNodeName().trim().equals("dir")) {
                        Element childElement = (Element) childNode;
                        if(childElement.hasAttribute("name")) {
                            if (childElement.getAttribute("name").contains(attrName)) {
                                return childNode;
                            }
                        }
                    }
                }
            }
        }
        catch(Exception ex) {
            Logger.error(ex.getMessage());
        }
        return null;
    }

    public static String getElementValue(NodeList data, String elementName) {
        try {
            for(int i=0;  i < data.getLength(); i++){
                Element dataElement = (Element)data.item(i);
                NodeList nodeList = dataElement.getElementsByTagName(elementName);
                Element nodeElement = (Element)nodeList.item(0);
                NodeList elementList = nodeElement.getChildNodes();
                return ((Node)elementList.item(0)).getNodeValue();
            }
        }
        catch(Exception ex) {
            Logger.error(ex.getMessage());
        }
        return null;
    }

    public static String getAttributeValue(NodeList data, String elementName, String attrName) {
        try {
            for(int i=0;  i < data.getLength(); i++){
                Element dataElement = (Element)data.item(i);
                NodeList nodeList = dataElement.getElementsByTagName(elementName);
                Element nodeElement = (Element)nodeList.item(0);
                if(nodeElement.hasAttribute(attrName)){
                    return nodeElement.getAttribute(attrName);
                } else {
                    return null;
                }
            }
        }
        catch(Exception ex) {
            Logger.error(ex.getMessage());
        }
        return null;
    }

    public static String getSettingValue(String fileName, String dataName, String elementName) {
        try {
            Document xmlDoc = ExitParser.getDocument(fileName);
            NodeList dataList = xmlDoc.getElementsByTagName(dataName);
            return ExitParser.getElementValue(dataList, elementName);
        } catch(Exception e) {
            return null;
        }
    }

    public static void setSettingValue(String fileName, String dataName, String elementName, String elementValue) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(fileName);

            // Get the root element
            Node data = doc.getFirstChild();

            NodeList dataList = doc.getElementsByTagName(dataName);

            Node elemValue = null;

            try {
                for(int i=0;  i < dataList.getLength(); i++){
                    Node dataItem = dataList.item(i);
                    Element dataElement = (Element)dataItem;
                    NodeList nodeList = dataElement.getElementsByTagName(elementName);
                    Element nodeElement = (Element)nodeList.item(0);
                    NodeList elementList = nodeElement.getChildNodes();
                    elemValue = elementList.item(0);
                }
            }
            catch(Exception ex) {
                Logger.error(ex.getMessage());
            }

            // I am not doing any thing with it just for showing you
            String currentElemValue = elemValue.getNodeValue();

            elemValue.setTextContent(elementValue);

            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory
                    .newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(fileName));
            transformer.transform(source, result);

        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (TransformerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static String getSettingValue_player(String elementName) {
        return ExitParser.getSettingValue("./profiles/" + LoginScreen.USERNAME + "/player.xml", "playerdata", elementName);
    }
    public static void setSettingValue_player(String elementName, String elementValue) {
        ExitParser.setSettingValue("./profiles/" + LoginScreen.USERNAME + "/player.xml", "playerdata", elementName, elementValue);
    }
    
    public static String getSettingValue_theme(String elementName) {
        return ExitParser.getSettingValue("./theme.xml", "themedata", elementName);
    }

    public static void setSettingValue_theme(String elementName, String elementValue) {
        ExitParser.setSettingValue("./theme.xml", "themedata", elementName, elementValue);
    }

    public static String getSettingValue_system(String elementName) {
        return ExitParser.getSettingValue("./config.xml", "systemdata", elementName);
    }
    public static void setSettingValue_system(String elementName, String elementValue) {
        ExitParser.setSettingValue("./config.xml", "systemdata", elementName, elementValue);
    }

    public static String passHasher(String string) {
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(string.getBytes("UTF-8"));
            StringBuffer hexString = new StringBuffer();

            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public static ArrayList<String> getWebsite(String website) { // http://www.website.com/web/

        String website2 = website;
        website2 = website
                .toLowerCase()
                .replace("http://", "")
                .replace("http:/", "")
                .replace("https://", "")
                .replace("https:/", "")
                .replace("browser://", "")
                .replace("browser:/", "")
                .replace("ftp://", "")
                .replace("ftp:/", "")
                .replace("www.", "")
                .trim();

        String website3 = website2;
        if (website3.contains("/")) {
            website3 = website3.split("/", 2)[0];
        }
        try {
            ArrayList<String> web_list = new ArrayList<String>();
            if (ExitParser.getSettingValue(ExitParser.class.getResource("/web/websites.xml").toURI().toString(), website3.replace(".", "_"), "ingame").contains("true")) {
                if (ExitParser.getSettingValue(ExitParser.class.getResource("/web/websites.xml").toURI().toString(), website3.replace(".", "_"), "jarfile").contains("true")) {
                    if (website2.contains("/")) {
                        File subdir = new File((ExitParser.class.getResource(ExitParser.getSettingValue(ExitParser.class.getResource("/web/websites.xml").toURI().toString(), website3.replace(".", "_"), "sublocation")+ website2).toURI().toString()) + website2.split("/", 2)[1]);
                        if (subdir.isFile() | subdir.isDirectory()) {
                            web_list.add((ExitParser.class.getResource(ExitParser.getSettingValue(ExitParser.class.getResource("/web/websites.xml").toURI().toString(), website3.replace(".", "_"), "sublocation")+ website2).toURI().toString()) + website2.split("/", 2)[1]);
                        } else {
                            Logger.debug(subdir.toString());
                            return getWebsite("error404");
                        }
                        web_list.add(ExitParser.getSettingValue(ExitParser.class.getResource("/web/websites.xml").toURI().toString(), website3.replace(".", "_"), "prefix") + website2);
                    } else {
                        web_list.add(ExitParser.class.getResource(ExitParser.getSettingValue(ExitParser.class.getResource("/web/websites.xml").toURI().toString(), website3.replace(".", "_"), "location")).toURI().toString());
                        web_list.add(ExitParser.getSettingValue(ExitParser.class.getResource("/web/websites.xml").toURI().toString(), website3.replace(".", "_"), "prefix") + website2);
                    }
                    return web_list;
                } else {
                    if (website2.contains("/")) {
                        File subdir = new File(System.getProperty("user.dir"), (ExitParser.getSettingValue(ExitParser.class.getResource("/web/websites.xml").toURI().toString(), website3.replace(".", "_"), "sublocation")) + website2.split("/", 2)[1]);
                        if (subdir.isFile() | subdir.isDirectory()) {
                            web_list.add((new File(System.getProperty("user.dir"), (ExitParser.getSettingValue(ExitParser.class.getResource("/web/websites.xml").toURI().toString(), website3.replace(".", "_"), "sublocation"))).toURI().toString()) + website2.split("/", 2)[1]);
                        } else {
                            subdir = (new File((ExitParser.getSettingValue(ExitParser.class.getResource("/web/websites.xml").toURI().toString(), website3.replace(".", "_"), "sublocation")) + website2.split("/", 2)[1]));
                            if (subdir.isFile() | subdir.isDirectory()) {
                                web_list.add((new File((ExitParser.getSettingValue(ExitParser.class.getResource("/web/websites.xml").toURI().toString(), website3.replace(".", "_"), "sublocation")) + website2.split("/", 2)[1])).toURI().toString());
                            } else {
                                Logger.debug(subdir.toString());
                                return getWebsite("error404");
                            }
                        }
                        web_list.add(ExitParser.getSettingValue(ExitParser.class.getResource("/web/websites.xml").toURI().toString(), website3.replace(".", "_"), "prefix") + website2);
                    } else {
                        if ((new File(System.getProperty("user.dir"), (ExitParser.getSettingValue(ExitParser.class.getResource("/web/websites.xml").toURI().toString(), website3.replace(".", "_"), "location")))).isFile()) {
                            web_list.add((new File(System.getProperty("user.dir"), (ExitParser.getSettingValue(ExitParser.class.getResource("/web/websites.xml").toURI().toString(), website3.replace(".", "_"), "location"))).toURI().toString()));
                        } else {
                            if ((new File((ExitParser.getSettingValue(ExitParser.class.getResource("/web/websites.xml").toURI().toString(), website3.replace(".", "_"), "location")))).isFile()) {
                                web_list.add((new File((ExitParser.getSettingValue(ExitParser.class.getResource("/web/websites.xml").toURI().toString(), website3.replace(".", "_"), "location"))).toURI().toString()));
                            } else {
                                return getWebsite("error404");
                            }
                        }
                        web_list.add(ExitParser.getSettingValue(ExitParser.class.getResource("/web/websites.xml").toURI().toString(), website3.replace(".", "_"), "prefix") + website2);
                    }
                    return web_list;
                }
            } else {
                web_list.add(ExitParser.getSettingValue(ExitParser.class.getResource("/web/websites.xml").toURI().toString(), website3.replace(".", "_"), "location"));
                web_list.add(ExitParser.getSettingValue(ExitParser.class.getResource("/web/websites.xml").toURI().toString(), website3.replace(".", "_"), "location"));
                return web_list;
            }
        } catch(Exception e) {
            Logger.error(e.getMessage());
            return getWebsite("error404");
        }
    }

    public static void writeXml(Document document, String fileName) throws TransformerConfigurationException, TransformerException, FileNotFoundException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.transform(new DOMSource(document), new StreamResult(fileName));
    }

    public static Document readXml(InputStream stream) throws SAXException, IOException, ParserConfigurationException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setIgnoringComments(true);
        dbf.setIgnoringElementContentWhitespace(true);

        DocumentBuilder db = null;
        db = dbf.newDocumentBuilder();
        db.setEntityResolver(new NullResolver());

        return db.parse(stream);
    }

}

class NullResolver implements EntityResolver {
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException,
            IOException {
        return new InputSource(new StringReader(""));
    }
}