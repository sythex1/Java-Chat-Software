package com.servsoft;
import java.io.BufferedReader;
import java.io.IOException;
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
    private boolean done;
    private ExecutorService pool;

    public Server() {
        done = false;
        connections = new ArrayList<>();
    }

    @Override

    //establishes server
    public void run() {
        try{
        server = new ServerSocket(0211);
        pool = Executors.newCachedThreadPool();
        while (!done) {
           Socket client = server.accept();
           ConnectionHandler handler = new ConnectionHandler(client);
           connections.add(handler);
           pool.execute(handler);
           }
        } catch (IOException e) {
            shutdown();
        }
    }
    
    public void broadcast(String message){
        for (ConnectionHandler ch : connections) {
            if (ch != null) {
                ch.sendMessage(message);
            }
        }
    }

    public void shutdown() {
    try {
        done = true;
        if (!server.isClosed()) {
           server.close();
       }
        for (ConnectionHandler ch : connections) {
            ch.shutdown();
       }
    } catch (IOException e){}
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
                shutdown();
            }

        }

        public void sendMessage(String message) {
            out.println(message);
        }

        public void shutdown() {
            try {
            in.close();
            out.close(); 
            if (!client.isClosed()) {
                client.close();
             }
            } catch (IOException e) {}
        }

        public static void main(String[] args) {
            Server server = new Server();
            server.run();
        }
    }
}
