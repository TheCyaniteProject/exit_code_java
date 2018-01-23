package exitcode;

import javafx.application.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.effect.*;
import javafx.scene.image.*;
import javafx.scene.paint.*;
import javafx.geometry.*;
import java.util.stream.IntStream;

// Internal dependancies: GameResourceLoader, Main, Logger

public class LoadingScreen {

    public static String backgroundImage = "/resources/wallpapers/Mountain_Landscape_by_Giulia_Filippini_and_David_Refoua.png";
    public static Pane main_root;
    public static ProgressBar loadingBar;
    public static Label customizableLabel;

    public static Boolean page_open = false;
    private static Integer length = 0;

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

        //Main Menu
        BorderPane loadWindow = new BorderPane();
        loadWindow.getStylesheets().add("login.css");
        loadWindow.setStyle("-fx-background-color: rgba(100, 100, 100, .0);");
        loadWindow.setPrefSize(800, 350); //Size (Width, height)
        //position the loadWindow
        loadWindow.layoutXProperty().bind(root.widthProperty().subtract(loadWindow.widthProperty()).divide(2));
        loadWindow.layoutYProperty().bind(root.heightProperty().subtract(loadWindow.heightProperty()).divide(2));
        root.getChildren().add(loadWindow);

        //Main Content
        VBox loadWindowContent = new VBox();
        loadWindow.setCenter(loadWindowContent);


        // Add Logo
        //gameLogo
        ImageView gameLogo = new ImageView();
        gameLogo.setPreserveRatio(true);
        gameLogo.setFitWidth(290); //Image Size
        Image logoSource = new Image(Main.class.getResourceAsStream("images/Logo.png"));
        gameLogo.setImage(logoSource);
        HBox logoFrame = new HBox();
        logoFrame.getChildren().add(gameLogo);
        loadWindowContent.getChildren().add(logoFrame);
        logoFrame.setPadding(new Insets(30, 30, 30, ( (800 / 2) - (290 / 2) ))); //Top, Right, Bottom, Left

        // Add Thin Loading Bar
        ProgressBar loadingBar = new ProgressBar(-1.0);
        LoadingScreen.loadingBar = loadingBar;
        loadingBar.setPrefSize(800, 1);
        loadWindowContent.getChildren().add(loadingBar);
        loadingBar.getStylesheets().add("login.css");
        loadingBar.setStyle("-fx-accent: teal;");

        HBox textBox = new HBox();
        loadWindowContent.getChildren().add(textBox);
        textBox.setPadding(new Insets(5, 0, 0, 0)); //Top, Right, Bottom, Left

        // "Loading..." Text
        Label loadingLabel = new Label("Loading");
        textBox.getChildren().add(loadingLabel);
        loadingLabel.setFont(Main.loadFont(Main.mainMenuFont, Main.mainMenuFontSize+4));
        loadingLabel.setStyle("-fx-text-fill: white;");
        Thread thread = new Thread(() -> {
            for (;;) {
                if (!LoadingScreen.page_open) {
                    break;
                }
                if (length >= 1) {
                    IntStream.range(0, length).forEachOrdered(n -> {
                        // new String(new char[generation]).replace("\0", "-")
                        Platform.runLater(() -> { loadingLabel.setText("Loading" + new String(new char[length]).replace("\0", ".")); });
                        try { Thread.sleep(800); }catch(InterruptedException ignored){}
                    });
                } else {
                    Platform.runLater(() -> { loadingLabel.setText("Loading"); });
                    try { Thread.sleep(800); }catch(InterruptedException ignored){}
                }
                if (!length.equals(3)) {
                    length++;
                } else {
                    length = 0;
                }
            }
            return;
        });
        thread.setDaemon(true);
        thread.start();

        // Spacer
        Region spacerRegion = new Region();
        HBox.setHgrow(spacerRegion, Priority.ALWAYS);
        textBox.getChildren().add(spacerRegion);

        // Customizable Text
        Label customizableLabel = new Label("Please Wait..");
        LoadingScreen.customizableLabel = customizableLabel;
        textBox.getChildren().add(customizableLabel);
        customizableLabel.setFont(Main.loadFont(Main.mainMenuFont, Main.mainMenuFontSize+4));
        customizableLabel.setStyle("-fx-text-fill: white;");

        // Finish off by running tasks..

        LoadingScreen.loadTasks();
    }

    public static void setTaskText(String text) {
        Platform.runLater(() -> { LoadingScreen.customizableLabel.setText(text); });
    }

    public static void setLoadingBar(double intiger) {
        Platform.runLater(() -> { LoadingScreen.loadingBar.setProgress(intiger); });
    }

    public static void loadTasks() {
        Thread thread = new Thread(() -> {
            // Prints Important information about the system.
            Logger.printSystemInfo();

            Platform.runLater(() -> { Main.main_primaryStage.toFront(); }); //Fixes a bug where sometimes the Windows taskbar is ontop of the game.

            LoadingScreen.setLoadingBar(0.0);

            Logger.debug("PATREON DISABLED!");
            //Patreon.updatePatronList();

            Logger.debug("theme.xml moved to: ./ (changed in Desktop.java and ExitParser.java)");

            LoadingScreen.setLoadingBar(0.0);
            // Load Mods
            API.modLoader();
            
            LoadingScreen.setLoadingBar(-1.0);
            // Load Game Wallpapers
            LoadingScreen.setTaskText("Loading Game Wallpapers");
            GameResourceLoader.loadGameWallpapers();
            // Load Player Wallpapers
            LoadingScreen.setTaskText("Loading Installed Wallpapers");
            GameResourceLoader.loadPlayerWallpapers();
            // Load Game Fonts
            LoadingScreen.setTaskText("Loading Game Fonts");
            GameResourceLoader.loadGameFonts();
            // Load Player Fonts
            LoadingScreen.setTaskText("Loading Installed Fonts");
            GameResourceLoader.loadPlayerFonts();
            
            // Finish
            LoadingScreen.setLoadingBar(1.0);
            LoadingScreen.setTaskText("All Done.");
            
            Platform.runLater(() -> {
                page_open = false;
                Main.mainMenu();
            });

            return;
        });
        thread.setDaemon(true);
        thread.start();
    }

}