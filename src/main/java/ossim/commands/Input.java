package ossim.commands;

import ossim.exceptions.SimulatorRuntimeException;
import ossim.simulator.UserModeProcess;

public class Input implements Command{

    private String variableName;
    
    public Input(String variableName) {
        super();
        this.variableName = variableName;
    }
    @Override
    public void execute(UserModeProcess proccess) throws SimulatorRuntimeException {
        // TODO Auto-generated method stub
        
    }
    
}
