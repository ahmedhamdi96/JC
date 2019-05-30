import java.awt.EventQueue;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JLabel;

import java.awt.Color;
import java.awt.Component;



public class GUI {

	public JFrame frame;
	public JPanel start;
	public JPanel main;
	public JTextArea msgsFromClient;
	public JTextArea incomingMsgs;
	private String in;
	public JTextField name;
	JTextField selectedClient;
	JTextArea allClients;
	public boolean accepted;
	private Client myClient;
	

	
	public void setMyClient(Client client){
		this.myClient=client;
	}
	
	public String getIn() {
		return in;
	}

	public void setIn(String in) {
		this.in = in;
	}


	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI window = new GUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public  void initialize() {
		frame = new JFrame();
		frame.setVisible(true);
		frame.setBounds(100,100,1000,600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.setTitle("ChaCha");
		frame.setIconImage(new ImageIcon("cha-logo.jpg").getImage());
		
		start = new JPanel();
		start.setBounds(0, 0, 984, 561);
		start.setLayout(null);

		name = new JTextField();
		name.setBounds(10,475,150,50);
		start.add(name);
		
		JButton enter = new JButton();
		enter.setText("Enter your username");
		enter.setFont(new Font(Font.SANS_SERIF,Font.BOLD,16));
		enter.setBackground(Color.blue);
		enter.setForeground(Color.white);
		enter.setBounds(165, 475, 200, 50);
		enter.addActionListener(myClient);
		enter.setVisible(true);
		start.add(enter);
		
		JLabel background = new JLabel();
		background.setBounds(0, 0,frame.getWidth(), frame.getHeight());
		background.setIcon(new ImageIcon("networkmap.png"));
		start.add(background);
		
		
		main = new JPanel();
		main.setBounds(0,0,984,561);
		main.setLayout(null);

		
		msgsFromClient = new JTextArea();
		msgsFromClient.setAlignmentY(Component.TOP_ALIGNMENT);
		msgsFromClient.setAlignmentX(Component.LEFT_ALIGNMENT);
		msgsFromClient.setBounds(0, 480, 810, 81);
		msgsFromClient.setColumns(10);
		main.add(msgsFromClient);
		
		JButton send = new JButton("SEND");
		send.setBounds(809, 480, 175, 81);
		send.addActionListener(myClient);
		main.add(send);

		
		incomingMsgs = new JTextArea("");
		incomingMsgs.setEditable(false);
		incomingMsgs.setVisible(true);
		incomingMsgs.setBounds(0, 35, 803, 425);
		
		JScrollPane incomingMsgsScroll= new JScrollPane(incomingMsgs);
		incomingMsgsScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		incomingMsgsScroll.setBounds(0, 35,803, 425);
		//main.add(incomingMsgs);
		incomingMsgsScroll.setVisible(true);
		main.add(incomingMsgsScroll);
	
		
		
		
		allClients = new JTextArea("");
		allClients.setEditable(false);
		allClients.setVisible(true);
		allClients.setBounds(804, 35, 180, 375);
		
		JScrollPane allClientsScroll= new JScrollPane(allClients);
		allClientsScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		allClientsScroll.setBounds(804, 35, 180, 375);
		//main.add(allClients);
		allClientsScroll.setVisible(true);
		main.add(allClientsScroll);
		
		
		
		selectedClient = new JTextField("Choose a client");
		selectedClient.setBounds(802, 410, 300, 50);
		selectedClient.setName("selectedClient");
		main.add(selectedClient);
		
		
		
		start.setVisible(true);
		frame.getContentPane().add(start);
		frame.getContentPane().setVisible(true);
		frame.getContentPane().validate();
		frame.getContentPane().repaint();
		frame.setResizable(false);
	}
	
	public void startTheChat(){
		frame.getContentPane().remove(start);
		frame.getContentPane().validate();
		
		
		frame.getContentPane().repaint();
		main.setVisible(true);
		frame.getContentPane().add(main);
		frame.getContentPane().validate();
		frame.getContentPane().repaint();
		
	}
	
	public  void ShowWarning(){
		JOptionPane pane = new JOptionPane("Enter a valid username", JOptionPane.ERROR_MESSAGE);
		JDialog dialog = pane.createDialog( "Sorry");
		dialog.setVisible(true);
	}
}
