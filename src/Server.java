import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private static List<ClientHandler> clients = new ArrayList<>();

    public static void main(String[] args) {
        int port = 9090;

        try (ServerSocket server = new ServerSocket(port)) {
            while (true) {
                Socket clientSocket = server.accept();
                System.out.println("New client connected: " + clientSocket);
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler);
                Thread thread = new Thread(clientHandler);
                thread.start();

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void send(String message, ClientHandler sender) {
        for (ClientHandler client : new ArrayList<>(clients)) {
            if (client != sender) {
                client.sendMessage(message);
            }
        }
    }

    public static void changeName(ClientHandler client, String newName) {
        for (ClientHandler clientHandler : clients) {
            if (clientHandler != client && clientHandler.getClientName().equals(newName)) {
                client.sendMessage("used name try something else");
                return;
            }
        }

        client.setClientName(newName);
        System.out.println(client.getClientName() + " changed name to " + newName);
    }

    public static void activePeople(ClientHandler clientHandler){
        clientHandler.sendMessage(String.valueOf(clients.size()));
    }

    public static void disconnect(ClientHandler clientHandler){
        clients.remove(clientHandler);
        for (ClientHandler clientHandler1 : clients){
            System.out.println(clientHandler1);
        }

    }

    public static void sendPersonalMessage(ClientHandler sender, String targetName, String message) {
        for (ClientHandler client : clients) {
            if (client.getClientName().equals(targetName)) {
                client.sendMessage("[Personal message from " + sender.getClientName() + "]:" + message);
                sender.sendMessage("[Personal message to " + targetName + "]:" + message);
                return;
            }
        }
    }
}

