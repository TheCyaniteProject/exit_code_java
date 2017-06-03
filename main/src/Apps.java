package application;

import javafx.stage.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.effect.*;
import javafx.beans.value.*;
import javafx.scene.paint.*;
import javafx.scene.input.*;
import javafx.scene.text.*;
import javafx.geometry.*;
import javafx.event.*;
import java.util.*;

public class Apps {

    static String username = new String("root@NIX:~$ ");

    public static void spawn_QuickPad_Window(Pane root, ArrayList<Pane> open_windows) {
        // The Node Window (In-Game App)
        DraggableNode node = new DraggableNode();
        node.getStylesheets().add("config.css");
        node.setPrefSize(300, 225); // Window Size
        // define the style via css
        node.setStyle("-fx-background-color: rgb(35, 35, 37);" + "-fx-border-color: rgb(39, 39, 40)");
        // position the node
        node.setLayoutX(10 + node.getPrefWidth());

        //Shadows
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
        textArea.setPromptText("Enter some text...");
        // add the node to the root pane 
        root.getChildren().add(node);
        open_windows.add(node);
    }

    public static void spawn_Terminal_Window(Pane root, ArrayList<Pane> open_windows) {
        // The Node Window (In-Game App)
        DraggableNode node = new DraggableNode();
        node.getStylesheets().add("config.css");
        node.setPrefSize(350, 225); // Window Size
        // define the style via css
        node.setStyle("-fx-background-color: rgb(35, 35, 37);" + "-fx-border-color: rgb(39, 39, 40)");
        // position the node
        node.setLayoutX(10 + node.getPrefWidth());

        //Shadows
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
        Label win_name = new Label("Terminal");
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
        ScrollPane termWindow = new ScrollPane();
        termWindow.setFitToWidth(true);
        node.setCenter(termWindow);
        VBox terminalApp = new VBox();
        termWindow.setContent(terminalApp);
        Label termArea = new Label();
        termArea.setStyle("-fx-text-fill: white");
        termArea.setPrefWidth(node.getPrefWidth());
        termArea.setWrapText(true);
        terminalApp.getChildren().add(termArea);
        termArea.setText("   This is a WIP, and has limited commands.\n");
        TagArea termEntry = new TagArea();
        termEntry.setWrapText(true);
        termEntry.setStyle("-fx-text-fill: white");
        terminalApp.getChildren().add(termEntry);

        termEntry.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>()
        {
            @Override
            public void handle(KeyEvent ke) {
                if (ke.getCode().equals(KeyCode.ENTER)) {
                    ke.consume(); // necessary to prevent event handlers for this event
                    String output = new String(termEntry.getText().replace(username, ""));
                    System.out.println("[Console Command: " + output + "]");
                    Console.runCommand(output, username, termArea);
                    termEntry.setText(username);
                    termEntry.end();
                }
            }
        });

        // add the node to the root pane 
        root.getChildren().add(node);
        open_windows.add(node);
    }

    public static void spawn_Settings_Window(Pane root, ArrayList<Pane> open_windows, Stage primaryStage) {
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
        Label win_name = new Label("System Settings");
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
        ToggleButton fullscreen_mode = new ToggleButton("Fullscreen On");
        system_settings.getChildren().add(fullscreen_mode);
        fullscreen_mode.setOnAction(toggle_fullscreen ->  {
            if (fullscreen_mode.isSelected()) {
                System.out.println("[Fullscreen Toggle OFF]");
                primaryStage.setFullScreen(false);
                fullscreen_mode.setText("Fullscreen Off");
            } else {
                System.out.println("[Fullscreen Toggle ON]");
                primaryStage.setFullScreen(true);
                fullscreen_mode.setText("Fullscreen On");
            }
        });

        ToggleButton shadow_mode = new ToggleButton("Shadows On");
        system_settings.getChildren().add(shadow_mode);
        shadow_mode.setOnAction(toggle_shadows ->  {
            if (shadow_mode.isSelected()) {
                for (Pane winnode : open_windows) {
                    System.out.println("[ShadowEffect Toggle OFF]");
                    winnode.setEffect(null);
                }
                shadow_mode.setText("Shadows Off");
            } else {
                //DropShadow dropShadow = new DropShadow();
                dropShadow.setRadius(5.0);
                dropShadow.setColor(Color.color(0, 0, 0, 0.6));
                for (Pane winnode : open_windows) {
                    System.out.println("[ShadowEffect Toggle ON]");
                    winnode.setEffect(dropShadow);
                }
                shadow_mode.setText("Shadows On");
            }
        });
        // add the node to the root pane 
        root.getChildren().add(node);
        open_windows.add(node);
    }

    public static void spawn_Start_Window(Pane root, ArrayList<Pane> open_windows) {
        // The Node Window (In-Game App)
            DraggableNode node = new DraggableNode();
            node.setFocusTraversable(true);
            node.getStylesheets().add("config.css");
            node.setPrefSize(300, 225); // Window Size
            // define the style via css
            node.setStyle("-fx-background-color: rgb(35, 35, 37);" + "-fx-border-color: rgb(39, 39, 40)");
            // position the node
            node.setLayoutX(10 + node.getPrefWidth()); //stage.getWidth() stage.getX()

            //ShadowTest
            DropShadow dropShadow = new DropShadow();
            dropShadow.setRadius(5.0);
            dropShadow.setColor(Color.color(0, 0, 0, 0.6));

            node.setEffect(dropShadow);
            //node.setEffect(null); //remove effect

            //TitleBar

            //Listener
            node.focusedProperty().addListener(new ChangeListener<Boolean>()
            {
                @Override
                public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue)
                {
                    if (newPropertyValue)
                    {
                        System.out.println("Textfield on focus");
                    }
                    else
                    {
                        System.out.println("Textfield out focus");
                    }
                }
            });
            
            //App
           
            // add the node to the root pane 
            root.getChildren().add(node);
            open_windows.add(node);
    }

    public static void main(String[] args) {

    }

}

// This is an extra class for the Terminal

class TagArea extends TextArea{
    String initTag;
    String closeTag;
    public TagArea(){
        this.initTag = Apps.username;
        setTagText(initTag);
        super.textProperty().addListener(new ChangeListener<String>(){
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(!newValue.contains(initTag)){
                    setTagText(oldValue);
                }
            }
        });
    }
    public void setTagText(String text){
        super.setText(text);
    }
}
