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

/**
 * This is the Server.java file. this file is responsible for running the server, recieving messages from users and sending them to other users and creating a chat log when closed. the user is given a nickname, and a timestamp everytime a message is sent.
 * Below will be a documentation on how this software works. 
 * @author Thomas John Kendall
 */

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

    /**
     * This section is responsible for establishing the server.
     * Here is where you can find the port number for the server, which is 2211 by default. 
     * this is where you'll need to look if you want to change the server's port.
     * the server listens for a client to join, and once one does, it is accepted if the requesting port number matches and is established within the server.
     */
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
    
    /** 
     * Establishes the broadcast method, designed to send whatever message a user writes to every other user connected.
     */
    public void broadcast(String message){
        for (ConnectionHandler ch : connections) {
            if (ch != null) {
                ch.sendMessage(message);
            }
        }
    }


    /**
     * the user is what every client's information is saved to.
     * 
     */
    class ConnectionHandler implements Runnable{

        private Socket user;
        private BufferedReader in;
        private PrintWriter out;
        private String nick;
        
        public ConnectionHandler(Socket user) {
            this.user = user;
        }

        @Override

        /**
         * This is the main function in the server app, designed to display a message to users when they join, broadcasts to the rest of the user when they join (once they pick a nickname),
         * recieves whatever message they submitted, and sends it to the other users connected. every variable serves an important use here, so I'll go through them.
         * writer: writes all chat messages sent to a chat log file. this is saved as a .json file by default but can be changed to whatever the operator's preference. the contents are properly saved once the server is closed. if no pre-existing chatlog exists to write to, it will create one itself.
         * message: stores the message typed out and submitted by the user.
         * dateTime: grabs the current date and time.
         * formatter: formats the date and time into a set pattern. in this case its "dd-mm-yyyy" for the day, and "HH:mm:ss" for the time.
         * time: stores the finalised, formatted date and time.
         * nick: stores the nickname the user inputs.
         * the exception is triggered when a client leaves. not only does it broadcast when someone leaves to other users but also makes a note in the server itself.
         */
        public void run() {
            try{
                FileWriter writer = new FileWriter("ChatLog.json", true);
                out = new PrintWriter(user.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(user.getInputStream()));
                out.println("Welcome to the chat room, please enter a nickname: ");
                /**describe nick variable here */
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
                broadcast(nick + " has left the chat");
            } 

        }

        public void sendMessage(String message) {
            out.println(message);
        }

    }
}
