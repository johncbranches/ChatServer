package org.academiadecodigo.bootcamp;

import java.io.*;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Scanner;

public class Client1 {

	private String HOST = "127.0.0.1";
	private final int PORT = 55555;
	private Socket clientSocket = null;
	private BufferedReader breader = null;
	private BufferedWriter bwriter = null;
	private Scanner scanner = new Scanner(System.in);

	public void start() {
	
		
		System.out.println("Type the host that appears on your server console.");
		System.out.println("It should be similar to     192.168.1.69");
        
		HOST = scanner.nextLine();
		
		try {

			clientSocket = new Socket(HOST, PORT);
			bwriter = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
			breader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

			// Starting chat
			Thread thread = new Thread(new ClientWorker());
			thread.start();

		} catch (Exception e) {
			System.out.println("Either you need to run the server first, or no server was"
					+ " found at that specified address.");
			System.exit(1);
		}

		try {
			String message;
			boolean beginning = true;
			
			while (true) {
				// assigning the name to the user using Strategy design pattern 
				//, class Alias.
				if (beginning) {
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
			System.out.println("The server seems to have been disconnected.");
			System.exit(1);
		} finally {
			close();
		}

	}

	private void close() {
		if (clientSocket == null) {
			return;
		}
		try {
			clientSocket.close();
			scanner.close();
		} catch (IOException e) {
			System.out.println("There was a problem closing the socket.");
		}
	}

	// Class ClientWorker
	private class ClientWorker implements Runnable {

		@Override
		public void run() {

			String message1 = null;

			while (true) {
				try {
					message1 = breader.readLine();
					if (message1 == null) {
						close();
						System.exit(0);
					}

				} catch (Exception e) {
					System.out.println("The server was disconnected.");
					close();
					System.exit(1);
				}

				System.out.println(message1);

			}
		}
	}
}
