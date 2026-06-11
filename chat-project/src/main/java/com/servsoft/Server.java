package com.servsoft;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.io.PrintWriter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.io.OutputStream;
import java.time.*;
import java.time.format.DateTimeFormatter;

/**
 * This is the Server.java file. this file is responsible for running the server, recieving messages from users and sending them to other users and creating a chat log when closed. the user is given a nickname, and a timestamp everytime a message is sent.
 * Below will be a documentation on how this software works. 
 * @author Thomas John Kendall
 */

public class Server implements Runnable{

    private static final int chatPort = 2211;
    private static ArrayList<ConnectionHandler> connections;
    private ServerSocket server;
    private ExecutorService pool;
    private static boolean isOnline = false;
    public boolean isOnline() {
        return isOnline;
    }
    

    public Server() {
        connections = new ArrayList<>();
    }

    public static void main(String[] args) {   
            new Thread(new Server()).start();
            new Thread(new WebServer(8080)).start();
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
        server = new ServerSocket(chatPort);
        pool = Executors.newCachedThreadPool();
        isOnline = true;
        System.out.println("Chat server started.");
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
 * This where the contents of the server status page is handled.
 * information like if the chat server is online, how many people are connected and what the chat server's port is.
 */
    public static class DefaultHandler implements HttpHandler{

        @Override
        public void handle(HttpExchange exchange) throws IOException{
            OutputStream out = exchange.getResponseBody();
            
            StringBuilder sb = new StringBuilder();
            sb.append("<html><body>");
            sb.append("<h1>Chat Server Status</h1>");
            sb.append("Chat server port is: " + chatPort + "<br>");
            if(isOnline) {
            sb.append("Online: True<br>");
            } else {
            sb.append("Online: False<br>");
            }
            sb.append("Users Online: " + connections.size());
            sb.append("</body></html>");

            byte[] responseBytes = sb.toString().getBytes("UTF-8");
            exchange.sendResponseHeaders(200, responseBytes.length);
            out.write(responseBytes);
            out.flush();
            out.close();
        }
        
       
    }


    /**
     * This section is responsible for establishing the http server, which is used for displaying server status and info on a webpage while its running.
     */
    public static class WebServer implements Runnable {
        private int port;

        WebServer(int port) {
            this.port = port;
        }

        public void run() {
            ExecutorService threadPool = Executors.newCachedThreadPool();
            Thread.setDefaultUncaughtExceptionHandler((Thread t, Throwable e) -> {
                e.printStackTrace();
            });
            try{
                HttpServer server = HttpServer.create(new InetSocketAddress("localhost", port), 0);
                server.createContext("/", new DefaultHandler());
                server.setExecutor(threadPool);
                server.start();
                System.out.println("Http server started.");
            } catch(Exception e){
                e.printStackTrace();
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
         * writer: writes all chat messages sent to a chat log file. this is saved as a .json file by default but can be changed to whatever the operator's preference. 
         * Having a chat log is incredibly important for legal concerns; if someone uses the server for illegal or malicious reasons, its important to have hard proof of them doing so.
         * having timestamps everytime a message sent is also for this same reason. 
         * the contents are properly saved once the server is closed. if no pre-existing chatlog exists to write to, it will create one itself.
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
