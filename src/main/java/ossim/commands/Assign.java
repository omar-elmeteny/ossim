package ossim.commands;

import ossim.exceptions.SimulatorRuntimeException;
import ossim.simulator.UserModeProccess;

public class Assign implements Command{
    
    private String toVariable;
    private String fromVariable;

    public Assign(String toVariable, String fromVariable) {
        super();
        this.toVariable = toVariable;
        this.fromVariable = fromVariable;
    }

    @Override
    public void execute(UserModeProccess proccess) throws SimulatorRuntimeException {
        // TODO Auto-generated method stub
        
    }
}
