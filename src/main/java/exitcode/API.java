package exitcode;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;

import java.lang.Runnable;
import java.util.*;

// Internal dependancies: Logger, Desktop, LoadingScreen

public class API {

    /*/
    NOTE:
    This is our WIP API. Everything here (and in this entire game) is subject to
    change without warning.

    Please check that your addon works with each new game release.

    !!ATTENTION!!
    We are not responsible for misuse of this API, or any Modder-implimented code.
    Modders take full responsibility for their own code, as well as ANY and ALL
    issues and damage that they may potentially cause.

    We kindly ask Modders to be respectful, and not create and/or distribute potentially
    or perpously harmful code. Doing so not only hurts YOUR reputation, but also this
    community as a whole.

    Thank you~

    Allison Smith - Head Developer of ExitCode
    /*/


    /**
     * These are Runnable's to be executed after the login page loads.
     */
    private static Map<String, Runnable> login_tasks = new TreeMap<>();

    /**
     * These are Runnable's to be executed after the Desktop loads.
     * (Desktop mods go here)
     */
    private static Map<String, Runnable> desktop_tasks = new TreeMap<>();

    /**
     * These are secondary Runnable's to be executed after the Desktop loads.
     * (Apps and other Addons go here, to make sure they load correctly after desktop mods)
     */
    private static Map<String, Runnable> desktop_tasks2 = new TreeMap<>();

    /**
     * These are Runnable's to be executed when the game shutsdown.
     * (Use these to propperly kill threads and close files to avoid potential data loss)
     */
    private static Map<String, Runnable> shutdown_tasks = new TreeMap<>();

    /**
     * This is a public modders resource. It's intended for communication between mods.
     * 
     * Other mods use this too. So don't clear it or anything like that.
     */
    public static Map<String, ?> resource_map = new TreeMap<>();

    /**
     * This is a public modders resource. It's intended for communication between mods.
     * 
     * Other mods use this too. So don't clear it or anything like that.
     */
    public static Hashtable<String, ?> resource_hashtable = new Hashtable<>();
    

    /**
     * Adds a new function to login_tasks.
     * These are Runnable's to be executed after the Login Page loads.
     *  
     * @param uniqueObjectIdentifier This is a String ID that should be unique to a single
     * function, or other resource. This is used to help troubleshoot problems, and enable you
     * to reference your addon's implimented functions, or other resources.
     * 
     * @param function This is the Runnable to be executed.
     */
    public static void addLoginTask(String uniqueObjectIdentifier, Runnable function) {
        API.login_tasks.put(uniqueObjectIdentifier, function);
    }

    /**
     * Adds a new function to desktop_tasks.
     * These are Runnable's to be executed after the Desktop loads.
     * (Desktop mods go here)
     *  
     * @param uniqueObjectIdentifier This is a String ID that should be unique to a single
     * function, or other resource. This is used to help troubleshoot problems, and enable you
     * to reference your addon's implimented functions, or other resources.
     * 
     * @param function This is the Runnable to be executed.
     */
    public static void addDesktopTask(String uniqueObjectIdentifier, Runnable function) {
        API.desktop_tasks.put(uniqueObjectIdentifier, function);
    }

    /**
     * Adds a new function to desktop_tasks2.
     * These are secondary Runnable's to be executed after the Desktop loads.
     * (Apps and other Addons go here, to make sure they load correctly after desktop mods)
     *  
     * @param uniqueObjectIdentifier This is a String ID that should be unique to a single
     * function, or other resource. This is used to help troubleshoot problems, and enable you
     * to reference your addon's implimented functions, or other resources.
     * 
     * @param function This is the Runnable to be executed.
     */
    public static void addSecondaryDesktopTask(String uniqueObjectIdentifier, Runnable function) {
        API.desktop_tasks2.put(uniqueObjectIdentifier, function);
    }

    /**
     * Adds a new function to desktop_tasks2.
     * These are secondary Runnable's to be executed after the Desktop loads.
     * (Apps and other Addons go here, to make sure they load correctly after desktop mods)
     *  
     * @param uniqueObjectIdentifier This is a String ID that should be unique to a single
     * function, or other resource. This is used to help troubleshoot problems, and enable you
     * to reference your addon's implimented functions, or other resources.
     * 
     * @param function This is the Runnable to be executed.
     */
    public static void addShutdownTask(String uniqueObjectIdentifier, Runnable function) {
        API.shutdown_tasks.put(uniqueObjectIdentifier, function);
    }

    /**
     * Spawns an in-game Notification.
     * 
     * @param title The is the Title/Header of the Notification.
     * 
     * @param body The is the Body/Text/Sum of the Notification.
     * 
     * <br><br>
     * 
     * Example:<br>
     * 
     * {@code API.spawnNotification("System", "Hello, from the matrix!");}
     * 
     * <h4>System</h4>
     * <hr>
     * <em>Hello, from the matrix!</em>
     */
    public static void spawnNotification(String title, String body) {

    }

