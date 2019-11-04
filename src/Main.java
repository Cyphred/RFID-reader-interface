import java.util.Scanner;

public class Main {
    public static Scanner sc = new Scanner(System.in); // TODO Remove temporary Scanner in Main Class
    public static RFIDReaderInterface rfidInterface = new RFIDReaderInterface();
    public static void main(String[] args) {
        int menuState = 0;

        while (menuState != -1) {
            if (menuState == 0) {
                System.out.println("[MENU]");
                System.out.println("1 - Scan RFID");
                System.out.println("2 - Verify Connection");

                menuState = sc.nextInt();
            }
            else if (menuState == 1) {
                System.out.println("Scan your RFID");
                char[] serialNumber = rfidInterface.listenForIDSerialNumber();
                System.out.print("Serial Number: ");
                for (char sn: serialNumber) {
                    System.out.print(sn);
                }
                System.out.println();

                menuState = 0;
            }
            else if (menuState == 2) {
                System.out.println("Verifying connection to RFID Reader...");
                if (rfidInterface.verifyConnection()) {
                    System.out.println("Connected.");
                }
                else {
                    System.out.println("Disconnected.");
                }

                menuState = 0;
            }
            else {
                System.out.println("Invalid Menu Option");
                menuState = 0;
            }
        }
    }
}