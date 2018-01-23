package addon;

import exitcode.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Set;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Created by JuliusFreudenberger & Cyanite on 19.08.2017.
 */
public class Plugin {
    static {
        Console.addGlobalCommand(new Echo());
        Console.addGlobalCommand(new Help());
        Console.addGlobalCommand(new Clear());
        Console.addGlobalCommand(new Pid());
        Console.addGlobalCommand(new Kill());
        Console.addGlobalCommand(new Cat());
        Console.addGlobalCommand(new Shutdown());
        Console.addGlobalCommand(new Debug());
        Console.addGlobalCommand(new DebugTest());
    }
}

class Echo extends Command {

    Echo() {
        command = "echo";
        desc = "Prints text into the terminal";
    }

    @Override
    public void run() {
        Console.printText(termArea, body);
    }
}

class Help extends Command {

    Help() {
        command = "help";
        desc = "Shows all commands and their description";
    }

    @Override
    public void run() {
        StringBuilder output = new StringBuilder();
        Console.getGlobalCommands().forEach((command -> {
            if (command.isDebug()) {
                if (console.isDebug()) {
                    output.append(command.getCommandName()).append(" - ").append(command.getDescription()).append('\n');
                }
            } else {
                output.append(command.getCommandName()).append(" - ").append(command.getDescription()).append('\n');
            }
        }));
        output.setLength(output.length() - 1); // Removes the last NewLine character.
        Console.printText(termArea, output.toString());
    }
}

class Clear extends Command {

    Clear() {
        command = "clear";
        desc = "Clears the console";
    }

    @Override
    public void run() {
        termArea.setText("");
    }
}

class Pid extends Command {

    Pid() {
        command = "pid";
        desc = "Shows a list of all running processes";
    }

    @Override
    public void run() {
        StringBuilder pidList = new StringBuilder();
        Set<Integer> keys = Apps.apps.keySet();
        for (Integer key : keys) {
            pidList.append("\n   ").append(key).append(": ").append(Apps.apps.get(key).get(1));
        }
        Console.printText(termArea, pidList.toString());
    }
}

class Kill extends Command {

    Kill() {
        command = "kill";
        desc = "Kills a process. You need to pass it's PID";
    }

    @Override
    public void run() {
        if (body.equals("null")) {
            Console.printText(termArea, "kill: Please enter a Process ID(PID)");
        } else {
            boolean value = false;
            Set<Integer> keys = Apps.apps.keySet();
            for (Integer key : keys) {
                if (key.equals(Integer.valueOf(body))) {
                    value = true;
                }
            }
            if (value) {
                Apps.killCommand(Integer.valueOf(body));
            } else {
                Console.printText(termArea, "kill: Process does not exist");
            }
        }
    }
}


class Cat extends Command {
    
    Cat() {
        command = "cat";
        desc = "Copies a file's content to standard output";
    }

    @Override
    public void run() {
        String path = console.getNavigator().getCurrentDirectory();
        Document doc = ExitParser.getDocFromZip(console.getNavigator().getSystem());
        NodeList dataList = doc.getElementsByTagName("info");

        Node node = console.getNavigator().getDirectory(path);

        
        if (body.trim().startsWith(ExitParser.getAttributeValue(dataList, "top", "dir"))) {
            if (console.getNavigator().checkFile(body.trim())) {
                Console.printText(termArea, console.getNavigator().readStringFromFile(console.getNavigator().getFile(body.trim())));
            }
        } else {
            if (!path.endsWith("/")) {
                body = path + "/" + body.trim();
            } else {
                body = path + body.trim();
            }
            if (console.getNavigator().checkFile(body.trim())) {
                Console.printText(termArea, console.getNavigator().readStringFromFile(console.getNavigator().getFile(body.trim())));
            } else {
                Console.printText(termArea, "cat: Could not find the file specified");
            }
        }

        console.getNavigator().setDirectory(path); // To address a bug there the directory would randomly change after the command runs

    }

}

class Shutdown extends Command {

    Shutdown() {
        command = "shutdown";
        desc = "Brings the system down";
    }

    @Override
    public void run() {
        Desktop.shutdown();
    }
}

class Logout extends Command {

    Logout() {
        command = "logout";
        desc = "Logs out of the current session";
    }

    @Override
    public void run() {
        Desktop.logout();
    }
}

class Debug extends Command {

    Debug() {
        command = "debug";
        desc = "Changes the mode of debugging";
    }

    @Override
    public void run() {
        console.setDebug(!console.isDebug());
        Console.printText(termArea, "debug: " + console.isDebug());
    }
}

class DebugTest extends Command {
    
    DebugTest() {
            command = "debugtest";
            desc = "A simple test for debugging";
            debug = true;
        }
    
        @Override
        public void run() {
            ArrayList<String> list = new ArrayList<String>();
            list.addAll(Arrays.asList(
                "Supercalifragilisticexpialidocious!",
                "Potato!",
                "Tickity, Tock.",
                "Woah. Dude.",
                ">:DDDDDDD",
                "Pink Fluffy Uni- Oh, didn't see you there. :o",
                "Ah-SQUEEEEEEEEEEEEEEEEEEEK! :D",
                ";3",
                "[ACCESS DENIED]                 ;3",
                "Uhhh..... Hi.",
                "YOU DON KNO THE POWARR - OF THA DARK SIDEE",
                "\"You really thought I was, - what you were looking for.\" Ooohhhhhh~ - Ooooohhhhhh~ \"Guess I got in your head. - You just got BAIT-ED!\""
            ));

            String random = list.get(new Random().nextInt(list.size()));
            Console.printText(termArea, random);
        }
    }