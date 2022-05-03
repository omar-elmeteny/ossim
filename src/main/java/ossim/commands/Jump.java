package ossim.commands;

import ossim.exceptions.SimulatorRuntimeException;
import ossim.simulator.UserModeProcess;

public class Jump implements Command{

    private int positionOffset;
    
    public Jump(int positionOffset) {
        super();
        this.positionOffset = positionOffset;
    }

    @Override
    public void execute(UserModeProcess process) throws SimulatorRuntimeException {
        process.setProgramCounter(process.getProgramCounter() + positionOffset);
    }
    
    @Override
    public String toString() {
        return "jump " + positionOffset;    
    }
}
