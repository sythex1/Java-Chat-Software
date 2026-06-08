package com.servsoft;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.io.PrintWriter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable{

    private ArrayList<ConnectionHandler> connections;
    private ServerSocket server;
    private ExecutorService pool;

    public Server() {
        connections = new ArrayList<>();
    }

    public static void main(String[] args) {
            Server server = new Server();
            server.run();
        }

    @Override

    //establishes server
    public void run() {
        try{
        server = new ServerSocket(2211);
        pool = Executors.newCachedThreadPool();
        while (true) {
           Socket client = server.accept();
           ConnectionHandler handler = new ConnectionHandler(client);
           connections.add(handler);
           pool.execute(handler);
           }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void broadcast(String message){
        for (ConnectionHandler ch : connections) {
            if (ch != null) {
                ch.sendMessage(message);
            }
        }
    }


    //handles client connections
    class ConnectionHandler implements Runnable{

        private Socket client;
        private BufferedReader in;
        private PrintWriter out;
        private String nickname;
        
        public ConnectionHandler(Socket client) {
            this.client = client;
        }

        @Override

        // handles entering nickname and messaging
        public void run() {
            try{
                out = new PrintWriter(client.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                out.println("Welcome to the chat room, please enter a nickname: ");
                nickname = in.readLine();
                System.out.println(nickname + " connected!");
                broadcast(nickname + " joined the chat!");
                String message;
                while ((message = in.readLine()) != null) {
                        broadcast(nickname + ": " + message);
                    }
            } catch(Exception e) {
                e.printStackTrace();
            }

        }

        public void sendMessage(String message) {
            out.println(message);
        }

    }
}
