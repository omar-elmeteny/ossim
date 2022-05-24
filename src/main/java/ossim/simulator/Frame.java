package ossim.simulator;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import ossim.exceptions.SimulatorRuntimeException;

public class Frame implements Serializable {

    private static final long serialVersionUID = 1L;
    private final Object[] data;
    private final int frameIndex;
    private long lastUse;
    private int page;
    private int processID;

    public Frame(int frameIndex) {
        super();
        setPage(-1);
        data = new Object[1 << OperatingSystem.pageSizeBits];
        this.frameIndex = frameIndex;
    }

    public int getProcessID() {
        return processID;
    }

    public void setProcessID(int processID) {
        this.processID = processID;
    }

    public int getFrameIndex() {
        return frameIndex;
    }

    public Object[] getData() {
        return data;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public long getLastUse() {
        return lastUse;
    }

    public void setLastUse(long lastUse) {
        this.lastUse = lastUse;
    }

    public boolean isFree() {
        return (processID == 0);
    }

    public boolean isPCBFrame() {
        return (page == (1 << (OperatingSystem.logicalMemorySizeBits - OperatingSystem.pageSizeBits)) - 1);
    }

    public void setObjectAt(int i, Object value) {
        data[i] = value;
    }

    public Object getObjectAt(int i) {
        return data[i];
    }

    public void save() throws SimulatorRuntimeException {
        try {
            FileOutputStream fileOut = new FileOutputStream("swap/" + processID + "_" + page + ".mem");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(this);
            out.close();
            fileOut.close();
        } catch (IOException i) {
            throw new SimulatorRuntimeException("Cannot save frame");
        }
    }

    public static Frame load(int processID, int page) throws SimulatorRuntimeException {
        try {
            FileInputStream fileIn = new FileInputStream("swap/" + processID + "_" + page + ".mem");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            Frame frame = (Frame) in.readObject();
            in.close();
            fileIn.close();
            return frame;
        } catch (IOException i) {
           throw new SimulatorRuntimeException("Cannot load frame from disk");
        } catch (ClassNotFoundException c) {
            throw new SimulatorRuntimeException("Cannot load frame from disk");
        }
    }
}
