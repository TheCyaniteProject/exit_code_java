package addon;

import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;
import javafx.scene.text.Text;
import javafx.beans.value.*;
import java.lang.Runnable;
import javafx.event.*;
import java.util.*;

import javafx.scene.control.TextField;
import java.awt.FontFormatException;
import javafx.scene.layout.Priority;
import javafx.application.Platform;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.web.*;
import javafx.scene.control.ColorPicker;
import javafx.scene.paint.ImagePattern;
import javafx.scene.effect.DropShadow;
import javafx.scene.control.ComboBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.Node;
import javafx.scene.Parent;

import java.io.*;

import exitcode.*;

public class Plugin {

    private static String termRoot = "$";

    private static String ps1;

    private static String currentDirectory = "~";

    public static void println(String name, String string) { // Printing debug info..
        API.println(String.format("%s: %s", name, string));
    }

    public static void theAppPack_AppSpawner() {
        create_Terminal_Button();
        create_QuickPad_Button();
        create_Browser_Button();
        create_Setting_Button();
    }

    static { // Put things here you wish to execute on load
        API.addSecondaryDesktopTask("net.thecyaniteproject.theapppack.createapps", () -> theAppPack_AppSpawner());
    }

    public static void create_QuickPad_Button() {
        // Printing debug info..
        println("QuickPad", "Building QuickPad!");
        API.addNewApp("Quickpad", () ->  {
            create_QuickPad_Window();
        });
    }

    public static void create_QuickPad_Window() {
        String localPath = "";

        //Create a new window
        WindowBuilder window = new WindowBuilder("QuickPad");
        window.setProcessName("quickpad.app");
        //App
        VBox content = new VBox();
        window.setCenter(content);
        HBox menu = new HBox();
        menu.getStylesheets().add("config.css");
        content.getChildren().add(menu);

        Button open_file = new Button("Open");
        Button saveAs_file = new Button("Save As");
        menu.getChildren().addAll(open_file, saveAs_file);

        TextArea textArea = new TextArea();
        textArea.prefWidthProperty().bind(window.widthProperty());
        textArea.prefHeightProperty().bind(window.heightProperty().subtract(menu.heightProperty()).subtract(window.getCornerLabel().heightProperty()));
        textArea.setWrapText(true);
        content.getChildren().add(textArea);

        open_file.setOnAction(open ->  {
            FileExplorer explorer = new FileExplorer("/root");
            explorer.openfile(() -> {
                FileNavigator fileNavigator = new FileNavigator(Main.defaultSystem, "/root");
                if (fileNavigator.checkFile(explorer.getOutput())) {
                    org.w3c.dom.Node fileNode = fileNavigator.getFile(explorer.getOutput());
                    if (fileNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                        textArea.setText(FileNavigator.readStringFromFile(fileNode));
                    }
                } else {
                    Apps.create_Popup_Window("QuickPad", String.format("Error: \"%s\" is not a valid file.", explorer.getOutput()));
                }
            });
            explorer.spawn();
        });

        saveAs_file.setOnAction(saveAs ->  {
            FileExplorer explorer = new FileExplorer("/root");
            explorer.savefile(() -> {
                FileNavigator fileNavigator = new FileNavigator(Main.defaultSystem, "/root");
                if (fileNavigator.checkFile(explorer.getOutput())) {
                    org.w3c.dom.Node fileNode = fileNavigator.getFile(explorer.getOutput());
                    if (fileNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                        FileNavigator.writeStringToFile(fileNode, textArea.getText());
                        ExitParser.writeDocToZip(fileNode.getOwnerDocument(), fileNavigator.getSystem());
                    }
                } else {
                    String path = explorer.getOutput();
                    System.out.println(path);
                    int p=path.lastIndexOf("/");
                    String fileName =path.substring(p+1);
                    String path2 = null;
                    org.w3c.dom.Document doc = ExitParser.getDocFromZip(fileNavigator.getSystem());
                    org.w3c.dom.NodeList dataList = doc.getElementsByTagName("info");
                    path2 = path.replace(fileName, "");
                    if (!path2.trim().equals(ExitParser.getAttributeValue(dataList, "top", "dir"))) {
                        path2 = path.replace("/"+fileName, "");
                    }
                    System.out.println(path2);
                    System.out.println(fileName);
                    org.w3c.dom.Node dirNode = fileNavigator.getDirectory(path2);
                    fileNavigator.makeFile(dirNode, fileName, textArea.getText());
                    ExitParser.writeDocToZip(dirNode.getOwnerDocument(), fileNavigator.getSystem());
                }
            });
            explorer.spawn();
        });

        try {
            open_file.setFont(Desktop.loadFont(Desktop.desktopFont, Desktop.desktopFontSize));
            saveAs_file.setFont(Desktop.loadFont(Desktop.desktopFont, Desktop.desktopFontSize));
            textArea.setFont(Desktop.loadFont(Desktop.desktopFont, Desktop.desktopFontSize));
        } catch (NullPointerException e) {}
        textArea.setStyle(Apps.windowFontColor + "-fx-border-style: solid inside; -fx-border-width: .2; -fx-border-color: rgb(100, 100, 100);");
        //Spawn the window
        window.spawnWindow();
        window.placeApp();
    }

