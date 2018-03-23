package com.theChatApp.chatapp.server;

import java.net.InetAddress;

public class ServerClient {
	public int port;
	public String name;
	public InetAddress address;
	public final int Id;
	public int attempt = 0;
	
	public ServerClient(String name,InetAddress address,int port,final int Id){
		this.name = name;
		this.port= port;
		this.address = address;
		this.Id = Id;
	}
	
	public int getID(){
		return Id;
	}
 
}
