package com.theChatApp.chatapp.server;

import java.net.InetAddress;

public class ServerClient {
	public int port;
	public String name;
	public InetAddress address;
	public final int ID;
	public int attempt = 0;
	
	public ServerClient(String name,InetAddress address,int port,final int ID){
		this.name = name;
		this.port= port;
		this.address = address;
		this.ID = ID;
	}
	
	public int getID(){
		return ID;
	}
 
}
