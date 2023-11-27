import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        String adrs = "127.0.0.1";
        int port = 9090;

        try (Socket s = new Socket(adrs, port)) {
            DataInputStream inp = new DataInputStream(s.getInputStream());
            DataOutputStream out = new DataOutputStream(s.getOutputStream());
            System.out.println("welcome to chat");
            Thread receiveThread = new Thread(() -> {
                try {
                    while (true) {
                        String receivedMsg = inp.readUTF();
                        System.out.println(receivedMsg);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            receiveThread.start();

            Scanner sin = new Scanner(System.in);
            while (true) {

                String m = sin.nextLine();
                out.writeUTF(m);
                out.flush();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

