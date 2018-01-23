package exitcode;

import javafx.scene.control.Label;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import exitcode.FileNavigator;

// Internal dependancies: Command, FileNavigator

public class Console {

    private static Map<String, Command> globalCommands = new TreeMap<>();
    private FileNavigator fileNavigator;
    private Map<String, Command> privateCommands = new TreeMap<>();
    private boolean rootPriv = false;
    private boolean debug = false;
    private boolean lock = false;
    private String localUserInstance = LoginScreen.USERNAME.toLowerCase();

    public Console(String defaultDirectory, FileNavigator fileNavigator) { // Temporary
        
        this.fileNavigator = fileNavigator;
        this.fileNavigator.setDirectory(defaultDirectory);

    }

    public void setLocalUser(String username) {
        this.localUserInstance = username;
    }

    public String getLocalUser() {
        return this.localUserInstance;
    }

    public FileNavigator getNavigator() {
        return this.fileNavigator;
    }

    /**
     * Sets the value of this.lock
     *
     * @param mode Sets the lock state (true/false) in this console.
     * @author Allison Smith (Cyanite)
     */
    public void setLock(boolean mode) {
        this.lock = mode;
    }

    /**
     * Returns the value of this.lock
     *
     * @return true if console is locked, false if otherwise.
     * @author Allison Smith (Cyanite)
     */
    public boolean locked() {
        return this.lock;
    }

    /**
     * Sets the value of this.debug
     *
     * @param mode Sets the debug mode state (true/false) in this console.
     * @author Allison Smith (Cyanite)
     */
    public void setDebug(boolean mode) {
        this.debug = mode;
    }

    /**
     * Returns the value of this.debug
     *
     * @return true if console is in debug mode, false if otherwise.
     * @author Allison Smith (Cyanite)
     */
    public boolean isDebug() {
        return this.debug;
    }

    /**
     * Adds a new command to the Map of globalCommands.
     *
     * @param command The command to add.
     * @author JuliusFreudenberger
     */
    public static void addGlobalCommand(Command command) {
        globalCommands.putIfAbsent(command.getCommandName(), command);
    }

    /**
     * Checks if a command exists, and returns it's value.
     *
     * @param commandName The name of the command to check.
     * @return Command if commandName is in the globalCommands Map
     * @throws NullPointerException if the command does not exist, or if the passed value is null.
     * @author Allison Smith (Cyanite)
     */
    public static Command getGlobalCommand(String commandName) throws NullPointerException {
        if (globalCommands.containsKey(commandName)) {
            return globalCommands.get(commandName);
        }
        throw new NullPointerException("The given command does not exist!");
    }

    /**
     * Returns a collection of all global commandNames.
     *
     * @return the values of the global commands
     * @author JuliusFreudenberger
     */
    public static Collection<Command> getGlobalCommands() {
        return globalCommands.values();
    }

    /**
     * Adds a new command to the Map of globalCommands.
     *
     * @param command Command type object.
     * @throws NullPointerException if the command does not exist.
     * @author JuliusFreudenberger
     */
    public static void setGlobalCommand(Command command) throws NullPointerException {
        globalCommands.replace(command.getCommandName(), command);
    }

    /**
     * Removes a command from the globalCommands Map.
     *
     * @param commandName The name of the command to remove.
     * @throws NullPointerException if the provided String is null.
     * @author Allison Smith (Cyanite)
     */
    public static void removeGlobalCommand(String commandName) throws NullPointerException {
        globalCommands.remove(commandName);
    }


    /**
     * Part of the function to execute a Command.
     *
     * @param input    The command to run.
     * @param termArea The JavaFX Label to output to.
     * @param body     The body of the command. (Everything originally after the first space)
     * @return true if the Command ran successfully, false otherwise.
     * @author Allison Smith (Cyanite)
     * @see exitcode.Console#exeCommand(String, Label, String)
     * @see exitcode.Console#exePrivate(String, Label, String)
     */
    private boolean exeGlobal(String input, Label termArea, String body) {
        if (globalCommands.containsKey(input)) {
            Command command = globalCommands.get(input);
            if (!(command == null)) {
                command.runCommand(termArea, body, this);
                return true;
            }
        }
        return false;
    }

    /**
     * A simple method for printing text to a terminal.
     * 
     * @param termArea The Label to print to.
     * 
     * @param text The String to print.
     */
    public static void printText(Label termArea, String text) {
        termArea.setText(String.format("%s%n%s", termArea.getText(), text));
    }

    /**
     * Sets the value of this.rootPriv
     *
     * @param permission Sets the state of root privileges (true/false) in this console.
     * @author Allison Smith (Cyanite)
     */
    public void setRootPrivs(boolean permission) {
        this.rootPriv = permission;
    }

    /**
     * Returns the value of this.rootPriv
     *
     * @return true if console "has root privileges", false if otherwise.
     * @author Allison Smith (Cyanite)
     */
    public boolean hasRootPrivs() {
        return this.rootPriv;
    }

