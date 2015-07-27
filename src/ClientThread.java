import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;


public class ClientThread extends Thread {

	Socket clientSocket;
	static DataOutputStream outToServer;
	static BufferedReader inFromServer;
	
	
	public ClientThread() {
		
	}
	
	public ClientThread(Socket s) throws IOException {
		clientSocket = s;
		outToServer = new DataOutputStream(clientSocket.getOutputStream());
		inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	}
	
	public void run() {
		String response;

		try {
			while(true){
				response = inFromServer.readLine();
				Client.chat.append(response + '\n');
				//System.out.println(response);
			}
		} catch (Exception e) {
			System.out.println("OOPS! It seems like the other party has just closed his/her connection.");
		}
	}
}
