package ossim.commands;

import ossim.exceptions.SimulatorRuntimeException;
import ossim.simulator.UserModeProccess;

public interface Command {
    
    void execute(UserModeProccess proccess) throws SimulatorRuntimeException;
}
