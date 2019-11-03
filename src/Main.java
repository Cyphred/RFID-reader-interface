public class Main {
    public static RFIDReaderInterface rfidInterface = new RFIDReaderInterface();
    public static void main(String[] args) {
        while (true) {
            System.out.println("Please scan your RFID.");
            char[] serialNumber = rfidInterface.listenForIDSerialNumber();
            System.out.print("Your Serial Number is ");
            for (char c: serialNumber) {
                System.out.print(c);
            }
            System.out.println();
        }
    }
}