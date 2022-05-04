package ossim.simulator;

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
    static final private Hashtable<Integer,ArrayList<String>> arrivingPrograms = new Hashtable<>();

    private OperatingSystem() {
        super();
    }

    public static Scheduler getScheduler() {
        return scheduler;
    }

    private static UserModeProcess launchProgram(String programPath) throws SimulatorSyntaxException, IOException{
        UserModeProcess process = new UserModeProcess(nextProcessID++, programPath);
        scheduler.addNewProcess(process);
        return process;
    }

    private static void runCycle(){
        UserModeProcess process = scheduler.schedule();
        if(process != null){
            Command cmd = process.getNextCommand();
            try {
                Display.printExecutingInstruction(process, cmd);
                cmd.execute(process);
                if(!process.hasCommands()){
                    scheduler.finishRunningProcess();
                }
            } catch (SimulatorRuntimeException e) {
                Display.displayProcessErrorMessage(process, e.getMessage());
                scheduler.finishRunningProcess();
            }
        }
    } 

    public static void addArrivingProgram(int time, String programPath){
        ArrayList<String> list = arrivingPrograms.get(time);
        if(list == null){
            list = new ArrayList<>();
            arrivingPrograms.put(time, list);
        }
        list.add(programPath);
    }

    public static void run(){
        while(hasRunningPrograms() || !arrivingPrograms.isEmpty()){
            launchArrivingPrograms();
            runCycle();
            currentTime++;
        }
    }

    private static boolean hasRunningPrograms(){
        return scheduler.getRunningProcess() != null || !scheduler.getReadyQueue().isEmpty() || !scheduler.getBlockedProcesses().isEmpty();
    }

    public static void launchArrivingPrograms(){
        ArrayList<String> programs = arrivingPrograms.get(currentTime);
        if(programs == null)
            return;
        for(String programPath : programs){
            try {
                launchProgram(programPath);
            } catch (SimulatorSyntaxException e) {
                Display.printLaunchError(programPath,e.getMessage());
            } catch (IOException e) {
                Display.printLaunchError(programPath,e.getMessage());
            }
        } 
        arrivingPrograms.remove(currentTime);   
    }
}