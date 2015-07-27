import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

class ServerTwo {

	public static final int port = 6002;
	public static ServerSocket welcomeSocket = null;
	public static ArrayList <String> clientnames2 = new ArrayList<String>();
	public static ArrayList<Socket> clientsockets2 = new ArrayList<Socket>();
	public static Socket ConnectionToServerOne;

	public ServerTwo() throws UnknownHostException, IOException {
		
	}


	public static void main(String args[]) throws UnknownHostException, IOException
	{
		
		ConnectionToServerOne = new Socket("127.0.0.1", 6001);
		ServerTwo.clientnames2.add("ServerTwo");
		ServerTwo.clientsockets2.add(ConnectionToServerOne);
		runSurver();
	}

	public static void runSurver() {

		try {
			
			welcomeSocket = new ServerSocket(port);

			while(true) {
				Socket connectionSocket = welcomeSocket.accept();
				new ServerThread(connectionSocket, clientnames2, clientsockets2, 2).start();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}