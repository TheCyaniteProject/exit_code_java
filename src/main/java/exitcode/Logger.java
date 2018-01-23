package exitcode;

public class Logger {

    public static void printSystemInfo() {
        Logger.messageCustom("INFO", "");
        Logger.messageCustom("INFO",
            String.format("OS: %s, Version: %s, Arch: %s | Java: %s, JRE: %s",
                System.getProperty("os.name"),
                System.getProperty("os.version"),
                System.getProperty("os.arch"),
                System.getProperty("java.version"),
                System.getProperty("java.runtime.version")
            )
        );
        Logger.messageCustom("INFO", "");
    }

    public static void message(String message) {
        System.out.println(String.format("[OUTPUT] %s", message));
    }

    public static void warn(String warningMessage) {
        System.err.println(String.format("[WARNING] %s", warningMessage));
    }

    public static void error(String errorMessage) {
        System.err.println(String.format("[ERROR] %s", errorMessage));
    }
    
    public static void messageCustom(String messageTitle, String message) {
        System.out.println(String.format("[%s] %s", messageTitle, message));
    }

    public static void errorCustom(String errorTitle, String errorMessage) {
        System.err.println(String.format("[%s] %s", errorTitle, errorMessage));
    }

    public static void debug(String debugText) {
        int callersLineNumber = Thread.currentThread().getStackTrace()[2].getLineNumber();
        String callersClassName = Thread.currentThread().getStackTrace()[2].getClassName();
        System.err.println(String.format("[DEBUG: %s:%s] %s", callersClassName, callersLineNumber, debugText));
    }
}