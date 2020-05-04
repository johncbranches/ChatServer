package org.academiadecodigo.bootcamp;

import java.io.IOException;
import java.net.Socket;
import java.util.Hashtable;
import java.util.Map;

public class ReceiveNull implements SendStrategy{

    private Map<Integer, Socket> sockets;
    private  Map<Integer, String> names = new Hashtable<>();

    @Override
    public void send(Server.ServerWorker Get, int userId, String message) {
        sockets=Get.getSockets();
names=Get.getNames();

        try {
            sockets.get(userId).close();
        } catch (IOException e) {
            System.out.println("Couldn't close the connection from user " + names.get(userId));
        }

    }

}
