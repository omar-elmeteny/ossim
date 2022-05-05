package ossim.commands;

import ossim.exceptions.SimulatorRuntimeException;
import ossim.simulator.OperatingSystem;
import ossim.simulator.UserModeProcess;

public class ReadFile implements Command{
    
    private String fileNameVariableName;
    private String outputVariableName;

    public ReadFile(String fileNameVariableName, String outputVariableName) {
        super();
        this.fileNameVariableName = fileNameVariableName;
        this.outputVariableName = outputVariableName;
    }

    @Override
    public void execute(UserModeProcess process) throws SimulatorRuntimeException {
        OperatingSystem.readFile(process.readVariable(fileNameVariableName), outputVariableName);
    }

    @Override
    public String toString() {
        return "assign " + outputVariableName + " readFile " + fileNameVariableName;
    }
}
