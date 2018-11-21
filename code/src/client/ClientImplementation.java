package client;

import helpers.Config;
import helpers.Helpers;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import server.ServerInterface;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Random;

public class ClientImplementation extends UnicastRemoteObject implements ClientInterface {

    private static ServerInterface server;
    public static ClientController controller;
    private String username;
    private String hexColor;

    private ClientImplementation(String username) throws RemoteException {
        setUsername(username);
        setColor();
    }

    @Override
    public void showClientLogin(ClientInterface client) throws RemoteException {
        Label timestamp = new Label(Helpers.getTimestamp());
        Text message = new Text(client.getUsername() + " has joined.");
        message.setFont(Font.font(null, FontWeight.BOLD, 12));
        message.setFill(Color.valueOf(client.getHexColor()));

        controller.setClientsList();

        show(timestamp, null, message);
    }

    @Override
    public void showClientMessage(ClientInterface client, String rawMessage) throws RemoteException {
        Label timestamp = new Label(Helpers.getTimestamp());
        Label username = new Label(client.getUsername() + ":");
        username.setTextFill(Color.valueOf(client.getHexColor()));
        username.setStyle("-fx-font-weight: bold");
        Text message = new Text(rawMessage.trim());

        show(timestamp, username, message);
    }

    @Override
    public void showClientMessage(ClientInterface client, String rawMessage, ClientInterface whisperee) throws RemoteException {
        Label timestamp = new Label(Helpers.getTimestamp());
        Label username = new Label(client.getUsername() + " > " + whisperee.getUsername() + ":");
        username.setTextFill(Color.valueOf(client.getHexColor()));
        username.setStyle("-fx-font-weight: bold");
        username.setStyle("-fx-font-style: italic");
        Text message = new Text(rawMessage.trim());
        message.setStyle("-fx-font-style: italic");

        show(timestamp, username, message);
    }

    @Override
    public void show(Label timestamp, Label username, Text message) throws RemoteException {
        controller.appendMessage(timestamp, username, message);
    }

    @Override
    public String getUsername() throws RemoteException {
        return this.username;
    }

    private void setUsername(String username) {
        this.username = Helpers.ucfirst(username);
    }

    private void setColor() {
        Random random = new Random();
        Color color = Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255));
        hexColor = color.toString();
    }

    public String getHexColor() throws RemoteException{
        return hexColor;
    }

    static void sendMessage(ClientInterface speaker, String message) throws RemoteException {
        server.send(speaker, message);
    }

    static void whisperMessage(ClientInterface whisperer, ClientInterface whisperee, String message) throws RemoteException {
        server.whisper(whisperer, whisperee, message);
    }

    static ClientInterface connect(String username) {
        ClientInterface client = null;

        try {
            Registry registry = LocateRegistry.getRegistry(Config.PORT);
            server = (ServerInterface) registry.lookup(Config.SERVER_NAME);
            client = server.register(new ClientImplementation(username));
        } catch (RemoteException | NotBoundException e) {
            Helpers.alertException(e);
            Platform.exit();
            System.exit(0);
        }

        return client;
    }

    static public ArrayList<ClientInterface> getClientsList() throws RemoteException {
        return server.getClientsList();
    }
}
