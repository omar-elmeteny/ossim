package ossim.instructions;

import ossim.exceptions.SimulatorRuntimeException;
import ossim.simulator.OperatingSystem;
import ossim.simulator.UserModeProcess;

public class SemWait implements Instruction{

    private String resource;

    public SemWait(String resource) {
        super();
        this.resource = resource;
    }

    @Override
    public void execute(UserModeProcess process) throws SimulatorRuntimeException {
        OperatingSystem.semWait(resource);
    }
    
    @Override
    public String toString() {
        return "semWait " + resource;
    }
}
