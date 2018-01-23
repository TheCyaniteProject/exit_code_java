package exitcode;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;
import java.lang.Exception;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

// Internal dependancies: ExitParser, Console, Command, Logger

public class FileNavigator {

    private String system = Main.defaultSystem;

    private String location;

    private String homeSystem;

    private String home;

    private String uid = "1000";

    /**
     * A process for navigating the files of a system.
     *
     * @param homeLocation path/to/system.xml file, located within the .ecsave file.
     *
     * @param homePath The path/to/home directory within the in-game file system.
     */
    public FileNavigator(String homeLocation, String homePath) {
        setHome(homeLocation, homePath);
    }

    /**
     * Sets the homeLocation, and homePath of the fileSystem.
     *
     * @param homeLocation path/to/system.xml file, located within the .ecsave file.
     *
     * @param homePath The path/to/home directory within the in-game file system.
     */
    public void setHome(String homeLocation, String homePath) {
        this.homeSystem = homeLocation;
        this.home = homePath;
    }

    public ArrayList<String> getHome() {
        ArrayList<String> homeList = new ArrayList<String>();
        homeList.add(homeSystem);
        homeList.add(home);

        return homeList;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public String getSystem() {
        return this.system;
    }

    public Node getFile(String path) {
        path = path.replace("\\", "/");
        String fileName;
        String path2;
        Console console = new Console("/", this);
        Document doc = ExitParser.getDocFromZip(console.getNavigator().getSystem());
        NodeList dataList = doc.getElementsByTagName("info");
        if (path.endsWith("/")) {
            return null;
        } else {
            fileName = (new File(path)).getName();
            path2 = path.replace(fileName, "");
            if (!path2.trim().equals(ExitParser.getAttributeValue(dataList, "top", "dir"))) {
                path2 = (new File(path)).getParent();
            }
        }

        Node childNode = ((Element)getDirectory(path2)).getFirstChild();
        try {
            while( childNode.getNextSibling()!=null ){
                childNode = childNode.getNextSibling();
                if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                    if (childNode.getNodeName().trim().equals("file")) {
                        Element childElement = (Element) childNode;
                        if(childElement.hasAttribute("name")) {
                            if (childElement.getAttribute("name").contains(fileName.trim())) {
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

    public void makeFile(Node node, String fileName, String data) {
        Element newFile = node.getOwnerDocument().createElement("file");
        Element nodeElement = (Element) node;
        nodeElement.appendChild(newFile);
        newFile.appendChild(nodeElement.getOwnerDocument().createCDATASection(data));
        newFile.setAttribute("name", fileName);
        newFile.setAttribute("owner", this.uid);
        newFile.setAttribute("group", this.uid);
        newFile.setAttribute("ownerp", "rwx");
        newFile.setAttribute("groupp", "r-x");
        newFile.setAttribute("otherp", "r-x");
    }

    public void makeFile(Node node, String fileName) {
        makeFile(node, fileName, " ");
    }

    public static String readStringFromFile(Node node) {
        if (node == null) {
            return null;
        }
        return node.getTextContent();
    }

    public static Object readObjectFromFile(Node node) {
        try {
            byte b[] = readStringFromFile(node).getBytes();
            ByteArrayInputStream byteArray = new ByteArrayInputStream(b);
            ObjectInputStream objectIn = new ObjectInputStream(byteArray);
            return (Object) objectIn.readObject();
        } catch (Exception e) {
            Logger.error(e.getMessage());
        }
        return null;
    }

    public static void writeStringToFile(Node node, String string) {
        if (node.hasChildNodes()) {
            Node childNode = node.getFirstChild();
            node.removeChild(childNode);
            while( childNode.getNextSibling()!=null ){
                childNode = childNode.getNextSibling();
                node.removeChild(childNode);
            }
        }
        node.appendChild(node.getOwnerDocument().createCDATASection(string));
    }

    public static void writeObjectToFile(Node node, Object object) {
        try {
            ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
            ObjectOutputStream objectOut = new ObjectOutputStream(byteArray);
            objectOut.writeObject(object);
            objectOut.flush();
            writeStringToFile(node, byteArray.toString());
        } catch (Exception e) {
            Logger.error(e.getMessage());
        }
    }

    public void sendToOblivion(Node node) { // Removes the node | Used to delete files and folders or anything else.
        node.getParentNode().removeChild(node);
    }

    public Boolean checkFile(String path) {
        path = path.replace("\\", "/");
        String fileName;
        String path2;
        Console console = new Console("/", this);
        Document doc = ExitParser.getDocFromZip(console.getNavigator().getSystem());
        NodeList dataList = doc.getElementsByTagName("info");
        if (path.endsWith("/")) {
            return false;
        } else {
            fileName = (new File(path)).getName();
            path2 = path.replace(fileName, "");
            if (!path2.trim().equals(ExitParser.getAttributeValue(dataList, "top", "dir"))) {
                path2 = (new File(path)).getParent();
            }
        }
        Node childNode = ((Element)getDirectory(path2)).getFirstChild();
        try {
            while( childNode.getNextSibling()!=null ){
                childNode = childNode.getNextSibling();
                if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                    if (childNode.getNodeName().trim().equals("file")) {
                        Element childElement = (Element) childNode;
                        if(childElement.hasAttribute("name")) {
                            if (childElement.getAttribute("name").contains(fileName.trim())) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        catch(Exception ex) {
            Logger.error(ex.getMessage());
        }
        return false;
    }

    public Node getDirectory(String path) {
        Document doc = ExitParser.getDocFromZip(system);
        NodeList dataList = doc.getElementsByTagName("files");
        Node node = dataList.item(0);
        Node pathItem = parsePath(node, path);
        return pathItem;
    }

    public String getCurrentDirectory() {
        return this.location;
    }

    public String getCWD() {
        return getCurrentDirectory();
    }

    public void setDirectory(String path) {
        this.location = path;
    }

    public void makeDirectory(Node node, String folderName) { // TODO
        Element newFolder = node.getOwnerDocument().createElement("dir");
        Element nodeElement = (Element) node;
        nodeElement.appendChild(newFolder);
        newFolder.setAttribute("name", folderName);
        newFolder.setAttribute("owner", this.uid);
        newFolder.setAttribute("group", this.uid);
        newFolder.setAttribute("ownerp", "rwx");
        newFolder.setAttribute("groupp", "r-x");
        newFolder.setAttribute("otherp", "r-x");
    }

    public Boolean checkDirectory(String path) {
        Node dir = getDirectory(path);
        return !(dir == null);

    }

    public static Map<String, Boolean> getBoolFromInt(Integer Int){
        ArrayList<Boolean> result = new ArrayList<>();
        char[] intArray = Int.toString().toCharArray();
        String[] perms = {"owner.read", "owner.write", "owner.execute",
        "group.read", "group.write", "group.execute",
        "other.read", "other.write", "other.execute"};
        for(char Char : intArray){
            switch(Char){
                case '0':
                    result.add(Boolean.FALSE);
                    result.add(Boolean.FALSE);
                    result.add(Boolean.FALSE);
                    break;
                case '1':
                    result.add(Boolean.FALSE);
                    result.add(Boolean.FALSE);
                    result.add(Boolean.TRUE);
                    break;
                case '2':
                    result.add(Boolean.FALSE);
                    result.add(Boolean.TRUE);
                    result.add(Boolean.FALSE);
                    break;
                case '3':
                    result.add(Boolean.FALSE);
                    result.add(Boolean.TRUE);
                    result.add(Boolean.TRUE);
                    break;
                case '4':
                    result.add(Boolean.TRUE);
                    result.add(Boolean.FALSE);
                    result.add(Boolean.FALSE);
                    break;
                case '5':
                    result.add(Boolean.TRUE);
                    result.add(Boolean.FALSE);
                    result.add(Boolean.TRUE);
                    break;
                case '6':
                    result.add(Boolean.TRUE);
                    result.add(Boolean.TRUE);
                    result.add(Boolean.FALSE);
                    break;
                case '7':
                    result.add(Boolean.TRUE);
                    result.add(Boolean.TRUE);
                    result.add(Boolean.TRUE);
                    break;
            }
        }
        Map<String, Boolean> permissions = new HashMap<>();
        for(int i = 0; i < result.size(); i++){
            permissions.put(perms[i], result.get(i));
        }
        return permissions;
    }


    public Node parsePath(Node node, String path) {
        Document doc = ExitParser.getDocFromZip(system);
        NodeList dataList = doc.getElementsByTagName("info");
        if (path.contains("\\")) {
            path = path.replace("\\", "/");
        } else if (path.trim().equals("")) {
            return null;
        }
        if (path.trim().equals(ExitParser.getAttributeValue(dataList, "top", "dir"))) {
            return node;
        }
        if (path.startsWith(ExitParser.getAttributeValue(dataList, "top", "dir"))) {
            path = path.replaceFirst(Pattern.quote(ExitParser.getAttributeValue(dataList, "top", "dir")), "");
        }

        List<String> pathList = new ArrayList<String>();
        if (path.contains("/")) {
            pathList = new ArrayList<String>(Arrays.asList(path.split("/")));
        } else {
            pathList.add(path);
        }

        for (String string : pathList) {
            node = ExitParser.getNextSetAttr(node, string);
        }
        return node;
    }
}