package connection;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class Writer extends Thread{
    private Socket client;
    private BufferedWriter out;


    public Writer (Socket client){
        this.client = client;
    }
    @Override
    public void run(){
        try {
            out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
            BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
            while(true){
                String msg =console.readLine();
                out.write(msg+"\n"); out.flush();

            }
        } catch (Exception e){
            e.printStackTrace();
        }

    }
}
