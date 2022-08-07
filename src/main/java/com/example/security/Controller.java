package com.example.security;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import model.CommunicationChannel;

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

    private CommunicationChannel channel;

    private List<String> messageHistory = new ArrayList<>();

    public Controller() {
        channel = new CommunicationChannel(this);
    }

    @FXML
    public void openPort() {
        channel.waitForConnection(PORT);
    }
    public void receiveMessage(String m) {
        messageHistory.add(m + " <- " + channel.getTheOthersIP());
        updateChatLabel();
    }

    @FXML
    public void sendMessage() {
        if (!channel.isConnected())
            channel.connectTo(ip_field.getText(), PORT);

        String m = message_field.getText();
        channel.sendMessage(m);
        messageHistory.add(m + "<- DU");
        updateChatLabel();
    }

    private void updateChatLabel() {
        String chat = "";
        for (String m : messageHistory) {
            chat += m + "\n";
        }
        chat_label.setText(chat);
    }
}