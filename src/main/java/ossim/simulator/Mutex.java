package ossim.simulator;

import java.util.LinkedList;
import java.util.Queue;

import ossim.exceptions.SimulatorRuntimeException;

public class Mutex {

    // The current process owning the resource
    private UserModeProcess owner;
    // Queue of the processes waiting to access the resource
    final private Queue<UserModeProcess> blockedProcesses;

    public Mutex() {
        super();
        blockedProcesses = new LinkedList<>();
    }

    public Queue<UserModeProcess> getBlockedProcesses() {
        return blockedProcesses;
    }

    public UserModeProcess getOwner() {
        return owner;
    }

    public void setOwner(UserModeProcess owner) {
        this.owner = owner;
    }

    public boolean wait(UserModeProcess process){
        if(owner != null){
            blockedProcesses.add(process);
            return false;
        }
        else{
            owner = process;
            return true;
        }
    }

    public UserModeProcess signal(UserModeProcess process) throws SimulatorRuntimeException{
        if(owner != process){
            throw new SimulatorRuntimeException("Process doesn't own the mutex");
        }
        if(!blockedProcesses.isEmpty()){
            owner = blockedProcesses.remove();
        }
        else{
            owner = null;
        }
        return owner;
    }

}