    public static void create_Terminal_Button() {
        // Printing debug info..
        println("TerminalApp", "Building Terminal!");
        API.addNewApp("Terminal", () -> {
            create_Terminal_Window();
        });
    }

    public static void create_Terminal_Window() {
        //Create a new window
        WindowBuilder window = new WindowBuilder("Terminal");
        window.setProcessName("terminal.app");
        window.setPrefSize(350, 225); // Window Size (Width, height)
        //App
        ScrollPane termWindow = new ScrollPane();
        termWindow.setFitToWidth(true);
        window.setCenter(termWindow);
        VBox terminalApp = new VBox();
        termWindow.setContent(terminalApp);


        AutoTextArea aTextArea = new AutoTextArea();

        terminalApp.getChildren().add(aTextArea);
        aTextArea.getTextArea().setStyle("-fx-text-fill: white;");

        aTextArea.setText("This is a WIP, and has limited commands.");

        try {
            aTextArea.setFont(Desktop.loadFont(Apps.terminalFont, Apps.terminalFontSize));
        } catch (NullPointerException e) {}


        FileNavigator fileNavigator = new FileNavigator(Main.defaultSystem, "/home/cyanite");
        exitcode.Console terminal = new exitcode.Console("/", fileNavigator);
        if (terminal.hasRootPrivs()) {
            termRoot = "#";
        } else {
            termRoot = "$";
        }
        if (terminal.getNavigator().getCurrentDirectory().equals(terminal.getNavigator().getHome().get(1))) {
            currentDirectory = "~";
        } else {
            currentDirectory = terminal.getNavigator().getCurrentDirectory();
        }
        Plugin.ps1 = String.format("%s@%s:%s%s ",
            terminal.getLocalUser(),
            LoginScreen.SYSTEMNAME.toLowerCase(),
            currentDirectory,
            termRoot); // does terminal have root privs? $=no #=yes
        TagArea termEntry = new TagArea(Plugin.ps1);
        try {
            termEntry.setFont(Desktop.loadFont(Apps.terminalFont, Apps.terminalFontSize));
        } catch (NullPointerException e) {}
        termEntry.setWrapText(true);
        termEntry.setStyle("-fx-text-fill: white;");
        termEntry.setPrefHeight(10);
        terminalApp.getChildren().add(termEntry);
        //to perform the auto scroll
        termWindow.vvalueProperty().bind(termEntry.layoutYProperty().add(termEntry.heightProperty()));
        termEntry.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>()
        {
            @Override
            public void handle(KeyEvent ke) {
                if (ke.getCode().equals(KeyCode.ENTER)) {
                    ke.consume(); // necessary to prevent event handlers for this event
                    String output = new String(termEntry.getText().replace(Plugin.ps1, ""));
                    API.println("[Console Command: " + output + "]");
                    terminal.runCommand(output, Plugin.ps1, aTextArea.getLabel());
                    termEntry.setText(Plugin.ps1);
                    if (terminal.hasRootPrivs()) {
                        termRoot = "#";
                    } else {
                        termRoot = "$";
                    }
                    if (terminal.getNavigator().getCurrentDirectory().equals(terminal.getNavigator().getHome().get(1))) {
                        currentDirectory = "~";
                    } else {
                        currentDirectory = terminal.getNavigator().getCurrentDirectory();
                    }
                    Plugin.ps1 = String.format("%s@%s:%s%s ",
                        terminal.getLocalUser(),
                        LoginScreen.SYSTEMNAME.toLowerCase(),
                        currentDirectory,
                        termRoot);
                    termEntry.setTagText(Plugin.ps1);
                    termEntry.setText(Plugin.ps1);
                    termEntry.end();
                }
            }
        });
        //Spawn the window
        
