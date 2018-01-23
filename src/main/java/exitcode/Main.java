package exitcode;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;

import javafx.application.*;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.effect.*;
import javafx.scene.image.*;
import javafx.scene.paint.*;
import javafx.scene.text.Font;
import javafx.scene.input.*;
import javafx.geometry.*;
import javafx.event.*;

// Internal dependancies: SystemsScreen, Logger, Desktop, ExitParser, LoginScreen

public class Main extends Application {//TODO // Search

    public static String backgroundImage = "/resources/wallpapers/Mountain_Landscape_by_Giulia_Filippini_and_David_Refoua.png";
    public static String mainMenuFont = "/resources/fonts/Roboto-Regular.ttf";
    public static Integer mainMenuFontSize = 12;
    public static boolean fullscreenBool = true;
    public static Stage main_primaryStage;
    public static Pane main_root;
    public static Scene main_scene;

    public static Boolean page_open = false;
    private static Integer length = 0;

    public static File savefile = new File(System.getProperty("user.dir"), "saves/new_game.ecsave");
    public static String defaultSystem = "systems/localhost.system.xml";

    public static ArrayList<String> patrons = new ArrayList<String>();

    public static ArrayList<String> wallpapers = new ArrayList<String>();
    public static ArrayList<String> fonts = new ArrayList<String>();

