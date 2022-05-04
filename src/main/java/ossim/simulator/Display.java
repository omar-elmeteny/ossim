package ossim.simulator;

import ossim.commands.Command;

public class Display {

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_PURPLE = "\u001B[35m";
    private static final String ANSI_CYAN = "\u001B[36m";

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

    public static void printQueues(Scheduler scheduler){
        if(scheduler.getRunningProcess() != null){
            System.out.print(ANSI_GREEN + "Running Process: " + ANSI_BLUE + "PID: " + ANSI_RESET + scheduler.getRunningProcess().getProcessID() + " ");
        }
        System.out.print(ANSI_GREEN + "Ready Queue: " + ANSI_RESET);
        boolean flag = false;
        for(UserModeProcess process : scheduler.getReadyQueue()){
            if(flag)
                System.out.print(", ");
            else
                flag = true;    
            System.out.print(ANSI_BLUE + "PID: " + ANSI_RESET + process.getProcessID());
        }
        System.out.print(" ");
        System.out.print(ANSI_GREEN + "Blocked Queue: " + ANSI_RESET);
        flag = false;
        for(UserModeProcess process : scheduler.getBlockedProcesses()){
            if(flag)
                System.out.print(", ");
            else
                flag = true;    
            System.out.print(ANSI_BLUE + "PID: " + ANSI_RESET + process.getProcessID());
        }
        System.out.print(" ");
        System.out.print(ANSI_GREEN + "Finished Queue: " + ANSI_RESET);
        flag = false;
        for(UserModeProcess process : scheduler.getFinishedProcesses()){
            if(flag)
                System.out.print(", ");
            else
                flag = true;    
            System.out.print(ANSI_BLUE + "PID: " + ANSI_RESET + process.getProcessID());
        }
        System.out.println();
    }

    public static void displayProcessErrorMessage(UserModeProcess process, String errorMessage){
        System.out.println(ANSI_RED + "Error in Process: " + ANSI_BLUE + "PID: " + ANSI_RESET + process.getProcessID() + " " + ANSI_RED + errorMessage + ANSI_RESET);
    }

    public static void printLaunchError(String programPath, String errorMessage) {
        System.out.println(ANSI_RED + "Failed to launch program: " + ANSI_BLUE + "path " + ANSI_RESET + programPath + " " + ANSI_RED + errorMessage + ANSI_RESET);
    }

    public static void printProcessState(UserModeProcess process, ProcessState oldProcessState){
        System.out.println(ANSI_YELLOW + "State change " + ANSI_BLUE + "PID: " + ANSI_RESET + process.getProcessID() + " " + ANSI_PURPLE + oldProcessState + ANSI_RESET + " -> " + ANSI_GREEN + process.getState() + ANSI_RESET);
    }
}