    /**
     * "Simply" runs a command. Yep.
     *
     * @param input    The command String to parse and run. i.e: "echo Hello world!"
     * @param ps1      The ps1 to be inserted when outputting to the console. i.e: "root@system:~# "
     * @param termArea (Ohh, boy!) A JavaFX label to output to. This serves as the Terminal "log" per-say.
     * @author JuliusFreudenberger
     * @author Allison Smith (Cyanite)
     * @see exitcode.Console#formatCommand(String)
     * @see exitcode.Console#checkCommand(String)
     * @see exitcode.Console#exeCommand(String, Label, String)
     * @see exitcode.Console#printText(Label, String)
     */
    public void runCommand(String input, String ps1, Label termArea) {
        ArrayList<String> output = formatCommand(input);
        String command = output.get(0);
        String body = output.get(1);
        printText(termArea, String.format("%n%s%s", ps1, input));
        if (checkCommand(command)) {
            exeCommand(command, termArea, body);
        } else {
            if (!(command.trim().equals(""))) {
                printText(termArea, ("\"" + command + "\" is not a valid command."));
            }
        }
    }

    /**
     * Executes the Command. First, checks if exePrivate() was able to run the command.
     * If not, then runs exeGlobal(). This way, private commands overwrite global commands
     * but private commands only exist in a single Console instance.
     *
     * @param input    The command to run.
     * @param termArea The JavaFX Label to output to.
     * @param body     The body of the command. (Everything originally after the first space)
     * @author Allison Smith (Cyanite)
     * @see exitcode.Console#exePrivate(String, Label, String)
     * @see exitcode.Console#exeGlobal(String, Label, String)
     */
    private void exeCommand(String input, Label termArea, String body) {
        if (!exePrivate(input, termArea, body)) {
            exeGlobal(input, termArea, body);
        }
    }

    /**
     * Part of the function to execute a Command.
     *
     * @param commandName The command to run.
     * @param termArea    The JavaFX Label to output to.
     * @param body        The body of the command. (Everything originally after the first space)
     * @return true if the Command ran successfully, false otherwise.
     * @author Allison Smith (Cyanite)
     * @see exitcode.Console#exeCommand(String, Label, String)
     * @see exitcode.Console#exeGlobal(String, Label, String)
     */
    private boolean exePrivate(String commandName, Label termArea, String body) {
        if (this.privateCommands.containsKey(commandName)) {
            Command command = this.privateCommands.get(commandName);
            if (!(command == null)) {
                command.runCommand(termArea, body, this);
                return true;
            }
        }
        return false;
    }

    /**
     * Adds a new command to the Map of privateCommands.
     *
     * @param command The command to add.
     * @author JuliusFreudenberger
     */
    public void addPrivateCommand(Command command) {
        this.privateCommands.putIfAbsent(command.getCommandName(), command);
    }

    /**
     * Checks if a command exists, and returns it's value.
     *
     * @param commandName The name of the command to check.
     * @return Command if commandName is in the privateCommands Map
     * @throws NullPointerException if the command does not exist, or if the passed value is null.
     * @author Allison Smith (Cyanite)
     */
    public Command getPrivateCommand(String commandName) throws NullPointerException {
        if (this.privateCommands.containsKey(commandName)) {
            return this.privateCommands.get(commandName);
        }
        throw new NullPointerException("The specified command does not exist!");
    }

    /**
     * Returns a Set of all private commandNames.
     *
     * @return the keySet of the private commands
     * @author JuliusFreudenberger
     */
    public Collection<Command> getPrivateCommands() {
        return privateCommands.values();
    }

    public void setPrivateCommand(Command command) throws NullPointerException {
        this.privateCommands.replace(command.getCommandName(), command);
    }

    public void removePrivateCommand(String commandName) throws NullPointerException {
        this.privateCommands.remove(commandName);
    }

    private boolean checkCommand(String commandName) {
        if (checkPrivate(commandName)) {
            return true;
        } else {
            return checkGlobal(commandName);
        }
    }

    private boolean checkPrivate(String commandName) {
        if (this.privateCommands.containsKey(commandName)) {
            if (this.privateCommands.get(commandName).isDebug()) {
                return this.isDebug();
            } else {
                return true;
            }
        } else {
            return false;
        }
    }
    
    /**
     * Checks if a Global command exists.
     *
     * @param commandName The name of the command to check for.
     * @return true if the command exists, false otherwise.
     * @author Allison Smith (Cyanite)
     */
    private boolean checkGlobal(String commandName) {
        if (globalCommands.containsKey(commandName)) {
            if (globalCommands.get(commandName).isDebug()) {
                return this.isDebug();
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    private ArrayList<String> formatCommand(String commandName) {
        String command;
        String body;
        ArrayList<String> output = new ArrayList<>();
        if (!commandName.contains(" ")) {
            command = commandName.trim();
            body = "null";
        } else {
            command = commandName.split(" ", 2)[0];
            body = commandName.split(" ", 2)[1];
            if (body.trim().equals("")) {
                body = "null";
            }
        }
        output.add(command);
        output.add(body);
        return output;
    }
        
}