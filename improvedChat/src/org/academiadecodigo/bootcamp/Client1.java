package org.academiadecodigo.bootcamp;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client1 {
    private String HOST = "127.0.0.1";
    private final int PORT = 55555;
    private BufferedReader breader = null;
    private BufferedWriter bwriter = null;
    private String username;
    private int userId;

    public void start() {
        Socket clientSocket = null;
        Scanner scanner = new Scanner(System.in);

        try {

            clientSocket = new Socket(HOST, PORT);

            bwriter = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            breader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            bwriter.write("Request userId");
            bwriter.newLine();
            bwriter.flush();

           userId = Integer.parseInt(breader.readLine());

            //Starting chat
            Thread thread = new Thread(new ClientWorker());
            thread.start();

        } catch (Exception e) {
            System.out.println("You need to run the server first");
            e.printStackTrace();
        }

        try {
            String message;
            boolean beginning= true;

            while (true) {
                if (beginning){
                    message = "/alias";
                    beginning = false;
                } else {
                    message = scanner.nextLine();
                }
                switch (message) {
                    case ("/quit"):
                        System.exit(0);
                        break;

                    case ("/help"):
                        System.out.println("Controls:");
                        System.out.println("/help  show all commands");
                        System.out.println("/list  shows all users in the chat");
                        System.out.println("/alias  allows the user to change his username");
                        System.out.println("/quit  ends this username's chat");
                        break;
                    default:

                        bwriter.write(message);
                        bwriter.newLine();
                        bwriter.flush();
                        break;
                }
            }
        } catch (IOException e) {
            System.out.println("Couldn't send message.");
            System.exit(1);
        } finally {
            close(clientSocket);
        }

    }

    private void close(Socket socket) {
        if (socket == null) {
            return;
        }
        try {
            socket.close();
        } catch (IOException e) {
            System.out.println("There was a problem closing the socket");
        }
    }

    //Class ClientWorker
    private class ClientWorker implements Runnable {


        @Override
        public void run() {

            String message1 = null;

            while (true) {
                try {
                    message1 = breader.readLine();
                    if (message1 == null) {
                        System.exit(0);
                    }

                } catch (Exception e) {
                    System.out.println("Couldn't read message");
                    System.exit(1);
                }

                System.out.println(message1);


            }
        }
    }
}