    public static void mainMenu() {
        Main.main_primaryStage.getScene().setRoot(Main.main_root); //Best line of code EVER
        
        Main.preStartupCheck();

        final BorderPane root_menu = new BorderPane();
        Main.page_open = true;
        main_root.setPrefSize(800, 600); // Set default size of the window: (Width, height)
        main_root.setMinSize(800, 600);
        main_root.getChildren().add(root_menu);
        root_menu.prefWidthProperty().bind(main_root.widthProperty()); // Fit root_menu to main_root (yes, both Pane's are required.)
        root_menu.prefHeightProperty().bind(main_root.heightProperty());
        root_menu.setBackground(Background.EMPTY); // Makes the background color transparent, else the background fills with WHITE color.
        main_root.setBackground(Background.EMPTY);
        //Wallpaper
        Image backgroundImage = new Image(Main.class.getResourceAsStream(Main.backgroundImage));
        ImageView imageView = new ImageView(backgroundImage);
        imageView.fitWidthProperty().bind(main_root.widthProperty());
        imageView.fitHeightProperty().bind(main_root.heightProperty());
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
        main_root.getChildren().add(imageView);


        //Main Menu
        BorderPane mainMenu = new BorderPane();
        mainMenu.getStylesheets().add("login.css");
        mainMenu.setStyle("-fx-background-color: rgba(100, 100, 100, .1);");
        mainMenu.setPrefWidth(350); //Size (Width)
        // position the mainMenu
        mainMenu.layoutXProperty().bind(main_root.widthProperty().subtract(mainMenu.widthProperty().multiply(2)).divide(3));
        mainMenu.layoutYProperty().bind(main_root.heightProperty().subtract(mainMenu.heightProperty()).divide(2));
        main_root.getChildren().add(mainMenu);

        //Main Content
        VBox mainContent = new VBox();
        mainMenu.setCenter(mainContent);
        //gameLogo
        ImageView gameLogo = new ImageView();
        gameLogo.setPreserveRatio(true);
        gameLogo.setFitWidth(290); //Image Size
        Image logoSource = new Image(Main.class.getResourceAsStream("images/Logo.png"));
        gameLogo.setImage(logoSource);
        HBox logoFrame = new HBox();
        logoFrame.getChildren().add(gameLogo);
        logoFrame.setPadding(new Insets(30, 30, 30, 30)); //Top, Right, Bottom, Left
        mainMenu.setTop(logoFrame);

        HBox playButtonBox = new HBox();
        mainContent.getChildren().add(playButtonBox);
        Button playButton = new Button("Play Game");
        playButtonBox.getChildren().add(playButton);
        playButton.setFont(Main.loadFont(Main.mainMenuFont, Main.mainMenuFontSize+6));
        playButton.prefWidthProperty().bind(mainMenu.widthProperty().subtract(60));
        playButtonBox.setPadding(new Insets(0, 30, 10, 30)); //Top, Right, Bottom, Left
        playButton.setOnAction(playButtonClick -> {
            SystemsScreen.load();
        });

        HBox settingsButtonBox = new HBox();
        mainContent.getChildren().add(settingsButtonBox);
        Button settingsButton = new Button("Settings");
        settingsButtonBox.getChildren().add(settingsButton);
        settingsButton.setFont(Main.loadFont(Main.mainMenuFont, Main.mainMenuFontSize+6));
        settingsButton.prefWidthProperty().bind(mainMenu.widthProperty().subtract(60));
        settingsButtonBox.setPadding(new Insets(0, 30, 50, 30)); //Top, Right, Bottom, Left
        settingsButton.setOnAction(settingsButtonClick -> {
            //TODO 
        });

        HBox modsButtonBox = new HBox();
        mainContent.getChildren().add(modsButtonBox);
        Button modsButton = new Button("Mods");
        modsButtonBox.getChildren().add(modsButton);
        modsButton.setFont(Main.loadFont(Main.mainMenuFont, Main.mainMenuFontSize+6));
        modsButton.prefWidthProperty().bind(mainMenu.widthProperty().subtract(60));
        modsButtonBox.setPadding(new Insets(0, 30, 10, 30)); //Top, Right, Bottom, Left
        modsButton.setOnAction(modsButtonClick -> {
            //TODO 
        });

        HBox quitButtonBox = new HBox();
        mainContent.getChildren().add(quitButtonBox);
        Button quitButton = new Button("Quit Game");
        quitButtonBox.getChildren().add(quitButton);
        quitButton.setFont(Main.loadFont(Main.mainMenuFont, Main.mainMenuFontSize+6));
        quitButton.prefWidthProperty().bind(mainMenu.widthProperty().subtract(60));
        quitButtonBox.setPadding(new Insets(0, 30, 25, 30)); //Top, Right, Bottom, Left
        quitButton.setOnAction(quitButtonClick -> {
            Main.closeGame();
        });

        //Patreon window
        BorderPane patreonWindow = new BorderPane();
        patreonWindow.getStylesheets().add("login.css");
        patreonWindow.setStyle("-fx-background-color: rgba(100, 100, 100, .1);");
        patreonWindow.setPrefWidth(350); //Size (Width)
        // position the patreonWindow
        patreonWindow.layoutXProperty().bind(main_root.widthProperty().subtract(patreonWindow.widthProperty().multiply(2)).divide(3).add(main_root.widthProperty().subtract(patreonWindow.widthProperty().multiply(2)).divide(3)).add(patreonWindow.widthProperty()));
        patreonWindow.layoutYProperty().bind(main_root.heightProperty().subtract(patreonWindow.heightProperty()).divide(2));
        main_root.getChildren().add(patreonWindow);

        //Main Content
        VBox patreonContent = new VBox();
        patreonWindow.setCenter(patreonContent);
        //PatreonLogo
        ImageView patreonLogo = new ImageView();
        patreonLogo.setPreserveRatio(true);
        patreonLogo.setFitWidth(290); //Image Size
        Image pLogoSource = new Image(Main.class.getResourceAsStream("images/patreon.png"));
        patreonLogo.setImage(pLogoSource);
        HBox pLogoFrame = new HBox();
        pLogoFrame.getChildren().add(patreonLogo);
        pLogoFrame.setPadding(new Insets(30, 30, 30, 30)); //Top, Right, Bottom, Left
        patreonWindow.setTop(pLogoFrame);

        HBox tyBox = new HBox();
        patreonContent.getChildren().add(tyBox);
        Label thankYou = new Label("A huge \"Thank You!\" to our Patrons!");
        tyBox.getChildren().add(thankYou);
        thankYou.prefWidthProperty().bind(patreonWindow.widthProperty().subtract(60));
        thankYou.setAlignment(Pos.CENTER);
        thankYou.setStyle("-fx-text-fill: white;");
        tyBox.setPadding(new Insets(0, 30, 10, 30)); //Top, Right, Bottom, Left

        HBox patreonBox = new HBox();
        patreonContent.getChildren().add(patreonBox);
        Label patron = new Label();
        patreonBox.getChildren().add(patron);
        patron.setPrefHeight(25);
        patron.prefWidthProperty().bind(patreonWindow.widthProperty().subtract(60));
        patron.setStyle("-fx-border-color: white;" + "-fx-text-fill: white;" + "-fx-background-color: #F96854;");
        patron.setAlignment(Pos.CENTER);
        patreonBox.setPadding(new Insets(0, 30, 25, 30)); //Top, Right, Bottom, Left

        Thread thread = new Thread(() -> {
            if (Main.patrons.size() > 1) {
            for (int i = 0;; i++)
                {
                    if (!Main.page_open) {
                        break;
                    }
                    if (i > 1) {
                        if (i-1 == Main.patrons.size()-1) {
                            i = 0;
                        }
                    }
                    Main.length = i; // Because of stupid Lambda rules..
                    Platform.runLater(() -> {
                        try {
                            patron.setText(Main.patrons.get(Main.length));
                        } catch (Exception e) {
                            Main.page_open = false;
                        }
                    });
                    try { Thread.sleep(2000); }catch(InterruptedException ignored){}
                }
            } else if (Main.patrons.size() == 1) {
                Platform.runLater(() -> { patron.setText(Main.patrons.get(0)); });
                return;
            } else {
                Platform.runLater(() -> { patron.setText("{ An Error Occurred }"); });
                return;
            }
            return;
        });
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public void start(Stage primaryStage) {
        Main.main_primaryStage = primaryStage;

        final Pane root = new Pane();
        main_root = root;
        final Scene scene = new Scene(main_root);
        Main.main_scene = scene;
        // Add default color
        scene.setFill(Color.BLACK);
        // Create the Wallpaper
        try {
            Image image = new Image(Main.class.getResourceAsStream(Main.backgroundImage));
            ImagePattern pattern = new ImagePattern(image);
            //scene.setFill(pattern); //I moved the up in the code, and applied affects. But left this here because
            //this is the propper way to impliment this, and this throws a warning in the chat when the image is missing.
        } catch (IllegalArgumentException s) {
            Logger.warn(String.format("\"%s\" was not found.", Main.backgroundImage));
            Main.main_scene.setFill(Color.web("#0c071b"));
        }

        // Finally, show the stage
        primaryStage.setTitle("ExitCode");
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() { //Propperly kill the app
            @Override
            public void handle(WindowEvent t) {
                Main.preExitTasks();
                Platform.exit();
                System.exit(0);
            }
        });
        // This sets the fullscreen exit key to blank, and removes the "press ESC to leave fullscreen" because I need Esc for the menu.
        primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        // This forces fullscreen in the current Stage
        primaryStage.setFullScreen(true);

        // Start loading screen
        LoadingScreen.load();
    }

