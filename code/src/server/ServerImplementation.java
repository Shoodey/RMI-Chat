package server;

import client.ClientInterface;
import helpers.Config;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

class ServerImplementation extends UnicastRemoteObject implements ServerInterface {

    static boolean online = false;
    private static Registry registry;
    private ArrayList<ClientInterface> clients;

    private ServerImplementation() throws RemoteException {
        clients = new ArrayList<>();
    }

    private static void start() throws RemoteException {
        registry = LocateRegistry.createRegistry(Config.PORT);
        registry.rebind(Config.SERVER_NAME, new ServerImplementation());
        System.out.println("Server running...");
        online = true;
    }

    private static void stop() throws RemoteException, NotBoundException {
        registry.unbind(Config.SERVER_NAME);
        UnicastRemoteObject.unexportObject(registry, true);
        System.out.println("Server terminated.");
        online = false;
    }

    @Override
    public ClientInterface register(ClientInterface newClient) throws RemoteException {
        clients.add(newClient);

        for (ClientInterface client : clients) {
            client.showClientLogin(newClient);
        }

        return newClient;
    }

    @Override
    public void send(ClientInterface speaker, String message) throws RemoteException {
        for (ClientInterface client : clients) {
            client.showClientMessage(speaker, message);
        }
    }

    @Override
    public void whisper(ClientInterface whisperer, ClientInterface whisperee, String message) throws RemoteException {
        whisperer.showClientMessage(whisperer, message, whisperee);
        whisperee.showClientMessage(whisperer, message, whisperee);
    }

    public ArrayList<ClientInterface> getClientsList() {
        return clients;
    }

    static String getStatus() {
        return online ? "Online" : "Offline";
    }

    static void toggleStatus() throws RemoteException, NotBoundException {
        if (online) stop();
        else start();
    }
}
