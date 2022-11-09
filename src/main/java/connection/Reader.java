package connection;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

public class Reader extends Thread{

    private Socket client;
    private BufferedReader in;


    public Reader (Socket client){
        this.client = client;

    }
    @Override
    public void run(){
        try {
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            while(true){
                String msg = in.readLine();
                System.out.println(msg);
            }
        } catch (Exception e){
            e.printStackTrace();
        }

    }
}