    public static void toggleFullscreen() {
        fullscreenBool = !fullscreenBool;
        main_primaryStage.setFullScreen(fullscreenBool);
    }

    public static void closeGame() {
        Main.preExitTasks();
        Main.main_primaryStage.close();
    }

    public static void preExitTasks() {
        if (Desktop.isOpen) {
            Desktop.logout();
        }
        API.executeShutdownTasks(); // Mod Stuff
        Logger.messageCustom("EXITCODE", "Shutting down! Goodbye, cruel world!");
    }

    public static void main(String[] args) {
        Logger.messageCustom("EXITCODE", "Starting up! Hello, world!");
        launch(args);
    }

    public static Font loadFont(String fontName, double fontSize) {
            if (fontName == null) {
                return Font.loadFont("/resources/fonts/Roboto-Regular.ttf", fontSize);
            }

        try {
            java.net.URL fontUrl = Main.class.getResource(fontName);
            if(fontUrl != null) {
                return Font.loadFont(fontUrl.toExternalForm(), fontSize);
            } else {
                return Font.loadFont("file:" + fontName, fontSize);
            }

        } catch(IllegalArgumentException | NullPointerException s) {
            Logger.warn("\""+ fontName +"\" was not found. Using default font");
            return Font.loadFont("/resources/fonts/Roboto-Regular.ttf", fontSize);
        }
    }

    public static void preStartupCheck() {
        File configFile = new File("./config.xml");
        if (!configFile.isFile()) {
            Logger.warn("Config file does not exist! Creating one.");
            try {
                PrintWriter out = new PrintWriter("./config.xml");
                out.printf(
                        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>%n%n" +
                        "<data>%n" +
                        "    <systemdata>%n" +
                        "        <showwarning>true</showwarning>%n" +
                        "    </systemdata>%n" +
                        "</data>");
                out.close();
            } catch (Exception e) {
                Logger.error(e.getMessage());
            }
            Logger.message("Showing Early-Access Warning");
            StartupWarning.start();
        } else {
            if (ExitParser.getSettingValue_system("showwarning").equals("true")) {
                Logger.message("Showing Early-Access Warning");
                StartupWarning.start();
            }
        }
    }
}

