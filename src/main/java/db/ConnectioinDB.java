package db;

import org.postgresql.ds.PGPoolingDataSource;
import org.postgresql.ds.PGSimpleDataSource;

import java.io.*;
import java.net.Socket;
import java.sql.*;
import java.util.Properties;

public class ConnectioinDB extends Thread {

    private Connection conn;
    private static int count;

    private boolean flag;
    private BufferedReader in;
    private BufferedWriter out;

    private String name;
    private Socket client;
    private static PGSimpleDataSource dataSource = new PGSimpleDataSource();

    static {

        dataSource.setServerNames(
                new String[]{"localhost"}
        );
        dataSource.setPortNumbers(
                new int[] {5432}
        );
        dataSource.setUser("postgres");
        dataSource.setPassword("root");
        dataSource.setDatabaseName("serverlogin");

    }

    public ConnectioinDB(Socket client) throws IOException {
        this.client = client;
        in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
        start();
    }

    @Override
    public void run() {
        try {
            this.flag = false;
            boolean flag = false;
            int count=0;
            this.conn = connect();
            out.write("have you ever been registered?" + "\n"); out.flush();
            String answer = in.readLine();
            switch (answer){
                case "yes":
                    logIn();
                    break;
                case "no":
                    register();

            }




            this.flag = true;
            synchronized (this) {

                notify();
            }
        }
        catch (IOException | SQLException e){
            throw new RuntimeException(e);
        }
    }

    public synchronized boolean getFlag() throws InterruptedException {
        wait();
        return this.flag;
    }

    public synchronized void setFlag(boolean flag){
        this.flag = flag;
    }

    public String getUserName() {
        return this.name;
    }


    private Connection connect() throws SQLException {
        return dataSource.getConnection();
    }


    private ResultSet getSet(String login) throws SQLException {
        String query = "SELECT * FROM users WHERE login = '" + login + "'";
        return conn.createStatement().executeQuery(query);
    }


    private void logIn() throws IOException, SQLException {
        out.write("enter yout login and password" + "\n"); out.flush();

        while(!flag) {
            String received = in.readLine();
            String[] words = received.split(" ");
            ResultSet result= getSet(words[0]);
            while (!result.isBeforeFirst()){
                out.write("login incorrect, please repeat" + "\n"); out.flush();
                received = in.readLine();
                words = received.split(" ");
                result= getSet(words[0]);
            }

            while (result.next()) {
                String passwd = result.getString("password");
                if (words.length == 2 && passwd.equals(words[1])){
                    this.name = words[0];
                    out.write("login success"+"\n"); out.flush();
                    flag = true;
                } else {
                    out.write("password incorrect, please repeat" + "\n"); out.flush();
                    break;

                }
            }
        }
    }

    public void register() throws SQLException, IOException {
        boolean nextStep = false;
        String[] recieve;
        out.write("Create your login and password" + "\n"); out.flush();
        PreparedStatement pstat = conn.prepareStatement("SELECT * FROM users", 	ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
        ResultSet result = pstat.executeQuery();

        while(!nextStep) {
            result.beforeFirst();
            recieve = in.readLine().split(" ");
            while (result.next()) {
                String login = result.getString("login");
                if (login.equals(recieve[0])) {
                    out.write("This login is exist. Please, enter another login" + "\n");
                    out.flush();
                    break;
                }
            }
            if(!result.next()) {
                putROW(recieve[0], recieve[1]);
                this.name = recieve[0];
                nextStep = true;

            }
        }
        out.write("account create successfully!" + "\n"); out.flush();
    }

    private void putROW(String login, String password){
        count++;
        String query = "INSERT INTO Users (login, password) VALUES ('"+ login + "','" + password + "')";
        try(Statement statement = conn.createStatement()){
            statement.execute(query);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }






}
