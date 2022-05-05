package ossim.commands;

import ossim.exceptions.SimulatorRuntimeException;
import ossim.simulator.OperatingSystem;
import ossim.simulator.UserModeProcess;

public class WriteFile implements Command{
    
    private String fileNameVariable;
    private String dataVariable;

    public WriteFile(String fileNameVariable, String dataVariable) {
        super();
        this.fileNameVariable = fileNameVariable;
        this.dataVariable = dataVariable;
    }

    @Override
    public void execute(UserModeProcess process) throws SimulatorRuntimeException {
        OperatingSystem.writeFile(process.readVariable(fileNameVariable), process.readVariable(dataVariable));
    }

    @Override
    public String toString() {
        return "writeFile " + fileNameVariable + " " + dataVariable;
    }
}
