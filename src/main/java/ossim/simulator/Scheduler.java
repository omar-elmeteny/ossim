package ossim.simulator;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Scheduler {
    
    private static final int cyclesPerSlot = 2;
    private int runningProcessCycles = 0;
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

    public UserModeProcess getRunningProcess() {
        return runningProcess;
    }

    public ArrayList<UserModeProcess> getFinishedProcesses() {
        return finishedProcesses;
    }

    public ArrayList<UserModeProcess> getBlockedProcesses() {
        return blockedProcesses;
    }

    public Queue<UserModeProcess> getReadyQueue() {
        return readyQueue;
    }

    public static int getCyclesperslot() {
        return cyclesPerSlot;
    }

    public void addNewProcess(UserModeProcess process){
        process.setState(ProcessState.READY);
        readyQueue.add(process);
        Display.printQueues(this);
    }

    public void blockRunningProcess(){
        runningProcess.setState(ProcessState.BLOCKED);
        blockedProcesses.add(runningProcess);
        runningProcess = null;
        Display.printQueues(this);
    }

    public void wakeUpProcess(UserModeProcess process){
        blockedProcesses.remove(process);
        readyQueue.add(process);
        process.setState(ProcessState.READY);
        Display.printQueues(this);
    }

    public void finishRunningProcess(){
        runningProcess.setState(ProcessState.FINISHED);
        finishedProcesses.add(runningProcess);
        runningProcess = null;
        Display.printQueues(this);
    }

    public UserModeProcess schedule(){
        if(runningProcess != null){
            runningProcessCycles++;
            if(runningProcessCycles >= cyclesPerSlot){
                if(readyQueue.isEmpty()){
                    runningProcessCycles = 0;
                    return runningProcess;
                }
                preemptRunningProcess();
            }
            else{
                return runningProcess;
            }
        }
        if(!readyQueue.isEmpty()){
            runningProcess = readyQueue.remove();
            runningProcessCycles = 0;
            runningProcess.setState(ProcessState.RUNNING);
            Display.printQueues(this);
        }    
        return runningProcess;
    }

    public void preemptRunningProcess(){
        runningProcess.setState(ProcessState.READY);
        readyQueue.add(runningProcess);
        runningProcess = null;
        Display.printQueues(this);
    }

}
