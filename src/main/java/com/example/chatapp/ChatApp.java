package com.example.chatapp;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class ChatApp extends Application {
    private boolean isServer =false;///server = true, client = false
    private TextArea messages = new TextArea();

    private NetworkConnection connection = isServer  ? createServer() : createClient();
    private Parent createContent(){
        messages.setPrefHeight(550);
        TextField input = new TextField();
        input.setOnAction(event -> {
            String message = isServer ? "Server: " : "Client: ";
            message += input.getText();
            input.clear();

            messages.appendText(message + "\n");

            try {
                connection.send(message);
            } catch (Exception e) {
               e.printStackTrace();
               messages.appendText("Failed to send\n");
            }
        });
        VBox root = new VBox(20,messages,input);
        root.setPrefSize(600,600);
        return root;
    }

    public void init() throws Exception{
        connection.startConnection();
    }

    @Override
    public void start(Stage stage) throws IOException {
        stage.setScene(new Scene(createContent()));
        if(isServer)stage.setTitle("Server");
        else stage.setTitle("Client");
        stage.show();
    }

    public void stop() throws Exception{
        connection.closeConnection();
    }

    private Server createServer(){
        return new Server(55555, data -> {
            Platform.runLater(()->{
                messages.appendText(  data.toString() + "\n");
            });
        });
    }

    private Client createClient(){
        return new Client("127.0.0.1",55555,data->{
            Platform.runLater(() ->{
                messages.appendText( data.toString() + "\n");
            });
        });
    }
    public static void main(String[] args) {
        launch();
    }
}