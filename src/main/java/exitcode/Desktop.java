package exitcode;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Font;
import javafx.scene.layout.*;
import javafx.scene.image.*;
import javafx.scene.paint.*;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javafx.event.*;
import java.io.*;

// Internal dependancies: Main, Logger, LoginScreen, SystemsScreen, ExitParser, Apps, API

public class Desktop {

    public static Boolean useWallpaper = true;
    public static String desktopWallpaper = "/resources/wallpapers/PiXElos_Mountain_Landscape_by_Giulia_Filippini_and_David_Refoua.png";
    public static Color desktopColor = Color.web("#003333");
    public static String desktopFont = "/resources/fonts/Roboto-Regular.ttf";
    public static Integer desktopFontSize = 12;
    
    public static String taskbarMainColor = "-fx-background-color: rgba(35, 35, 37, 1);";
    public static String taskbarFontColor = "-fx-text-fill: rgba(211, 211, 211, 1);";

    public static Color taskbarMainColor_c = Color.web("#232325");
    public static Color taskbarFontColor_c = Color.web("#d3d3d3");

    public static Pane main_root;
    public static Scene main_scene;
    public static HBox desktop_taskbar;
    public static HBox main_appbar;
    public static HBox main_utilbar;
    public static Runnable startMenu = null;
    public static Runnable startMenuClose = null;
    public static Boolean startMenuIsOpen = false;
    public static Boolean isOpen = false;

    private static String system_systemLocation;

    public static void start() {
        start(Main.defaultSystem);
    }

    public static void start(String systemLocation) {

        system_systemLocation = systemLocation;

        // we use a default pane without layout such as HBox, VBox etc.
        final Pane root = new Pane();
        main_root = root;
        final BorderPane root_desktop = new BorderPane();
		root.setPrefSize(800, 600); // Set default size of the window: (Width, height)
        root.getChildren().add(root_desktop);
        root_desktop.prefWidthProperty().bind(root.widthProperty()); // Fit root_desktop to root (yes, both Pane's are required.)
        root_desktop.prefHeightProperty().bind(root.heightProperty());
        root_desktop.setBackground(Background.EMPTY); // Makes the background color transparent, else the background fills with color.
        root.setBackground(Background.EMPTY);

        //TaskBar
        HBox task_bar = new HBox();
        task_bar.getStylesheets().add("config.css");
        task_bar.setStyle(Desktop.taskbarMainColor + "-fx-border-color: rgb(39, 39, 40)");
        root_desktop.setBottom(task_bar);
        Desktop.desktop_taskbar = task_bar;
        //Start
        Button start_button = new Button(" PiXEl  |");
        start_button.setFont(loadFont(Desktop.desktopFont, Desktop.desktopFontSize));
        start_button.setStyle(Desktop.taskbarFontColor);
        task_bar.getChildren().add(start_button);
        start_button.setOnAction(open ->  {
            if (Desktop.startMenuIsOpen) {
                Apps.closeStartMenu();
            } else {
                Apps.spawnStartMenu();
            }
        });

        //AppBar
        HBox app_bar = new HBox();
        task_bar.getChildren().add(app_bar);
        HBox.setHgrow(app_bar, Priority.ALWAYS);
        Desktop.main_appbar = app_bar;

        //AppBar
        HBox util_bar = new HBox();
        task_bar.getChildren().add(util_bar);
        Desktop.main_utilbar = util_bar;

        // Clock
        Label clockLabel = new Label("  --:--  ");
        Desktop.main_utilbar.getChildren().add(clockLabel);
        clockLabel.setFont(loadFont(Desktop.desktopFont, Desktop.desktopFontSize));
        clockLabel.setStyle(Desktop.taskbarFontColor);
        clockLabel.prefHeightProperty().bind(Desktop.main_utilbar.heightProperty());

        Timeline timeline = new Timeline(
        new KeyFrame(Duration.seconds(0),
            new EventHandler<ActionEvent>() {
                @Override public void handle(ActionEvent actionEvent) {
                    Calendar time = Calendar.getInstance();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                    clockLabel.setText("  " + simpleDateFormat.format(time.getTime()) + "  ");
                }
            }
        ),
        new KeyFrame(Duration.seconds(1))
        );
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

        Apps.setDefaultStartMenu();

        API.executeAll_Desktop(); // Mod Stuffs

        //Change the stage root
        Main.main_primaryStage.getScene().setRoot(Desktop.main_root); //Best line of code EVER
        Logger.messageCustom("SYSTEM", "Login: " + LoginScreen.USERNAME);

        API.executeAll_Desktop2(); // Mod Stuffs

        Apps.create_Explorer_Button();

        // Load Player Theme
        Desktop.loadPlayerTheme();
        
        if (Boolean.valueOf(ExitParser.getSettingValue_theme("useWallpaper"))) {
            try {
                InputStream imageInputStream = Desktop.class.getResourceAsStream(Desktop.desktopWallpaper);
                Image desktopImage;
                if(imageInputStream != null){
                    desktopImage  = new Image(imageInputStream);
                }else{
                    desktopImage = new Image("file:" + Desktop.desktopWallpaper);
                }
                ImagePattern pattern = new ImagePattern(desktopImage);
                Main.main_scene.setFill(pattern);
            } catch(IllegalArgumentException | NullPointerException s) {
                Logger.warn("\""+ Desktop.desktopWallpaper +"\" was not found.");
                Main.main_scene.setFill(Desktop.desktopColor);
            }
        } else {
            Main.main_scene.setFill(Desktop.desktopColor);
        }
    }

