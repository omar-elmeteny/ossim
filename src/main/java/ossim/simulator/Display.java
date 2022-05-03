package ossim.simulator;

import ossim.commands.Command;

public class Display {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";
    

    public static void printExecutingInstruction(UserModeProcess process, Command cmd){
        System.out.println(ANSI_BLUE + "PID: " + ANSI_RESET + process.getProcessID() + " ," + ANSI_BLUE + " CMD: " + ANSI_RESET + cmd.toString());
    }

    public static void printAskingForInput(UserModeProcess process){
        System.out.print(ANSI_CYAN + "Enter input for PID " + process.getProcessID() + ": " + ANSI_RESET);
    }

    public static void printProcessOutput(UserModeProcess process, String output){
        System.out.println(ANSI_PURPLE + "Output from PID " + process.getProcessID() + ": " + ANSI_RESET + output);
    }

    public static void printProcess(UserModeProcess process){
        System.out.println(ANSI_BLUE + "Process created with PID " + ANSI_RESET + process.getProcessID() + ANSI_BLUE + " from " + ANSI_RESET + process.getProgramPath());
    }
}
