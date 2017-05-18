package application;

import java.util.*;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.Separator;
import javafx.geometry.Orientation;
import javafx.scene.layout.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Region;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.geometry.Insets;
import javafx.scene.paint.ImagePattern;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import javafx.scene.layout.Priority;
import javafx.scene.effect.DropShadow;

// Required for fullscreen toggle
import javafx.scene.input.KeyCombination;

public class TestApp extends Application {

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
        root_desktop.setBackground(Background.EMPTY); // Else The background fills with color
        root.setBackground(Background.EMPTY);

        Button open_new = new Button("QuickPad");
        open_new.setOnAction(open ->  {

            // The Node Window (In-Game App)
            DraggableNode node = new DraggableNode();
            node.getStylesheets().add("config.css");
            node.setPrefSize(300, 225); // Window Size
            // define the style via css
            node.setStyle("-fx-background-color: rgb(35, 35, 37);" + "-fx-border-color: rgb(39, 39, 40)");
            // position the node
            node.setLayoutX(10 + node.getPrefWidth());

            //ShadowTest
            DropShadow dropShadow = new DropShadow();
            dropShadow.setRadius(5.0);
            dropShadow.setColor(Color.color(0, 0, 0, 0.6));

            node.setEffect(dropShadow);
            //node.setEffect(null); //remove effect

            //TitleBar
            HBox title_bar = new HBox();
            title_bar.setPrefHeight(10);
            title_bar.setPadding(new Insets(0,0,0,5));
            //title_bar.setStyle("-fx-background-color: rgb(0, 0, 0, 0.6)");
            //Name
            Label win_name = new Label("Quickpad");
            win_name.setStyle("-fx-text-fill: white");
            //Spacer
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            //Quit
            Button win_close = new Button("X");
            win_close.setOnAction(close ->  {
                open_windows.remove(node);
                root.getChildren().remove(node);
            });
            title_bar.getChildren().addAll(win_name, spacer, win_close);
            title_bar.setAlignment(Pos.CENTER_RIGHT);
            node.setTop(title_bar);
            //App
            TextArea textArea = new TextArea();
            textArea.setStyle("-fx-text-fill: white");
            textArea.setWrapText(true);
            node.setCenter(textArea);
            textArea.setText("Enter some text...");
            // add the node to the root pane 
            root.getChildren().add(node);
            open_windows.add(node);
        });

        

        
        
            
        //TaskBar
        HBox task_bar = new HBox();
        task_bar.getStylesheets().add("config.css");
        task_bar.setStyle("-fx-background-color: rgb(35, 35, 37);" + "-fx-border-color: rgb(39, 39, 40)");
        root_desktop.setBottom(task_bar);
        //Start
        Button start_button = new Button("{ Nix }");
        task_bar.getChildren().add(start_button);

        //AppBar
        HBox app_bar = new HBox();
        task_bar.getChildren().add(app_bar);
        HBox.setHgrow(app_bar, Priority.ALWAYS);
        //Apps
        app_bar.getChildren().add(open_new);

        //Util
        Button settings_button = new Button("Settings");
        task_bar.getChildren().add(settings_button);
        settings_button.setOnAction(open ->  {
            // The Node Window (In-Game App)
            DraggableNode node = new DraggableNode();
            node.getStylesheets().add("config.css");
            node.setPrefSize(300, 225); // Window Size
            // define the style via css
            node.setStyle("-fx-background-color: rgb(35, 35, 37);" + "-fx-border-color: rgb(39, 39, 40)");
            // position the node
            node.setLayoutX(10 + node.getPrefWidth());

            //ShadowTest
            DropShadow dropShadow = new DropShadow();
            dropShadow.setRadius(5.0);
            dropShadow.setColor(Color.color(0, 0, 0, 0.6));

            node.setEffect(dropShadow);
            //node.setEffect(null); //remove effect

            //TitleBar
            HBox title_bar = new HBox();
            title_bar.setPrefHeight(10);
            title_bar.setPadding(new Insets(0,0,0,5));
            //title_bar.setStyle("-fx-background-color: rgb(0, 0, 0, 0.6)");
            //Name
            Label win_name = new Label("Settings");
            win_name.setStyle("-fx-text-fill: white");
            //Spacer
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            //Quit
            Button win_close = new Button("X");
            win_close.setOnAction(close ->  {
                open_windows.remove(node);
                root.getChildren().remove(node);
            });
            title_bar.getChildren().addAll(win_name, spacer, win_close);
            title_bar.setAlignment(Pos.CENTER_RIGHT);
            node.setTop(title_bar);
            //App
            VBox system_settings = new VBox();
            node.setCenter(system_settings);

            // Toggle Fullscreen
            ToggleButton fullscreen_mode = new ToggleButton("Fullscreen: OFF");
            system_settings.getChildren().add(fullscreen_mode);
            fullscreen_mode.setOnAction(toggle_fullscreen ->  {
                if (fullscreen_mode.isSelected()) {
                    primaryStage.setFullScreen(true);
                    fullscreen_mode.setText("Fullscreen: ON");
                } else {
                    primaryStage.setFullScreen(false);
                    fullscreen_mode.setText("Fullscreen: OFF");
                }
            });

            ToggleButton shadow_mode = new ToggleButton("Shadows: ON");
            system_settings.getChildren().add(shadow_mode);
            shadow_mode.setOnAction(toggle_shadows ->  {
                if (shadow_mode.isSelected()) {
                    //DropShadow dropShadow = new DropShadow();
                    dropShadow.setRadius(5.0);
                    dropShadow.setColor(Color.color(0, 0, 0, 0.6));
                    for (Pane winnode : open_windows) {
                        winnode.setEffect(dropShadow);
                    }
                    shadow_mode.setText("Shadows: ON");
                } else {
                    for (Pane winnode : open_windows) {
                        winnode.setEffect(null);
                    }
                    shadow_mode.setText("Shadows: OFF");
                }
            });
            shadow_mode.setSelected(true);

            // add the node to the root pane 
            root.getChildren().add(node);
            open_windows.add(node);
        });















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



        // finally, show the stage
        primaryStage.setTitle("ExitCode");
        primaryStage.setScene(scene);
        primaryStage.show();
        // Enables fullscreen. Need to add a toggle.
            // This sets the fullscreen exit key to blank, and removes the "press ESC to leave full screen" because I need Esc for the menu.
        primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
            // This forces fullscreen in the current Stage
        //primaryStage.setFullScreen(true);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
