import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
public class Client extends JFrame implements ActionListener  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static boolean run = true;
	static String name;
	static DataOutputStream outToServer;
	static BufferedReader inFromServer;
	static boolean closeConnection;
	static String sentence;
	static Socket clientSocket = null;
	public static int port = 0;
	public static int count = 2; //to tell us when to set gotPort to true

	static JTextArea chat = new JTextArea();
	static JTextField input = new JTextField();
	static JButton send = new JButton("Send");
	static JButton quit = new JButton("Quit");
	static JButton connect = new JButton("Connect");
	static ArrayList<String> chatLog = new ArrayList<String>();
	static boolean sendingName = true;
	static boolean gotPort = false;

	public Client() {
		super("Chat Application");
		setSize(600,600);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		JPanel keys = new JPanel();
		keys.setLayout(new GridLayout(2,1));
		keys.add(quit);
		keys.add(connect);
		JPanel south = new JPanel();
		south.setLayout(new BorderLayout());
		south.add("West", keys);
		south.add("Center", input);
		south.add("East", send);
		JLabel title = new JLabel("Chatting Application", JLabel.CENTER);
		setLayout(new BorderLayout());
		add("North", title);
		add("Center", chat);
		add("South", south);
		chat.setEditable(false);
		setVisible(true);
		send.addActionListener(this);
		quit.addActionListener(this);
		connect.addActionListener(this);
		//new Server();
		chat.append("User, please send the port number you wish to connect to," + '\n' + "then send your name and finally press connect to connect to server." + '\n');

	}

	public static boolean contains(Socket s) {
		for(int i = 0; i <Server.clientsockets1.size();i++) {
			if(Server.clientsockets1.get(i)==s)
				return true;
		}
		return false;
	}

	public static void main(String args[]) throws Exception 	{
		new Client();
	}

	public void connect() {
		try {

			clientSocket = new Socket("127.0.0.1", port);
			outToServer = new DataOutputStream(clientSocket.getOutputStream());
			inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));


		}catch(Exception e) {
			chat.append("Could not connect to server." + '\n');
		}
		boolean nameEntered = false;
		while(!nameEntered) {
			try {
				name = chatLog.get(chatLog.size()-1);
				outToServer.writeBytes(name + '\n');
				nameEntered = true;

			} catch (IOException e) {
				chat.append("Please enter Your name first" + '\n');
			}
		}
		try {
			new ClientThread(clientSocket).start();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void send() {
		String s = input.getText();
		if(sendingName == false && gotPort == true) {
			chat.append("Me: " + ServerThread.getMessage(s) + '\n');
		}
		chatLog.add(s);
		if(sendingName == true && gotPort == false) {
			port = Integer.parseInt(input.getText());
		}
		if(sendingName == false && gotPort == true) {	
			try {
				outToServer.writeBytes(s + '\n');

				if(s.equalsIgnoreCase("BYE") || s.equalsIgnoreCase("QUIT")) {
					outToServer.close();
					inFromServer.close();
					clientSocket.close();
					this.dispose();
				}
			}catch(Exception e) {
				chat.append("You can not send an empty string!");
				e.printStackTrace();
			}
			input.setText("");
		}
		gotPort = true;
		count--;
		if(count ==0)
			sendingName = false;
		input.setText("");
	}

	public void quit() {
		String s = chatLog.get(chatLog.size()-1);
		try {
			if(clientSocket!= null && (s.equalsIgnoreCase("BYE") || s.equalsIgnoreCase("QUIT"))) {
				outToServer.close();
				inFromServer.close();
				clientSocket.close();
				this.dispose();
			}

		}catch(Exception e) {
		}
	}


	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == send) {
			send();
		}
		if(e.getSource() == connect) {
			connect();
		}
		if(e.getSource() == quit) {
			chatLog.add("quit");
			quit();
		}
	}
}