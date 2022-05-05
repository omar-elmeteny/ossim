package ossim.simulator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

import ossim.commands.Command;
import ossim.exceptions.SimulatorRuntimeException;
import ossim.exceptions.SimulatorSyntaxException;

public class OperatingSystem {

    static final private Scheduler scheduler = new Scheduler();
    static private int nextProcessID = 1;
    static private int currentTime = 0;
    static final private Object outputSync = new Object();
    static final private Object fileSync = new Object();
    static final private Hashtable<Integer, ArrayList<String>> arrivingPrograms = new Hashtable<>();
    static final private Object inputSync = new Object();

    private OperatingSystem() {
        super();
    }

    public static Scheduler getScheduler() {
        return scheduler;
    }

    private static UserModeProcess launchProgram(String programPath) throws SimulatorSyntaxException, IOException {
        UserModeProcess process = new UserModeProcess(nextProcessID++, programPath);
        scheduler.addNewProcess(process);
        return process;
    }

    private static void runCycle() {
        UserModeProcess process = scheduler.schedule();
        if (process != null) {
            Command cmd = process.getNextCommand();
            if (cmd == null) {
                scheduler.finishRunningProcess();
                return;
            }
            DisplayWindow.printExecutingInstruction(process, cmd);
            try {
                cmd.execute(process);
            } catch (SimulatorRuntimeException e) {
                DisplayWindow.displayProcessErrorMessage(process, e.getMessage());
                scheduler.finishRunningProcess();
                return;
            }
            if (!process.hasCommands()) {
                scheduler.finishRunningProcess();
            }
        }
    }

    public static void addArrivingProgram(int time, String programPath) {
        ArrayList<String> list = arrivingPrograms.get(time);
        if (list == null) {
            list = new ArrayList<>();
            arrivingPrograms.put(time, list);
        }
        list.add(programPath);
    }

    public static void run() {
        while (scheduler.hasRunningPrograms() || !arrivingPrograms.isEmpty()) {
            launchArrivingPrograms();
            runCycle();
            currentTime++;
        }
    }

    private static void launchArrivingPrograms() {
        ArrayList<String> programs = arrivingPrograms.remove(currentTime);
        if (programs == null)
            return;
        for (String programPath : programs) {
            try {
                launchProgram(programPath);
            } catch (SimulatorSyntaxException e) {
                DisplayWindow.printLaunchError(programPath, e.getMessage());
            } catch (IOException e) {
                DisplayWindow.printLaunchError(programPath, e.getMessage());
            }
        }
    }

    public static void input(String variableName) {
        UserModeProcess process = scheduler.getRunningProcess();
        scheduler.blockRunningProcess();
        Thread thread = new Thread(() -> {
            synchronized(inputSync){
                String text = DisplayWindow.askForInput(process);
                process.writeVariable(variableName, text);
                scheduler.wakeUpProcess(process);
            }
        });
        thread.start();
    }

    public static void print(String text){
        UserModeProcess process = scheduler.getRunningProcess();
        scheduler.blockRunningProcess();
        Thread thread = new Thread(() -> {
            synchronized(outputSync){
                DisplayWindow.printProcessOutput(process, text);
                scheduler.wakeUpProcess(process);
            }
        });
        thread.start();
    }

    public static void readFile(String fileName, String outputVariableName){
        UserModeProcess process = scheduler.getRunningProcess();
        scheduler.blockRunningProcess();
        Thread thread = new Thread(() -> {
            synchronized(fileSync){
                File file = new File(fileName);
                try{
                    BufferedReader br = new BufferedReader(new FileReader(file));
                    try {
                        String st;
                        String result = "";
                        while ((st = br.readLine()) != null) {
                            result += st;
                        }
                        process.writeVariable(outputVariableName, result);
                    } finally {
                        br.close();
                    }
                }catch(IOException e) {
                    DisplayWindow.displayProcessErrorMessage(process, "readFile system call failed: " + e.getMessage());
                    scheduler.terminateBlockedProcess(process);
                } 
            }
        });
        thread.start();
    }

    public static void writeFile(String fileName, String content){
        UserModeProcess process = scheduler.getRunningProcess();
        scheduler.blockRunningProcess();
        Thread thread = new Thread(() -> {
            synchronized(fileSync){
                File file = new File(fileName);
                try{
                    BufferedWriter br = new BufferedWriter(new FileWriter(file));
                    try {
                        br.write(content);
                    } finally {
                        br.close();
                    }
                }catch(IOException e) {
                    DisplayWindow.displayProcessErrorMessage(process, "writeFile system call failed: " + e.getMessage());
                    scheduler.terminateBlockedProcess(process);
                } 
            }
        });
        thread.start();
    }
}