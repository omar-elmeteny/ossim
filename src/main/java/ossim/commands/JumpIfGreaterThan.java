package ossim.commands;

import ossim.exceptions.SimulatorRuntimeException;
import ossim.simulator.UserModeProccess;

public class JumpIfGreaterThan implements Command{
    
    private String left;
    private String right;
    private int positionOffset;

    public JumpIfGreaterThan(String left, String right, int positionOffset) {
        super();
        this.left = left;
        this.right = right;
        this.positionOffset = positionOffset;
    }

    @Override
    public void execute(UserModeProccess proccess) throws SimulatorRuntimeException {
        // TODO Auto-generated method stub
        
    }
}
