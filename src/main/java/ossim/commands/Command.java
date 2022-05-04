package ossim.commands;

import ossim.exceptions.SimulatorRuntimeException;
import ossim.simulator.UserModeProcess;

// Represents abstraction for all types of commands that a process could run
public interface Command {
    
    // This is called by the interpreter(OperatingSystem class) to execute the commands
    // When the command fails, a SimulatorRuntimeException is thrown which is caught by the interpreter causing the process to be terminated
    void execute(UserModeProcess process) throws SimulatorRuntimeException;
}
