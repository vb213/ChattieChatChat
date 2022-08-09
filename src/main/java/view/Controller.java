package view;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import model.CommunicationChannel;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

public class Controller {
    public static final int PORT = 6667;

    @FXML
    public TextField ip_field;
    @FXML
    public TextArea message_field;
    @FXML
    public Label chat_label;
    @FXML
    public Label lineStatus_label;
    @FXML
    public Label connectionStatus_label;

    private CommunicationChannel channel;

    private List<String> messageHistory = new ArrayList<>();

    public Controller() {
        channel = new CommunicationChannel(this);
    }

    @FXML
    protected void initialize() {
        setConnectionStatusLabel(false);
        channel.waitForConnection(PORT);
    }

    public void setConnectionStatusLabel(boolean connected) {
        if(connected) {
            connectionStatus_label.setTextFill(Color.GREEN);
            connectionStatus_label.setText("Connected to: \n" + channel.getTheOthersIP());
            return;
        }
        connectionStatus_label.setTextFill(Color.RED);
        connectionStatus_label.setText("Not Connected");
    }

    public void setLineStatusLabel(boolean online) {
        if(online) {
            lineStatus_label.setTextFill(Color.GREEN);
            lineStatus_label.setText("Online");
            return;
        }
        lineStatus_label.setTextFill(Color.RED);
        lineStatus_label.setText("Offline");
    }


    public void receiveMessage(String m) {
        messageHistory.add(m + " <- " + channel.getTheOthersIP());
        updateChatLabel();
    }

    @FXML
    public void sendMessage() {
        connect_toIP();

        String m = message_field.getText();
        channel.sendMessage(m);
        messageHistory.add(m + "<- DU");
        updateChatLabel();
    }

    @FXML
    public void connect_toIP() {
        if (!channel.isConnected()) {
            try {
                channel.connectTo(ip_field.getText(), PORT);
            } catch (ConnectException e) {
                System.out.println("Destination Address refused connection!!");
            }
        } else {
            try {
                channel.stop();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void updateChatLabel() {
        String chat = "";
        for (String m : messageHistory) {
            chat += m + "\n";
        }
        chat_label.setText(chat);
    }
}