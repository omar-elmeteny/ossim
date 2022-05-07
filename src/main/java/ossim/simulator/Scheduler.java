package ossim.simulator;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import ossim.view.DisplayWindow;

public class Scheduler {
    
    // Represents the number of instructions in the fixed time slice
    private static final int instructionsPerSlice = 2;
    // Represents the number of instructions currently executed in the time slice
    private int instructionsInCurrentSlice = 0;
    final private Queue<UserModeProcess> readyQueue;
    final private ArrayList<UserModeProcess> blockedProcesses;
    final private ArrayList<UserModeProcess> finishedProcesses;
    private UserModeProcess runningProcess;

    public Scheduler() {
        super();
        readyQueue = new LinkedList<>();
        blockedProcesses = new ArrayList<>();
        finishedProcesses = new ArrayList<>();
    }

    public synchronized UserModeProcess getRunningProcess() {
        return runningProcess;
    }

    public synchronized ArrayList<UserModeProcess> getFinishedProcesses() {
        return finishedProcesses;
    }

    public synchronized ArrayList<UserModeProcess> getBlockedProcesses() {
        return blockedProcesses;
    }

    public synchronized Queue<UserModeProcess> getReadyQueue() {
        return readyQueue;
    }

    public static int getCyclesperslot() {
        return instructionsPerSlice;
    }

    // OS ready to take on an additional process
    public void addNewProcess(UserModeProcess process){
        process.setState(ProcessState.READY);
        readyQueue.add(process);
        DisplayWindow.printQueues(this);
    }

    // Waiting for event
    public synchronized void blockRunningProcess(){
        runningProcess.setState(ProcessState.BLOCKED);
        blockedProcesses.add(runningProcess);
        runningProcess = null;
        DisplayWindow.printQueues(this);
    }

    // Event occurs
    public synchronized void wakeUpProcess(UserModeProcess process){
        blockedProcesses.remove(process);
        readyQueue.add(process);
        process.setState(ProcessState.READY);
        DisplayWindow.printQueues(this);
    }

    // Normal Completion or Termination due to a runtime error
    public synchronized void finishRunningProcess(){
        runningProcess.setState(ProcessState.FINISHED);
        finishedProcesses.add(runningProcess);
        runningProcess = null;
        DisplayWindow.printQueues(this);
    }

    // Termination due to a system call error
    public synchronized void terminateBlockedProcess(UserModeProcess process){
        blockedProcesses.remove(process);
        process.setState(ProcessState.FINISHED);
        finishedProcesses.add(process);
        DisplayWindow.printQueues(this);
    }

    // Choose process to be executed
    public synchronized UserModeProcess schedule(){
        // Determine whether to preempt the current running process or keep it running 
        if(runningProcess != null){
            instructionsInCurrentSlice++;
            if(instructionsInCurrentSlice >= instructionsPerSlice){
                // The cuurent time slice reached its maximum instructions
                if(readyQueue.isEmpty()){
                    // The current running process is allowed to run another time slice because there are no processes in the ready queue
                    instructionsInCurrentSlice = 0;
                    return runningProcess;
                }
                preemptRunningProcess();
            }
            else{
                // The current time slice is not expired 
                // The current process is allowed to continue execution
                return runningProcess;
            }
        }
        // running process should be null here
        if(!readyQueue.isEmpty()){
            // Choosing the next ready process
            runningProcess = readyQueue.remove();
            instructionsInCurrentSlice = 0;
            runningProcess.setState(ProcessState.RUNNING);
            DisplayWindow.printQueues(this);
        }    
        return runningProcess;
    }

    // Preemption
    public synchronized void preemptRunningProcess(){
        runningProcess.setState(ProcessState.READY);
        readyQueue.add(runningProcess);
        runningProcess = null;
        DisplayWindow.printQueues(this);
    }

    public synchronized boolean hasRunningPrograms() {
        return getRunningProcess() != null || !getReadyQueue().isEmpty()
                || !getBlockedProcesses().isEmpty();
    }

}
