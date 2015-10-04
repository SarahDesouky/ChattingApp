import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;


public class ServerThread extends Thread {

	Socket socket = null;
	public static String clientSentence = "";
	BufferedReader inFromClient;
	static String message;
	static ArrayList <String> clientnames = new ArrayList<String>();
	static ArrayList <Socket> clientsockets = new ArrayList<Socket>();
	boolean join;
	int number = 0;
	boolean notAName;

	public ServerThread(Socket connectionSocket, ArrayList<String> c, ArrayList<Socket> s, int number) throws IOException {
		this.socket = connectionSocket;
		clientnames = c;
		clientsockets = s;
		this.number = number;

		if( (number == 1) && !(clientExists("ServerOne"))) {
			Server.ConnectionToServerTwo = new Socket("127.0.0.1",6002);
			clientsockets.add(Server.ConnectionToServerTwo);
			clientnames.add("ServerOne");
		}

	}


	public static boolean clientExists(String name) {
		for(int i = 0; i< clientnames.size();i++) {
			if(clientnames.get(i).equals(name))
				return true;
		}
		return false;
	}

	public static Socket getDestinationSocket(String sentence) throws IOException {
		String s = "";

		for(int i =0; i<sentence.length(); i++) {
			if(sentence.charAt(i)==',') {
				s= sentence.substring(0, i);
				break;
			}
			else {
				s+= sentence.charAt(i) + "";
			}
		}

		for(int i =0; i<clientnames.size();i++) {
			if(s.equals(clientnames.get(i))) {
				return clientsockets.get(i);
			}
		}
		return clientsockets.get(0);
	}

	public String getMemberList() {
		String s = "";
		for(int i =1 ; i<clientnames.size();i++) {
			s += clientnames.get(i) + " ";
		}
		return s;
	}

	public static String getMessage(String s) {
		String message = "";
		for(int i = 0; i<s.length();i++) {
			if(s.charAt(i)==',') {
				message = s.substring(i+1, s.length());
			}
		}
		return message;
	}

	public boolean comingFromClient(String s) {
		if(s.charAt(0)== '/'){
			return false;
		}
		return true;
	}

	public String removeSlash(String s) {
		if(s.charAt(0)== '/'){
			return s.substring(1);
		}
		else 
			return s;
	}


	public void run() {

		try {

			inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			DataOutputStream ServeroutToClient = new DataOutputStream(socket.getOutputStream());
			String message = inFromClient.readLine();
			if(!comingFromClient(message)) {
				join = true;
				notAName = true;
			}
			while(!join) {
				
			//if connection is being established between two servers and the message i received was preceded with a slash
			//then it was coming from the other server, in this case the join is true and the message i recieved 
			// is not the name of a client connecting to my server
			//unless i have this condition, i will never be able to satisfy if(join) condition for a server-server connection
			//this is the only way to set join to true if it's not a client first sending a name to this server
			
				
				if(!clientExists(message)) {
					clientnames.add(message);
					clientsockets.add(socket);
					ServeroutToClient.writeBytes("Client: " + message + " Successfully joined." + '\n');
					join = true;
				}
				// as long as i haven't joined which is the default state, I will try to add the name the client is sending me 
				//to the list of client names on my server and also add the client socket, i then set join to true;
				else {
					ServeroutToClient.writeBytes(message + " is an already existing name." + '\n');
				}
			}
			if(join) {
				while(true) {
					//is entered only once the first time after a server-server connection is established
					//and then notAName is set to false
					if(notAName) {
						message = removeSlash(message);
						clientSentence =  message;
						notAName = false;
					}
					//I always remove slash regardless, whether it was from clients to clients on the same server
					//or clients on different servers which is when  i add a slash in the first place
					else {
						clientSentence = removeSlash(inFromClient.readLine());
					}

					if(clientSentence.equals("<getMemberList>")) {
						ServeroutToClient.writeBytes(getMemberList() + '\n');
					}
					else {
						if(clientSentence.equalsIgnoreCase("BYE") || clientSentence.equalsIgnoreCase("QUIT")) {
							clientnames.remove(message);
							clientsockets.remove(socket);
							inFromClient.close();
							ServeroutToClient.close();
							socket.close();
						}
						else {

							if(clientSentence!=null && getDestinationSocket(clientSentence)!=null) {
								Socket destination =getDestinationSocket(clientSentence);
								DataOutputStream destinationOutput = new DataOutputStream(destination.getOutputStream());
								if(destination == clientsockets.get(0)) {
									destinationOutput.writeBytes("/" + clientSentence + '\n');
								}
								else {
									destinationOutput.writeBytes("Your Friend: " + getMessage(clientSentence) + '\n');
								}
							}
						} 
					}
				}
			}
		}
		catch (Exception e) {
			System.out.println("We are sorry, but something has went wrong with the connection!");

		}
	}













}
