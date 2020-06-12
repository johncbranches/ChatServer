package org.academiadecodigo.bootcamp;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.Map;

public class ListUsers implements SendStrategy {

    private Map<Integer, BufferedWriter> buWriter;
    private Map<Integer, String> names;

    @Override
    public void send(Server.ServerWorker server, int userId, String message) {
        buWriter = server.getBuWriter();
        names = server.getNames();

        //String builder message
        StringBuilder stringBuilder = new StringBuilder();
        
        stringBuilder.append("Users online:");
        
        for (int userNr : buWriter.keySet()) {
            stringBuilder.append("\n" + names.get(userNr));
        }
        
        message = stringBuilder.toString();

        BufferedWriter bw = buWriter.get(userId);
        try {
        bw.write(message);
        bw.newLine();
        bw.flush();
        } catch (Exception e) {
        	System.out.println("Couldn't send message.");
        }
            }
}
