package server;

import client.ClientInterface;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface ServerInterface extends Remote {

    ClientInterface register(ClientInterface client) throws RemoteException;

    void send(ClientInterface speaker, String message) throws RemoteException;

    void whisper(ClientInterface whisperer, ClientInterface whisperee, String message) throws RemoteException;

    ArrayList<ClientInterface> getClientsList() throws RemoteException;
}
