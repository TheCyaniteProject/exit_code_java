package exitcode;

import javafx.application.*;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.effect.*;
import javafx.scene.image.*;
import javafx.beans.value.*;
import javafx.scene.paint.*;
import javafx.scene.input.*;
import javafx.geometry.*;
import javafx.event.*;
import org.w3c.dom.*;
import java.io.*;
import java.net.URI;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;

// Internal dependancies: Main, Logger, Desktop, SystemsScreen

public class LoginScreen {

    public static String USERNAME = null;
    public static String SYSTEMNAME = null;
    public static String HOMEPATH = "/root";
    public static String loginWallpaper = "/resources/wallpapers/Mountain_Landscape_by_Giulia_Filippini_and_David_Refoua.png";
    public static String loginMenuFont = "/resources/fonts/Roboto-Regular.ttf";
    public static Integer loginMenuFontSize = 12;
    public static Pane main_root;
    public static Label main_profileWarning;
    public static TextField main_usernameField;
    public static TextField main_passwordField;

    public static File savefile = Main.savefile; // Backwards Compatibility
    public static String defaultSystem = Main.defaultSystem; // Backwards Compatibility

    public static void loadSystem() {

        // we use a default pane without layout such as HBox, VBox etc.
        final Pane root = new Pane();
        main_root = root;
        final BorderPane root_menu = new BorderPane();
        root.setPrefSize(800, 600); // Set default size of the window: (Width, height)
        root.setMinSize(800, 600);
        root.getChildren().add(root_menu);
        root_menu.prefWidthProperty().bind(root.widthProperty()); // Fit root_menu to root (yes, both Pane's are required.)
        root_menu.prefHeightProperty().bind(root.heightProperty());
        root_menu.setBackground(Background.EMPTY); // Makes the background color transparent, else the background fills with WHITE color.
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
        loginWindow.setPrefWidth(300); //Size (Width, height)
        // position the loginWindow
        loginWindow.layoutXProperty().bind(root.widthProperty().subtract(loginWindow.widthProperty()).divide(2));
        loginWindow.layoutYProperty().bind(root.heightProperty().subtract(loginWindow.heightProperty()).divide(2));
        root.getChildren().add(loginWindow);

        
        //LoginScreen Content
        VBox mainContent = new VBox();
        mainContent.setPadding(new Insets(10, 10, 10, 10)); //Top, Right, Bottom, Left
        loginWindow.setCenter(mainContent);

        HBox profileBox = new HBox();
        mainContent.getChildren().add(profileBox);
        profileBox.setPadding(new Insets(0, 0, 10, 0)); //Top, Right, Bottom, Left

        Pane profileImage = new Pane();
        profileBox.getChildren().add(profileImage);
        profileImage.setPrefSize(100, 100);
        profileImage.setStyle("-fx-background-color: rgba(100, 100, 100, .1);");

        VBox profileContent = new VBox();
        profileBox.getChildren().add(profileContent);

        //GameLogo
        ImageView gameLogo = new ImageView();
        gameLogo.setPreserveRatio(true);
        gameLogo.setFitWidth(178); //Image Size
        Image logoSource = new Image(LoginScreen.class.getResourceAsStream("images/Logo.png"));
        gameLogo.setImage(logoSource);
        HBox logoFrame = new HBox();
        logoFrame.getChildren().add(gameLogo);
        logoFrame.setPadding(new Insets(10, 0, 10, 10)); //Top, Right, Bottom, Left
        profileContent.getChildren().add(logoFrame);

        VBox usernameBox = new VBox();
        usernameBox.setPadding(new Insets(10, 0, 10, 10)); //Top, Right, Bottom, Left
        profileContent.getChildren().add(usernameBox);

        Label usernameLabel = new Label("USERNAME");
        usernameBox.getChildren().add(usernameLabel);
        usernameLabel.prefWidthProperty().bind(usernameBox.widthProperty());
        usernameLabel.setStyle("-fx-text-fill: white;");
        usernameLabel.setAlignment(Pos.CENTER);

        Pane usernameSepperator = new Pane();
        usernameSepperator.setStyle("-fx-background-color: white;");
        usernameSepperator.setPrefHeight(1);
        usernameSepperator.prefWidthProperty().bind(usernameBox.widthProperty());
        usernameBox.getChildren().add(usernameSepperator);

        TextField passwordField = new PasswordField();
        LoginScreen.main_passwordField = passwordField;
        mainContent.getChildren().add(passwordField);
        passwordField.prefWidthProperty().bind(mainContent.widthProperty().subtract(20));
        passwordField.setStyle("-fx-background-color: rgba(100, 100, 100, .1);" + "-fx-border-style: solid inside;" + "-fx-border-width: 1;" + "-fx-border-color: rgb(100, 100, 100);" + "-fx-prompt-text-fill: lightgray;");
        passwordField.setPromptText("Password");

        HBox buttonBox = new HBox();
        buttonBox.setPadding(new Insets(10, 0, 0, 0)); //Top, Right, Bottom, Left
        mainContent.getChildren().add(buttonBox);

        Button shutdownButton = new Button("  Shutdown  ");
        shutdownButton.setStyle("-fx-background-color: rgba(100, 100, 100, .1);" + "-fx-border-insets: 0;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button loginButton = new Button("Login");
        loginButton.setStyle("-fx-background-color: rgba(100, 100, 100, .1);" + "-fx-border-insets: 0;");
        loginButton.prefWidthProperty().bind(shutdownButton.widthProperty());

        buttonBox.getChildren().addAll(shutdownButton, spacer, loginButton);

        shutdownButton.setOnAction(shutdownClick -> {
            SystemsScreen.load();
        });

        loginButton.setOnAction(loginClick -> {
            Logger.debug("Setting Mock System Info");
            LoginScreen.USERNAME = "cyanite";
            LoginScreen.SYSTEMNAME = "sinfull-pc";
            Desktop.start();
        });

        Logger.debug("Setting passwordField text: 'Password'");
        passwordField.setText("Password");

        API.executeAll_Login();

        // Finalizing stuffs -----------------------
        Logger.messageCustom("SYSTEM", "Startup");
        Main.main_primaryStage.getScene().setRoot(LoginScreen.main_root); //Best line of code EVER

    }

}


class Account {

