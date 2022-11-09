package connection;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {


    private static Socket client;

    public static void main(String[] args) throws IOException, InterruptedException {
        client= new Socket(InetAddress.getLocalHost(),4040);
        new Writer(client).start();
        new Reader(client).start();

    }


}
