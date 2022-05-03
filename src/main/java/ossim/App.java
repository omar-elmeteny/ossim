package ossim;

import java.io.IOException;
import java.util.ArrayList;

import ossim.simulator.Parser;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws IOException 
    {
        ArrayList<String> program1 = Parser.readFile("programs\\Program_1.txt");
        ArrayList<String> program2 = Parser.readFile("programs\\Program_2.txt");
        ArrayList<String> program3 = Parser.readFile("programs\\Program_3.txt");
    }
}
