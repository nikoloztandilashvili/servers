import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

class ClientHandler implements Runnable {
    private Socket clientSocket;
    private DataInputStream inp;
    private DataOutputStream out;
    private String clientName;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.clientName = "Client";
        try {
            this.inp = new DataInputStream(clientSocket.getInputStream());
            this.out = new DataOutputStream(clientSocket.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setClientName(String newName) {
        this.clientName = newName;
    }

    public String getClientName() {
        return clientName;
    }


    public void sendMessage(String message) {
        try {
            out.writeUTF(message);
            out.flush();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    @Override
    public void run() {
        try {
            String msg;
            while (true) {
                msg = inp.readUTF();
                if (msg.startsWith("/changename ")) {
                    String newName = msg.substring("/changename ".length());
                    Server.changeName(this, newName);
                } else if (msg.startsWith("/pm ")) {
                    String[] parts = msg.split(" ", 3);
                    if (parts.length == 3) {
                        String targetName = parts[1];
                        String personalMessage = parts[2];
                        Server.sendPersonalMessage(this, targetName, personalMessage);
                    } else {
                        sendMessage("wrong /pm command format. pls use: /pm targetName message");
                    }
                }else if(msg.equals("/activePeople"))
                {
                    Server.activePeople(this);
                }
                else {
                    try {
                        Server.send("[" + clientName + "]: " + msg, this);
                    } catch (Exception e) {
                        sendMessage("try again");
                    }
                }
            }
        } catch (IOException e) {
            System.out.println(clientSocket + " disconected");
            Server.disconnect(this);
        }
    }
}
