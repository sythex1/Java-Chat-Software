Hello, this is my first attempt at making a chat room, with a seperate server and client app, all in Java.

This is my project has been made using Maven, so to build the project, install maven (https://maven.apache.org/) use the command "mvn package". You should find the .jar file created in Java-Chat-Software/chat-project/target. A build has already been created, so you would only want to do this if you've made your own changes. The chat application is also set to look for localhost, so to look for a server on a different device you will need to change what IP it looks for in the code and re-build.

once the server application is ran, a server status webpage should be created. this is generated in localhost and the port number by default is 8080, so to access it just type "http://localhost:8080/" into the URL bar.

to run the java build, download the zip file and extract the contents into a folder. once done, navigate to the "chat-project" folder and open it with your terminal and run your command of choice:

"java -cp .\target\chat-project-1.0.jar com.servsoft.Server" will run the server application.
"java -cp .\target\chat-project-1.0.jar com.chatsoft.Chat" will run the chat application.


Current features

Server Application (using classes "Server" and "ConnectionHandler"):
•	Server can be established and accept clients to send and receive messages
•	Can give users a nickname
•	Can notify other users when someone joins/leaves
•	Gives each message a time stamp detailing what day and time the message was sent
•	Can log all chat messages into a .json file
•	Can establish a http server for a server status and information webpage (WebServer class)
•	Can display server status information such as the port number, if the chat server is online and how many users are currently chatting (DefaultHandler class)

Chat Application (using classes "Chat" and "InputHandler"):
•	Users can join a server and choose which nickname they would like to use during the session
•	Users can send and receive messages from other users via the server
•	Users can receive messages while typing/idle





