package addon;

import javafx.scene.layout.BorderPane;
import javafx.scene.control.Button;
import java.awt.FontFormatException;
import javafx.scene.layout.Pane;
import java.lang.Runnable;
import javafx.scene.web.*;
import java.util.*;

import exitcode.*;

public class Plugin {

    public static void createAppWindow() {
        //Create a new window
        WindowBuilder window = new WindowBuilder("jSnake");
        window.setPrefSize(450, 350); // Window Size (Width, height)
        window.setProcessName("jsnake.app");

        //App
        Pane browserRoot = new Pane();
        window.setCenter(browserRoot);

        WebView browser = new WebView();
        browser.prefWidthProperty().bind(browserRoot.widthProperty());
        browser.prefHeightProperty().bind(browserRoot.heightProperty());
        browserRoot.getChildren().add(browser);
        
        WebEngine webEngine = browser.getEngine();
        try {
            webEngine.load((Plugin.class.getResource("/web/index.html")).toString());
        } catch(Exception e) {}
        
        //Spawn the window
        window.spawnWindow();
        window.placeApp();
    }

    public static void println(String string) { // Printing debug info..
        exitcode.API.println(String.format("jSnake: %s", string));
    }

    public static void createAppButton() {
        // Printing debug info..
        println("Building jSnake!");
        Button app_button = new Button("jSnake");
        app_button.getStylesheets().add("config.css");
        app_button.setFont(Desktop.loadFont(Desktop.desktopFont, Desktop.desktopFontSize));
        Desktop.main_appbar.getChildren().add(app_button);
        app_button.setOnAction(open ->  {
            createAppWindow();
        });
    }

    static { // Put things here you wish to execute on load
        exitcode.API.addSecondaryDesktopTask("net.thecyaniteproject.jsnakeapp.createappspawner", () -> createAppButton());
    }
}