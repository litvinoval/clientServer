package connection;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Server {


    private static Socket client;
    private static ServerSocket server;

    private static volatile List<ServerSomething> clients = new ArrayList<>();

    public static void main(String[] args) throws IOException, SQLException {

        server = new ServerSocket(4040);
        while(true) {
            client = server.accept();
            ServerSomething serverSomething = new ServerSomething(client);

        }
    }
    public static synchronized void addClient(ServerSomething sm){
        clients.add(sm);
    }

    public static synchronized List<ServerSomething> getClients(){
        return clients;
    }



}
