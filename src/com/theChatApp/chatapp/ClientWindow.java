package com.theChatApp.chatapp;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;

public class ClientWindow extends JFrame implements Runnable {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtMessage;
	private JTextArea history;
	private DefaultCaret caret;
	private Client client;
	
	private Thread run,listen;
	private boolean running = false;
	
	public ClientWindow(String name,String address,int port) {
		client = new Client(name,address,port);
		boolean check = client.openConnection(address);
		
		if(!check){
			System.err.println("Connection failed");
			console("Connection falied");
		}
		createWindow();
		console("Establishing a connection to the : " + address + " : " +port + " user " +name);
		String connection = "/c/" + name +"/e/";
		client.send(connection.getBytes());
		running = true;
		run = new Thread(this,"Running");
		run.start();
	}

	public void createWindow(){
		try{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}catch(Exception e){
			e.printStackTrace();
		}
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(850,550);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWeights = new double[]{0.0, 1.0, 0.0, 0.0};
		gbl_contentPane.columnWidths = new int[] { 28, 815, 30, 7 }; // SUM = 880
		gbl_contentPane.rowHeights = new int[] { 25, 485, 40 }; // SUM = 550
		contentPane.setLayout(gbl_contentPane);

		history = new JTextArea();
		history.setEditable(false);
		caret = (DefaultCaret)history.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		
		JScrollPane scroll = new JScrollPane(history);
		GridBagConstraints scrollConstraints = new GridBagConstraints();
		scrollConstraints.insets = new Insets(0, 20, 20, 20);
		scrollConstraints.fill = GridBagConstraints.BOTH;
		scrollConstraints.gridx = 0;
		scrollConstraints.gridy = 0;
		scrollConstraints.gridwidth = 3;
		scrollConstraints.gridheight = 2;
		contentPane.add(scroll, scrollConstraints);
		
		txtMessage = new JTextField();
		txtMessage.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER){
					send(txtMessage.getText(),true);
				}
			}
		});
		GridBagConstraints gbc_txtMessage = new GridBagConstraints();
		gbc_txtMessage.insets = new Insets(0, 0, 0, 5);
		gbc_txtMessage.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtMessage.gridx = 1;
		gbc_txtMessage.gridy = 2;
		contentPane.add(txtMessage, gbc_txtMessage);
		txtMessage.setColumns(10);
		
		JButton btnNewButton = new JButton("Send");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				send(txtMessage.getText(),true);
			}
		});
		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.insets = new Insets(0, 0, 0, 5);
		gbc_btnNewButton.gridx = 2;
		gbc_btnNewButton.gridy = 2;
		contentPane.add(btnNewButton, gbc_btnNewButton);
		
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				String disconnect = "/d/"+client.getID()+"/e/";
				send(disconnect,false);
				running = false;
				client.close();
				
			}
		});
		setTitle("LeChat");
		setVisible(true);
		//when the client window is opened the pointer should be on message
		txtMessage.requestFocus();
	}
		public void send(String message,boolean text){
			//if there is no input message
			if(message.equals(""))
					return;
			if(text){
			message = client.getName() + " : " + message;
			message = "/m/" + message +"/e/";
			txtMessage.setText("");
			}
			client.send(message.getBytes());			
		}
		
		public void run(){
			listen();
		}
		
		//managing the clients
		public void listen(){
			listen = new Thread("Listen"){
				public void run(){
					while(running){
						String message = client.receive();
						if(message.startsWith("/c/")){
							client.setID(Integer.parseInt(message.split("/c/|/e/")[1]));
							console("Conencted to the server With the ID :" );
						}else if(message.startsWith("/m/")){
							//do something so that all of our clients should receive
							//since we can send the message to ourselves to avoid it
							String text = message.substring(3);
							text = text.split("/e/")[0];
							console(text);
						}else if(message.startsWith("/i/")){
							String text = "/i/" +client.getID() + "/e/";
							send(text,false);
						}
					}
				}
			};
			listen.start();
		}
		
		public void console(String message){
			history.append(message + " \n\r");
			history.setCaretPosition(history.getDocument().getLength());
		}
	
	}

