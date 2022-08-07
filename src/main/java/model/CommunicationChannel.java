package model;

import com.example.security.Controller;
import javafx.application.Platform;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class CommunicationChannel {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    private Controller c;
    private String ip;

    public CommunicationChannel(Controller c) {
        this.c = c;
    }

    public void waitForConnection(int port) {
        new Thread(() -> {
            try {
                waitingThreadMethod(port);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void waitingThreadMethod(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("Waiting for pairing :)(:");
        try {
            clientSocket = serverSocket.accept();
        } catch (SocketException e){
            // Wartevorgang abgebrochen
            return;
        }
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        ip = clientSocket.getInetAddress().getHostAddress();
        System.out.println("Server connected");
        startListeningThread();
    }

    public void connectTo(String ip, int port) {
        try {
            stop();
            System.out.println("Stopped and tried to connect");
            clientSocket = new Socket(ip, port);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            System.out.println("Client connected");
            this.ip = ip;
            startListeningThread();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String m) {
        if(isConnected()) {
            System.out.println("Sending: " + m);
            out.println(m);
        } else {
            System.out.println("Not connected!!!" + m);
        }
    }

    private void startListeningThread() {
        System.out.println("Started Listening Thread");
        new Thread(() -> {
            try {
                listen();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
    private void listen() throws IOException {
        String next;
        while((next = in.readLine()) != null) {
            System.out.println("Listening: " + next);
            final String msg = next;
            Platform.runLater(() ->c.receiveMessage(msg));
        }
        System.out.println("Ended listening. in.ready=" + in.ready());
    }


    public void stop() throws IOException{
        if(in != null) in.close();
        if(out != null) out.close();
        if(clientSocket != null) clientSocket.close();
        if(serverSocket != null)serverSocket.close();
    }

    public boolean isConnected() {
        return clientSocket!= null && clientSocket.isConnected();
    }

    public String getTheOthersIP() {
        return ip;
    }
}
