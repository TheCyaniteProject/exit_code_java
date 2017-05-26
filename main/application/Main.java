package application;

import javafx.application.*;
import javafx.stage.*;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.effect.*;
import javafx.scene.image.*;
import javafx.beans.value.*;
import javafx.scene.paint.*;
import javafx.scene.input.*;
import javafx.geometry.*;
import javafx.event.*;
import java.util.*;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {

        ArrayList<Pane> open_windows = new ArrayList<Pane>();

        // we use a default pane without layout such as HBox, VBox etc.
        final Pane root = new Pane();
        final BorderPane root_desktop = new BorderPane();
		root.setPrefSize(800, 475); // Set default size of the window: (Width, height)
        root.getChildren().add(root_desktop);
        root_desktop.prefWidthProperty().bind(root.widthProperty()); // Fit root_desktop to root (yes, both Pane's are required.)
        root_desktop.prefHeightProperty().bind(root.heightProperty());
        root_desktop.setBackground(Background.EMPTY); // Makes the background color transparent, else the background fills with color.
        root.setBackground(Background.EMPTY);
 
        //TaskBar
        HBox task_bar = new HBox();
        task_bar.getStylesheets().add("config.css");
        task_bar.setStyle("-fx-background-color: rgb(35, 35, 37);" + "-fx-border-color: rgb(39, 39, 40)");
        root_desktop.setBottom(task_bar);

        //Start
        Button start_button = new Button("{ Nix }");
        task_bar.getChildren().add(start_button);
        start_button.setOnAction(open ->  {
            //Apps.spawn_Start_Window(root, open_windows);
        });

        //AppBar
        HBox app_bar = new HBox();
        task_bar.getChildren().add(app_bar);
        HBox.setHgrow(app_bar, Priority.ALWAYS);

        //Apps
        Button open_terminal = new Button("Terminal");
        app_bar.getChildren().add(open_terminal);
        open_terminal.setOnAction(open ->  {
            Apps.spawn_Terminal_Window(root, open_windows);
        });
        Button open_quickpad = new Button("QuickPad");
        app_bar.getChildren().add(open_quickpad);
        open_quickpad.setOnAction(open ->  {
            Apps.spawn_QuickPad_Window(root, open_windows);
        });
        

        //Util
        Button settings_button = new Button("Settings");
        task_bar.getChildren().add(settings_button);
        settings_button.setOnAction(open ->  {
            Apps.spawn_Settings_Window(root, open_windows, primaryStage);
        });

        Button quit_game = new Button("Shutdown");
        task_bar.getChildren().add(quit_game);
        quit_game.setOnAction(toggle_fullscreen ->  {
            preExitTasks();
            primaryStage.close();
        });


        // Finalizing stuffs


        final Scene scene = new Scene(root);
        // CSS Styling
        //scene.getStylesheets().add("config.css");
		// Add default color
        scene.setFill(Color.rgb(0, 128, 128));
		// Create the Wallpaper
        try
        {
		    Image image = new Image("wallpaper.jpg");
            ImagePattern pattern = new ImagePattern(image);
            scene.setFill(pattern);
        }
        catch(IllegalArgumentException s)
        {
            System.out.println("\"wallpaper.jpg\" was not found.");
        }

        // Finally, show the stage
        primaryStage.setTitle("ExitCode");
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() { //Propperly kill the app
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });
        // This sets the fullscreen exit key to blank, and removes the "press ESC to leave fullscreen" because I need Esc for the menu.
        primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        // This forces fullscreen in the current Stage
        primaryStage.setFullScreen(true);
    }

    public static void main(String[] args) {
        System.out.println("[System startup - Hello, world!]");
        launch(args);
    }

    public void preExitTasks() {
        System.out.println("[System shutdown - Goodbye, cruel world!]");
    }

}
