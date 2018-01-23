package exitcode;

import javafx.geometry.*;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.layout.*;
import javafx.scene.effect.*;
import javafx.scene.paint.*;
import javafx.scene.input.*;
import javafx.event.*;
import java.io.*;
import java.util.*;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

// Internal dependancies: WindowBuilder, FileNavigator, Main, Logger

public class FileExplorer {
    
        private WindowBuilder window = new WindowBuilder("Files");
        private FileNavigator fileNavigator = new FileNavigator(Main.defaultSystem, "/root");
        private TextField fileTextField;
        private String staringDir;
        private String explorerMode;
        private String outputString;
        private Runnable postProcess;
    
        public FileExplorer(String path) {
            this.staringDir = path;
            this.fileNavigator.setDirectory(path);
        }
        
        public FileExplorer() {
            this.explorerMode = "explorer";
            Document doc = ExitParser.getDocFromZip(this.fileNavigator.getSystem());
            NodeList dataList = doc.getElementsByTagName("info");
            this.staringDir = ExitParser.getAttributeValue(dataList, "top", "dir");
        }
    
        private void create_Explorer_Window() {
            ArrayList<String> explorerModes = new ArrayList<String>(Arrays.asList("explorer", "openfile", "savefile", "openfolder"));
            if (this.explorerMode == null) {
                this.explorerMode = "explorer";
            }
            if (!explorerModes.contains(this.explorerMode)) {
                Logger.error("reate_Explorer_Window(): invalid mode. Must be either: explorer, openfile, savefile, or openfolder.");
                return;
            } else {
                if (!(this.explorerMode.equals("explorer"))) {
                    if (this.postProcess == null) {
                        Logger.error("No process to run on close.");
                        throw new NullPointerException();
                    }
                }
            }
    
            ArrayList<ArrayList<String>> itemList = new ArrayList<ArrayList<String>>();
            itemList.add(new ArrayList<String>(Arrays.asList("Home", "/")));
            itemList.add(new ArrayList<String>(Arrays.asList("Desktop", "/root/Desktop")));
            itemList.add(new ArrayList<String>(Arrays.asList("Downloads", "/root/Downloads")));
            itemList.add(new ArrayList<String>(Arrays.asList("Documents", "/root/Documents")));
            
            //Create a new window
            this.window.setProcessName("explorer.app");
            this.window.setPrefSize(450, 350); // Window Size (Width, height)
    
            VBox page = new VBox();
            this.window.setCenter(page);
            page.getStylesheets().add("config.css");
    
            HBox urlBar = new HBox();
            TextField urlEntry = new TextField();
            urlBar.getChildren().add(urlEntry);
            urlBar.prefWidthProperty().bind(this.window.widthProperty());
            urlEntry.prefWidthProperty().bind(urlBar.widthProperty());
            urlBar.setStyle("-fx-border-style: solid inside; -fx-border-width: .2; -fx-border-color: rgb(100, 100, 100);");
            urlEntry.setStyle(Apps.windowFontColor + "-fx-border-style: solid inside; -fx-border-width: .2; -fx-border-color: rgb(100, 100, 100);");
            urlEntry.setText(staringDir);
    
            HBox content = new HBox();
            content.prefWidthProperty().bind(this.window.widthProperty());
    
            HBox fileBar = new HBox();
    
            Label fileTypeLabel = new Label("  File name:    ");
            fileTypeLabel.prefHeightProperty().bind(fileBar.heightProperty());
            fileTypeLabel.setStyle(Apps.windowFontColor);
            Button finishButton = new Button("Open");
            finishButton.setStyle(Apps.windowFontColor);
            this.fileTextField = new TextField();
            this.fileTextField.setPrefWidth(50);
            this.fileTextField.setStyle(Apps.windowFontColor + "-fx-border-style: solid inside; -fx-border-width: .2; -fx-border-color: rgb(100, 100, 100);");
            this.fileTextField.prefWidthProperty().bind((content.widthProperty().subtract(fileTypeLabel.widthProperty())).subtract(finishButton.widthProperty()));
    
            finishButton.setOnAction(buttonClick -> {
                String directory = this.fileNavigator.getCurrentDirectory();
                if (!(this.fileTextField.getText().trim().equals(""))) {
                    if (this.fileNavigator.checkDirectory(directory)) {
                        if (!this.fileNavigator.getCurrentDirectory().endsWith("/")) {
                            directory = this.fileNavigator.getCurrentDirectory()+"/";
                        }
                        directory = directory+this.fileTextField.getText().trim();
                        if (this.explorerMode.equals("openfolder")) {
                            if (this.fileNavigator.checkDirectory(directory)) {
                                this.outputString = directory;
                                this.window.killWindow();
                                postProcess.run();
                            }
                        } else {
                            this.outputString = directory;
                            this.window.killWindow();
                            postProcess.run();
                        }
                    }
                }
            });
    
            fileBar.getChildren().add(fileTypeLabel);
            fileBar.getChildren().add(this.fileTextField);
            fileBar.getChildren().add(finishButton);
    
            if (this.explorerMode.equals("openfolder")) {
                this.window.setWindowTitle("Open Folder");
                fileTypeLabel.setText("  Folder name:    ");
            } else if (this.explorerMode.equals("savefile")) {
                this.window.setWindowTitle("Save File");
                finishButton.setText("Save");
            } else if (this.explorerMode.equals("openfile")) {
                this.window.setWindowTitle("Open File");
            }
    
            if (!(this.explorerMode == null || this.explorerMode.equals("explorer"))) {
                content.prefHeightProperty().bind(this.window.heightProperty().subtract(urlBar.heightProperty()).subtract(fileBar.heightProperty()));
                page.getChildren().addAll(urlBar, content, fileBar);
            } else {
                content.prefHeightProperty().bind(this.window.heightProperty().subtract(urlBar.heightProperty()));
                page.getChildren().addAll(urlBar, content);
            }
    
            ScrollPane leftContent = new ScrollPane();
            leftContent.setStyle("-fx-border-style: solid inside; -fx-border-width: .2; -fx-border-color: rgb(100, 100, 100);");
            leftContent.prefHeightProperty().bind(content.heightProperty());
            leftContent.setPrefWidth(250);
    
            VBox leftVBox = new VBox();
            leftContent.setContent(leftVBox);
            leftVBox.prefWidthProperty().bind(leftContent.widthProperty().subtract(20));
    
            ScrollPane rightContent = new ScrollPane();
            rightContent.setStyle("-fx-border-style: solid inside; -fx-border-width: .2; -fx-border-color: rgb(100, 100, 100);");
            rightContent.prefHeightProperty().bind(content.heightProperty());
            rightContent.prefWidthProperty().bind(content.widthProperty());
    
            VBox rightVBox = new VBox();
            rightContent.setContent(rightVBox);
            rightVBox.prefWidthProperty().bind(rightContent.widthProperty().subtract(20));
    
            content.getChildren().addAll(leftContent, rightContent);
    
            addButtons(rightVBox, staringDir, urlEntry);
    
            for (ArrayList<String> item : itemList) {
                HBox newItem = new HBox();
                leftVBox.getChildren().add(newItem);
                Button itemButton = new Button(item.get(0));
                newItem.getChildren().add(itemButton);
                itemButton.setOnAction(buttonClick ->  {
                    rightVBox.getChildren().clear();
                    urlEntry.setText(item.get(1));
                    addButtons(rightVBox, item.get(1), urlEntry);
                });
            }
            FileNavigator fileNavigator = this.fileNavigator;
            urlEntry.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent ke) {
                    if (ke.getCode().equals(KeyCode.ENTER)) {
                        ke.consume(); // necessary to prevent event handlers for this event
                        if (fileNavigator.checkDirectory(urlEntry.getText())) {
                            rightVBox.getChildren().clear();
                            addButtons(rightVBox, urlEntry.getText(), urlEntry);
                        }
                    }
                }
            });
    
            //Spawn the window
            this.window.spawnWindow();
            this.window.placeApp();
        }
    
        public void setPostProcess(Runnable postProcess) {
            this.postProcess = postProcess;
        }
    
        public void explorer() {
            this.explorerMode = "explorer";
        }
    
        public void openfile(Runnable postProcess) {
            this.postProcess = postProcess;
            this.explorerMode = "openfile";
        }
    
        public void openfile() {
            this.explorerMode = "openfile";
        }
    
        public void savefile(Runnable postProcess) {
            this.postProcess = postProcess;
            this.explorerMode = "savefile";
        }
    
        public void savefile() {
            this.explorerMode = "savefile";
        }
    
        public void openfolder(Runnable postProcess) {
            this.postProcess = postProcess;
            this.explorerMode = "openfolder";
        }
    
        public void openfolder() {
            this.explorerMode = "openfolder";
        }
    
    
        public void spawn(String path) {
            this.staringDir = path;
            create_Explorer_Window();
        }
    
        public void spawn() {
            Document doc = ExitParser.getDocFromZip(this.fileNavigator.getSystem());
            NodeList dataList = doc.getElementsByTagName("info");
            if (this.staringDir == null) {
                this.staringDir = ExitParser.getAttributeValue(dataList, "top", "dir");
            }
            create_Explorer_Window();
        }
    
        public String getOutput() {
            return this.outputString;
        }
        
        public void resetOutput() {
            this.outputString = null;
        }
    
        public WindowBuilder getWindowBuilder() {
            return this.window;
        }
    
        private void addButtons(VBox vbox, String directory, TextField urlEntry) {
    
            org.w3c.dom.Element dataElement = (org.w3c.dom.Element) this.fileNavigator.getDirectory(directory);
            org.w3c.dom.Node childNode = dataElement.getFirstChild();
            try {
                while( childNode.getNextSibling()!=null ){
                    childNode = childNode.getNextSibling();
                    if (childNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                        org.w3c.dom.Element childElement = (org.w3c.dom.Element) childNode;
                        if(childElement.hasAttribute("name")) {
                            HBox newObject = new HBox();
                            newObject.getStylesheets().add("config.css");
                            newObject.prefWidthProperty().bind(vbox.widthProperty());
                            vbox.getChildren().add(newObject);
                            Label objectButton = new Label("  " + childElement.getAttribute("name"));
                            objectButton.setStyle(Apps.windowFontColor);
                            Region objectRegion = new Region();
                            HBox.setHgrow(objectRegion, Priority.ALWAYS);
                            Label objectLabel = new Label();
                            newObject.getChildren().addAll(objectButton, objectRegion, objectLabel);
                            objectLabel.setStyle(Apps.windowFontColor);
                            String explorerMode = this.explorerMode;
                            TextField fileTextField = this.fileTextField;
                            FileNavigator fileNavigator = this.fileNavigator;
                            if (childNode.getNodeName().trim().equals("dir")) {
                                objectLabel.setText("File Folder");
                                objectButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
                                    @Override
                                    public void handle(MouseEvent mouseEvent) {
                                        if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
                                            if (explorerMode.equals("openfolder")) {
                                                fileTextField.setText(childElement.getAttribute("name"));
                                            }
                                            if(mouseEvent.getClickCount() == 2){
                                                vbox.getChildren().clear();
                                                if (!directory.endsWith("/")) {
                                                    fileNavigator.setDirectory(directory+"/"+childElement.getAttribute("name"));
                                                } else {
                                                    fileNavigator.setDirectory(directory+childElement.getAttribute("name"));
                                                }
                                                urlEntry.setText(fileNavigator.getCurrentDirectory());
                                                addButtons(vbox, fileNavigator.getCurrentDirectory(), urlEntry);
                                            }
                                        }
                                    }
                                });
                            } else if (childNode.getNodeName().trim().equals("file")) {
                                objectButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
                                    @Override
                                    public void handle(MouseEvent mouseEvent) {
                                        if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
                                            if (!explorerMode.equals("openfolder")) {
                                                fileTextField.setText(childElement.getAttribute("name"));
                                            }
                                        }
                                    }
                                });
                                objectLabel.setText(childNode.getTextContent().getBytes().length + " B    " + "File");
                            }
                        }
                    }
                }
            } catch(Exception ex) {
                Logger.error(ex.getMessage());
            }
        }
    }