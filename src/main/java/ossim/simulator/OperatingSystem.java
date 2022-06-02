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

    // attribute to assign a unique ID to the new process(incremented every time a
    // process is created)
    static private long currentTime = 0L;
    // hashtable to lookup programs by arrival time(arrival time is the search key)
    static final private Hashtable<Long, ArrayList<String>> arrivingPrograms = new Hashtable<>();
    // hashtable to lookup resources by name(resource name is the search key)
    static final public int pageSizeBits = 2;
    static final private Frame[] physicalMemory = initializePhysicalMemory();
    static final public int logicalMemorySizeBits = 5;

    private OperatingSystem() {
        super();
    }

    public static Frame[] getPhysicalmemory() {
        return physicalMemory;
    }

    public static Scheduler getScheduler() {
        return (Scheduler) physicalMemory[0].getObjectAt(2);
    }

    @SuppressWarnings("unchecked")
    public static Hashtable<String, Mutex> getMutexes() {
        return (Hashtable<String, Mutex>) physicalMemory[0].getObjectAt(0);
    }

    public static int getNextProcessID() {
        return (Integer) physicalMemory[0].getObjectAt(1);
    }

    public static void incrementNextProcessID() {
        physicalMemory[0].setObjectAt(1, getNextProcessID() + 1);
    }

    @SuppressWarnings("unchecked")
    public static Hashtable<Integer, UserModeProcess> getProcesses() {
        return (Hashtable<Integer, UserModeProcess>) physicalMemory[0].getObjectAt(3);
    }

    private static Frame[] initializePhysicalMemory() {
        Frame[] frames = new Frame[40 / (1 << pageSizeBits)];
        frames[0] = new Frame(0);
        frames[0].setObjectAt(0, new Hashtable<>()); // getMutexes()
        frames[0].setObjectAt(1, 1); // nextProcessID
        frames[0].setObjectAt(2, new Scheduler()); // getScheduler()
        frames[0].setObjectAt(3, new Hashtable<>()); // processes
        File swapFolder = new File("swap");
        File[] files = swapFolder.listFiles((f) -> f.getName().endsWith(".mem"));
        if (files != null) {
            for (File file : files) {
                file.delete();
            }
        }
        return frames;
    }

    private static Frame findFreeFrame() throws SimulatorRuntimeException {
        long min = Long.MAX_VALUE;
        int minIndex = -1;
        for (int i = 1; i < physicalMemory.length; i++) {
            if (physicalMemory[i] == null) {
                physicalMemory[i] = new Frame(i);
                return physicalMemory[i];
            }
            if (physicalMemory[i].isFree()) {
                return physicalMemory[i];
            }
            if (physicalMemory[i].getLastUse() < min && !physicalMemory[i].isPCBFrame()) {
                min = physicalMemory[i].getLastUse();
                minIndex = i;
            }
        }
        if (minIndex == -1) {
            throw new SimulatorRuntimeException("Out of Memory");
        }
        Frame swappedOutFrame = physicalMemory[minIndex];
        swappedOutFrame.save();
        DisplayWindow.addSwapOut(swappedOutFrame.getProcessID(), swappedOutFrame.getPage(), minIndex);
        UserModeProcess process = getProcesses().get(swappedOutFrame.getProcessID());
        int[] pageTable = process.getPcb().getPageTable();
        pageTable[swappedOutFrame.getPage()] = -1;
        Frame frame = new Frame(minIndex);
        physicalMemory[minIndex] = frame;
        return frame;
    }

    // parses the program text file and creates a new process and tells the
    // getScheduler() to add it to the ready queue
    private static UserModeProcess launchProgram(String programPath)
            throws SimulatorSyntaxException, IOException, SimulatorRuntimeException {
        int page = (1 << (logicalMemorySizeBits - pageSizeBits)) - 1;
        int processID = getNextProcessID();
        incrementNextProcessID();
        Frame frame = allocateFrame(page, processID);
        UserModeProcess process = new UserModeProcess(programPath, new PCB(frame, processID));
        getProcesses().put(processID, process);
        ArrayList<Instruction> instructions = Parser.parseFile(programPath);
        for (int i = 0; i < instructions.size(); i++) {
            writeMemory(process, i, instructions.get(i));
        }
        writeMemory(process, (1 << logicalMemorySizeBits) - 1 - (1 << pageSizeBits), instructions.size());
        DisplayWindow.addMemoryTable(getPhysicalmemory());
        getScheduler().addNewProcess(process);
        return process;
    }

    private static synchronized Frame allocateFrame(int page, int processID) throws SimulatorRuntimeException {
        Frame frame = findFreeFrame();
        frame.setProcessID(processID);
        frame.setPage(page);
        return frame;
    }

    // schedule the next ready process to run and executes the next instruction
    private static void runCycle() {
        UserModeProcess process = getScheduler().schedule();
        if (process != null) {
            try {
                Instruction instruction = process.getNextInstruction();
                if (instruction == null) {
                    finishRunningProcess();
                    return;
                }
                DisplayWindow.printExecutingInstruction(process, instruction);
                instruction.execute(process);
                if (!process.hasInstructions()) {
                    finishRunningProcess();
                }
                DisplayWindow.addMemoryTable(physicalMemory);
            } catch (SimulatorRuntimeException e) {
                DisplayWindow.displayProcessErrorMessage(process, e.getMessage());
                finishRunningProcess();
                return;
            }
        }
    }

    // when a process finishes normally or due to an error, signals all the
    // resources it owns and moves it to the fiished queue
    private static void finishRunningProcess() {
        signalOwnedResources(getScheduler().getRunningProcess());
        getScheduler().finishRunningProcess();
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
        while (getScheduler().hasRunningPrograms() || !arrivingPrograms.isEmpty()) {
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
            } catch (SimulatorRuntimeException e) {
                DisplayWindow.printLaunchError(programPath, e.getMessage());
            }
        }
    }

    public static void input(String variableName) {
        UserModeProcess process = getScheduler().getRunningProcess();
        long startTime = currentTime;
        // because this is an IO Operation, the process is blocked
        getScheduler().blockRunningProcess();
        // run the IO Operation in a seperate thread to allow other processes to
        // continue execution
        Thread thread = new Thread(() -> {
            String text = DisplayWindow.askForInput(process);
            try {
                process.writeVariable(variableName, text);
            } catch (SimulatorRuntimeException e) {
                DisplayWindow.displayProcessErrorMessage(process, "input system call failed: " + e.getMessage());
                terminateBlockedProcess(process);
                return;
            }
            // When the IO Operation is completed, wakeup the process that started the
            // operation by adding it to the ready queue
            getScheduler().wakeUpProcess(process);
            long endTime = currentTime;
            DisplayWindow.printIOTime(process, "input", startTime, endTime);
        });
        thread.start();
    }

    public static void print(String text) {
        UserModeProcess process = getScheduler().getRunningProcess();
        long startTime = currentTime;
        // because this is an IO Operation, the process is blocked
        getScheduler().blockRunningProcess();
        // run the IO Operation in a seperate thread to allow other processes to
        // continue execution
        Thread thread = new Thread(() -> {
            DisplayWindow.printProcessOutput(process, text);
            // When the IO Operation is completed, wakeup the process that started the
            // operation by adding it to the ready queue
            getScheduler().wakeUpProcess(process);
            long endTime = currentTime;
            DisplayWindow.printIOTime(process, "print", startTime, endTime);
        });
        thread.start();
    }

    public static void readFile(String fileName, String outputVariableName) {
        UserModeProcess process = getScheduler().getRunningProcess();
        long startTime = currentTime;
        // because this is an IO Operation, the process is blocked
        getScheduler().blockRunningProcess();
        // run the IO Operation in a seperate thread to allow other processes to
        // continue execution
        Thread thread = new Thread(() -> {
            File file = new File(fileName);
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                try {
                    String st;
                    String result = "";
                    while ((st = br.readLine()) != null) {
                        result += st + "\n";
                    }
                    try {
                        process.writeVariable(outputVariableName, result);
                    } catch (SimulatorRuntimeException e) {
                        DisplayWindow.displayProcessErrorMessage(process,
                                "readFile system call failed: " + e.getMessage());
                        terminateBlockedProcess(process);
                        return;
                    }
                } finally {
                    br.close();
                }
                // When the IO Operation is completed, wakeup the process that started the
                // operation by adding it to the ready queue
                getScheduler().wakeUpProcess(process);
                long endTime = currentTime;
                DisplayWindow.printIOTime(process, "readFile", startTime, endTime);
            } catch (IOException e) {
                DisplayWindow.displayProcessErrorMessage(process, "readFile system call failed: " + e.getMessage());
                terminateBlockedProcess(process);
            }
        });
        thread.start();
    }

    // called when the IO operation fails to terminate the process and signal owned
    // getMutexes()
    private static void terminateBlockedProcess(UserModeProcess process) {
        signalOwnedResources(process);
        getScheduler().terminateBlockedProcess(process);
    }

    public static void writeFile(String fileName, String content) {
        UserModeProcess process = getScheduler().getRunningProcess();
        long startTime = currentTime;
        // because this is an IO Operation, the process is blocked
        getScheduler().blockRunningProcess();
        // run the IO Operation in a seperate thread to allow other processes to
        // continue execution
        Thread thread = new Thread(() -> {
            File file = new File(fileName);
            try {
                BufferedWriter br = new BufferedWriter(new FileWriter(file));
                try {
                    br.write(content);
                } finally {
                    br.close();
                }
                // When the IO Operation is completed, wakeup the process that started the
                // operation by adding it to the ready queue
                getScheduler().wakeUpProcess(process);
                long endTime = currentTime;
                DisplayWindow.printIOTime(process, "writeFile", startTime, endTime);
            } catch (IOException e) {
                DisplayWindow.displayProcessErrorMessage(process, "writeFile system call failed: " + e.getMessage());
                terminateBlockedProcess(process);
            }
        });
        thread.start();
    }

    private static Mutex getMutex(String name) {
        if (!getMutexes().containsKey(name)) {
            getMutexes().put(name, new Mutex());
        }
        return getMutexes().get(name);
    }

    public static void semWait(String name) {
        Mutex mutex = getMutex(name);
        Boolean result = mutex.wait(getScheduler().getRunningProcess());
        if (!result) {
            // wait could not acquire the mutex, so the process is blocked
            getScheduler().blockRunningProcess();
        }
    }

    public static void semSignal(String name) throws SimulatorRuntimeException {
        Mutex mutex = getMutex(name);
        UserModeProcess process = mutex.signal(getScheduler().getRunningProcess());
        if (process != null) {
            // wakeup the new owner of the mutex
            getScheduler().wakeUpProcess(process);
        }
    }

    // If the process finishes without calling semSignal, signal all the
    // getMutexes()
    // owned by the process
    public static void signalOwnedResources(UserModeProcess process) {
        for (Mutex m : getMutexes().values()) {
            if (process == m.getOwner()) {
                UserModeProcess processToWakeUp;
                try {
                    processToWakeUp = m.signal(process);
                } catch (SimulatorRuntimeException e) {
                    processToWakeUp = null;
                }
                if (processToWakeUp != null) {
                    getScheduler().wakeUpProcess(processToWakeUp);
                }
            }
        }
    }

    public static synchronized Object readMemory(UserModeProcess process, int logicalAddress) throws SimulatorRuntimeException {
        int[] pageTable = process.getPcb().getPageTable();
        int page = logicalAddress >>> pageSizeBits;
        int frameIndex = pageTable[page];
        Frame frame;
        if (frameIndex == -1) {
            try {
                frame = Frame.load(process.getProcessID(), page);
            } catch (SimulatorRuntimeException e) {
                return null;
            }
            frame = putFrameInPhysicalMemory(frame);
            pageTable[page] = frame.getFrameIndex();
        } else {
            frame = physicalMemory[frameIndex];
        }
        int offset = logicalAddress - (page << pageSizeBits);
        frame.setLastUse(currentTime);
        return frame.getObjectAt(offset);
    }

    public static synchronized void writeMemory(UserModeProcess process, int logicalAddress, Object value)
            throws SimulatorRuntimeException {
        int[] pageTable = process.getPcb().getPageTable();
        int page = logicalAddress >>> pageSizeBits;
        int frameIndex = pageTable[page];
        Frame frame;
        if (frameIndex == -1) {
            try {
                frame = Frame.load(process.getProcessID(), page);
                frame = putFrameInPhysicalMemory(frame);
            } catch (SimulatorRuntimeException e) {
                frame = allocateFrame(page, process.getProcessID());
            }
            pageTable[page] = frame.getFrameIndex();
        } else {
            frame = physicalMemory[frameIndex];
        }
        int offset = logicalAddress - (page << pageSizeBits);
        frame.setObjectAt(offset, value);
        frame.setLastUse(currentTime);
    }

    public static Frame putFrameInPhysicalMemory(Frame loadedFrame) throws SimulatorRuntimeException{
        Frame frame = allocateFrame(loadedFrame.getPage(), loadedFrame.getProcessID());
        for(int i = 0;i < (1 << pageSizeBits);i++){
            frame.setObjectAt(i, loadedFrame.getObjectAt(i));
        }
        DisplayWindow.addSwapIn(loadedFrame.getProcessID(), loadedFrame.getPage(), frame.getFrameIndex());
        return frame;
    }

}