    static Pane main_root;

    public static void start() {

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
        //Wallpaper
        Image backgroundImage = new Image(Apps.class.getResourceAsStream(LoginScreen.loginWallpaper));
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
        loginWindow.setPrefSize(350, 600); //Size (Width, height)
        // position the loginWindow
        loginWindow.layoutXProperty().bind(root.widthProperty().subtract(loginWindow.widthProperty()).divide(2));
        loginWindow.layoutYProperty().bind(root.heightProperty().subtract(loginWindow.heightProperty()).divide(2));
        root.getChildren().add(loginWindow);

        //LoginScreen Content
        VBox mainContent = new VBox();
        loginWindow.setCenter(mainContent);
        //NixLogo
        ImageView nixLogo = new ImageView();
        nixLogo.setPreserveRatio(true);
        nixLogo.setFitWidth(250); //Image Size
        Image logoSource = new Image(LoginScreen.class.getResourceAsStream("images/Logo.png"));
        nixLogo.setImage(logoSource);
        HBox logoFrame = new HBox();
        logoFrame.getChildren().add(nixLogo);
        logoFrame.setPadding(new Insets(60, 55, 60, 55)); //Top, Right, Bottom, Left
        loginWindow.setTop(logoFrame);
        //Title
        HBox titleBox = new HBox();
        titleBox.setPadding(new Insets(0, 0, 5, 15)); //Top, Right, Bottom, Left
        mainContent.getChildren().add(titleBox);
        Label title = new Label("Create New Profile");
        title.setStyle("-fx-text-fill: lightgray");
        titleBox.getChildren().add(title);
        //Username
        HBox userBox = new HBox();
        mainContent.getChildren().add(userBox);
        userBox.setStyle("-fx-background-color: rgba(100, 100, 100, .1);");
        //MaskLogo
        ImageView maskLogo = new ImageView();
        maskLogo.setPreserveRatio(true);
        maskLogo.setFitHeight(20);
        Image maskSource = new Image(LoginScreen.class.getResourceAsStream("images/mask.png"));
        maskLogo.setImage(maskSource);
        HBox logoFrame2 = new HBox();
        logoFrame2.getChildren().add(maskLogo);
        logoFrame2.setPadding(new Insets(10, 10, 10, 20)); //Top, Right, Bottom, Left
        userBox.getChildren().add(logoFrame2);
        //Entry
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        LoginScreen.main_usernameField = usernameField;
        usernameField.setPrefSize(350, 40); //Size (Width, height)
        usernameField.setStyle("-fx-text-fill: lightgray;" + "-fx-prompt-text-fill: lightgray;");
        userBox.getChildren().add(usernameField);
        //PasswordCheck
        CheckBox checkBox = new CheckBox("Password protect this profile");
        mainContent.getChildren().add(checkBox);
        checkBox.setSelected(true);
        //Password
        HBox passBox = new HBox();
        mainContent.getChildren().add(passBox);
        passBox.setStyle("-fx-background-color: rgba(100, 100, 100, .1);");
        //LockLogo
        ImageView lockLogo = new ImageView();
        lockLogo.setPreserveRatio(true);
        lockLogo.setFitHeight(20);
        Image lockSource = new Image(LoginScreen.class.getResourceAsStream("images/lock.png"));
        lockLogo.setImage(lockSource);
        HBox logoFrame3 = new HBox();
        logoFrame3.getChildren().add(lockLogo);
        logoFrame3.setPadding(new Insets(10, 10, 10, 23)); //Top, Right, Bottom, Left
        passBox.getChildren().add(logoFrame3);
        //Entry
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        LoginScreen.main_passwordField = passwordField;
        passwordField.setPrefSize(350, 40); //Size (Width, height)
        passwordField.setStyle("-fx-text-fill: lightgray;" + "-fx-prompt-text-fill: lightgray;");
        passBox.getChildren().add(passwordField);

        //Password
        HBox passBox1 = new HBox();
        mainContent.getChildren().add(passBox1);
        passBox1.setStyle("-fx-background-color: rgba(100, 100, 100, .1);");
        //LockLogo
        ImageView lockLogo1 = new ImageView();
        lockLogo1.setPreserveRatio(true);
        lockLogo1.setFitHeight(20);
        Image lockSource1 = new Image(LoginScreen.class.getResourceAsStream("images/lock.png"));
        lockLogo1.setImage(lockSource1);
        HBox logoFrame31 = new HBox();
        logoFrame31.getChildren().add(lockLogo1);
        logoFrame31.setPadding(new Insets(10, 10, 10, 23)); //Top, Right, Bottom, Left
        passBox1.getChildren().add(logoFrame31);
        //Entry
        PasswordField passwordField1 = new PasswordField();
        passwordField1.setPromptText("Repeat Password");
        LoginScreen.main_passwordField = passwordField1;
        passwordField1.setPrefSize(350, 40); //Size (Width, height)
        passwordField1.setStyle("-fx-text-fill: lightgray;" + "-fx-prompt-text-fill: lightgray;");
        passBox1.getChildren().add(passwordField1);

        //Buttons
        HBox buttonBox = new HBox();
        buttonBox.setPrefHeight(10);
        //NewProfile Button
        Button backButton = new Button("Back");
        backButton.setPrefWidth(100);
        //Spacer
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        //LogIn Button
        Button createButton = new Button("Create");
        createButton.setPrefWidth(100);
        buttonBox.getChildren().addAll(backButton, spacer, createButton);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        loginWindow.setBottom(buttonBox);
        //Events
        usernameField.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent ke) {
                if (ke.getCode().equals(KeyCode.ENTER)) {
                    ke.consume(); // necessary to prevent event handlers for this event
                    if (checkBox.isSelected()) {
                        passwordField.requestFocus();
                    } else {
                        Account.create(true, usernameField.getText(), usernameField.getText());
                    }
                }
            }
        });
        passwordField.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent ke) {
                if (ke.getCode().equals(KeyCode.ENTER)) {
                    ke.consume(); // necessary to prevent event handlers for this event
                    if (checkBox.isSelected()) {
                        passwordField1.requestFocus();
                    } else {
                        Account.create(true, usernameField.getText(), usernameField.getText());
                    }
                }
            }
        });
        passwordField1.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent ke) {
                if (ke.getCode().equals(KeyCode.ENTER)) {
                    ke.consume(); // necessary to prevent event handlers for this event
                    userBox.setStyle("-fx-background-color: rgba(100, 100, 100, .1)");
                    passBox.setStyle("-fx-background-color: rgba(100, 100, 100, .1)");
                    passBox1.setStyle("-fx-background-color: rgba(100, 100, 100, .1)");
                    //Username
                    String[] chars = usernameField.getText().split("(?!^)");
                    String letters = "abcdefghijklmnopqrstuvwxyz";
                    for (int i = 0; i < chars.length; i++) {
                        if (((letters + letters.toUpperCase() + "0123456789_-").contains(chars[i])) &&
                                (usernameField.getText().length() >= 1) &&
                                (usernameField.getText().length() <= 63)) {
                            //Password
                            if (checkBox.isSelected()) {
                                if (passwordField.getText().length() > 3) {
                                    //Password1
                                    if (passwordField1.getText().equals(passwordField.getText())) {
                                        Account.create(true, passwordField.getText(), usernameField.getText());
                                    } else {
                                        passBox1.setStyle("-fx-background-color: rgba(255, 0, 0, .1)");
                                    }
                                } else {
                                    passBox.setStyle("-fx-background-color: rgba(255, 0, 0, .1)");
                                }
                            } else {
                                Account.create(true, usernameField.getText(), usernameField.getText());
                            }
                        } else {
                            userBox.setStyle("-fx-background-color: rgba(255, 0, 0, .1)");
                        }
                    }
                }
            }
        });
        //backButton Action Event
        backButton.setOnAction(bba -> {
            Account.cancel();
        });
        createButton.setOnAction(cba -> {
            userBox.setStyle("-fx-background-color: rgba(100, 100, 100, .1)");
            passBox.setStyle("-fx-background-color: rgba(100, 100, 100, .1)");
            passBox1.setStyle("-fx-background-color: rgba(100, 100, 100, .1)");
            //Username
            String[] chars = usernameField.getText().split("(?!^)");
            String letters = "abcdefghijklmnopqrstuvwxyz";
            for (int i = 0; i < chars.length; i++) {
                if (((letters + letters.toUpperCase() + "0123456789_-").contains(chars[i])) &&
                        (usernameField.getText().length() >= 1) &&
                        (usernameField.getText().length() <= 63)) {
                    //Password
                    if (checkBox.isSelected()) {
                        if (passwordField.getText().length() > 3) {
                            //Password1
                            if (passwordField1.getText().equals(passwordField.getText())) {
                                Account.create(true, passwordField.getText(), usernameField.getText());
                            } else {
                                passBox1.setStyle("-fx-background-color: rgba(255, 0, 0, .1)");
                            }
                        } else {
                            passBox.setStyle("-fx-background-color: rgba(255, 0, 0, .1)");
                        }
                    } else {
                        Account.create(true, usernameField.getText(), usernameField.getText());
                    }
                } else {
                    userBox.setStyle("-fx-background-color: rgba(255, 0, 0, .1)");
                }
            }

        });
        //checkBox Listener Event
        checkBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
            public void changed(ObservableValue<? extends Boolean> ov,
                                Boolean old_val, Boolean new_val) {
                if (checkBox.isSelected()) {
                    passwordField.setEditable(true);
                    passBox.setStyle("-fx-background-color: rgba(100, 100, 100, .1);");
                    passwordField.setStyle("-fx-text-fill: lightgray;" + "-fx-prompt-text-fill: lightgray;");
                    passwordField1.setEditable(true);
                    passBox1.setStyle("-fx-background-color: rgba(100, 100, 100, .1);" + "-fx-text-fill: lightgray;");
                    passwordField1.setStyle("-fx-text-fill: lightgray;" + "-fx-prompt-text-fill: lightgray;");
                } else {
                    passwordField.setEditable(false);
                    passBox.setStyle("-fx-background-color: rgba(70, 70, 70, .1);" + "-fx-text-fill: gray;");
                    passwordField.setStyle("-fx-text-fill: gray;" + "-fx-prompt-text-fill: gray;");
                    passwordField1.setEditable(false);
                    passBox1.setStyle("-fx-background-color: rgba(70, 70, 70, .1);" + "-fx-text-fill: gray;");
                    passwordField1.setStyle("-fx-text-fill: gray;" + "-fx-prompt-text-fill: gray;");
                }
            }
        });


        //Change the stage root
        Main.main_primaryStage.getScene().setRoot(Account.main_root); //Best line of code EVER
        System.out.println("[Creating Profile]");
    }

    public static void create(Boolean usepassword, String password, String username) {
        File file = new File("./profiles/" + username.trim().toLowerCase() + "/player.xml");
        Boolean b = false;
        if (!file.exists()) {
            b = file.getParentFile().mkdirs();
            if (b) {
                System.out.println("[Created new profile directory]");
                try {
                    b = file.createNewFile();
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
                if (b) {
                    System.out.println("[Created new player.xml]");
                    try {
                        PrintWriter out = new PrintWriter("./profiles/" + username.trim().toLowerCase() + "/player.xml");
                        String pass;
                        if (usepassword) {
                            pass = ExitParser.passHasher(password);
                        } else {
                            pass = ExitParser.passHasher(username);
                        }
                        out.printf(
                                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>%n" +
                                        "<data>%n" +
                                        "    <playerdata>%n" +
                                        "        <username>%s</username>%n" +
                                        "        <password type=\"sha256\">%s</password>%n" +
                                        "    </playerdata>%n" +
                                        "</data>", username.trim(), pass);
                        out.close();
                        Account.exit();
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                    }
                } else {
                    System.out.println("[Failed to create profile]");
                }
            } else {
                System.out.println("[Failed to create profile directory]");
            }
        }
    }

    public static void cancel() {
        Main.main_primaryStage.getScene().setRoot(LoginScreen.main_root); // Changes the scene to the Login screen
        System.out.println("[Profile Creation Canceled]");
    }

    public static void exit() {
        Main.main_primaryStage.getScene().setRoot(LoginScreen.main_root); // Changes the scene to the Login screen
        System.out.println("[Profile Creation Completed]");
    }

}


