package ossim.instructions;

import java.io.Serializable;

import ossim.exceptions.SimulatorRuntimeException;
import ossim.simulator.UserModeProcess;

// Represents abstraction for all types of instructions that a process could run
public interface Instruction extends Serializable{
    
    // This is called by the interpreter(OperatingSystem class) to execute the instructions
    // When the instruction fails, a SimulatorRuntimeException is thrown which is caught by the interpreter causing the process to be terminated
    void execute(UserModeProcess process) throws SimulatorRuntimeException;
}
