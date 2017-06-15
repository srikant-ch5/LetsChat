package com.theChatApp.chatapp.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class Server implements Runnable {
	
	private List<ServerClient> clients = new ArrayList<ServerClient>();
	private List<Integer> clientResponse = new ArrayList<Integer>();
	
	private DatagramSocket socket;
	private int port;
	private boolean running = false;
	private Thread run,manage,receive,send;
	private final int MAX_ATTEMPT = 5;
	private boolean raw = false;
	
	public  Server(int port){
		this.port = port;
		try{
			socket = new DatagramSocket(port);
		}catch(SocketException e){
			e.printStackTrace();
		}
		//running the server thread
		run = new Thread(this,"Server");
		run.start();
	}
	
	public void run(){
		running = true;
		System.out.println("Server started on port "+ port);
		manageClients();
		receive();
		
		//let the server send commands to clients
		Scanner sc = new Scanner(System.in);
		while(running){
			//running means waits for the input since this is in seperate thread
			String text = sc.nextLine();
			if(!text.startsWith("/")){
				sendToAll("/m/Server : " + text + "/e/");
				continue;
			}
			text = text.substring(1);
			//if we want to see the raw packets we have to type raw
			if(text.equals("/raw")){
				if(raw)
					System.out.println("raw mode on");
				else 
					System.out.println("raw mode off");
				
				raw = !raw;
			}else if(text.equals("clients")){//to see who are online
				System.out.println("Clients who are online are :");
				System.out.println("#####");
				for(int i=0;i<clients.size();i++){
					ServerClient c = clients.get(i);
					System.out.println(c.name + " : " );
				}
				System.out.println("#####");
			}
		}
		
	}
	
	private void manageClients(){
		manage = new Thread("Manage"){
			public void run(){
				//Managing the clients
				while(running){
					sendToAll("/i/server");
					//let slow down the process
					try{
						Thread.sleep(2000);
					}catch(InterruptedException e){
						e.printStackTrace();
					}
					for(int i=0;i<clients.size();i++){
						ServerClient c = clients.get(i);
						
						if(!clientResponse.contains(c.getID())){
							if(c.attempt >= MAX_ATTEMPT){
								disconnect(c.getID(),false);
							}else{
								c.attempt++;
							}
						}else {
							clientResponse.remove(new Integer(c.getID()));
							c.attempt = 0;
						}
					}
				}
		}
	};
	manage.start();
	}
	
	private void receive(){
		receive = new Thread("Receive"){
			public void run(){
				while(running){
					byte[] data = new byte[1024];
					DatagramPacket packet = new DatagramPacket(data,data.length);
					try {
						socket.receive(packet);
					} catch (IOException e) {
						e.printStackTrace();
					}
					process(packet);
					//create a new ServerCLient obj and then add to the clients
				}
			}
		};
		receive.start();
	}
	
	private void sendToAll(String message){
		if(message.startsWith("/m/")){
			String text = message.substring(3);
			text = text.split("/e/")[0];
			System.out.println(text);
		}
		for(int i=0;i<clients.size();i++){
			ServerClient client = clients.get(i);
			send(message.getBytes(),client.address,client.port);
		}
	}
	
	private void send(final byte[] data,final InetAddress address,final int port ){
		send = new Thread("Send"){
			public void run(){
				DatagramPacket packet = new DatagramPacket(data,data.length,address,port);
				try{
					socket.send(packet);
				}catch(IOException e){
					e.printStackTrace();
				}
			}
		};
		send.start();
	}
	
	private void send(String message,InetAddress address,int port){
		message += "/e/";
		send(message.getBytes(),address,port);
	}
	
	public void process(DatagramPacket packet){
		String str = new String(packet.getData());
		
		if(raw){//raw mode is on
			System.out.println(str);
		}
		
		if(str.startsWith("/c/")){
			String name = str.split("/c/|/e/")[1];
			int id = UniqueIdentifier.getIdentifier();
			System.out.println(name + " ( "+id +") connected" );
			System.out.println("Identifier : " +id);
			clients.add(new ServerClient(name,packet.getAddress(),packet.getPort(),id));
			String ID = "/c/" + id;
			send(ID, packet.getAddress(), packet.getPort());
		}else if(str.startsWith("/m/")){
			sendToAll(str);
		}else if(str.startsWith("/d/")){
			String id = str.split("/d/|/e/")[1];
			disconnect(Integer.parseInt(id),true);
			
		}else if(str.startsWith("/i/")){
			clientResponse.add(Integer.parseInt(str.split("/i/|/e/")[1]));
		}
		else{
			System.out.println(str);
		}
	}
	//disconnect the client with the passed id
	public void disconnect(int id,boolean status){
		ServerClient c = null;
		for(int i =0;i<clients.size();i++){
			if(clients.get(i).getID() == id){
				c= clients.get(i);
				clients.remove(i);
				break;
			}
		}
		String message = "";
		if (status) {
			message = "Client " + c.name + " (" + c.getID() + ") @ " + c.address.toString() + ":" + c.port + " disconnected.";
		} else {
			message = "Client " + c.name + " (" + c.getID() + ") @ " + c.address.toString() + ":" + c.port + " timed out.";
		}
		System.out.println(message);
	}
	
}
