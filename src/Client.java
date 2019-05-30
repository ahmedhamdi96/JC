import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JButton;

public class Client  implements ActionListener{
	
	private static Socket clientSocket;
	private static PrintWriter OutoServer=null;
	private static BufferedReader fromServer=null;
	
	
	GUI view;
	
	public Client(){
	}

	public static void main(String[] args) {
		Client c = new Client();
		c.start();
	
	}
	
	public void start(){
		
		view = new GUI();
		view.setMyClient(this);
		view.initialize();
		view.frame.addWindowListener (new WindowAdapter()
		{
	@Override
	public void windowClosing(WindowEvent e){
		OutoServer.println("QUIT ");
		e.getWindow().dispose();
	}
		});
		String hostName = "Dell-AhmedHamdi";        //hostname and ports are taken from terminal/cmd input again to be adjusted in GUI.
		int port =8000;
		
		
		 try{
			    clientSocket = new Socket(hostName, port);
			    
			     OutoServer =
			        new PrintWriter(clientSocket.getOutputStream(), true);
			   fromServer =
			        new BufferedReader(
			            new InputStreamReader(clientSocket.getInputStream()));
			    
		 }
		 catch (Exception e) {
			 System.err.println(e.getMessage());
			 return;
		 }
		 
 		 //System.out.println("Connected,please enter \"join\" followed by a nickname(separated by a space)");
		 
		 new Thread(new Runnable() {
			
			@Override
			public void run() {
				//OutoServer.println(userInput);	 
			}
		}).start();
		 
		 new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					boolean firstTime=true;
					 try {
						 	String reply;
							while(( reply=fromServer.readLine())!=null){
								if(reply.equalsIgnoreCase("This nickname is already used")){
									view.ShowWarning();
								}
								
								else if (reply.split(" ")[0].equalsIgnoreCase("$")){
									if(firstTime){
										view.startTheChat();
										view.frame.setTitle("ChaCha Window");
										firstTime = false;
									}
									loginToChat(reply.split(" ")[1]);
								}
								else{
									view.incomingMsgs.append(reply+"\n");
								}

							 }
						} catch (IOException e) {
							System.err.println(e.getMessage());
							return;
						}
						 
					
				}
			}).start();
	}



	
	public void loginToChat(String allClients){
		ArrayList<String> server0 = new ArrayList<String>();
		ArrayList<String> server1 = new ArrayList<String>();
		ArrayList<String> server2 = new ArrayList<String>();
		ArrayList<String> server3 = new ArrayList<String>();
		String[] clientsNames = allClients.split(",");
		for(int i=0; i<clientsNames.length; i++){
			if(clientsNames[i].charAt(clientsNames[i].length()-1)=='0')
			{
				server0.add(clientsNames[i].substring(0,clientsNames[i].length()-1));
			}
			else if(clientsNames[i].charAt(clientsNames[i].length()-1)=='1')
			{
				server1.add(clientsNames[i].substring(0,clientsNames[i].length()-1));
			}
			else if(clientsNames[i].charAt(clientsNames[i].length()-1)=='2')
			{
				server2.add(clientsNames[i].substring(0,clientsNames[i].length()-1));
			}
			else if(clientsNames[i].charAt(clientsNames[i].length()-1)=='3')
			{
				server3.add(clientsNames[i].substring(0,clientsNames[i].length()-1));
			}
		}

		String text="Server 1:\n";
		for(String s:server0){
			text+="*"+s+"\n";
		}
		text+="Server 2:\n";
		for(String s:server1){
			text+="*"+s+"\n";
		}
		text+="Server 3:\n";
		for(String s:server2){
			text+="*"+s+"\n";
		}
		text+="Server 4:\n";
		for(String s:server3){
			text+="*"+s+"\n";
		}
		view.allClients.setText(text);
		view.allClients.repaint();
		view.allClients.validate();
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		JButton button = (JButton)e.getSource();
	switch(button.getText()){
	case "Enter your username":{
	if(view.name.getText().equals("")){
		view.ShowWarning();
	}
	else
	{
		
		PrintWriter p = null;
		try {
			p = new PrintWriter(clientSocket.getOutputStream(),true);
			p.println("join "+ view.name.getText());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}
		
	}break;
	case "SEND":
	{
		if( ! view.msgsFromClient.getText().equals("")){
			String s = view.msgsFromClient.getText();
			s = "chat " + view.selectedClient.getText()+ " " + s;
			OutoServer.println(s);
			view.msgsFromClient.setText("");
		}
	}
		
		
	}
	}

}