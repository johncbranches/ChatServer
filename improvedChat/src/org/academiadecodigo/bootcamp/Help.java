package org.academiadecodigo.bootcamp;

import org.academiadecodigo.bootcamp.Block;
import org.academiadecodigo.bootcamp.SendStrategy;
import org.academiadecodigo.bootcamp.Server;

import java.io.BufferedWriter;
import java.net.Socket;
import java.util.Map;

public class Help implements SendStrategy {
    private Map<Integer, BufferedWriter> buWriter;
    private Map<Integer, Socket> sockets;
    private Block block = Block.getBlock();

    @Override
    public void send(Server.ServerWorker Get, int userId, String message) {

    }
}
