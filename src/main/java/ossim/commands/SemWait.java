package ossim.commands;

import ossim.exceptions.SimulatorRuntimeException;
import ossim.simulator.UserModeProcess;

public class SemWait implements Command{

    private String resource;

    public SemWait(String resource) {
        super();
        this.resource = resource;
    }

    @Override
    public void execute(UserModeProcess process) throws SimulatorRuntimeException {
        // TODO Auto-generated method stub
        
    }
    

}