class StartupWarning {
    
        static Pane main_root;
    
        public static void start() {
    
            // we use a default pane without layout such as HBox, VBox etc.
            final Pane root = new Pane();
            StartupWarning.main_root = root;
            final BorderPane root_desktop = new BorderPane();
            root.setPrefSize(800, 600); // Set default size of the window: (Width, height)
            root.getChildren().add(root_desktop);
            root_desktop.prefWidthProperty().bind(root.widthProperty()); // Fit root_desktop to root (yes, both Pane's are required.)
            root_desktop.prefHeightProperty().bind(root.heightProperty());
            root_desktop.setBackground(Background.EMPTY); // Makes the background color transparent, else the background fills with color.
            root.setBackground(Background.EMPTY);
            //Wallpaper
            Image backgroundImage = new Image(LoginScreen.class.getResourceAsStream(LoginScreen.loginWallpaper));
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
    
    
            //Login window
            BorderPane loginWindow = new BorderPane();
            loginWindow.getStylesheets().add("login.css");
            loginWindow.setStyle("-fx-background-color: rgba(100, 100, 100, .1);");
            loginWindow.setPrefSize(700, 500); //Size (Width, height)
            // position the loginWindow
            loginWindow.layoutXProperty().bind(root.widthProperty().subtract(loginWindow.widthProperty()).divide(2));
            loginWindow.layoutYProperty().bind(root.heightProperty().subtract(loginWindow.heightProperty()).divide(2));
            root.getChildren().add(loginWindow);
    
            //LoginScreen Content
            VBox mainContent = new VBox();
            mainContent.setPadding(new Insets(50,80,50,80)); //Top, Right, Bottom, Left
            loginWindow.setCenter(mainContent);
            //NixLogo
            ImageView nixLogo = new ImageView();
            nixLogo.setPreserveRatio(true);
            nixLogo.setFitWidth(250); //Image Size
            Image logoSource = new Image(LoginScreen.class.getResourceAsStream("images/Logo.png"));
            nixLogo.setImage(logoSource);
            HBox logoFrame = new HBox();
            logoFrame.getChildren().add(nixLogo);
            logoFrame.setPadding(new Insets(0,0,40,0)); //Top, Right, Bottom, Left
            mainContent.getChildren().add(logoFrame);
            //Label
            Label systemLabel1 = new Label("Warning! This game is in Early Access!\nIt is under active development, "+
                    "however it is open to the public so we can gather valuable feedback. By clicking \"I understand\", you "+
                    "acknowledge that this game may be unstable. It may contain bugs, and may also be missing important features. "+
                    "You are playing at your own risk."+
                    "\n\nThank you for trying ExitCode in it's unfinished state.");
            systemLabel1.setStyle("-fx-text-fill: lightgray;" + "-fx-border-style: solid inside;" + "-fx-border-width: .5;" +
                    "-fx-border-insets: 5;" + "-fx-border-color: rgb(100, 100, 100);" + "-fx-padding: 5;" + "-fx-text-alignment: center;");
            systemLabel1.setWrapText(true);
            systemLabel1.setFont(Desktop.loadFont(LoginScreen.loginMenuFont, LoginScreen.loginMenuFontSize+3));
            mainContent.getChildren().add(systemLabel1);
            Region spacer1 = new Region();
            VBox.setVgrow(spacer1, Priority.ALWAYS);
            HBox finishBox = new HBox();
            finishBox.setPadding(new Insets(35,0,0,0)); //Top, Right, Bottom, Left
            mainContent.getChildren().addAll(spacer1, finishBox);
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            Button finish_button = new Button(" I understand ");
            finish_button.setOnAction(finish -> {
                ExitParser.setSettingValue_system("showwarning", "false");
                Main.mainMenu();
            });
            finishBox.getChildren().addAll(spacer, finish_button);
    
            Main.main_primaryStage.getScene().setRoot(StartupWarning.main_root);
        }
    }