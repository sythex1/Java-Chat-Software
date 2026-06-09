package com.servsoft;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.io.PrintWriter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.time.*;
import java.time.format.DateTimeFormatter;

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
            System.out.println("Error. Server could not be established.");
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

        private Socket user;
        private BufferedReader in;
        private PrintWriter out;
        private String nick;
        
        public ConnectionHandler(Socket user) {
            this.user = user;
        }

        @Override

        // handles entering nickname, messaging and logging messages to a .json file
        public void run() {
            try{
                FileWriter writer = new FileWriter("ChatLog.json", true);
                out = new PrintWriter(user.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(user.getInputStream()));
                out.println("Welcome to the chat room, please enter a nickname: ");
                nick = in.readLine();
                System.out.println(nick + " connected!");
                broadcast(nick + " joined the chat!");
                String message;
                while ((message = in.readLine()) != null) {
                        LocalDateTime dateTime = LocalDateTime.now();
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
                        String time = dateTime.format(formatter);
                        broadcast("[" + time + "]" + " " + nick + ": " + message);
                        writer.write("[" + time + "]" + " " + nick + ": " + message + System.lineSeparator());
                        writer.flush();
                    }
                    writer.close();
            } catch(Exception e) {
                System.out.println(nick + " disconnected.");
            } 

        }

        public void sendMessage(String message) {
            out.println(message);
        }

    }
}
