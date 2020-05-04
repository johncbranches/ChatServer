package org.academiadecodigo.bootcamp;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.Map;

public class ListUsers implements SendStrategy {

    private Map<Integer, BufferedWriter> buWriter;
    private Map<Integer, String> names;
    private Block block = Block.getBlock();

    @Override
    public void send(Server.ServerWorker Get, int userId, String message) {
        buWriter = Get.getBuWriter();
        names = Get.getNames();

        //String builder message
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Users online:\n");
        for (int userNr : buWriter.keySet()) {
            stringBuilder.append(names.get(userNr) + "\n");
        }
        message = stringBuilder.toString();

        synchronized (block) {

            for (int user : buWriter.keySet()) {
                try {

                    buWriter.get(user).write(message + "\n");
                    buWriter.get(user).flush();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
