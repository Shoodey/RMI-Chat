package client;

import javafx.scene.control.Label;
import javafx.scene.text.Text;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientInterface extends Remote {

    void showClientLogin(ClientInterface client) throws RemoteException;

    void showClientMessage(ClientInterface client, String message) throws RemoteException;

    void showClientMessage(ClientInterface client, String message, ClientInterface whisperee) throws RemoteException;

    void show(Label timestamp, Label username, Text message) throws RemoteException;

    String getUsername() throws RemoteException;

    String getHexColor() throws RemoteException;
}
