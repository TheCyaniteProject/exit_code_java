package exitcode;

import java.io.*;
import java.net.URI;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;

// Internal dependancies: Main

class GameResourceLoader {

    //LoadFonts
    public static void loadGameFonts() {
        try {
            URI uri = Main.class.getResource("/resources/fonts/").toURI();
            boolean shouldCloseFileSystem = false;
            Path myPath;
            if (uri.getScheme().equals("jar")) {
                FileSystem fileSystem = FileSystems.newFileSystem(uri, Collections.<String, Object>emptyMap());
                myPath = fileSystem.getPath("/resources/fonts/");
                shouldCloseFileSystem = true;
            } else {
                myPath = Paths.get(uri);
            }
            Stream<Path> walk = Files.walk(myPath, 2);
            for (Iterator<Path> it = walk.iterator(); it.hasNext();){
                String extension = "";
                Path path = it.next();
                String fileName = path.getFileName().toString();
                int i = fileName.lastIndexOf('.');
                if (i >= 0) {
                    extension = fileName.substring(i + 1);
                }
                if (extension.equals("ttf")) {
                    java.awt.Font f = java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT, Main.class.getResourceAsStream("/resources/fonts/" + fileName));
                    Main.fonts.add("/resources/fonts/"+fileName);
                    Logger.message("System Font \"" + f.getName() + "\" found.");
                }
            }
            walk.close();
            if (shouldCloseFileSystem) {
                myPath.getFileSystem().close();
            }
        } catch (Exception e) {
            Logger.error("COULD NOT LOAD SYSTEM FONT!");
        }
    }

    
    // LoadPlayerFonts
    public static void loadPlayerFonts() {
        File dir2 = new File("resources/fonts");
        if (!dir2.isDirectory()) {
            Logger.message("Creating Directory resources/fonts");
            dir2.mkdirs();
        } else {
            File[] directoryListing2 = dir2.listFiles();
            if (directoryListing2 != null) {
                String extension2 = "";
                for (File child2 : directoryListing2) {
                    try {
                        int i = child2.toString().lastIndexOf('.');
                        if (i >= 0) {
                            extension2 = child2.toString().substring(i + 1);
                        }
                        if (extension2.equals("ttf")) {
                            java.awt.Font f2 = java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT, new FileInputStream(child2.toString()));
                            child2.toString();
                            Main.fonts.add(child2.toString());
                            Logger.message("Player Font \"" + f2.getName() + "\" found.");
                        }
                    } catch (Exception e2) {
                        Logger.error("COULD NOT LOAD PLAYER FONT!");
                    }
                }
            }
        }
    }

    //LoadWallpapers
    public static void loadGameWallpapers() {
        try {
            URI uri = Main.class.getResource("/resources/wallpapers/").toURI();
            Path myPath;
            boolean shouldCloseFileSystem = false;
            if (uri.getScheme().equals("jar")) {
                FileSystem fileSystem = FileSystems.newFileSystem(uri, Collections.<String, Object>emptyMap());
                myPath = fileSystem.getPath("/resources/wallpapers/");
                shouldCloseFileSystem = true;
            } else {
                myPath = Paths.get(uri);
            }
            Stream<Path> walk = Files.walk(myPath, 2);
            for (Iterator<Path> it = walk.iterator(); it.hasNext();){
                String extension1 = "";
                String fileName= it.next().getFileName().toString();
                int i = fileName.lastIndexOf('.');
                if (i >= 0) {
                    extension1 = fileName.substring(i + 1);
                }
                if (Arrays.asList("jpg", "png").contains(extension1)) {
                    Main.wallpapers.add("/resources/wallpapers/"+ fileName);
                    Logger.message("System Wallpaper \"" + fileName.replace("_", " ") + "\" found.");
                }
            }
            walk.close();
            if(shouldCloseFileSystem) {
                myPath.getFileSystem().close();
            }
        } catch (Exception e){
            Logger.error("COULD NOT LOAD SYSTEM WALLPAPER!");
        }
    }

    //LoadPlayerWallpapers
    public static void loadPlayerWallpapers() {
        File dir3 = new File("resources/wallpapers");
        if (!dir3.isDirectory()) {
            Logger.message("Creating Directory resources/wallpapers");
            dir3.mkdirs();
        } else {
            File[] directoryListing3 = dir3.listFiles();
            if (directoryListing3 != null) {
                String extension3 = "";
                for (File child3 : directoryListing3) {
                    try {
                        int i = child3.toString().lastIndexOf('.');
                        if (i >= 0) {
                            extension3 = child3.toString().substring(i + 1);
                        }
                        if (Arrays.asList("jpg", "png").contains(extension3)) {
                            Main.wallpapers.add(child3.toString());
                            Logger.message("Player Wallpaper \"" + child3.toString().replace("resources\\wallpapers\\", "").replace("_", " ") + "\" found.");
                        }
                    } catch (Exception e1) {
                        Logger.error("COULD NOT LOAD PLAYER WALLPAPER!");
                    }
                }
            }
        }
    }
}