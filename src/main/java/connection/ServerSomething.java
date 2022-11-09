package connection;
import db.ConnectionDB;

import java.io.*;
import java.net.Socket;
import java.sql.SQLException;

public class ServerSomething extends Thread{

    private Socket client;
    private  BufferedReader in;
    private  BufferedWriter out;

    private static final StringBuffer stringBuffer = new StringBuffer();

    private String name;

    public ServerSomething(Socket client) throws IOException {
        this.client = client;
        start();
    }

    public void run(){
        try {

            out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));

            ConnectionDB conn = new ConnectionDB(this.client);
            conn.getFlag();

            Server.addClient(this);

            System.out.println(Thread.currentThread() + "поток ожил");
            if(!stringBuffer.isEmpty()){
                String[] words = stringBuffer.toString().split("\n");
                for (String i : words){
                    out.write(i + "\n"); out.flush();
                }
            }
            String msg;
            name = conn.getUserName();
            while (!(msg = in.readLine()).equals("exit")) {

                for (ServerSomething i : Server.getClients()) {

                    if (!i.equals(this)) {
                        i.out.write(name+ ": " + msg + "\n");
                        i.out.flush();
                    } else{
                        stringBuffer.append(name+ ": " + msg + "\n");
                    }
                }

            }



        } catch (Exception e){
            e.printStackTrace();
        }

    }


}
