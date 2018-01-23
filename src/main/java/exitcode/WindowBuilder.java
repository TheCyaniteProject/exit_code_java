package exitcode;

import javafx.scene.control.Label;

import javafx.geometry.*;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.image.*;
import javafx.scene.paint.Color;
import javafx.scene.Cursor;
import javafx.scene.layout.*;
import javafx.scene.effect.*;
import javafx.scene.input.*;
import javafx.event.*;
import java.util.*;

// Internal dependancies: Desktop, Main, Logger, Apps

public class WindowBuilder extends DraggableNode {

    private String processName = "process.app";
    private Boolean hidden = false;
    private String windowTitle;
    private Label windowTitleLabel;
    private Label re_corner;
    private HBox title_bar;
    private HBox bottom_bar;
    private Integer pid;
    private HBox appButtonBox;
    private Button appButton;


    public WindowBuilder() {
        this.windowTitle = "";
        init();
    }

    public WindowBuilder(String windowTitle) {
        this.windowTitle = windowTitle;
        init();
    }

    private void init() {
        getStylesheets().add("config.css");
        setPrefSize(300, 225); // Window Size (Width, height)
        setMinSize(250, 200);
        // define the style via css
        setStyle(Apps.windowMainColor + "-fx-border-color: rgb(39, 39, 40);");
        // position the node
        setLayoutX((Desktop.main_root.getWidth() - getPrefWidth()) / 2);
        setLayoutY((Desktop.main_root.getHeight() - getPrefHeight()) / 2);
        //Shadows
        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(5.0);
        dropShadow.setColor(Color.color(0, 0, 0, 0.6));
        if (ExitParser.getSettingValue_theme("shadowsenabled").equals("true")) {
            setEffect(dropShadow);
        } else {
            setEffect(null);
        }

        //TitleBar
        HBox title_bar = new HBox();
        this.title_bar = title_bar;
        title_bar.setPrefHeight(10);
        title_bar.setStyle(Apps.windowHeaderColor + "-fx-border-color: transparent;" + "-fx-border-width: .2;");
        title_bar.setPadding(new Insets(0,0,0,5));
        //Name
        Label win_name = new Label(this.windowTitle);
        this.windowTitleLabel = win_name;
        try {
            win_name.setFont(Desktop.loadFont(Desktop.desktopFont, Desktop.desktopFontSize));
        } catch (NullPointerException e) {}
        win_name.setStyle(Apps.windowFontColor);
        //Spacer
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        //Minimize
        Button win_minimize = new Button("", new ImageView(Apps.minimize_img));
        win_minimize.setOnAction(minimize ->{
            this.toggleMinimizeWindow();
        });
        //Maximize
        Button win_maximaze = new Button("");
        win_maximaze.setGraphic(new ImageView(Apps.maximize_img));
        win_maximaze.setOnAction(maximize -> {
            this.maximizeWindow();
        });
        title_bar.setOnMouseClicked(event -> { //double click on task bar to maximize/restore
            if(event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2){
                this.maximizeWindow();
            }
        });
        //Quit
        Button win_close = new Button("", new ImageView(Apps.close_img));
        win_close.setPrefSize(5, 5);
        win_close.setOnAction(close ->  {
            this.killWindow();
        });
        title_bar.getChildren().addAll(win_name, spacer, win_minimize, win_maximaze, win_close);
        title_bar.setAlignment(Pos.CENTER_RIGHT);
        setTop(title_bar);

        //Resize
        HBox bottom_bar = new HBox();
        this.bottom_bar = bottom_bar;
        bottom_bar.setAlignment(Pos.CENTER_RIGHT);
        setBottom(bottom_bar);
        Label re_corner = new Label("///");
        this.re_corner = re_corner;
        re_corner.setStyle(Apps.windowFontColor);
        bottom_bar.getChildren().add(re_corner);

        Hashtable<String, Boolean> toggle = new Hashtable<String, Boolean>();
        toggle.put("key", false);

        Hashtable<String, Double> value = new Hashtable<String, Double>();
        value.put("key1", 0.0);
        value.put("key2", 0.0);

        re_corner.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                //Get mouse position relitive to window (getWidth() - (event.getSceneX() - getLayoutX()))
                if (!toggle.get("key")) {
                    value.put("key1", (getWidth() - (event.getSceneX() - getLayoutX())));
                    value.put("key2", (getHeight() - (event.getSceneY() - getLayoutY())));
                    toggle.put("key", true);
                }
                //Resize window to mouse position
                setPrefSize(
                        ((event.getSceneX() - getLayoutX()) + value.get("key1")),
                        ((event.getSceneY() - getLayoutY()) + value.get("key2")));
                event.consume();
                Main.main_scene.setCursor(Cursor.SE_RESIZE);
            }
        });
        re_corner.setOnMouseReleased((event) -> {
            value.put("key1", 0.0);
            value.put("key2", 0.0);
            toggle.put("key", false);
            Main.main_scene.setCursor(Cursor.DEFAULT);
        });
        //Mouse Cursor stuff
        re_corner.setOnMouseEntered(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent me) {
                Main.main_scene.setCursor(Cursor.SE_RESIZE);
            }
        });
        re_corner.setOnMouseExited(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent me) {
                Main.main_scene.setCursor(Cursor.DEFAULT);
            }
        });
    }

    private void createAppButton() {
        HBox appButtonBox = new HBox();
        Button appButton = new Button(this.windowTitle);
        appButton.getStylesheets().add("config.css");
        appButton.setFont(Desktop.loadFont(Desktop.desktopFont, Desktop.desktopFontSize));

        appButtonBox.getChildren().add(appButton);

        appButton.setOnAction(click -> {
            if (this.hidden) {
                this.toggleMinimizeWindow();
                this.toFront();
            } else {
                this.toFront();
            }
        });

        HBox titlebar = this.title_bar;
        appButton.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                titlebar.setStyle(titlebar.getStyle() + "-fx-border-color: rgb(255, 255, 100);");
            }
        });
    
        appButton.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                titlebar.setStyle(titlebar.getStyle() + "-fx-border-color: transparent;");
            }
        });

        this.appButtonBox = appButtonBox;
        this.appButton = appButton;
        Desktop.main_appbar.getChildren().add(appButtonBox);
    }

    private void destroyAppButton() {
        Desktop.main_appbar.getChildren().remove(this.appButtonBox);
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public String getProcessName() {
        return this.processName;
    }

    public Label getWindowTitleLabel() {
        return this.windowTitleLabel;
    }

    public Label getCornerLabel() {
        return this.re_corner;
    }

    public void setWindowTitle(String windowTitle) {
        this.windowTitle = windowTitle;
        this.windowTitleLabel.setText(windowTitle);
        if (!(this.appButton == null)) {
            this.appButton.setText(windowTitle);
        }
    }

    public void toggleMinimizeWindow() {
        this.setVisible(this.hidden);
        this.hidden = !this.hidden;
    }

    public void hideWindow() {
        this.setVisible(false);
    }

    public void showWindow() {
        this.setVisible(true);
    }

    public void maximizeWindow() {
        if(isMaximized()) {
            restoreSize();
        } else {
            maximize();
        }
    }

    public void killWindow() {
        if (Apps.open_windows.contains(this)) {
            Apps.open_windows.remove(this);
            Desktop.main_root.getChildren().remove(this);
            //Pid stuff
            Iterator<Integer> iterate = Apps.apps.keySet().iterator();

            while  (iterate.hasNext()) {
                int key = iterate.next();
                ArrayList<Object> i = Apps.apps.get(key);

                if (i.get(0).equals(this)) {
                    Logger.messageCustom("APP", String.format("Closed %s, PID: %d", i.get(1), key));
                    iterate.remove();
                    this.destroyAppButton();
                }
            }
        }

    }

    public void placeApp() {
        setLayoutX((Desktop.main_root.getWidth() - getPrefWidth()) / 2);
        setLayoutY((Desktop.main_root.getHeight() - getPrefHeight()) / 2);
    }

    public void spawnWindow() {
        this.pid = Apps.get_processID();
        Desktop.main_root.getChildren().add(this);
        Apps.open_windows.add(this);
        //Pid stuff
        ArrayList<Object> data = new ArrayList<Object>();
        data.add(this);
        data.add(this.processName);

        Apps.apps.put(this.pid, data);
        Logger.messageCustom("APP", String.format("Spawned %s, PID: %s", this.processName, this.pid));

        this.createAppButton();
    }

}