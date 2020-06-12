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
	private Socket clientSocket1 = null;

	private final Map<Integer, Socket> sockets = new Hashtable<>();
	private final Map<Integer, BufferedWriter> buWriter = new Hashtable<>();
	private final Map<Integer, BufferedReader> buReader = new Hashtable<>();
	private final Map<Integer, String> names = new Hashtable<>();
	private final Map<String, SendStrategy> commands = new Hashtable<>();

	private Block block = Block.getBlock();

	// Constructor

	public Server() {

		commands.put("/list", new ListUsers());
		commands.put("/alias", new Alias());
	}

	// Method start

	public void start() {

		// Server setup

		Thread thread;
		ServerSocket serverSocket = null;

		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			System.out.println("Port " + port + " is already being used.");
		}

		System.out.println("You can now ask clients to join in!");

		try {

			String currentHost = InetAddress.getLocalHost().toString().split("/")[1];
			System.out.println("The current host is " + currentHost);

		} catch (Exception e) {
			System.out.println("Could't get the current host.");
		}

		// Server loop

		while (true) {

			try {

				clientSocket1 = serverSocket.accept();

				thread = new Thread(new ServerWorker(clientSocket1,currentClient));
				thread.start();

				currentClient++;

			} catch (IOException e) {
				System.out.println("There was a problem setting up the new client");
			}

		}


	}

	public void send(String message, int userId) {

		synchronized (block) {

			BufferedWriter buffWriter = null;

			// Send message to all active users, except the one that sent the message

			for (int user : buWriter.keySet()) {

				try {

					if (user== userId || sockets.get(user).isClosed()) {
						continue;
					}

					buffWriter=buWriter.get(user);

					buffWriter.write(message);
					buffWriter.newLine();
					buffWriter.flush();

				} catch (IOException e) {
					System.out.println("It wasn't possible to send this message to user " + names.get(user) + ".");	
				}

			}
		}
	}
	// If the specified user doesn
	// If username is the same for the same user, returns true
	// If username already exists, returns false
	// If username doesn't exist, returns true and replaces the new username for the old username

	public boolean nameIsFree(int userId, String username) {

		synchronized (block) {

			if (names.get(userId)!=null){
				if (names.get(userId).equals(username)){
					return true;
				}	
			}

			for (int userNr : names.keySet()){

				if (names.get(userNr).equals(username)){
					return false;
				}
			}

			if (names.get(userId)==null){
				
				names.put(userId, username);
				
				System.out.println(username + " just entered the chat.");
				send(username + " just entered the chat.", userId);
				
				return true;
			}
			System.out.println(names.get(userId) + " changed his name to " 
			+ username + ".");
			send(names.get(userId) + " changed his name to " + username + ".", userId);
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

		// The receiving messages' loop
		@Override
		public void run() {


			try {
				BufferedReader breader = new BufferedReader(new InputStreamReader(clientsocket.getInputStream()));
				BufferedWriter bwriter = new BufferedWriter(new OutputStreamWriter(clientsocket.getOutputStream()));

				synchronized(block) {
					buReader.put(userId, breader);

					sockets.put(userId, clientsocket);

					buWriter.put(userId, bwriter);
				}

			} catch (IOException e) {
				System.out.println(" Couldn't send message");

			}

			while (true) {
				try {

					read = buReader.get(userId).readLine();

					if (read == null) {

						close(sockets.get(userId), username, userId);
						break;
					}
					
					if (commands.containsKey(read)) {

						setStrategy(commands.get(read));
						strategy.send(this, userId, read);

						continue;
					}

					read = "<" + username + "> " + read;
					System.out.println(read);

					send(read, userId);


				} catch (IOException e) {

					close(sockets.get(userId), username, userId);

					break;
				}
			}


		}

		public void close(Socket socket, String username, int userId) {
			System.out.println(username + " left the chat.");

			send(username + " left the chat.", userId);
			
			try {
				socket.close();
			} catch (IOException e) {
				System.out.println("Couldn't close the socket.");
			}	
			
			synchronized(block){
				names.remove(userId);
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
