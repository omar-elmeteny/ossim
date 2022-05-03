package ossim;

import java.io.IOException;

import ossim.commands.Command;
import ossim.exceptions.SimulatorRuntimeException;
import ossim.exceptions.SimulatorSyntaxException;
import ossim.simulator.Display;
import ossim.simulator.UserModeProcess;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws IOException, SimulatorSyntaxException, SimulatorRuntimeException 
    {
        UserModeProcess process1 = new UserModeProcess(1, "programs/Program_1.txt");
        Command cmd;
        while((cmd = process1.getNextCommand()) != null){
            Display.printExecutingInstruction(process1, cmd);
            cmd.execute(process1);
        }
    }
}
