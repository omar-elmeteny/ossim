package ossim.commands;

import ossim.exceptions.SimulatorRuntimeException;
import ossim.simulator.UserModeProccess;

public class SemWait implements Command{

    private String resource;

    public SemWait(String resource) {
        super();
        this.resource = resource;
    }

    @Override
    public void execute(UserModeProccess proccess) throws SimulatorRuntimeException {
        // TODO Auto-generated method stub
        
    }
    

}