        window.spawnWindow();
        window.placeApp();
    }

    public static void create_Browser_Button() {
        // Printing debug info..
        println("BrowserApp", "Building Browser!");
        API.addNewApp("Browser", () -> {
            create_Browser_Window();
        });
    }

    public static void create_Browser_Window() {
        //Create a new window
        WindowBuilder window = new WindowBuilder("Browser");
        window.setProcessName("browser.app");
        window.setPrefSize(450, 350); // Window Size (Width, height)
        //App
        VBox browserBox = new VBox();
        window.setCenter(browserBox);

        HBox controlBox = new HBox();
        controlBox.setStyle("-fx-background-color: white; -fx-border-style: solid inside; -fx-border-width: .2; -fx-border-insets: 0; -fx-border-color: rgb(100, 100, 100);");
        browserBox.getChildren().add(controlBox);

        Button backButton = new Button("<");
        backButton.setStyle("-fx-text-fill: black;");
        controlBox.getChildren().add(backButton);
        Button forwardButton = new Button(">");
        forwardButton.setStyle("-fx-text-fill: black;");
        controlBox.getChildren().add(forwardButton);
        Button refreshButton = new Button("Re");
        refreshButton.setStyle("-fx-text-fill: black;");
        controlBox.getChildren().add(refreshButton);
        TextField urlField = new TextField();
        urlField.setStyle("-fx-text-fill: black; -fx-border-style: solid inside; -fx-border-width: .2; -fx-border-insets: 0; -fx-border-color: rgb(100, 100, 100);");
        HBox.setHgrow(urlField, Priority.ALWAYS);
        controlBox.getChildren().add(urlField);
        Button settingsButton = new Button("Settings");
        settingsButton.setStyle("-fx-text-fill: black;");
        controlBox.getChildren().add(settingsButton);

        Pane browserRoot = new Pane();
        browserBox.getChildren().add(browserRoot);

        WebView browser = new WebView();
        browser.prefWidthProperty().bind(browserBox.widthProperty());
        browser.prefHeightProperty().bind(browserBox.heightProperty().subtract(controlBox.heightProperty()));
        browserRoot.getChildren().add(browser);
        
        WebEngine webEngine = browser.getEngine();
        try {
            ArrayList<String> website = ExitParser.getWebsite("home");
            urlField.setText(website.get(1));
            webEngine.load(website.get(0));
        } catch(Exception e) {}

        backButton.setOnAction(refresh ->  {
            Platform.runLater(() -> {
                webEngine.executeScript("history.back()");
            });
        });
        forwardButton.setOnAction(refresh ->  {
            Platform.runLater(() -> {
                webEngine.executeScript("history.forward()");
            });
        });
        refreshButton.setOnAction(refresh ->  {
            webEngine.load(browser.getEngine().getLocation());
        });
        urlField.setOnKeyPressed(new EventHandler<KeyEvent>()
        {
            @Override
            public void handle(KeyEvent ke) {
                if (ke.getCode().equals(KeyCode.ENTER)) {
                browser.getEngine().loadContent("");
                ArrayList<String> website = ExitParser.getWebsite(urlField.getText());
                urlField.setText(website.get(1));
                urlField.end();
                webEngine.load(website.get(0));
                }
            }
        });
        
        //Spawn the window
        
        window.spawnWindow();
        window.placeApp();
    }

    public static void create_Setting_Button() {
        // Printing debug info..
        println("SettingsApp", "Building Settings!");
        API.addNewApp("Settings", () -> {
            create_Settings_Window();
        });
    }

    public static void create_Settings_Window() {
        //Create a new window
        WindowBuilder window = new WindowBuilder("Settings");
        window.setProcessName("settings.app");
        window.setPrefSize(300, 350);
        window.setMinSize(300, 350);
        //App
        HBox settings_area = new HBox();
        settings_area.prefWidthProperty().bind(window.widthProperty());
        settings_area.prefHeightProperty().bind(window.heightProperty());
        window.setCenter(settings_area);
        VBox settings_list = new VBox();
        settings_list.prefHeightProperty().bind(window.heightProperty());
        settings_list.setMinWidth(85);
        settings_area.getChildren().add(settings_list);
        ScrollPane settings_pane = new ScrollPane();
        settings_pane.prefHeightProperty().bind(window.heightProperty());
        settings_pane.prefWidthProperty().bind(window.widthProperty().subtract(settings_list.widthProperty()));
        settings_pane.setStyle("-fx-background-color: rgba(100, 100, 100, .1);");
        settings_area.getChildren().add(settings_pane);

        VBox system_settings = new VBox();
        system_settings.prefWidthProperty().bind(window.widthProperty().subtract(settings_list.widthProperty().add(17)));
        settings_pane.setContent(system_settings);
        VBox system_settings2 = new VBox();
        system_settings2.prefWidthProperty().bind(window.widthProperty().subtract(settings_list.widthProperty().add(17)));
        VBox credits_list = new VBox();
        credits_list.prefWidthProperty().bind(window.widthProperty().subtract(settings_list.widthProperty().add(17)));

        Button appearence = new Button("Appearence");
        appearence.setStyle(Apps.windowFontColor);
        settings_list.getChildren().add(appearence);
        appearence.setOnAction(toggle_shadows ->  {
            settings_pane.setContent(system_settings);
        });
        Button terminal = new Button("Terminal");
        terminal.setStyle(Apps.windowFontColor);
        settings_list.getChildren().add(terminal);
        terminal.setOnAction(toggle_shadows ->  {
            settings_pane.setContent(system_settings2);
        });
        Button credits = new Button("Credits");
        credits.setStyle(Apps.windowFontColor);
        settings_list.getChildren().add(credits);
        credits.setOnAction(show_credits ->  {
            settings_pane.setContent(credits_list);
        });
            // Credits
        Label creditsLabel = new Label(
            "\nDevelopers -\n\n" +
            "  Allison Bennett (Lead Dev)\n" +
            "  Simon Schuerrle (Co-Lead Dev)\n" +
            "  David Refoua (Lead Designer)\n" +
            "  Cosme Escobedo (Assistant Dev)\n" +
            "  Julius Freudenberger (Assistant Dev)\n" +
            "  Indrajeet Latthe (Backup Dev)\n"
        );
        creditsLabel.setStyle(Apps.windowFontColor);
        credits_list.getChildren().add(creditsLabel);

        Label creditsLabel1 = new Label(
            "\nResources -\n\n" +
            "  http://www.flaticon.com/ (3rd-Party Icons)\n" +
            "  https://pixabay.com/ (3rd-Party Wallpapers)\n"
            );
        creditsLabel1.setStyle(Apps.windowFontColor);
        credits_list.getChildren().add(creditsLabel1);

            // Terminal
        Label terminalLabel = new Label("Not yet Implimented.");
        terminalLabel.setStyle(Apps.windowFontColor);
        system_settings2.getChildren().add(terminalLabel);

            // Desktop Wallpaper / Color(s) (Dropdown?)
        Label desktopLabel = new Label("Desktop Background");
        desktopLabel.setStyle(Apps.windowFontColor);
        system_settings.getChildren().add(desktopLabel);
        ComboBox desktopComboBox = new ComboBox();
        system_settings.getChildren().add(desktopComboBox);
        desktopComboBox.getItems().addAll("Wallpaper", "Solid Color");
        desktopComboBox.getSelectionModel().select(0);

        Pane desktopPreview = new Pane();
        system_settings.getChildren().add(desktopPreview);

        Rectangle previewImage = new Rectangle(200,100);
        desktopPreview.getChildren().add(previewImage);

        try {
            InputStream imageInputStream = Plugin.class.getResourceAsStream(Desktop.desktopWallpaper);
            Image desktopImage;

            if(imageInputStream != null){
                desktopImage = new Image(imageInputStream);
            }else{
                desktopImage = new Image("File:" + Desktop.desktopWallpaper);
            }

            ImagePattern pattern = new ImagePattern(desktopImage);
            Main.main_scene.setFill(pattern);
            previewImage.setFill(pattern);
        } catch(IllegalArgumentException | NullPointerException s) {
            previewImage.setFill(Color.web("0x0d4eecff"));
        }

        ComboBox itemsComboBox = new ComboBox();
        system_settings.getChildren().add(itemsComboBox);
        itemsComboBox.setPromptText(Desktop.desktopWallpaper.replace("resources\\wallpapers\\", "").replace("/resources/wallpapers/", "").replace("_", " "));
        itemsComboBox.managedProperty().bind(itemsComboBox.visibleProperty());
        for (String wallpaper : Main.wallpapers) {
            itemsComboBox.getItems().add(wallpaper.replace("resources\\wallpapers\\", "").replace("/resources/wallpapers/", "").replace("_", " "));
        }

        itemsComboBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue ov, String t, String t1) {
                Desktop.desktopWallpaper = Main.wallpapers.get(itemsComboBox.getSelectionModel().getSelectedIndex());
                ExitParser.setSettingValue_theme("desktopWallpaper", Desktop.desktopWallpaper);
                try {
                    InputStream imageInputStream = Plugin.class.getResourceAsStream(Desktop.desktopWallpaper);
                    Image desktopImage;
                    if(imageInputStream != null){
                        desktopImage = new Image(imageInputStream);
                    }else{
                        desktopImage = new Image("file:" + Desktop.desktopWallpaper); // << ------------------------------
                    }
                    ImagePattern pattern = new ImagePattern(desktopImage);
                    Main.main_scene.setFill(pattern);
                    previewImage.setFill(pattern);
                } catch(IllegalArgumentException | NullPointerException s) {
                    Main.main_scene.setFill(Color.web("0x0d4eecff"));
                    previewImage.setFill(Color.web("0x0d4eecff"));
                }
            }
        });

        final ColorPicker colorPicker = new ColorPicker();
        colorPicker.setValue(Desktop.desktopColor);
        system_settings.getChildren().add(colorPicker);
        colorPicker.managedProperty().bind(colorPicker.visibleProperty());

        if (Desktop.useWallpaper) {
            colorPicker.setVisible(false);
        } else {
            itemsComboBox.setVisible(false);
            previewImage.setFill(Desktop.desktopColor);
            desktopComboBox.setValue("Solid Color");
        }

        colorPicker.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                Main.main_scene.setFill(colorPicker.getValue());
                previewImage.setFill(colorPicker.getValue());
                Desktop.desktopColor = colorPicker.getValue();
                ExitParser.setSettingValue_theme("desktopColor", Desktop.desktopColor.toString());
            }
        });

        //Apps.windowMainColor

        desktopComboBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue ov, String t, String t1) {
                if(t1.equals("Wallpaper")) {
                    try {
                        Image desktopImage = new Image(Plugin.class.getResourceAsStream(Desktop.desktopWallpaper));
                        ImagePattern pattern = new ImagePattern(desktopImage);
                        Main.main_scene.setFill(pattern);
                        previewImage.setFill(pattern);
                        Desktop.useWallpaper = true;
                    } catch (IllegalArgumentException e) {
                        Main.main_scene.setFill(Color.web("#0d4eecff"));
                        previewImage.setFill(Color.web("#0d4eecff"));
                        Desktop.useWallpaper = false;
                    }
                    itemsComboBox.setPromptText(Desktop.desktopWallpaper.replace("resources\\wallpapers\\", "").replace("_", " "));
                    itemsComboBox.setVisible(true);
                    colorPicker.setVisible(false);
                    ExitParser.setSettingValue_theme("useWallpaper", "true");
                } else {
                    Main.main_scene.setFill(Desktop.desktopColor);
                    previewImage.setFill(Desktop.desktopColor);
                    itemsComboBox.setPromptText("Select Color");
                    itemsComboBox.setVisible(false);
                    colorPicker.setVisible(true);
                    Desktop.useWallpaper = false;
                    ExitParser.setSettingValue_theme("useWallpaper", "false");
                }
            }
        });
        

        // Fonts / Font Size (Desktop/Windows/Terminal)
        Label fontLabel = new Label("Fonts");
        fontLabel.setStyle(Apps.windowFontColor);
        system_settings.getChildren().add(fontLabel);

        ComboBox fontsComboBox = new ComboBox();
        system_settings.getChildren().add(fontsComboBox);
        try {
            InputStream fontInputStream = Plugin.class.getResourceAsStream(Desktop.desktopFont);
            java.awt.Font f;
            if(fontInputStream != null){
                f = java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT, fontInputStream);
            }else{
                File fontFile = new File(Desktop.desktopFont);
                f = java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT, fontFile);
            }
            fontsComboBox.setPromptText(f.getName());
        } catch (FontFormatException | IOException e) {}
        for (String font : Main.fonts) {
            try {
                InputStream fontInputStream = Plugin.class.getResourceAsStream(font);
                java.awt.Font f;

                if(fontInputStream != null){
                    f = java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT, fontInputStream);
                }else{
                    File fontFile = new File(font);
                    f = java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT, fontFile);
                }

                fontsComboBox.getItems().add(f.getName());
            } catch (FontFormatException | IOException e) {}
        }

        // Window Colors / Transparency / FontColors
        Label windowLabel = new Label("Windows");
        windowLabel.setStyle(Apps.windowFontColor);
        system_settings.getChildren().add(windowLabel);

        Label windowLabel2 = new Label("  Title Color");
        windowLabel2.setStyle(Apps.windowFontColor);
        system_settings.getChildren().add(windowLabel2);

        final ColorPicker colorPicker2 = new ColorPicker();
        colorPicker2.setValue(Apps.windowHeaderColor_c);
        system_settings.getChildren().add(colorPicker2);

        colorPicker2.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                Apps.windowHeaderColor = String.format("-fx-background-color: %s;",
                (colorPicker2.getValue().toString().replace("0x", "#")));
                Apps.windowHeaderColor_c = colorPicker2.getValue();
                ExitParser.setSettingValue_theme("windowHeaderColor", Apps.windowHeaderColor_c.toString());

                window.getChildren().get(0).setStyle(Apps.windowHeaderColor);
            }
        });

        Label windowLabel1 = new Label("  Main Color");
        windowLabel1.setStyle(Apps.windowFontColor);
        system_settings.getChildren().add(windowLabel1);

        final ColorPicker colorPicker1 = new ColorPicker();
        colorPicker1.setValue(Apps.windowMainColor_c);
        system_settings.getChildren().add(colorPicker1);

        colorPicker1.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                Apps.windowMainColor = String.format("-fx-background-color: %s;",
                (colorPicker1.getValue().toString().replace("0x", "#")));
                Apps.windowMainColor_c = colorPicker1.getValue();
                ExitParser.setSettingValue_theme("windowMainColor", Apps.windowMainColor_c.toString());

                window.setStyle(Apps.windowMainColor + "-fx-border-color: rgb(39, 39, 40)");
            }
        });

        Label windowLabel3 = new Label("  Font Color");
        windowLabel3.setStyle(Apps.windowFontColor);
        system_settings.getChildren().add(windowLabel3);

        final ColorPicker colorPicker3 = new ColorPicker();
        colorPicker3.setValue(Apps.windowFontColor_c);
        system_settings.getChildren().add(colorPicker3);


            // Shadows
        String shadow_string;
        if (ExitParser.getSettingValue_theme("shadowsenabled").equals("true")) {
            shadow_string = "Shadows: ON";
        } else {
            shadow_string = "Shadows: OFF";
        }
        Button shadow_mode = new Button(shadow_string);
        shadow_mode.setStyle(Apps.windowFontColor);
        system_settings.getChildren().add(shadow_mode);
        shadow_mode.setOnAction(toggle_shadows ->  {
            if (ExitParser.getSettingValue_theme("shadowsenabled").equals("true")) {
                for (Pane winnode : Apps.open_windows) {
                    API.println("[ShadowEffect Toggle OFF]");
                    ExitParser.setSettingValue_theme("shadowsenabled", "false");
                    winnode.setEffect(null);
                }
                shadow_mode.setText("Shadows: OFF");
            } else {
                DropShadow dropShadow = new DropShadow();
                dropShadow.setRadius(5.0);
                dropShadow.setColor(Color.color(0, 0, 0, 0.6));
                for (Pane winnode : Apps.open_windows) {
                    API.println("[ShadowEffect Toggle ON]");
                    ExitParser.setSettingValue_theme("shadowsenabled", "true");
                    winnode.setEffect(dropShadow);
                }
                shadow_mode.setText("Shadows: ON");
            }
            
        });

        Label windowLabel4 = new Label("Taskbar");
        windowLabel4.setStyle(Apps.windowFontColor);
        system_settings.getChildren().add(windowLabel4);

        Label windowLabel5 = new Label("  Main Color");
        windowLabel5.setStyle(Apps.windowFontColor);
        system_settings.getChildren().add(windowLabel5);

        final ColorPicker colorPicker4 = new ColorPicker();
        colorPicker4.setValue(Desktop.taskbarMainColor_c);
        system_settings.getChildren().add(colorPicker4);

        colorPicker4.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                Desktop.taskbarMainColor = String.format("-fx-background-color: %s;",
                (colorPicker4.getValue().toString().replace("0x", "#")));
                Desktop.taskbarMainColor_c = colorPicker4.getValue();
                ExitParser.setSettingValue_theme("taskbarMainColor", Desktop.taskbarMainColor_c.toString());

                Desktop.desktop_taskbar.setStyle(Desktop.taskbarMainColor);
            }
        });

        Label windowLabel6 = new Label("  Font Color");
        windowLabel6.setStyle(Apps.windowFontColor);
        system_settings.getChildren().add(windowLabel6);

        final ColorPicker colorPicker5 = new ColorPicker();
        colorPicker5.setValue(Desktop.taskbarFontColor_c);
        system_settings.getChildren().add(colorPicker5);

        //Set<window> desktopButtons = new Set<window>();

        colorPicker5.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                Desktop.taskbarFontColor = String.format("-fx-text-fill: %s;",
                (colorPicker5.getValue().toString().replace("0x", "#")));
                Desktop.taskbarFontColor_c = colorPicker5.getValue();
                ExitParser.setSettingValue_theme("taskbarFontColor", Desktop.taskbarFontColor_c.toString());

                for (Node child : Desktop.desktop_taskbar.getChildren()) {
                    if (child instanceof Button) {
                        child.setStyle(Desktop.taskbarFontColor);
                    }
                }
                for (Node child : Desktop.main_appbar.getChildren()) {
                    if (child instanceof Button) {
                        child.setStyle(Desktop.taskbarFontColor);
                    }
                }
                for (Node child : Desktop.main_utilbar.getChildren()) {
                    if (child instanceof Button) {
                        child.setStyle(Desktop.taskbarFontColor);
                    }
                }
            }
        });

        colorPicker3.setOnAction(new EventHandler<ActionEvent>() {
    
            @Override
            public void handle(ActionEvent event) {
                Apps.windowFontColor = String.format("-fx-text-fill: %s;",
                (colorPicker3.getValue().toString().replace("0x", "#")));
                Apps.windowFontColor_c = colorPicker3.getValue();
                ExitParser.setSettingValue_theme("windowFontColor",Apps.windowFontColor_c.toString());

                window.getCornerLabel().setStyle(Apps.windowFontColor);
                window.getWindowTitleLabel().setStyle(Apps.windowFontColor);

                desktopLabel.setStyle(Apps.windowFontColor);
                credits.setStyle(Apps.windowFontColor);
                terminal.setStyle(Apps.windowFontColor);
                appearence.setStyle(Apps.windowFontColor);
                windowLabel3.setStyle(Apps.windowFontColor);
                windowLabel2.setStyle(Apps.windowFontColor);
                windowLabel1.setStyle(Apps.windowFontColor);
                windowLabel.setStyle(Apps.windowFontColor);
                fontLabel.setStyle(Apps.windowFontColor);
                shadow_mode.setStyle(Apps.windowFontColor);
                windowLabel4.setStyle(Apps.windowFontColor);
                windowLabel5.setStyle(Apps.windowFontColor);
                windowLabel6.setStyle(Apps.windowFontColor);
                terminalLabel.setStyle(Apps.windowFontColor);
                creditsLabel.setStyle(Apps.windowFontColor);
                creditsLabel1.setStyle(Apps.windowFontColor);
            }
        });

        fontsComboBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue ov, String t, String t1) {
                Desktop.desktopFont = Main.fonts.get(fontsComboBox.getSelectionModel().getSelectedIndex());
                ExitParser.setSettingValue_theme("desktopFont", Desktop.desktopFont);
                window.getWindowTitleLabel().setFont(Desktop.loadFont(Desktop.desktopFont, Desktop.desktopFontSize));
                try {
                    desktopLabel.setFont(Desktop.loadFont(Desktop.desktopFont, Desktop.desktopFontSize));
                    credits.setFont(Desktop.loadFont(Desktop.desktopFont, Desktop.desktopFontSize));
                    terminal.setFont(Desktop.loadFont(Desktop.desktopFont, Desktop.desktopFontSize));
                    appearence.setFont(Desktop.loadFont(Desktop.desktopFont, Desktop.desktopFontSize));
                    windowLabel3.setFont(Desktop.loadFont(Desktop.desktopFont, Desktop.desktopFontSize));
                    windowLabel2.setFont(Desktop.loadFont(Desktop.desktopFont, Desktop.desktopFontSize));
                    windowLabel1.setFont(Desktop.loadFont(Desktop.desktopFont, Desktop.desktopFontSize));
                    windowLabel.setFont(Desktop.loadFont(Desktop.desktopFont, Desktop.desktopFontSize));
                    fontLabel.setFont(Desktop.loadFont(Desktop.desktopFont, Desktop.desktopFontSize));
                    shadow_mode.setFont(Desktop.loadFont(Desktop.desktopFont, Desktop.desktopFontSize));
                    windowLabel4.setFont(Desktop.loadFont(Desktop.desktopFont, Desktop.desktopFontSize));
                    windowLabel5.setFont(Desktop.loadFont(Desktop.desktopFont, Desktop.desktopFontSize));
                    windowLabel6.setFont(Desktop.loadFont(Desktop.desktopFont, Desktop.desktopFontSize));
                    terminalLabel.setFont(Desktop.loadFont(Desktop.desktopFont, Desktop.desktopFontSize));
                    creditsLabel.setFont(Desktop.loadFont(Desktop.desktopFont, Desktop.desktopFontSize));
                    creditsLabel1.setFont(Desktop.loadFont(Desktop.desktopFont, Desktop.desktopFontSize));
                } catch (NullPointerException e) {}
            }
        });

        //Spawn the window
        
        window.spawnWindow();
        window.placeApp();
    }

}

class TagArea extends TextArea {
    private String tagValue;
    private String initTag;
    public TagArea(String starterTag) {
        this.initTag = starterTag;
        setTagText(initTag);
        super.textProperty().addListener(new ChangeListener<String>(){
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.contains(tagValue)) {
                    setTagText(oldValue);
                }
                //makes tagArea grow with text
                Text text = new Text(newValue+starterTag);
                text.setFont(Desktop.loadFont(Apps.terminalFont, Apps.terminalFontSize));
                text.setWrappingWidth(getWidth() - getPadding().getLeft()*2);
                double height = text.getLayoutBounds().getHeight() + 17;
                setPrefHeight(height);
            }
        });
    }

    public void setTagText(String text) {
        this.setText(text);
        this.tagValue = text;
    }
}