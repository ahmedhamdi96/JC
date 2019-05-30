import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Hashtable;

public class MasterServer{
	
	private ArrayList<Socket>availableServers;
	int port;
	private ServerSocket masterServerSocket;
	private static  Hashtable<Socket,GotReply> serversReplies=new Hashtable<Socket,GotReply>();
	ArrayList<ArrayList<String>> allServersClients ;
	
	public MasterServer(int port) {
		this.port=port;
		availableServers=new ArrayList<Socket>();
		allServersClients = new ArrayList<ArrayList<String>>();
	}
	
	public void startServer() throws ClassNotFoundException{
		
		try{
		     masterServerSocket = new ServerSocket(port);
		     
			}
			catch (Exception e) {
				System.out.println("error here "+ e.getMessage());
				return ;
			}
			
			
			while(true){
				try{
					Socket server = masterServerSocket.accept();
					new Thread(new Runnable() {
						
						@Override
						public void run() {
							try {
								availableServers.add(server);
								allServersClients.add(new ArrayList<String>());
								serversReplies.put(server, new GotReply());	
								startConnection(server);
						} catch (Exception e) {
							e.printStackTrace();
						}

						}
					}).start();
						
				}
				catch(IOException e){
					e.printStackTrace();
				}
			}		
	}
		
		
	public String sendAllServersClients(){
		String out = "";
		for(int i = 0 ; i < allServersClients.size(); i++)
		{
			for(int j=0 ; j < allServersClients.get(i).size();j++)
			{
				out+=allServersClients.get(i).get(j)+i+",";
			}
		}
		return out;
	}
	
	private void startConnection(Socket server) throws IOException, ClassNotFoundException{
		BufferedReader bf = new BufferedReader(new InputStreamReader(server.getInputStream()));
		String input ;
		int ttl=3;
		String result="";
		
		PrintWriter outString =new PrintWriter(server.getOutputStream(),true);
		while((input=bf.readLine())!=null){
			System.out.println("got " + input+" from server");
			
			if(input.split(" ")[0].equals("chat")){
				for(Socket nextServer: availableServers ){
					
					if(ttl==0)
						outString.println(Server.TTL);
						
					if(nextServer.getInetAddress()!=server.getInetAddress()
							|| nextServer.getPort()!=server.getPort())
					{
						 result=tryToForward(nextServer,input);
						if(!result.equals(Server.NOT_FOUND))
						{
								outString.println(result);
								break;
						}
						
						else 
							ttl--;
					}
					
					result="";
				}
				
				if(result=="")
				outString.println(Server.NOT_FOUND);
			}
			else if(input.length()> 10 && input.split(" ")[0].equalsIgnoreCase("NewUpdate"))
			{
				boolean found = false;
				String newUsername= input.split(" ")[1];
				for(int i = 0 ; i < allServersClients.size(); i++)
				{
					if(allServersClients.get(i).contains(newUsername))
					{
						outString.println("usernameUsed ");
						found = true;
						break;
					}
				}
				if(!found){
					outString.println("usernameAccepted ");
					int index = availableServers.indexOf(server);
					allServersClients.get(index).add(newUsername);
					for(Socket socket: availableServers)
					{if(socket == server)
					{
						outString.println("UpdateUrClients "+ sendAllServersClients());
					}
					else{	
						PrintWriter reminder = new PrintWriter(socket.getOutputStream(),true);
						reminder.println("UpdateUrClients " + sendAllServersClients());
					}
					}
				}

			}
			else if(input.length()> 0 && input.split(" ")[0].equalsIgnoreCase("GoodBye")){
				String usernameToBeRemoved = input.split(" ")[1];
				for(int i = 0 ; i < allServersClients.size(); i++)
				{
					for(int j=0 ; j < allServersClients.get(i).size();j++)
					{
						if(allServersClients.get(i).get(j).equals(usernameToBeRemoved)){
							allServersClients.get(i).remove(j);
							break;
						}
					}
				}
				for(Socket socket: availableServers)
				{if(socket == server)
				{
					outString.println("UpdateUrClients "+ sendAllServersClients());
				}
				else{	
					PrintWriter reminder = new PrintWriter(socket.getOutputStream(),true);
					reminder.println("UpdateUrClients " + sendAllServersClients());
				}
				}
			}
			else{
				System.out.println("the reply from last server contacted is " +input);
				serversReplies.get(server).gotReply=true;
				serversReplies.get(server).reply=input;
			}
			
				
			}
	}
	

	private String tryToForward(Socket server,String message) throws IOException{
		PrintWriter out= new PrintWriter(server.getOutputStream(),true);
		out.println(message);
		while(!serversReplies.get(server).gotReply)
			;
		serversReplies.get(server).gotReply=false;
		return serversReplies.get(server).reply;
	}

	public static void main(String[] args) {		
		MasterServer masterServer =new MasterServer(9000);
		try{
			masterServer.startServer();
		}
		catch (Exception e) {
			e.printStackTrace();
		}			
	}
}

class GotReply{
	boolean gotReply;
	String reply;
	
	public GotReply(){
		
	}
	
	public GotReply(boolean gotReply,String reply){
		this.gotReply=gotReply;
		this.reply=reply;
	}
}
