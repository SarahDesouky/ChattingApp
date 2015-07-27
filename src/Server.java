import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

class Server {

	public static final int port = 6001;
	public static ServerSocket welcomeSocket = null;
	public static ArrayList <String> clientnames1 = new ArrayList<String>();
	public static ArrayList<Socket> clientsockets1 = new ArrayList<Socket>();
	public static Socket ConnectionToServerTwo;
	

	public Server() throws UnknownHostException, IOException {		
	}


	public static void main(String args[])
	{	
		runSurver();
	}

	public static void runSurver() {

		try {
			welcomeSocket = new ServerSocket(port);

			while(true) {
				Socket connectionSocket = welcomeSocket.accept();
				new ServerThread(connectionSocket, clientnames1, clientsockets1, 1).start();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}