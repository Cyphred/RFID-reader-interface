import java.util.Scanner;

public class Main {
    public static Scanner sc = new Scanner(System.in);
    public static RFIDReaderInterface device = new RFIDReaderInterface();
    public static void main(String[] args) throws InterruptedException {
        int menuState = 0;

        while (menuState != 100) {
            if (menuState == 0) {
                System.out.println("[MENU]");
                System.out.println("1 - Basic Scan");
                System.out.println("2 - Challenge");
                System.out.println("3 - New Passcode");
                System.out.println("4 - Check RFID Status");

                System.out.println("\n100 - Exit");
                System.out.print("Input : ");
                menuState = Integer.parseInt(sc.nextLine());
            }
            else if (menuState == 1) {
                device.scan();
                menuState = 0;
            }
            else if (menuState == 2) {
                System.out.print("Enter passcode : ");
                device.challenge(sc.nextLine());
                menuState = 0;
            }
            else if (menuState == 3) {
                device.newPasscode();
                menuState = 0;
            }
            else if (menuState == 4) {
                device.checkRFIDStatus();
                menuState = 0;
            }
            else {
                System.out.println("Invalid Menu Option");
                menuState = 0;
            }
        }
        System.out.println("Exiting...");
        System.exit(0);
    }
}