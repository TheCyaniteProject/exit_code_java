package addon;

import exitcode.*;
import javafx.application.Platform;
import java.util.stream.IntStream;
import java.lang.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;

import java.io.*;
import javax.imageio.*;
import javafx.scene.image.*;
import javafx.scene.SnapshotParameters;
import javafx.embed.swing.SwingFXUtils;

/**
 * Created by JuliusFreudenberger & Cyanite on 19.08.2017.
 */

public class Plugin {
    static {
        exitcode.Console.addGlobalCommand(new Snap());
    }
}

class Snap extends Command {

    private static String snapshot = null;
    private static Integer delay = 0;
    private static Boolean doRun = true;
    private static Thread thread;

    Snap() {
        command = "snap";
        desc = "Snaps a screenshot (pass a number as a delay in second i.e: \"snap 5\" for a five second delay)";
    }

    @Override
    public void run() {
        doRun = true;
        delay = 0;
        if (!(body.trim().equals("null"))) {
            try {
                delay = Integer.valueOf(body.trim());
            } catch (NumberFormatException e) {
                Platform.runLater(() -> { exitcode.Console.printText(termArea, String.format("Unknown argument(s): \"%s\"", body)); });
                doRun = false;
            }
        }
        if (doRun) {
            thread = new Thread(() -> {
                if (delay >= 1) {
                    Platform.runLater(() -> { exitcode.Console.printText(termArea, String.format("Taking screenshot in %s seconds..", Snap.delay)); });
                    IntStream.range(0, delay).forEachOrdered(n -> {
                        Platform.runLater(() -> { exitcode.Console.printText(termArea, String.format("%s..", (Snap.delay - n))); });
                        try { Thread.sleep(1000); }catch(InterruptedException ignored){}
                    });
                } else {
                    Platform.runLater(() -> { exitcode.Console.printText(termArea, "Taking screenshot.."); });
                }
                Platform.runLater(() -> { exitcode.Console.printText(termArea, "Cheese!"); });
                Platform.runLater(() -> { takeSnap(); });
                Platform.runLater(() -> {
                    if (!(Snap.snapshot == null)) {
                        exitcode.Console.printText(termArea, String.format("Screenshot saved to: %s", Snap.snapshot));
                    } else {
                        exitcode.Console.printText(termArea, "Couldn't take Screenshot :(");
                    }
                });
                return;
            });
            thread.setDaemon(true);
            thread.start();
        }
    }

    public void takeSnap() {
        Path currentPath = Paths.get(System.getProperty("user.dir"));
        Path filePath = Paths.get(currentPath.toString(), "screenshots", "screenshot_" + (new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new java.util.Date())) + ".png");
        Snap.snapshot = filePath.toString();
        WritableImage image = Main.main_primaryStage.getScene().snapshot(null);
        
        // TODO: probably use a file chooser here
        File file = new File(Snap.snapshot);
        File dir = new File(file.getParent());
        if (!dir.isDirectory()) {
            dir.mkdir();
        }

        try {
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
            API.println("Screenshot saved to: " + Snap.snapshot);
        } catch (IOException e) {
            // TODO: handle exception here
        }
    }
}