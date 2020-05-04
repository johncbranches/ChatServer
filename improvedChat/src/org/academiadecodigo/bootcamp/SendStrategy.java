package org.academiadecodigo.bootcamp;

public interface SendStrategy {

    void send(Server.ServerWorker Get,int userId, String message);
}
