package ossim;

import java.io.IOException;

import ossim.exceptions.SimulatorRuntimeException;
import ossim.exceptions.SimulatorSyntaxException;
import ossim.simulator.OperatingSystem;
/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws IOException, SimulatorSyntaxException, SimulatorRuntimeException 
    {
        OperatingSystem.addArrivingProgram(0, "programs/Program_1.txt");
        OperatingSystem.addArrivingProgram(1, "programs/Program_2.txt");
        OperatingSystem.addArrivingProgram(4, "programs/Program_3.txt");
        OperatingSystem.run();
    }
}
