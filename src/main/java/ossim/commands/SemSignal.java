package ossim.commands;

import ossim.exceptions.SimulatorRuntimeException;
import ossim.simulator.UserModeProcess;

public class SemSignal implements Command{
    
    private String resource;

    public SemSignal(String resource) {
        super();
        this.resource = resource;
    }

    @Override
    public void execute(UserModeProcess process) throws SimulatorRuntimeException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public String toString() {
        return "semSignal " + resource;
    }
}
