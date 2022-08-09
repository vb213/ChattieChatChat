package model;

import view.Controller;
import javafx.application.Platform;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;

public class CommunicationChannel {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    private Controller c;
    private String ip;
    private boolean serverSocketWaiting = false;


    public CommunicationChannel(Controller c) {
        this.c = c;
    }

    public void waitForConnection(int port) {
        if(serverSocketWaiting)
            return;
        serverSocketWaiting = true;
        c.setLineStatusLabel(true);
        new Thread(() -> {
            try {
                waitingThreadMethod(port);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void waitingThreadMethod(int port) throws IOException {
        System.out.println("Waiting for pairing :)(:");
        try {
            serverSocket = new ServerSocket(port);
            clientSocket = serverSocket.accept();
        }catch (BindException e) {
            c.setLineStatusLabel(false);
            System.out.println("Port is bound!");
            return;
        }catch (SocketException e){
            // Wartevorgang abgebrochen
            c.setConnectionStatusLabel(false);
            return;
        } finally {
            serverSocketWaiting = false;
        }
        ip = clientSocket.getInetAddress().getHostAddress();
        initIOBuffers();

        serverSocket.close();
        System.out.println("Server connected");
        startListeningThread();
    }

    public void connectTo(String ip, int port) throws ConnectException {
        try {
            stop();
            System.out.println("Stopped and tried to connect");
            clientSocket = new Socket(ip, port);
            this.ip = ip;
            initIOBuffers();

            System.out.println("Client connected");

            startListeningThread();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initIOBuffers() throws IOException{
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        Platform.runLater(() -> c.setConnectionStatusLabel(true));
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
        c.setConnectionStatusLabel(false);
    }

    public boolean isConnected() {
        return clientSocket!= null && clientSocket.isConnected();
    }

    public String getTheOthersIP() {
        return ip;
    }
}