    public static void loadPlayerTheme() {
        File f = new File("./theme.xml");
        if (f.isFile()) {
            Apps.loadPlayerTheme();
        } else {
            // Desktop
            Desktop.useWallpaper = true;
            Desktop.desktopColor = Color.web("#003333");
            Desktop.desktopWallpaper = "/resources/wallpapers/PiXElos_Mountain_Landscape_by_Giulia_Filippini_and_David_Refoua.png";
            Desktop.desktopFont = "/resources/fonts/Roboto-Regular.ttf";
            Desktop.desktopFontSize = 12;
            Desktop.taskbarMainColor_c = Color.web("#232325");
            Desktop.taskbarFontColor_c = Color.web("#232325");

            Desktop.taskbarMainColor = "-fx-background-color: rgba(35, 35, 37, 1);";
            Desktop.taskbarFontColor = "-fx-text-fill: rgba(35, 35, 37, 1);";
            // Apps
            Apps.close_img = new Image(Apps.class.getResourceAsStream("images/windows_controls/window_control_close.png"));
            Apps.maximize_img = new Image(Apps.class.getResourceAsStream("images/windows_controls/window_control_maximize.png"));
            Apps.minimize_img = new Image(Apps.class.getResourceAsStream("images/windows_controls/window_control_minimize.png"));
            Apps.restore_img = new Image(Apps.class.getResourceAsStream("images/windows_controls/window_control_restore.png"));
            Apps.windowFont = "/resources/fonts/Roboto-Regular.ttf";
            Apps.windowFontSize = 12;
            Apps.terminalFont = "/resources/fonts/RobotoSlab-Regular.ttf";
            Apps.terminalFontSize = 11;
            Apps.windowHeaderColor_c = Color.web("#232325");
            Apps.windowMainColor_c = Color.web("#232325");
            Apps.windowFontColor_c = Color.web("#d3d3d3");

            Apps.windowHeaderColor = "-fx-background-color: rgba(35, 35, 37, 1);";
            Apps.windowMainColor = "-fx-background-color: rgba(35, 35, 37, 1);";
            Apps.windowFontColor = "-fx-text-fill: rgba(211, 211, 211, 1);";

            File configFile = new File("./theme.xml");
            if (!configFile.isFile()) {
                Logger.message("Player Theme File does not exist - creating one");
                try {
                    PrintWriter out = new PrintWriter("./theme.xml");
                    out.printf(
                        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>%n%n" +
                        "<data>%n" +
                        "    <themedata>%n" +
                        "        <!-- Desktop -->%n" +
                        "        <useWallpaper>true</useWallpaper>%n" +
                        "        <desktopColor>0x003333ff</desktopColor>%n" +
                        "        <desktopWallpaper>/resources/wallpapers/PiXElos_Mountain_Landscape_by_Giulia_Filippini_and_David_Refoua.png</desktopWallpaper>%n" +
                        "        <desktopFont>/resources/fonts/Roboto-Regular.ttf</desktopFont>%n" +
                        "        <desktopFontSize>12</desktopFontSize>%n" +
                        "        <taskbarMainColor>0x232325ff</taskbarMainColor>%n" +
                        "        <taskbarFontColor>0xd3d3d3ff</taskbarFontColor>%n" +
                        "        <!-- Apps -->%n" +
                        "        <close_img>images/windows_controls/window_control_close.png</close_img>%n" +
                        "        <maximize_img>images/windows_controls/window_control_maximize.png</maximize_img>%n" +
                        "        <minimize_img>images/windows_controls/window_control_minimize.png</minimize_img>%n" +
                        "        <restore_img>images/windows_controls/window_control_restore.png</restore_img>%n" +
                        "        <windowFont>/resources/fonts/Roboto-Regular.ttf</windowFont>%n" +
                        "        <windowFontSize>12</windowFontSize>%n" +
                        "        <terminalFont>/resources/fonts/RobotoSlab-Regular.ttf</terminalFont>%n" +
                        "        <terminalFontSize>11</terminalFontSize>%n" +
                        "        <windowHeaderColor>0x232325ff</windowHeaderColor>%n" +
                        "        <windowMainColor>0x232325ff</windowMainColor>%n" +
                        "        <windowFontColor>0xd3d3d3ff</windowFontColor>%n" +
                        "        <shadowsenabled>true</shadowsenabled>%n" +
                        "    </themedata>%n" +
                        "</data>");
                    out.close();
                } catch (Exception e) {
                    Logger.error(e.getMessage());
                }
            }
        }
    }

    public static String getSystemLocation() {
        return Desktop.system_systemLocation;
    }

    public static void shutdown() {
        Apps.preLogoutkill(); // Kills all open apps
        Logger.messageCustom("SYSTEM", "Shutdown");
        SystemsScreen.load();
    }

    public static void logout() {
        Apps.preLogoutkill(); // Kills all open apps
        Logger.messageCustom("SYSTEM", "Logout");
        LoginScreen.loadSystem();
    }

    public static Font loadFont(String fontName, double fontSize) {

        try {
            java.net.URL fontUrl = Desktop.class.getResource(fontName);
            if(fontUrl != null){
                return Font.loadFont(fontUrl.toExternalForm(), fontSize);
            }else{
                return Font.loadFont("file:" + fontName, fontSize);
            }

        } catch(IllegalArgumentException | NullPointerException s) {
            Logger.warn("\""+ fontName +"\" was not found. Using default font");
            return Font.loadFont("/resources/fonts/Roboto-Regular.ttf", fontSize);

        }
    }

}
