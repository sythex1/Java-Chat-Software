package com.chatsoft;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;
import java.io.PrintWriter;
import java.io.InputStreamReader;

/**
 * This is the Chat.java file. this file is responsible for allowing clients to connect to the server as users and submit messages to send to others.
 * Below is a documentation on how this file works.
 * @author Thomas John Kendall
 */
public class Chat implements Runnable {

    private BufferedReader in;
    private PrintWriter out;

    /**
     * this method is whats responsible for searching for servers and connecting once one that matches the port number and IP address is found. 
     * IP address is set to "127.0.0.1" for localhost by default, and should be changed if someone wishes to connect to a server on a seperate device.
     * the "out" variable is responsible for sending out the messages typed to the server, while the "in" variable is responsible for recieving messages the server sent.
     * the input handler is responsible for listening for keyboard input by the client. this is then sent to a seperate thread, allowing the app to listen for incoming messages while the user is still typing.
     * the "inMessage" is whats responsible for actually displaying the messages recieved from the server in the users console.
     */
    @Override
    public void run() {
        try {
            Socket client = new Socket("127.0.0.1", 2211);

            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));

            InputHandler inHandler = new InputHandler(); 
            Thread t = new Thread(inHandler);
            t.start();

            String inMessage;
            while ((inMessage = in.readLine()) != null) {
                System.out.println(inMessage);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *the "inReader" is whats responsible for actually letting the user type into the console while the app is running.
     */
    class InputHandler implements Runnable {

        @Override
        public void run() {
            try {
                BufferedReader inReader =
                        new BufferedReader(new InputStreamReader(System.in));

                while (true) {
                    String message = inReader.readLine();
                    out.println(message);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        Chat chatClient = new Chat();
        chatClient.run();
    }
}