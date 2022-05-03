package ossim.commands;

import ossim.exceptions.SimulatorRuntimeException;
import ossim.simulator.UserModeProccess;

public class Input implements Command{

    private String variableName;
    
    public Input(String variableName) {
        super();
        this.variableName = variableName;
    }
    @Override
    public void execute(UserModeProccess proccess) throws SimulatorRuntimeException {
        // TODO Auto-generated method stub
        
    }
    
}
