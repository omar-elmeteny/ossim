package ossim.simulator;

import java.util.LinkedList;
import java.util.Queue;

import ossim.exceptions.SimulatorRuntimeException;

public class Mutex {

    private UserModeProcess owner;
    final private Queue<UserModeProcess> blockedProcesses;
    private int counter = 0;

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
        if(process == owner){
            counter++;
            return true;
        }
        else if(owner != null){
            blockedProcesses.add(process);
            return false;
        }
        else{
            owner = process;
            counter = 1;
            return true;
        }
    }

    public UserModeProcess signal(UserModeProcess process) throws SimulatorRuntimeException{
        if(owner != process){
            throw new SimulatorRuntimeException("Process doesn't own the mutex");
        }
        counter--;
        if(counter == 0){
            if(!blockedProcesses.isEmpty()){
                owner = blockedProcesses.remove();
                counter = 1;
            }
            else{
                owner = null;
            }
            return owner;
        }
        return null;
    }

    public UserModeProcess release(){
        if(owner != null){
            counter = 0;
            owner = null;
            if(!blockedProcesses.isEmpty()){
                owner = blockedProcesses.remove();
                return owner;
            }
        }
        return null;
    }
}
