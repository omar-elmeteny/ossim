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
    static final private Hashtable<Integer, ArrayList<String>> arrivingPrograms = new Hashtable<>();
    static final private Hashtable<String,Mutex> mutexes = new Hashtable<>();

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
                finishRunningProcess();
                return;
            }
            DisplayWindow.printExecutingInstruction(process, cmd);
            try {
                cmd.execute(process);
            } catch (SimulatorRuntimeException e) {
                DisplayWindow.displayProcessErrorMessage(process, e.getMessage());
                finishRunningProcess();
                return;
            }
            if (!process.hasCommands()) {
                finishRunningProcess();
            }
        }
    }

    private static void finishRunningProcess() {
        releaseOwnedResources(scheduler.getRunningProcess());
        scheduler.finishRunningProcess();
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
            String text = DisplayWindow.askForInput(process);
            process.writeVariable(variableName, text);
            scheduler.wakeUpProcess(process);
        });
        thread.start();
    }

    public static void print(String text){
        UserModeProcess process = scheduler.getRunningProcess();
        scheduler.blockRunningProcess();
        Thread thread = new Thread(() -> {
            DisplayWindow.printProcessOutput(process, text);
            scheduler.wakeUpProcess(process);
        });
        thread.start();
    }

    public static void readFile(String fileName, String outputVariableName){
        UserModeProcess process = scheduler.getRunningProcess();
        scheduler.blockRunningProcess();
        Thread thread = new Thread(() -> {
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
                terminateBlockedProcess(process);
            } 
        });
        thread.start();
    }

    private static void terminateBlockedProcess(UserModeProcess process) {
        releaseOwnedResources(process);
        scheduler.terminateBlockedProcess(process);
    }

    public static void writeFile(String fileName, String content){
        UserModeProcess process = scheduler.getRunningProcess();
        scheduler.blockRunningProcess();
        Thread thread = new Thread(() -> {
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
                terminateBlockedProcess(process);
            } 
        });
        thread.start();
    }

    private static Mutex getMutex(String name){
        if(!mutexes.contains(name)){
            mutexes.put(name, new Mutex());
        }
        return mutexes.get(name);
    }

    public static void semWait(String name){
        Mutex mutex = getMutex(name);
        Boolean result = mutex.wait(scheduler.getRunningProcess());
        if(!result){
            scheduler.blockRunningProcess();
        }
    }

    public static void semSignal(String name) throws SimulatorRuntimeException{
        Mutex mutex = getMutex(name);
        UserModeProcess process = mutex.signal(scheduler.getRunningProcess());
        if(process != null){
            scheduler.wakeUpProcess(process);
        }
    }

    public static void releaseOwnedResources(UserModeProcess process){
        for(Mutex m : mutexes.values()){
            if(process == m.getOwner()){
                UserModeProcess processToWakeUp = m.release();
                if(processToWakeUp != null){
                    scheduler.wakeUpProcess(processToWakeUp);
                }
            }
        }
    }
}