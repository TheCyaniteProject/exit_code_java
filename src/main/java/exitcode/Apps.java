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

// Internal dependancies: Logger, Main, Desktop, WindowBuilder, ExitParser, FileExplorer

public class Apps {

    // Window Icons
    public static Image close_img = new Image(Apps.class.getResourceAsStream("images/windows_controls/window_control_close.png"));
    public static Image maximize_img = new Image(Apps.class.getResourceAsStream("images/windows_controls/window_control_maximize.png"));
    public static Image minimize_img = new Image(Apps.class.getResourceAsStream("images/windows_controls/window_control_minimize.png"));
    public static Image restore_img = new Image(Apps.class.getResourceAsStream("images/windows_controls/window_control_restore.png"));

    // Window Colors
    public static String windowHeaderColor = "-fx-background-color: rgba(35, 35, 37, 1);";
    public static String windowMainColor = "-fx-background-color: rgba(35, 35, 37, 1);";
    public static String windowFontColor = "-fx-text-fill: rgba(211, 211, 211, 1);";

    public static Color windowHeaderColor_c = Color.web("#232325");
    public static Color windowMainColor_c = Color.web("#232325");
    public static Color windowFontColor_c = Color.web("#d3d3d3");

    // Window Fonts
    public static String windowFont = "/resources/fonts/Roboto-Regular.ttf";
    public static Integer windowFontSize = 12;

    public static String terminalFont = "/resources/fonts/RobotoSlab-Regular.ttf";
    public static Integer terminalFontSize = 11;


    // Windows & Apps
    public static ArrayList<BorderPane> open_windows = new ArrayList<BorderPane>();
    public static Hashtable<Integer, ArrayList> apps = new Hashtable<Integer, ArrayList>();
    public static Map<String, Runnable> appButtons = new HashMap<String, Runnable>();

    public static String PS1 = new String(LoginScreen.USERNAME.toLowerCase() + "@" + LoginScreen.SYSTEMNAME.toLowerCase() + ":~$ ");


