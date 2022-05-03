package ossim.commands;

import ossim.exceptions.SimulatorRuntimeException;
import ossim.simulator.UserModeProcess;

public interface Command {
    
    void execute(UserModeProcess proccess) throws SimulatorRuntimeException;
}
