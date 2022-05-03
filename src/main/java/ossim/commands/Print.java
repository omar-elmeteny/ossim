package ossim.commands;

import ossim.exceptions.SimulatorRuntimeException;
import ossim.simulator.UserModeProcess;

public class Print implements Command{

    private String variable;

    public Print(String variable) {
        super();
        this.variable = variable;
    }

    @Override
    public void execute(UserModeProcess proccess) throws SimulatorRuntimeException {
        // TODO Auto-generated method stub
        
    }
    
}
