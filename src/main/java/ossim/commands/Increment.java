package ossim.commands;

import ossim.exceptions.SimulatorRuntimeException;
import ossim.simulator.UserModeProccess;

public class Increment implements Command{
    
    private String variable;

    public Increment(String variable) {
        super();
        this.variable = variable;
    }

    @Override
    public void execute(UserModeProccess proccess) throws SimulatorRuntimeException {
        // TODO Auto-generated method stub
        
    }
}
