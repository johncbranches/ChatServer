package org.academiadecodigo.bootcamp;

import java.io.*;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server {
    private int currentClient = 0;
    private final int port = 55555;
    private final int maxClients = 10;
    private final Map<Integer, Socket> sockets = new Hashtable<>();
    private final Map<Integer, BufferedWriter> buWriter = new Hashtable<>();
    private final Map<String, SendStrategy> commands = new Hashtable<>();
    private final Map<Integer, BufferedReader> buReader = new Hashtable<>();
    private final Map<Integer, String> names = new Hashtable<>();



    private Thread[] serverWorkers = new Thread[maxClients];
    private Socket clientSocket1 = null;
    private Block block = Block.getBlock();

    public void start() {


        Thread thread;
        commands.put("/list", new ListUsers());
        commands.put("/alias", new Alias());


        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.out.println("Couldn't connect to port");
        }
        System.out.println("You can now ask clients to join in!");
        while (true) {
            try {

                clientSocket1 = serverSocket.accept();

                thread = new Thread(new ServerWorker(clientSocket1,currentClient));
                thread.start();

                serverWorkers[currentClient] = thread;
                //bwriter[currentClient] = new BufferedWriter(new OutputStreamWriter(clientSocket[currentClient].getOutputStream()));

                int nrClosed = 0;
                currentClient++;


            } catch (IOException e) {
                e.printStackTrace();
            }

        }


    }

    public void send(String message, int userId) {
        synchronized (block) {
            for (int user : buWriter.keySet()) {
                try {

                    if (user== userId || sockets.get(user).isClosed()) {
                        continue;
                    }


                    buWriter.get(user).write(message);
                    buWriter.get(user).flush();
                } catch (IOException e) {
                    System.out.println("Couldn't send message");
                    System.exit(1);
                }
            }
        }
    }
        public boolean nameIsFree(int userId, String username) {
            synchronized (block) {
                for (int userNr:names.keySet()){
                    if (names.get(userNr).equals(username)){
                        return false;
                    }
                }
                if (names.get(userId)==null){
                    names.put(userId, username);
                    return true;
                }
                if (names.get(userId).equals(username)){
                    return true;
                }
                if (names.get(userId).equals(username)){
                    return false;
                }

                names.replace(userId,username);

                return true;
            }
        }



    public class ServerWorker implements Runnable {

        //Properties
        private Socket clientsocket;

        private String read;
        private String username;
        private int userId;
        private SendStrategy strategy;


        //Constructor
        private ServerWorker(Socket clientsocket, int userId) {
            this.clientsocket = clientsocket;
           this.userId = userId;

        }

        public boolean checkName(int userId, String username){
            return nameIsFree(userId, username);
        }

        //run
        @Override
        public void run() {


            try {
                BufferedReader breader = new BufferedReader(new InputStreamReader(clientsocket.getInputStream()));
                BufferedWriter bwriter = new BufferedWriter(new OutputStreamWriter(clientsocket.getOutputStream()));
                String received = breader.readLine();
                bwriter.write("" + userId);
                bwriter.newLine();
                bwriter.flush();
               /* boolean validUsername = false;
                // check if username is valid, and if so save it and send confirmation message ("//true")
                while (!validUsername) {
                    read = breader.readLine();
                    username = read.trim();
                    //Check if username exists
                    if (!nameIsFree(userId,username)) {
                        bwriter.write("//false");
                        bwriter.newLine();
                        bwriter.flush();
                        continue;
                    }

                    validUsername = true;
                    bwriter.write("//true");
                    bwriter.newLine();
                    bwriter.flush();

                }*/
                buReader.put(userId, breader);

                sockets.put(userId, clientsocket);

                buWriter.put(userId, bwriter);

            } catch (IOException e) {
                System.out.println(" Couldn't send message");
                System.exit(1);
            }
            
            while (true) {
                try {

                    read = buReader.get(userId).readLine();

                    if (commands.containsKey(read)) {
                        setStrategy(commands.get(read));
                        strategy.send(this, userId, read);
                        continue;
                    }

                    if (read == null) {
                        try {
                            sockets.get(userId).close();
                        } catch (IOException e) {
                            System.out.println("Couldn't close Socket.");
                        }
                        break;
                    }


                    read = "<" + username + "> " + read;
                    System.out.println(read);

                    send(read + "\n", userId);


                } catch (IOException e) {
                    System.out.println("Couldn't send the message");
                    System.exit(1);
                }
            }


        }

        public void setStrategy(SendStrategy strategy) {
            this.strategy = strategy;
        }

        public Map getBuWriter() {
            return buWriter;
        }

        public Map getSockets() {
            return sockets;
        }

        public Map getBuReader() {
            return buReader;
        }

        public void setUsername(String username) {
            this.username = username;
        }
        public Map<Integer, String> getNames() {
            return names;
    }

    }

}
