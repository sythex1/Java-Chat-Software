package com.chatsoft;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;
import java.io.PrintWriter;
import java.io.InputStreamReader;
public class Chat implements Runnable {

    private BufferedReader in;
    private PrintWriter out;

    //establishes connection to the server and user
    @Override
    public void run() {
        try{
            Socket user = new Socket("127.0.0.1", 2211);
            out = new PrintWriter(user.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(user.getInputStream()));
            InputHandler inHandler = new InputHandler(); 
            Thread t = new Thread(inHandler);
            t.start();

            String inMessage;
            while((inMessage = in.readLine()) != null) {
                System.out.println(inMessage);

            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    
    
    class InputHandler implements Runnable {

    //reads user
    @Override
    public void run() {
        try{
            BufferedReader inReader = new BufferedReader(new InputStreamReader(System.in));
            while (true){
                String message = inReader.readLine();
                out.println(message);
            }

        } catch (IOException e){
            e.printStackTrace();
        }

    }

    }

    public static void main(String[] args) {
        Chat chatClient = new Chat();
        chatClient.run();
    }
}