import com.fazecast.jSerialComm.*;

import java.io.PrintWriter;
import java.util.Scanner;

public class RFIDReaderInterface {
    private SerialPort selectedPort; // The selected port to be used
    private Scanner serialReader;
    private PrintWriter serialWriter;

    public RFIDReaderInterface() {
        System.out.println("===== RFIDReaderInterface by Cyphred =====");
        System.out.println("Initializing");

        System.out.println("Fetching available COM Ports");
        SerialPort availablePorts[] = SerialPort.getCommPorts(); // Gets all available COM Ports
        System.out.println(availablePorts.length + " COM Port/s found");

        int portNumbers = 1;
        // lists all available ports to console
        for (SerialPort sp: availablePorts) {
            System.out.println("  " + portNumbers++ + ". " + sp.getDescriptivePortName());
        }

        System.out.println("Attempting to open serial port");
        boolean portOpened = false;
        availablePortsLoop:
        // Iterates through each available port to attempt opening COM port
        // Each port will be tried 3 times before moving on to the next available port
        for (SerialPort sp: availablePorts) {
            System.out.println("Attempting to open \"" + sp.getSystemPortName() + "\"");
            sp.setComPortParameters(115200, 8, 1, 0); // default connection settings for arduino
            sp.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);

            portOpenAttemptsLoop:
            for (int attempt = 0; attempt < 3; attempt++) {
                if (sp.openPort()) {
                    System.out.println("\"" + sp.getSystemPortName() + "\" successfully opened");
                    selectedPort = sp;
                    portOpened = true;
                    break portOpenAttemptsLoop;
                }
                else {
                    System.out.println("Cannot open \"" + sp.getSystemPortName() + "\". Will try again... (Attempt " + (attempt + 1) + ")");
                }
            }

            if (portOpened) {
                break availablePortsLoop;
            }
            else {
                System.out.println("Failed to open \"" + sp.getSystemPortName() + "\" after 3 attempts.");
            }
        }

        if (portOpened) {
            serialReader = new Scanner(selectedPort.getInputStream()); // Start input stream for receiving data over serial
            serialWriter = new PrintWriter(selectedPort.getOutputStream()); // Start output stream for receiving data over serial
            System.out.println("Waiting for device to be ready");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {}
        }
        else {
            System.out.println("Could not open any ports.");
        }

        System.out.println("===== RFIDReaderInterface: End of Initialization =====");
    }

    public char[] listenForIDSerialNumber() {
        serialPrint("scan");
        String fetchedData = "";
        while (serialReader.hasNextLine()) {
            fetchedData = serialReader.nextLine();
            if (fetchedData.length() == 8) {
                break;
            }
        }
        return fetchedData.toCharArray();
    }

    public boolean verifyConnection(){
        serialPrint("ping");
        String temp = serialRead();
        System.out.println(temp);
        if (temp.equals("pong")) {
            return true;
        }
        return false;
    }

    private void serialPrint(String input) {
        serialWriter.print(input);
        serialWriter.flush();
    }

    private String serialRead() {
        String fetchedData = "";
        while (serialReader.hasNextLine()) {
            fetchedData = serialReader.nextLine();
        }
        System.out.println("Read: ");
        return fetchedData;
    }
}
