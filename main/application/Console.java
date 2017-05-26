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

// This is just a few command tests

public class Console {

    public static void runCommand(String input, String username, Label termArea) {
        String command = new String();
        String body = new String();
        if(!input.contains(" ")){
            command = input.trim();
            body    = "null";
        }
        else
        {
            command = input.split(" ", 2)[0];
            body    = input.split(" ", 2)[1];
            if(body.trim().equals("")) {
                body = "null";
            }
        }
        if(termArea.getText().trim().equals("")) {
            termArea.setText("   " + username + input);
        }
        else
        {
            termArea.setText(termArea.getText() + "\n   " + username + input);
        }
        if(!Arrays.asList("echo", "clear", "help").contains(command)) {
            termArea.setText(termArea.getText() + "\n   \"" + command + "\" is not a valid command.\n");
        }
        else
        {   
            if (command.trim().equals("help")) {
                termArea.setText(termArea.getText() + "\n   help - Shows a list of all commands"
                                +"\n   echo - Prints text to console"
                                +"\n   clear - Clears the console\n");
            }
            else if(command.trim().equals("echo")) {
                if(!body.equals("null")) {
                    termArea.setText(termArea.getText() + "\n   " + body + "\n");
                }
                else
                {
                    termArea.setText(termArea.getText() + "\n");
                }
            }
            else if (command.trim().equals("clear")) {
                termArea.setText("");
            }
        } 

    }
}