package org.academiadecodigo.bootcamp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.Map;

public class Alias implements SendStrategy{
    private Map<Integer, BufferedWriter> buWriter;
    private Map<Integer, BufferedReader> buReader;
    private Map<Integer, Socket> sockets;

    @Override
    public void send(Server.ServerWorker server, int userId, String message) {
        buWriter = server.getBuWriter();
        buReader = server.getBuReader();
        sockets = server.getSockets();

        BufferedWriter bwriter = buWriter.get(userId);
        BufferedReader breader = buReader.get(userId);
        Socket socket = sockets.get(userId);
        String newUsername = null;
        try{

        boolean validUsername = false;

            while (!validUsername) {
                
            	bwriter.write("Type the new username:");
                bwriter.newLine();
                bwriter.flush();

                newUsername = breader.readLine();
                newUsername = newUsername.trim();

                //Check if username exists
                if (server.checkName(userId, newUsername)) {
                    validUsername = true;
                } else {
                    bwriter.write("That username is taken. Please type a different one");
                    bwriter.newLine();
                    bwriter.flush();
                }
            }
            
            bwriter.write("Your username was successfully updated. You can chat now.");
            bwriter.newLine();
            bwriter.flush();

        } catch (IOException e) {
            System.out.println("Couldn't send or receive information.");
        }

        server.setUsername(newUsername);
    }
}
