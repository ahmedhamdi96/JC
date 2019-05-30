import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Hashtable;

public class Server  {
	
	public final static String TTL="TTL Exceeded!";
	public final static String NOT_FOUND="Client Not Found";
	public final static String MSG_FROM_SERVER_TO_CLIENT_FAILS="Sending Failed";
	public final static String SENT ="Sent!";
	
	private final static String [] ALLOWED_FORWARD_REPLIES={TTL,NOT_FOUND,MSG_FROM_SERVER_TO_CLIENT_FAILS,SENT};
	private ServerSocket serverSocket;
	private int port;
	private  Hashtable<String,Socket>clients=new Hashtable<String,Socket>();
	private Socket masterServerSocket;
	private static String reply=""; 
	private static boolean gotReply=false; 
	private Socket appendingSocket ;
	private String appendingUsername;
	
	public Server(int port){
		this.port=port;
	}
	
	public void startServer() throws IOException{
		
		String hostName="Dell-AhmedHamdi";        	
		int port =9000;
		try{
			masterServerSocket=new Socket(hostName, port);
			System.out.println("Master Server is Online.");
			new Thread(new Runnable() {
				@Override
				public void run() {
					try{
					listenToMasterServer();
					}
					catch(Exception e){
						e.printStackTrace();
					}
					
				}
			}).start();
		}
		catch (Exception e) {
			System.err.println("Something went wrong connecting with the master server");		
		}

		
		try{
		     serverSocket = new ServerSocket(this.port);
			}
			catch (Exception e) {
				System.out.println(e.getMessage());
				return;
			}
			
			while(true){
				try{
					
					Socket client=serverSocket.accept();
					System.out.println("a new Client has joined the server");
					new Thread(new Runnable() {
						
						@Override
						public void run() { 
							try {
								startClientConnection(client);
							} catch (IOException e) {
								try {
									client.close();
								} catch (IOException e1) {
									e1.printStackTrace();
								}
								if(clients.contains(client))
									clients.values().remove(client);
								
								System.out.println(e.getMessage());
								
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}).start();
						
				}
				catch (IOException e) {
					System.out.println(e.getMessage());
				}	
			}
		}

	private void startClientConnection(Socket client) throws IOException, InterruptedException{
		
		PrintWriter outFromServer= new PrintWriter(client.getOutputStream(),true);
		BufferedReader inFromClient = new BufferedReader(new InputStreamReader(client.getInputStream()));
		PrintWriter toMasterServer = new PrintWriter(masterServerSocket.getOutputStream(),true);
		String input;
		String name="";
		while((input=inFromClient.readLine())!=null){	
			String [] inputSplit=input.split(" ");
			System.out.println("received "+input);
			switch(inputSplit[0]){
			case "join":{ 
				System.out.println("got from user "+input);	
				name=inputSplit[1];
				if(!clients.containsKey(name)){
					try{
					appendingSocket=client;
					appendingUsername=name;
					toMasterServer.println("NewUpdate "+name);
				}	
				catch (Exception e) {
					outFromServer.println(input+" "+ "is a wrong command");
				}
				}
				else{
					outFromServer.println("This nickname is already used");
				}
			
			}break;
			case "chat":{
				if(!name.equals("")){ //Check if the user has joined , in GUI a framed composed of JOIN button only can handle this 
						String destination,content;
						destination=inputSplit[1];
						content=inputSplit[2]+restOfString(inputSplit,2);
						Message message= new Message(name,destination,3,content);	 

						if(!clients.containsKey(destination)){ 	
							outFromServer.println(tryForwarding(message));
						}
						else{
							String yield=(sendMessage(message))?"Sent!":"Sending Failed";
							outFromServer.println(yield);
						}
				}
				else{
					outFromServer.println("You have to join the chat-session first");
				}
			}
			break;
			case "QUIT":{
				toMasterServer.println("GoodBye "+name);
				if(clients.containsKey(name))
					clients.remove(name);
				client.close();
				return;
			}
			default:outFromServer.println("Not a known command"	);break;
			}
		}
		if(clients.contains(client))
			clients.values().remove(client);
	}
	
	
	private void listenToMasterServer() throws ClassNotFoundException, IOException{

		System.out.println("Oi mate");
		BufferedReader bf = new BufferedReader(new InputStreamReader(masterServerSocket.getInputStream()));
		String input ;
		
		PrintWriter outString = new PrintWriter(masterServerSocket.getOutputStream(),true);
		while((input=bf.readLine())!=null){
			System.out.println("got "+input+ " from master server");
			String [] inputSplit=input.split(" ");
			switch(inputSplit[0]){   
			case "chat": {
				String destination,content,source;
				source=inputSplit[1];
				destination=inputSplit[2];
				content=inputSplit[3]+restOfString(inputSplit,3);
				
				
				Message message= new Message(source,destination,3,content);
				if(!clients.containsKey(destination))
					outString.println(Server.NOT_FOUND);
				
				else{
					if(sendMessage(message))
						outString.println(SENT);
						
					else
						outString.println(MSG_FROM_SERVER_TO_CLIENT_FAILS);
				}
			}break;
			case"usernameUsed":{
				PrintWriter outToAppendingClient = new PrintWriter(appendingSocket.getOutputStream(), true);
				outToAppendingClient.println("This username is already used");
			}break;
			case"usernameAccepted":{
				clients.put(appendingUsername, appendingSocket);
			}break;
			case"UpdateUrClients":{
				for(Socket socket: clients.values()){
					PrintWriter outToAllClients = new PrintWriter(socket.getOutputStream(),true);
					outToAllClients.println("$ "+inputSplit[1]);
				}
			}break;
			default:System.out.println("In the default.");reply=input;gotReply=true;break;
			}
			
		}
		
	}

	public  String restOfString(String [] input,int index){
		String output=" ";
		if(input.length>index+1){
			for(int i=index+1;i<input.length;i++){
				output+=(input[i]+" ");
			}
		}
		
		return output;
	}

	public String tryForwarding(Message message) throws IOException{
	
		PrintWriter out = new PrintWriter(masterServerSocket.getOutputStream(),true);
		out.println("chat "+message.source+" "+message.destination+ " "+message.content);
	
		String s;
		
		while(!gotReply)
			;
		
		s=reply;
		gotReply=false;
		
		if( Arrays.asList(ALLOWED_FORWARD_REPLIES).contains(s))
			return s;
		
		return null;
	}
	
	public boolean sendMessage(Message message){

		PrintWriter w;
		try {
			w = new PrintWriter(clients.get(message.destination).getOutputStream(),true);
		} catch (IOException e) {
			return false;
		}
		w.println(message.source+": "+message.content);
		return true;
	}
	
	
	public ServerSocket getServerSocket() {
		return serverSocket;
	}

	public int getPort() {
		return port;
	}

	public Hashtable<String, Socket> getClients(){
		return clients;
	}
	
	public static void main(String [] args) throws IOException{
		System.out.println("Server Abo-6000");
			new Server(8000).startServer();
	}
	

}
