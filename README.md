Hello, this is my first attempt at making a chat room, with a seperate server and client app, all in Java.

This is my project has been made using Maven, so to build the project, install maven (https://maven.apache.org/) use the command "mvn package". You should find the .jar file created in Java-Chat-Software/chat-project/target. A build has already been created, so you would only want to do this if you've made your own changes.

to run the java build, download the zip file and extract the contents into a folder. once done, navigate to the "chat-project" folder and open it with your terminal and run your command of choice:

"java -cp .\target\chat-project-1.0.jar com.servsoft.Server" will run the server application.
"java -cp .\target\chat-project-1.0.jar com.chatsoft.Chat" will run the chat application.


