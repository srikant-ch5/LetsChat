package com.theChatApp.chatapp;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.JTable;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Login extends JFrame {

	private static final long serialVersionUID = 5392340202885398567L;
	private JPanel contentPane;
	private JTextField txtName;
	private JTextField txtAddress;
	private JLabel lblIpAddress;
	private JTextField txtPort;
	private JLabel lblAddressDesc;
	private JLabel lblPort;	
	
	public Login() {

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(300,450);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		txtName = new JTextField();
		txtName.setBounds(60, 84, 153, 19);
		contentPane.add(txtName);
		txtName.setColumns(10);
		
		JLabel lblName = new JLabel("Name:");
		lblName.setBounds(119, 57, 70, 15);
		contentPane.add(lblName);
		
		txtAddress = new JTextField();
		txtAddress.setBounds(60, 162, 151, 19);
		contentPane.add(txtAddress);
		txtAddress.setColumns(10);
		
		lblIpAddress = new JLabel("IP Address:");
		lblIpAddress.setBounds(99, 135, 114, 15);
		contentPane.add(lblIpAddress);
		
		txtPort = new JTextField();
		txtPort.setColumns(10);
		txtPort.setBounds(60, 267, 151, 19);
		contentPane.add(txtPort);
		
		lblAddressDesc = new JLabel("(eg:192.600.809)");
		lblAddressDesc.setBounds(83, 193, 130, 15);
		contentPane.add(lblAddressDesc);
		
		lblPort = new JLabel("Port:");
		lblPort.setBounds(119, 247, 70, 15);
		contentPane.add(lblPort);
		
		JLabel lblPortDesc = new JLabel("(eg: 8080)");
		lblPortDesc.setBounds(96, 290, 130, 15);
		contentPane.add(lblPortDesc);
		
		JButton btnLogin = new JButton("Login");
		btnLogin.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				String name = txtName.getText();
				String address = txtAddress.getText();
				int port = Integer.parseInt(txtPort.getText());
				login(name,address ,port);
			}
		});
		btnLogin.setBounds(82, 341, 117, 25);
		contentPane.add(btnLogin);
	}
	
	private void login(String name,String address,int port){
		dispose();
		new ClientWindow(name,address,port);
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Login frame = new Login();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