    /**
     * This sets the OS Start Menu. Each time this is run, it overwrites the last input.
     * 
     * @param function This is the Runnable that is executed to call the startmenu. In reality,
     * you could put anything here. Not just an actual Menu.
     */
    public static void setStartMenu(Runnable function) {
        Desktop.startMenu = function;
    }

    /**
     * This sets the OS Start Menu. Each time this is run, it overwrites the last input.
     * 
     * @param function This is the Runnable that is executed to close the startmenu.
     */
    public static void setStartMenuClose(Runnable function) {
        Desktop.startMenuClose = function;
    }

    public static void addNewApp(String appName, Runnable function) {
        Apps.appButtons.putIfAbsent(appName, function);
    }

    /**
     * Please use this instead of the regular "System.out.println()"
     * This will allow us to make use of your output.
     * 
     * This forwards output to Logger.message().
     * 
     * @param string This is the string to print.
     */

    public static void println(String string) {
        Logger.messageCustom("API", string);
    }

    //
    // Mods should never reference these.. (doing so would duplicate things... Basically, NEVER run more than once!)
    //
    
    /**
     * This executes all of the Runnable's stored within API.login_tasks
     * 
     * NOT TO BE REFERENCED BY MODS
     */

    static void executeAll_Login() {
        if (API.login_tasks.entrySet().size() >= 1) {
            API.println("Executing LoginPage Tasks..");
            for (Map.Entry<String, Runnable> entry : API.login_tasks.entrySet()) {
                API.println(String.format("Executing %s..", entry.getKey()));
                entry.getValue().run();
            }
        }
    }

    /**
     * This executes all of the Runnable's stored within API.desktop_tasks
     * 
     * NOT TO BE REFERENCED BY MODS
     */

    static void executeAll_Desktop() {
        if (API.desktop_tasks.entrySet().size() >= 1) {
            API.println("Executing Desktop Tasks..");
            for (Map.Entry<String, Runnable> entry : API.desktop_tasks.entrySet()) {
                API.println(String.format("Executing %s..", entry.getKey()));
                entry.getValue().run();
            }
        }
    }

    /**
     * This executes all of the Runnable's stored within API.desktop_tasks2
     * 
     * NOT TO BE REFERENCED BY MODS
     */

    static void executeAll_Desktop2() {
        if (API.desktop_tasks2.entrySet().size() >= 1) {
            API.println("Executing Secondary Desktop Tasks..");
            for (Map.Entry<String, Runnable> entry : API.desktop_tasks2.entrySet()) {
                API.println(String.format("Executing %s..", entry.getKey()));
                entry.getValue().run();
            }
        }
    }

    /**
     * This executes all of the Runnable's stored within API.shutdown_tasks
     * 
     * NOT TO BE REFERENCED BY MODS
     */

    static void executeShutdownTasks() {
        if (API.shutdown_tasks.entrySet().size() >= 1) {
            API.println("Executing Shutdown Tasks..");
            for (Map.Entry<String, Runnable> entry : API.shutdown_tasks.entrySet()) {
                API.println(String.format("Executing %s..", entry.getKey()));
                entry.getValue().run();
            }
        }
    }

    /**
     * This adds the .class files from an addon.eca (ExitCode Addon) to the game classpath.
     * 
     * NOT TO BE REFERENCED BY MODS
     * 
     * LoadingScreen.setLoadingBar(1.0);
            LoadingScreen.setTaskText("All Done.");
     */

    static void modLoader() {
        LoadingScreen.setLoadingBar(-1.0);
        LoadingScreen.setTaskText("Checking for addons");
        File folder = new File(System.getProperty("user.dir"), "addons");
        if (folder.isDirectory()) {
            API.println("Checking for addons...");
            if(folder.list().length > 0) {
                if (folder.list().length < 2) {
                    API.println("1 addon found!");
                } else {
                    API.println(String.format("%s addons found!", folder.list().length));
                }
                
                File[] listOfFiles = folder.listFiles();
                Integer num = 0;
                for (File file : listOfFiles) {
                    num++;
                    Double percent = (double)(((num * 100.0f) / folder.list().length) / 100);
                    if (file.isFile()) {
                        try {
                            LoadingScreen.setTaskText(String.format("Loading %s", file.getName()));
                            API.println(String.format("Loading %s...", file.getName()));
                            URLClassLoader child = new URLClassLoader(new URL[] {new URL("file:///" + file.getPath())});
                            Class.forName("addon.Plugin", true, child);
                            API.println(String.format("Finished loading %s!", file.getName()));
                        } catch(Exception e) {
                            API.println(e.toString());
                            Logger.error(String.format("Failed to load %s!", file.getName()));
                        }
                        LoadingScreen.setLoadingBar(percent);
                    }
                }
            } else {
                API.println("No mods found");
            }
        } else {
            folder.mkdir();
        }
        LoadingScreen.setLoadingBar(1.0);
    }
}