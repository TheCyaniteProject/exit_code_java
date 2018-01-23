package addon;

import exitcode.*;

import java.util.*;
import java.util.regex.Pattern;
import java.lang.Exception;
import java.util.zip.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class Plugin {
    static {
        Console.addGlobalCommand(new Dir());
        Console.addGlobalCommand(new Mkdir());
        Console.addGlobalCommand(new CD());
        Console.addGlobalCommand(new Rm());
    }

}

class Dir extends Command {

    Dir() {
        command = "dir";
        desc = "Prints the current working directory";
    }

    @Override
    public void run() {
        String path = console.getNavigator().getCurrentDirectory();

        Element dataElement = (Element) console.getNavigator().getDirectory(path);
        Node childNode = dataElement.getFirstChild();
        try {
            while( childNode.getNextSibling()!=null ){
                childNode = childNode.getNextSibling();
                if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element childElement = (Element) childNode;
                    if(childElement.hasAttribute("name")) {
                        if (childNode.getNodeName().trim().equals("dir")) {
                            Console.printText(termArea, String.format(
                                "%s d%s%s%s",
                                childElement.getAttribute("name"),
                                childElement.getAttribute("ownerp"),
                                childElement.getAttribute("groupp"),
                                childElement.getAttribute("otherp")
                            ));
                        } else {
                            Console.printText(termArea, String.format(
                                "%s -%s%s%s",
                                childElement.getAttribute("name"),
                                childElement.getAttribute("ownerp"),
                                childElement.getAttribute("groupp"),
                                childElement.getAttribute("otherp")
                            ));
                        }
                    }
                }
            }
        }
        catch(Exception ex) {
            System.out.println(ex.getMessage());
        }

    }

}

class CD extends Command {

    CD() {
        command = "cd";
        desc = "Changes the current working directory";
    }

    @Override
    public void run() {
        String path = console.getNavigator().getCurrentDirectory();
        Document doc = ExitParser.getDocFromZip(console.getNavigator().getSystem());
        NodeList dataList = doc.getElementsByTagName("info");
        if (body.trim().equals("~")) {
            body = console.getNavigator().getHome().get(1);
        } else if (body.trim().equals("..")) {
            if (path.length() > 1) {
                if (path.contains("/")) {
                    int lastIndexOf = path.lastIndexOf("/");
                    String substring1 = path.substring(0, lastIndexOf);
                    String substring2 = path.substring(lastIndexOf+1, path.length());
                    if (substring1.trim().equals("")) {
                        body = ExitParser.getAttributeValue(dataList, "top", "dir");
                    } else {
                        body = substring1;
                    }
                } else {
                    Console.printText(termArea, "cd: error");
                }
            } else {
                Console.printText(termArea, "cd: cannot move higher");
                return;
            }
        }
        if (!body.trim().equals("/")) {
                if (body.trim().endsWith("/")) {
                StringBuilder builder = new StringBuilder(body);
                builder.replace(body.lastIndexOf(","), body.lastIndexOf(",") + 1, ")" );
                body = builder.toString();
            }
        }
        if (!path.equals(body.trim())) {
            if (!body.trim().startsWith(ExitParser.getAttributeValue(dataList, "top", "dir"))) {
                String path2 = path;
                if (!path2.startsWith(ExitParser.getAttributeValue(dataList, "top", "dir"))) {
                    path2 = ExitParser.getAttributeValue(dataList, "top", "dir") + path2;
                }
                if (!path2.endsWith("/")) {
                    path2 = path2 + "/";
                }
                if (console.getNavigator().checkDirectory(path2 + body.trim())) {
                    console.getNavigator().setDirectory(path2 + body.trim());
                } else {
                    Console.printText(termArea, String.format("cd: \"%s\" is not a valid location/path", body));
                }
            } else if (console.getNavigator().checkDirectory(body.trim())) {
                console.getNavigator().setDirectory(body.trim());
            } else {
                Console.printText(termArea, String.format("cd: \"%s\" is not a valid location/path", body));
            }
        }

    }

}

class Mkdir extends Command {
    
    Mkdir() {
        command = "mkdir";
        desc = "Creates a new folder in the current directory";
    }

    @Override
    public void run() {
        String path = console.getNavigator().getCurrentDirectory();

        Node node = console.getNavigator().getDirectory(path);

        if (!console.getNavigator().checkDirectory(path + "/" + body.trim())) {
            console.getNavigator().makeDirectory(node, body.trim());
            ExitParser.writeDocToZip(node.getOwnerDocument(), console.getNavigator().getSystem());
        } else {
            Console.printText(termArea, String.format("mkdir: \"%s\" already exists", body));
        }

    }

}

class Rm extends Command {
    
    Rm() {
        command = "rm";
        desc = "Removes a file or a directory (and all of it's contents)";
    }

    @Override
    public void run() {
        String path = console.getNavigator().getCurrentDirectory();

        if (console.getNavigator().checkDirectory(path + "/" + body.trim())) {
            Node node = console.getNavigator().getDirectory(path + "/" + body.trim());
            console.getNavigator().sendToOblivion(node);
            ExitParser.writeDocToZip(node.getOwnerDocument(), console.getNavigator().getSystem());
        } else {
            Console.printText(termArea, String.format("rm: \"%s\" does not exist", body));
        }

    }

}