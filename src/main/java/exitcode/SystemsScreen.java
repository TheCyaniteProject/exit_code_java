package exitcode;

import javafx.application.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.effect.*;
import javafx.scene.image.*;
import javafx.scene.paint.*;
import javafx.geometry.*;

// Internal dependancies: Main, LoginScreen

public class SystemsScreen  {

    public static String backgroundImage = "/resources/wallpapers/Mountain_Landscape_by_Giulia_Filippini_and_David_Refoua.png";
    public static Pane main_root;

    public static Boolean page_open = false;

    public static void load() {
        final Pane root = new Pane();
        main_root = root;
        page_open = true;
        Main.main_primaryStage.getScene().setRoot(root); //Best line of code EVER
        final BorderPane root_menu = new BorderPane();
        root.setPrefSize(800, 600); // Set default size of the window: (Width, height)
        root.setMinSize(800, 600);
        root.getChildren().add(root_menu);
        root_menu.prefWidthProperty().bind(root.widthProperty()); // Fit root_menu to root (yes, both Pane's are required.)
        root_menu.prefHeightProperty().bind(root.heightProperty());
        root_menu.setBackground(Background.EMPTY); // Makes the background color transparent, else the background fills with WHITE color.
        root.setBackground(Background.EMPTY);
        //Wallpaper
        Image backgroundImage = new Image(LoadingScreen.class.getResourceAsStream(LoadingScreen.backgroundImage));
        ImageView imageView = new ImageView(backgroundImage);
        imageView.fitWidthProperty().bind(root.widthProperty());
        imageView.fitHeightProperty().bind(root.heightProperty());
        //Darken Effect
        Lighting lighting = new Lighting();
        lighting.setDiffuseConstant(1.0);
        lighting.setSpecularConstant(0.0);
        lighting.setSpecularExponent(0.0);
        lighting.setSurfaceScale(0.0);
        lighting.setLight(new Light.Distant(45, 45, Color.GRAY)); //Note to future self: Don't ever use Color.BLACK here. It doesn't work.
        //Blur Effect
        GaussianBlur gaussianBlur = new GaussianBlur();
        gaussianBlur.setInput(lighting); //To set multiple effects
        imageView.setEffect(gaussianBlur);
        //then you set to your node
        root.getChildren().add(imageView);

        //Systems Window
        BorderPane systemsWindow = new BorderPane();
        systemsWindow.getStylesheets().add("login.css");
        systemsWindow.setPrefSize(600, 600); //Size (Width, height)
        systemsWindow.prefHeightProperty().bind(root.heightProperty().subtract(100));
        systemsWindow.prefWidthProperty().bind(root.widthProperty().subtract(100));
        
        //position the systemsWindow
        systemsWindow.layoutXProperty().bind(root.widthProperty().subtract(systemsWindow.widthProperty()).divide(2));
        systemsWindow.layoutYProperty().bind(root.heightProperty().subtract(systemsWindow.heightProperty()).divide(2));
        root.getChildren().add(systemsWindow);

        //Buttons Menu
        HBox buttonsMenu = new HBox();
        systemsWindow.setTop(buttonsMenu);
        buttonsMenu.setPadding(new Insets(0, 0, 20, 0)); //Top, Right, Bottom, Left

        Button mainMenuButton = new Button("Main Menu");
        buttonsMenu.getChildren().add(mainMenuButton);
        mainMenuButton.setOnAction(mainMenuButtonClick -> {
            Main.mainMenu();
        });

        Region spacerRegion = new Region();
        HBox.setHgrow(spacerRegion, Priority.ALWAYS);
        buttonsMenu.getChildren().add(spacerRegion);

        Button shopButton = new Button("Shop");
        buttonsMenu.getChildren().add(shopButton);
        shopButton.prefWidthProperty().bind(mainMenuButton.widthProperty());
        shopButton.setDisable(true);

        //Main Content
        ScrollPane windowContent = new ScrollPane();
        windowContent.setStyle("-fx-background-color: rgba(100, 100, 100, .1);");
        systemsWindow.setCenter(windowContent);

        FlowPane flowContent = new FlowPane();
        flowContent.prefWidthProperty().bind(windowContent.widthProperty().subtract(17));
        windowContent.setContent(flowContent);

        // Create System
        HBox systemContainer = new HBox();
        systemContainer.setPadding(new Insets(20, 0, 0, 20)); //Top, Right, Bottom, Left
        flowContent.getChildren().add(systemContainer);

        BorderPane systemBox = new BorderPane();
        systemBox.setPrefSize(150, 150);
        systemBox.setStyle("-fx-background-color: rgba(100, 100, 100, .1);");
        systemContainer.getChildren().add(systemBox);

        Button launchButton = new Button("Launch");
        launchButton.setStyle("-fx-border-insets: 0.2;" + "-fx-padding: 0;");
        systemBox.setBottom(launchButton);
        launchButton.prefWidthProperty().bind(systemBox.widthProperty());
        launchButton.setOnAction(launchClick -> {
            LoginScreen.loadSystem();
        });

    }

}