    public static void loadPlayerTheme() {
        // Desktop
        Desktop.useWallpaper = Boolean.valueOf(ExitParser.getSettingValue_theme("useWallpaper"));
        Desktop.desktopColor = Color.web(ExitParser.getSettingValue_theme("desktopColor"));
        Desktop.desktopWallpaper = ExitParser.getSettingValue_theme("desktopWallpaper");
        Desktop.desktopFont = ExitParser.getSettingValue_theme("desktopFont");
        Desktop.desktopFontSize = Integer.valueOf(ExitParser.getSettingValue_theme("desktopFontSize"));
        Desktop.taskbarMainColor_c = Color.web(ExitParser.getSettingValue_theme("taskbarMainColor"));
        Desktop.taskbarMainColor = String.format("-fx-background-color: %s;",
                (ExitParser.getSettingValue_theme("taskbarMainColor").replace("0x", "#")));
        Desktop.desktop_taskbar.setStyle(Desktop.taskbarMainColor);
        Desktop.taskbarFontColor_c = Color.web(ExitParser.getSettingValue_theme("taskbarFontColor"));
        Desktop.taskbarFontColor = String.format("-fx-text-fill: %s;",
                (ExitParser.getSettingValue_theme("taskbarFontColor").replace("0x", "#")));
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
        Boolean wallpaper_Boolean = false;
        for (String wallpaper : Main.wallpapers) {
            if (wallpaper.equals(Desktop.desktopWallpaper)) {
                wallpaper_Boolean = true;
            }
        }
        if (!wallpaper_Boolean) {
            Logger.error("COULD NOT FIND THEME WALLPAPER!");
            Desktop.desktopWallpaper = "/resources/wallpapers/PiXElos_Mountain_Landscape_by_Giulia_Filippini_and_David_Refoua.png";
        }
        Boolean font_Boolean = false;
        for (String font : Main.fonts) {
            if (font.equals(Desktop.desktopFont)) {
                font_Boolean = true;
            }
        }
        if (!font_Boolean) {
            Logger.error("COULD NOT FIND THEME FONT!");
            Desktop.desktopFont = "/resources/fonts/Roboto-Regular.ttf";
        }
        if (Boolean.valueOf(ExitParser.getSettingValue_theme("useWallpaper"))) {
            try {
                InputStream imageInputStream = Desktop.class.getResourceAsStream(Desktop.desktopWallpaper);
                Image desktopImage;

                if(imageInputStream != null){
                    desktopImage = new Image(imageInputStream);
                }else{
                    desktopImage = new Image("File:" + Desktop.desktopWallpaper);
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
        // Apps
        close_img = new Image(Apps.class.getResourceAsStream(ExitParser.getSettingValue_theme("close_img")));
        maximize_img = new Image(Apps.class.getResourceAsStream(ExitParser.getSettingValue_theme("maximize_img")));
        minimize_img = new Image(Apps.class.getResourceAsStream(ExitParser.getSettingValue_theme("minimize_img")));
        restore_img = new Image(Apps.class.getResourceAsStream(ExitParser.getSettingValue_theme("restore_img")));
        windowFont = ExitParser.getSettingValue_theme("windowFont");
        windowFontSize = Integer.valueOf(ExitParser.getSettingValue_theme("windowFontSize"));
        terminalFont = ExitParser.getSettingValue_theme("terminalFont");
        terminalFontSize = Integer.valueOf(ExitParser.getSettingValue_theme("terminalFontSize"));
        windowHeaderColor_c = Color.web(ExitParser.getSettingValue_theme("windowHeaderColor"));
        windowHeaderColor = String.format("-fx-background-color: %s;",
                (ExitParser.getSettingValue_theme("windowHeaderColor").replace("0x", "#")));
        windowMainColor_c = Color.web(ExitParser.getSettingValue_theme("windowMainColor"));
        windowMainColor = String.format("-fx-background-color: %s;",
                (ExitParser.getSettingValue_theme("windowMainColor").replace("0x", "#")));
        windowFontColor_c = Color.web(ExitParser.getSettingValue_theme("windowFontColor"));
        windowFontColor = String.format("-fx-text-fill: %s;",
                (ExitParser.getSettingValue_theme("windowFontColor").replace("0x", "#")));
    }

        /*/
         * Thank you to Indrajeet Latthe for helping me figure out my problem was not
         * in preLogoutkill(), but it was in fact in killWindow(). And for rebuilding
         * killWindow(), and killCommand() for me. Also, I hope everything works out for
         * you and your bride-to-be. Remember; life is balenced. One must know pain before
         * they can find peace. ~ Allison
        /*/

    public static void killCommand(Integer pid) { // This is for "kill x"
        Apps.open_windows.remove(Apps.apps.get(pid).get(0));
        Desktop.main_root.getChildren().remove(Apps.apps.get(pid).get(0));
        //Pid stuff
        Logger.messageCustom("APP", String.format("Killed %s, PID: %s", Apps.apps.get(pid).get(1) , pid));
        Iterator<Integer> iterate = apps.keySet().iterator();
        while  (iterate.hasNext()) {
            ArrayList<Object> i = apps.get(iterate.next());
            iterate.remove();
        }
    }

    public static int get_processID() {
        int id_number = 1;
        Set<Integer> keys = Apps.apps.keySet();
        while (keys.contains(id_number)) {
            for(Integer key: keys){
                if (key.equals(id_number)) {
                    id_number++;
                }
            }
        }
        return id_number;
    }

    public static void preLogoutkill() {
        //Pid stuff
        Iterator<Integer> iterate = Apps.apps.keySet().iterator();
        while (iterate.hasNext()) {
            Integer pid = iterate.next();
            Apps.open_windows.remove(Apps.apps.get(pid).get(0));
            Desktop.main_root.getChildren().remove(Apps.apps.get(pid).get(0));
            //Pid stuff
            Logger.messageCustom("APP", String.format("Killed %s, PID: %s", Apps.apps.get(pid).get(1) , pid));
            iterate.remove();
        }
    }

    /*/
    PATREON | Immortal In Code
    ---------------------------------------------------------------
    Vornication - twitch.tv/vornication

    Even the most expressive and creative people feel like they know nothing at times.
    Look at the Dunning Kruger effect and know that you are not alone. Ignore these feelings,
    do what you enjoy and enjoy what you do... #SometimesIDreamAboutCheese
    /*/
    public static void create_ConfirmPopup_Window(String windowTitle, String message, Runnable ifyes, Runnable ifno, Integer width, Integer height) {
        //Create a new window
        WindowBuilder window = new WindowBuilder(windowTitle);
        window.setProcessName("popup.app");
        window.setPrefSize(width, height);
        
        VBox content = new VBox();
        window.setCenter(content);
        content.prefWidthProperty().bind(window.widthProperty());

        Label messageArea = new Label(message);
        try {
            messageArea.setFont(Desktop.loadFont(Desktop.desktopFont, Desktop.desktopFontSize));
        } catch (NullPointerException e) {}
        messageArea.setWrapText(true);
        messageArea.setStyle(Apps.windowFontColor);
        content.getChildren().add(messageArea);

        HBox buttons = new HBox();

        content.getChildren().add(buttons);
        buttons.prefWidthProperty().bind(content.widthProperty());

        Button yes = new Button("Yes");

        Button no = new Button("No");

        buttons.getChildren().addAll(yes, no);

        yes.setOnAction(yesclick -> {
            window.killWindow();
            if (!(ifyes == null)) {
                ifyes.run();
            }
        });

        no.setOnAction(noclick -> {
            window.killWindow();
            if (!(ifno == null)) {
                ifno.run();
            }
        });

        //Spawn the window
        window.spawnWindow();
        window.placeApp();
    }

    public static void create_ConfirmPopup_Window(String windowTitle, String message, Runnable ifyes, Runnable ifno) {
        Apps.create_ConfirmPopup_Window(windowTitle, message, ifyes, ifno, 300, 225);
    }

    public static void create_Popup_Window(String windowTitle, String message, Integer width, Integer height) {
        //Create a new window
        WindowBuilder window = new WindowBuilder(windowTitle);
        window.setProcessName("popup.app");
        window.setPrefSize(width, height);
        Label messageArea = new Label(message);
        try {
            messageArea.setFont(Desktop.loadFont(Desktop.desktopFont, Desktop.desktopFontSize));
        } catch (NullPointerException e) {}
        messageArea.setWrapText(true);
        messageArea.setStyle(Apps.windowFontColor);
        window.setCenter(messageArea);

        //Spawn the window
        window.spawnWindow();
        window.placeApp();
    }

    public static void create_Popup_Window(String windowTitle, String message) {
        Apps.create_Popup_Window(windowTitle, message, 300, 225);
    }

    public static void create_Explorer_Button() {
        API.addNewApp("Files", () -> {
            (new FileExplorer("/")).spawn();
        });
    }

    public static void spawnStartMenu() {
        if (!(Desktop.startMenu == null)) {
            if (!Desktop.startMenuIsOpen) {
                Desktop.startMenu.run();
                Desktop.startMenuIsOpen = true;
            }
        }
    }

    public static void closeStartMenu() {
        if (!(Desktop.startMenuClose == null)) {
            if (Desktop.startMenuIsOpen) {
                Desktop.startMenuClose.run();
                Desktop.startMenuIsOpen = false;
            }
        }
    }

    public static void setDefaultStartMenu() {
        BorderPane window = new BorderPane();
        API.setStartMenu(() -> {
            window.getStylesheets().add("config.css");
            window.setStyle(Apps.windowMainColor + "-fx-border-style: solid; -fx-border-width: 2;" + String.format("-fx-border-color%s", Desktop.taskbarMainColor.replace("-fx-background-color", "")));
            window.setPrefSize(175, 250);
            Desktop.main_root.getChildren().add(window);
            //window.setLayoutX(100);
            window.layoutYProperty().bind(Desktop.main_root.heightProperty().subtract(Desktop.desktop_taskbar.heightProperty()).subtract(window.heightProperty()));
            
            ScrollPane contentHolder = new ScrollPane();
            window.setTop(contentHolder);
            contentHolder.prefWidthProperty().bind(window.widthProperty());

            VBox menuContent = new VBox();
            contentHolder.setContent(menuContent);

            for (Map.Entry<String, Runnable> entry : Apps.appButtons.entrySet()) {
                Button button = new Button(entry.getKey());
                menuContent.getChildren().add(button);
                button.setOnAction(click -> {
                    Apps.closeStartMenu();
                    entry.getValue().run();
                });
            }
            
            HBox systemBar = new HBox();
            window.setBottom(systemBar);

            Button logoutButton = new Button("Logout");

            Region region = new Region();
            HBox.setHgrow(region, Priority.ALWAYS);

            Button shutdownButton = new Button("Shutdown");

            shutdownButton.setOnAction(click -> {
                closeStartMenu();
                create_ConfirmPopup_Window("Shutdown", "Are you sure you want to shutdown?\nAll unsaved progress will be lost.\n  ", () -> {
                    Desktop.shutdown();
                }, null);
            });

            logoutButton.setOnAction(click -> {
                closeStartMenu();
                create_ConfirmPopup_Window("Logout", "Are you sure you want to logout?\nAll unsaved progress will be lost.\n  ", () -> {
                    Desktop.logout();
                }, null);
            });

            systemBar.getChildren().addAll(logoutButton, region, shutdownButton);
        });
        API.setStartMenuClose(() -> {
            Desktop.main_root.getChildren().remove(window);
        });
    }

}