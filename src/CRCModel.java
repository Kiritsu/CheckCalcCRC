import java.util.ArrayList;

public class CRCModel {
    private ArrayList<StringBuilder> steps;
    private StringBuilder crc;
    private StringBuilder message;

    public CRCModel(ArrayList<StringBuilder> steps, StringBuilder message, StringBuilder crc) {
        this.steps = steps;
        this.message = message;
        this.crc = crc;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();

        for (StringBuilder step : steps) {
            str.append(step);
        }

        str.append('\n')
                .append("Le CRC est [")
                .append(crc)
                .append("] et le mot est : [")
                .append(message)
                .append(" ")
                .append(crc)
                .append("]");

        return str.toString();
    }

    /**
     * Gets the CRC.
     */
    public StringBuilder getCrc() {
        return crc;
    }

    /**
     * Gets the different strings steps.
     */
    public ArrayList<StringBuilder> getSteps() {
        return steps;
    }

    /**
     * Gets the initial binary message.
     */
    public String getMessage() {
        return message.toString();
    }

    /**
     * Returns the full message to send.
     */
    public String getFullMessage() {
        return message + "" + crc;
    }

    /**
     * Indicates whether CRC is empty which means binary message was properly sent.
     */
    public boolean isValid() {
        return crc.length() == 0;
    }
}
