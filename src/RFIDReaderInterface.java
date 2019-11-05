import com.fazecast.jSerialComm.*;

import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;

public class RFIDReaderInterface {
    private SerialPort selectedPort; // The selected port to be used
    private Scanner serialReader;
    private PrintWriter serialWriter;
    private String lastStringRead = "";
    private byte[] bytesRead;

    public RFIDReaderInterface() {
        System.out.println("===== RFIDReaderInterface by Cyphred =====");
        System.out.println("Initializing");

        System.out.println("Fetching available COM Ports");
        SerialPort availablePorts[] = SerialPort.getCommPorts(); // Gets all available COM Ports
        System.out.println(availablePorts.length + " COM Port/s found");

        int portNumbers = 1; // gives port numbers when printed
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
            selectedPort.addDataListener(new SerialPortDataListener() {
                @Override
                public int getListeningEvents() {
                    return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
                }

                @Override
                public void serialEvent(SerialPortEvent event) {
                    if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE)
                        return;
                    bytesRead = new byte[selectedPort.bytesAvailable()];
                    int numRead = (selectedPort.readBytes(bytesRead, bytesRead.length) - 2);
                    try {
                        lastStringRead = new String(bytesRead, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    // TODO Remove this temporary print statement
                    // For checking data received through serial
                    //System.out.println(numRead + " bytes read, " + lastStringRead.length() + " characters : \"" + lastStringRead + "\"");
                }
            });
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

    public String scan() {
        serialPrint("scan\n0");
        while (lastStringRead.length() != 10) {
            System.out.print("");
        }
        return getLastStringRead();
    }

    public boolean challenge(String passcode) throws InterruptedException {
        serialPrint("challenge\n" + passcode);
        while (true) {
            if (getLastStringRead().equals("ok")) {
                return true;
            }
            else if (getLastStringRead().equals("no")) {
                break;
            }
            System.out.print("");
        }
        return false;
    }

    private void serialPrint(String input) {
        serialWriter.print(input);
        serialWriter.flush();
    }

    private String getLastStringRead() {
        String returnValue = "";
        for (int x = 0; x < lastStringRead.length() - 2; x++) {
            returnValue += lastStringRead.toCharArray()[x];
        }
        return returnValue;
    }

    public void clearLastStringRead() {
        lastStringRead = "";
    }
}
