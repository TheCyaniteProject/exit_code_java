package exitcode;

import javafx.scene.control.Label;

// Internal dependancies: Console

public abstract class Command extends Thread {

    /**
     * This is where the Console Instance will be located after the command has run.
     */
    protected Console console;

    /**
     * This is where command arguments will be located after the command has run.
     */
    protected String body;

    /**
     * This is where the command was run from, specifically where you can output text to.
     * Please use Console.printText(termArea, text); to print to the console, otherwise
     * you risk wiping past output. (Maybe that's what you want)
     */
    protected Label termArea;

    /**
     * The name of the command (provided during initialization)
     */
    protected String command;

    /**
     * The description for the command showed in the help
     * Default value is "" (if no value is set, I don't want it to default to "null")
     */
    protected String desc = ""; // Default Value

    /**
     * Is this a debugging command..? Default: false
     */
    protected boolean debug = false; // Default Value

    /**
     * This is used for the executing of Commands.
     * It runs Commands in a seperate thread.
     * 
     * @see Runnable#run()
     * 
     * @param termArea This is a JavaFX Label that is being passed to the Command.
     * 
     * @param body This is the body of the command (everything after the first space).
     * It can hold arguments or whatever else the user passes to it.
     */
    public void runCommand(Label termArea, String body, Console console) {
        this.termArea = termArea;
        this.console = console;
        this.body = body;
        run();
    }

    @Override
    abstract public void run();

    /**
     * Returns the name of this command as String.
     * @return the name of the command
     */

    public String getCommandName() {
        return this.command;
    }

    /**
     * Returns the text description of this command as String.
     * @return the description of the command
     */

    public String getDescription() {
        return this.desc;
    }

    /**
     * @return Whether the command is for debugging or not - Default: false
     */

    public boolean isDebug() {
        return this.debug;
    }
}