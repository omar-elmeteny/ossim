package ossim.simulator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

import ossim.instructions.Instruction;
import ossim.view.DisplayWindow;
import ossim.exceptions.SimulatorRuntimeException;
import ossim.exceptions.SimulatorSyntaxException;

public class OperatingSystem {

    static final private Scheduler scheduler = new Scheduler();
    // attribute to assign a unique ID to the new process(incremented every time a process is created)
    static private int nextProcessID = 1;
    static private long currentTime = 0L;
    // hashtable to lookup programs by arrival time(arrival time is the search key)
    static final private Hashtable<Long, ArrayList<String>> arrivingPrograms = new Hashtable<>();
    // hashtable to lookup resources by name(resource name is the search key)
    static final private Hashtable<String,Mutex> mutexes = new Hashtable<>();

    private OperatingSystem() {
        super();
    }

    public static Scheduler getScheduler() {
        return scheduler;
    }

    // parses the program text file and creates a new process and tells the scheduler to add it to the ready queue
    private static UserModeProcess launchProgram(String programPath) throws SimulatorSyntaxException, IOException {
        UserModeProcess process = new UserModeProcess(nextProcessID++, programPath);
        scheduler.addNewProcess(process);
        return process;
    }

    // schedule the next ready process to run and executes the next instruction
    private static void runCycle() {
        UserModeProcess process = scheduler.schedule();
        if (process != null) {
            Instruction instruction = process.getNextInstruction();
            if (instruction == null) {
                finishRunningProcess();
                return;
            }
            DisplayWindow.printExecutingInstruction(process, instruction);
            try {
                instruction.execute(process);
            } catch (SimulatorRuntimeException e) {
                DisplayWindow.displayProcessErrorMessage(process, e.getMessage());
                finishRunningProcess();
                return;
            }
            if (!process.hasInstructions()) {
                finishRunningProcess();
            }
        }
    }

    // when a process finishes normally or due to an error, signals all the resources it owns and moves it to the fiished queue
    private static void finishRunningProcess() {
        signalOwnedResources(scheduler.getRunningProcess());
        scheduler.finishRunningProcess();
    }

    // add a program to launched at the specified time
    public static void addArrivingProgram(long time, String programPath) {
        ArrayList<String> list = arrivingPrograms.get(time);
        if (list == null) {
            list = new ArrayList<>();
            arrivingPrograms.put(time, list);
        }
        list.add(programPath);
    }

    // keeps running the programs until there are no programs to run
    public static void run() {
        while (scheduler.hasRunningPrograms() || !arrivingPrograms.isEmpty()) {
            launchArrivingPrograms();
            runCycle();
            currentTime++;
        }
    }

    // launch all programs that should start at the current time
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
        // because this is an IO Operation, the process is blocked
        scheduler.blockRunningProcess();
        // run the IO Operation in a seperate thread to allow other processes to continue execution
        Thread thread = new Thread(() -> {
            String text = DisplayWindow.askForInput(process);
            process.writeVariable(variableName, text);
            // When the IO Operation is completed, wakeup the process that started the operation by adding it to the ready queue
            scheduler.wakeUpProcess(process);
        });
        thread.start();
    }

    public static void print(String text){
        UserModeProcess process = scheduler.getRunningProcess();
        // because this is an IO Operation, the process is blocked
        scheduler.blockRunningProcess();
        // run the IO Operation in a seperate thread to allow other processes to continue execution
        Thread thread = new Thread(() -> {
            DisplayWindow.printProcessOutput(process, text);
            // When the IO Operation is completed, wakeup the process that started the operation by adding it to the ready queue
            scheduler.wakeUpProcess(process);
        });
        thread.start();
    }

    public static void readFile(String fileName, String outputVariableName){
        UserModeProcess process = scheduler.getRunningProcess();
        // because this is an IO Operation, the process is blocked
        scheduler.blockRunningProcess();
        // run the IO Operation in a seperate thread to allow other processes to continue execution
        Thread thread = new Thread(() -> {
            File file = new File(fileName);
            try{
                BufferedReader br = new BufferedReader(new FileReader(file));
                try {
                    String st;
                    String result = "";
                    while ((st = br.readLine()) != null) {
                        result += st + "\n";
                    }
                    process.writeVariable(outputVariableName, result);
                } finally {
                    br.close();
                }
                // When the IO Operation is completed, wakeup the process that started the operation by adding it to the ready queue
                scheduler.wakeUpProcess(process);
            }catch(IOException e) {
                DisplayWindow.displayProcessErrorMessage(process, "readFile system call failed: " + e.getMessage());
                terminateBlockedProcess(process);
            } 
        });
        thread.start();
    }

    // called when the IO operation fails to terminate the process and signal owned mutexes
    private static void terminateBlockedProcess(UserModeProcess process) {
        signalOwnedResources(process);
        scheduler.terminateBlockedProcess(process);
    }

    public static void writeFile(String fileName, String content){
        UserModeProcess process = scheduler.getRunningProcess();
        // because this is an IO Operation, the process is blocked
        scheduler.blockRunningProcess();
        // run the IO Operation in a seperate thread to allow other processes to continue execution
        Thread thread = new Thread(() -> {
            File file = new File(fileName);
            try{
                BufferedWriter br = new BufferedWriter(new FileWriter(file));
                try {
                    br.write(content);
                } finally {
                    br.close();
                }
                // When the IO Operation is completed, wakeup the process that started the operation by adding it to the ready queue
                scheduler.wakeUpProcess(process);
            }catch(IOException e) {
                DisplayWindow.displayProcessErrorMessage(process, "writeFile system call failed: " + e.getMessage());
                terminateBlockedProcess(process);
            } 
        });
        thread.start();
    }

    private static Mutex getMutex(String name){
        if(!mutexes.containsKey(name)){
            mutexes.put(name, new Mutex());
        }
        return mutexes.get(name);
    }

    public static void semWait(String name){
        Mutex mutex = getMutex(name);
        Boolean result = mutex.wait(scheduler.getRunningProcess());
        if(!result){
            // wait could not acquire the mutex, so the process is blocked
            scheduler.blockRunningProcess();
        }
    }

    public static void semSignal(String name) throws SimulatorRuntimeException{
        Mutex mutex = getMutex(name);
        UserModeProcess process = mutex.signal(scheduler.getRunningProcess());
        if(process != null){
            // wakeup the new owner of the mutex
            scheduler.wakeUpProcess(process);
        }
    }

    // If the process finishes without calling semSignal, signal all the mutexes owned by the process
    public static void signalOwnedResources(UserModeProcess process){
        for(Mutex m : mutexes.values()){
            if(process == m.getOwner()){
                UserModeProcess processToWakeUp;
                try {
                    processToWakeUp = m.signal(process);
                } catch (SimulatorRuntimeException e) {
                    processToWakeUp = null;
                }
                if(processToWakeUp != null){
                    scheduler.wakeUpProcess(processToWakeUp);
                }
            }
        }
    }
}