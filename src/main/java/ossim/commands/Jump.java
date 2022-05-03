package ossim.commands;

import ossim.exceptions.SimulatorRuntimeException;
import ossim.simulator.UserModeProccess;

public class Jump implements Command{

    private int positionOffset;
    
    public Jump(int positionOffset) {
        super();
        this.positionOffset = positionOffset;
    }
    @Override
    public void execute(UserModeProccess proccess) throws SimulatorRuntimeException {
        // TODO Auto-generated method stub
        
    }
    
}
