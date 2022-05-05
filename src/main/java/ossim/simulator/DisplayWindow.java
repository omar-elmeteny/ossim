package ossim.simulator;

import java.awt.Color;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import ossim.commands.Command;

public class DisplayWindow extends JFrame{

    private static String blue = "#21a1f1";
    private static DisplayWindow mainWindow;
    private JTextPane outputTextPane;
    private static StringBuilder outputText = new StringBuilder();
    private JScrollPane outputTextScroller;
    private HTMLEditorKit editorKit;
    private HTMLDocument htmlDoc;

    private DisplayWindow() {
        super();
        setTitle("OS Simulator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1500,800);
        editorKit = new HTMLEditorKit();
        htmlDoc = new HTMLDocument();
        outputTextPane = new JTextPane();
        outputTextPane.setEditorKit(editorKit);
        outputTextPane.setDocument(htmlDoc);
        outputTextPane.setEditable(false);
        outputTextPane.setBackground(Color.getHSBColor(216,0.28f,0.07f));
        outputTextPane.setContentType("text/html");
        outputTextPane.setCaretColor(outputTextPane.getBackground());
        outputTextPane.getCaret().setBlinkRate(0);
        outputTextScroller = new JScrollPane(outputTextPane);
        outputTextScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        outputTextScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        outputTextScroller.setOpaque(false);
        outputTextScroller.getViewport().setOpaque(false);
        add(outputTextScroller);
        setVisible(true);
        outputTextPane.addComponentListener(new ComponentAdapter(){
            @Override
            public void componentResized(ComponentEvent e) {
                JScrollBar vertical = outputTextScroller.getVerticalScrollBar();
                vertical.setValue(vertical.getMaximum() + 100);
            }
        });
    }

    public static DisplayWindow getMainWindow(){
        if(mainWindow == null){
            mainWindow = new DisplayWindow();
        }
        return mainWindow;
    }

    public static void printExecutingInstruction(UserModeProcess process, Command cmd){
        addText("PID: ", blue);
        addText(process.getProcessID() + " ,", "white");
        addText(" CMD: ", blue);
        addText(cmd.toString(), "white");
        addNewLine();
    }

    public static String askForInput(UserModeProcess process){
        String text = JOptionPane.showInputDialog("Enter input for PID " + process.getProcessID() + ": ");
        if(text == null)
            return "";
        return text;
    }


    public static void printProcessOutput(UserModeProcess process, String output){
        addText("Output from PID ", "#d287a5");
        addText(process.getProcessID() + ": ", "white");
        addText(output ,"white");
        addNewLine();
    }

    public static void printProcess(UserModeProcess process){
        addText("Process created with PID ", blue);
        addText(process.getProcessID() + " ", "white");
        addText("from ", blue);
        addText(process.getProgramPath(), "white");
        addNewLine();
    }

    public static void printQueues(Scheduler scheduler){
        if(scheduler.getRunningProcess() != null){
            addText("Running Process: ", "green");
            addText("PID: ", blue);
            addText(scheduler.getRunningProcess().getProcessID() + " ", "white");
        }
        addText("Ready Queue: ", "green");
        boolean flag = false;
        for(UserModeProcess process : scheduler.getReadyQueue()){
            if(flag)
                addText(", ", "white");
            else
                flag = true;    
            addText("PID: ", blue);
            addText(process.getProcessID() + " ", "white");
        }
        addText("Blocked Queue: ", "green");
        flag = false;
        for(UserModeProcess process : scheduler.getBlockedProcesses()){
            if(flag)
                addText(", ", "white");
            else
                flag = true;    
            addText("PID: ", blue);
            addText(process.getProcessID() + " ", "white");
        }
        addText("Finished Queue: ", "green");
        flag = false;
        for(UserModeProcess process : scheduler.getFinishedProcesses()){
            if(flag)
                addText(", ", "white");
            else
                flag = true;    
            addText("PID: ", blue);
            addText(process.getProcessID() + " ", "white");
        }
        addNewLine();
    }

    public static void displayProcessErrorMessage(UserModeProcess process, String errorMessage){
        addText("Error in Process ", "red");
        addText("PID ", blue);
        addText(process.getProcessID() + " ", "white");
        addText(errorMessage, "red");
        addNewLine();
    }

    public static void printLaunchError(String programPath, String errorMessage) {
        addText("Failed to launch program: ", "red");
        addText("path ", blue);
        addText(programPath + " ", "white");
        addText(errorMessage, "red");
        addNewLine();
    }

    public static void printProcessState(UserModeProcess process, ProcessState oldProcessState){
        addText("State Change ", "yellow");
        addText("PID: ", blue);
        addText(Integer.toString(process.getProcessID()) + " ","white");
        addText(oldProcessState + " ", "#d287a5");
        addText("-> ", "white");
        addText(process.getState() + "", "green");
        addNewLine();
    }


    private static void addText(String text, String color){
        outputText.append("<span style='font-family: monospace;font-size: 18px;font-weigth: bold;color: ");
        outputText.append(color);
        outputText.append("'>");
        outputText.append(text);
        outputText.append("</span>");     
    }

    private static void addNewLine(){
        getMainWindow().addHTML(outputText.toString());
        outputText.delete(0, outputText.length() - 1);
    }

    public void addHTML(String html){
        try {
            editorKit.insertHTML(htmlDoc,htmlDoc.getLength(), html,0, 0,  null);
        } catch (BadLocationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } 
    }
}