class NameSystem {

    static TextField main_systemField;
    static Pane main_root;

    public static void start() {

        // we use a default pane without layout such as HBox, VBox etc.
        final Pane root = new Pane();
        NameSystem.main_root = root;
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
        //imageView.setEffect(lighting);
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
        mainContent.setPadding(new Insets(50,80,0,80)); //Top, Right, Bottom, Left
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
        Label systemLabel = new Label("Welcome to the PiXEl OS installation!");
        systemLabel.setStyle("-fx-text-fill: lightgray");
        systemLabel.setFont(Desktop.loadFont(LoginScreen.loginMenuFont, LoginScreen.loginMenuFontSize+5));
        mainContent.getChildren().add(systemLabel);
        Label systemLabel1 = new Label("\nWe'll configure the environment for you.");
        systemLabel1.setStyle("-fx-text-fill: lightgray");
        systemLabel1.setFont(Desktop.loadFont(LoginScreen.loginMenuFont, LoginScreen.loginMenuFontSize+2));
        mainContent.getChildren().add(systemLabel1);
        Label systemLabel2 = new Label("\n\n\nPlease enter a hostname for this system.\n\n");
        systemLabel2.setStyle("-fx-text-fill: lightgray");
        systemLabel2.setFont(Desktop.loadFont(LoginScreen.loginMenuFont, LoginScreen.loginMenuFontSize+2));
        mainContent.getChildren().add(systemLabel2);
        //System Name
        HBox systemBox = new HBox();
        mainContent.getChildren().add(systemBox);
        systemBox.setStyle("-fx-background-color: rgba(100, 100, 100, .1)");
        //Entry
        TextField systemField = new TextField();
        NameSystem.main_systemField = systemField;
        systemField.setFont(Desktop.loadFont(LoginScreen.loginMenuFont, LoginScreen.loginMenuFontSize+8));
        systemField.setPrefHeight(40); //Size (Width, height)
        systemField.prefWidthProperty().bind(systemBox.widthProperty());
        systemField.setStyle("-fx-text-fill: lightgray");
        systemBox.getChildren().add(systemField);
        //Warning
        HBox warningBox = new HBox();
        warningBox.setPadding(new Insets(35,0,0,0)); //Top, Right, Bottom, Left
        mainContent.getChildren().add(warningBox);
        //MaskLogo
        ImageView maskLogo = new ImageView();
        maskLogo.setPreserveRatio(true);
        maskLogo.setFitHeight(27);
        Image maskSource = new Image(LoginScreen.class.getResourceAsStream("images/mask.png"));
        maskLogo.setImage(maskSource);
        HBox warningFrame2 = new HBox();
        warningFrame2.getChildren().add(maskLogo);
        warningFrame2.setPadding(new Insets(0,5,0,0)); //Top, Right, Bottom, Left
        warningBox.getChildren().add(warningFrame2);
        //Label
        Label systemLabel3 = new Label("Your Hostname should be unique and should only contain a-z 0-9 and dashes."+
                "\nPlease note that the Hostname differs from your Profile name. Try to be creative.");
        systemLabel3.setStyle("-fx-text-fill: gray");
        systemLabel3.setFont(Desktop.loadFont(LoginScreen.loginMenuFont, LoginScreen.loginMenuFontSize-2));
        warningBox.getChildren().add(systemLabel3);

        HBox finishBox = new HBox();
        finishBox.setPadding(new Insets(35,0,0,0)); //Top, Right, Bottom, Left
        mainContent.getChildren().add(finishBox);
        Label systemLabel4 = new Label("\nYour Hostname is permanent and cannot be changed. Once you're confident with it, click Finish.");
        systemLabel4.setStyle("-fx-text-fill: gray");
        systemLabel4.setFont(Desktop.loadFont(LoginScreen.loginMenuFont, LoginScreen.loginMenuFontSize-2));
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Button finish_button = new Button(" Finish ");
        finish_button.setOnAction(finish -> {
            if(!(systemField.getText().length() < 1)) {
                ExitParser.setSettingValue_system("systemname", systemField.getText().trim());
                if (!Main.savefile.exists()) {
                    try { ExitParser.createNewSave(Main.savefile); } catch (Exception e) {}
                }
                ExitParser.createNewSystem(Main.defaultSystem, ExitParser.getBuildDefaultSystem(ExitParser.getSettingValue_system("systemname")));
                LoginScreen.SYSTEMNAME = ExitParser.getSettingValue_system("systemname");
                System.out.println("[Set Hostname: " + LoginScreen.SYSTEMNAME + "]");
                Main.main_primaryStage.getScene().setRoot(LoginScreen.main_root);
            }
        });
        finishBox.getChildren().addAll(systemLabel4, spacer, finish_button);
        systemField.addEventFilter(KeyEvent.KEY_TYPED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                String character=event.getCharacter();
                event.consume();
                if(!(systemField.getText().length() >= 32)) {
                    character = character.replace(" ", "-").toLowerCase();
                    if(character.matches("[a-z]")) {
                        systemField.setText(systemField.getText() + character);
                        systemField.end();
                    } else if(character.matches("[\\d]|[-]")) {
                        if(!(systemField.getText().length() < 1)) {
                            systemField.setText(systemField.getText() + character);
                            systemField.end();
                        }
                    }
                }
            }
        });

        Main.main_primaryStage.getScene().setRoot(NameSystem.main_root);
    }
}
