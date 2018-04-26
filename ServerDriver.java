/*
 * Networked Chat with RSA Encryption/Decryption
 * Project 5
 * CS 342 - Fall 2017
 * 
 * - Margi Katwala
 * - Bushra Baqui
 * - Aditya Sinha
 * 
 * **ServerDriver.java**  
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

@SuppressWarnings("serial")
public class ServerDriver extends JFrame
{
	private static final Font TITLE_FONT = new Font("Times new Roman", Font.BOLD, 30);
	private Server server; 
 
 JTextArea textDisplay; 
 JList<String> connectClients;
 DefaultListModel<String> listConnected;
 DefaultListModel<String> listKeys;
 JButton startServerButton; 
 
 public ServerDriver() 
 {  
	  super("Server"); 
	
	  getContentPane().setLayout(new BorderLayout(20, 20));
	  getContentPane().setBackground(Color.WHITE);  
	
	  JPanel titlePanel = new JPanel();
	  titlePanel.setBackground(Color.WHITE);
	  JLabel titleLabel = new JLabel("Server");
	  titleLabel.setFont(TITLE_FONT);
	  titleLabel.setForeground(Color.GREEN);
	  titlePanel.add(titleLabel);
	  
	  getContentPane().add(titlePanel, BorderLayout.NORTH);  
	
	  listConnected = new DefaultListModel<>();
	  connectClients = new JList<>(listConnected);
	  listKeys =  new DefaultListModel<>();

	  connectClients.setEnabled(false);	  
	  textDisplay = new JTextArea(20, 50);
	  textDisplay.setEditable(false);
	  
	  textDisplay.setForeground(Color.GREEN);
	  
	  JPanel panelButton = new JPanel(new GridLayout(3, 1, 20, 20));
	  panelButton.setBorder(new EmptyBorder(50,50, 50, 50));
	  panelButton.setOpaque(false);  
	
	  startServerButton = new JButton("Start Server");
	  startServerButton.setFont(new Font(Font.SERIF, Font.BOLD, 20));
	  startServerButton.addActionListener(new ActionListener() 
	  {		  
		  public void actionPerformed(ActionEvent e) 
		  {
			  try 
			  {
				  server = new Server(); 
			 	  server.start(); 
			 	  startServerButton.setEnabled(false);
			  } 
			  catch (IOException e1) 
			  { 
				  showMessageDialog("Server could not be started.");
			  }
		  }
	  });
	  
  panelButton.add(startServerButton);

  JButton btnExit = new JButton("Exit");
  btnExit.setFont(new Font(Font.SERIF, Font.BOLD, 20));
  btnExit.addActionListener(new ActionListener() 
  {
	   @Override
	   public void actionPerformed(ActionEvent e)
		   {
		  	   System.exit(0); 
		   }
	 	});
	  
	  panelButton.add(btnExit);  	  
	  getContentPane().add(panelButton, BorderLayout.EAST);	  
	  pack();
	  setLocationRelativeTo(null);
	  setVisible(true);
	  setDefaultCloseOperation(EXIT_ON_CLOSE);
 }

 public void displayMessage(String message)
 {
	 textDisplay.append(message+"\n");
 } 

 public void showMessageDialog(String message)
 {
	 JOptionPane.showMessageDialog(this, message);
 }

 public static void main(String[] args)
 {
	 new ServerDriver();
 }
 
 class Server extends Thread
 {
  
  private static final int PORT = 8585;  
  
  private boolean running;
  private ServerSocket serverSocket;
  private List<Client> clients;   

  public Server() throws IOException 
  {
	   running = true;
	   serverSocket = new ServerSocket(PORT);
	   clients = new ArrayList<>();
	   displayMessage("Server Running at " + InetAddress.getLocalHost().getHostAddress() + " on port "
	     + serverSocket.getLocalPort());
  }  
  
  @Override
  public void run() 
  {
	  
	  while(running)
	  {	   
//		  displayMessage("Waiting for client to connect...");
		  try 
		  {
		   Socket socket = serverSocket.accept(); 
//		     displayMessage("Client connected!");
		   Client client = new Client(socket); 
		   clients.add(client); 
		   client.start(); 
		   
		  }
		  catch (IOException e)
		  { 
		   showMessageDialog(e.getMessage()+"\nExiting...");
		   System.exit(0);
		  }    
	   }
  }  
   
  public synchronized void sendToAll(Message message) 
  {
	   List<Client> connected = getConnectedClients(); 
	   for (Client client : connected) 
	   {
	    if (!client.username.equals(message.getMessageSender()))  
	    client.sendMessage(message);
	   }
  }
  

  public synchronized void sendToFew(List<String> usernames,Message message)
  {
   for (Client client : clients) 
	   {
	    if(usernames.contains(client.username)) 
	     client.sendMessage(message);
	   }
  }  

  public synchronized void sendToOne(String username,Message message)
  {
	   for (Client client : clients) 
	   {
		    if(username.equals(client.username))
		    { 
			     client.sendMessage(message);
			     return;
		    }
	   }
  } 

  public synchronized void userConnectDisconnect(MessageType type,String newUser,String pubKey)
  {
	   List<Client> connected = getConnectedClients();
	   Message newUserConnected = new Message(type, "SEVER", "ALL", newUser,pubKey);
	   for (Client client : connected) 
	   {
		   if(!client.username.equals(newUser))
		   {
			   client.sendMessage(newUserConnected);
		   }
	   }
  }
  
  public synchronized boolean validateUsername(String username)
  {
	   
	   if(username.equals("SERVER") || username.length() == 0 || username.contains("||"))
	   return false;
	   
	   for (Client client : clients)
	   {
		   if(client.username.equals(username))
		   return false;
	   }
	   return true;
  }  

  public synchronized Client getClient(String username)
  {
	   for (Client client : clients) 
	   {
		   if(client.username.equals(username))
		   return client;
	   }
	   return null;
  }  

  public synchronized List<Client> getConnectedClients()
  {
	   List<Client> connected = new ArrayList<>();
	   int size = listConnected.size();
	   for (int i = 0; i < size; i++) 
	   {
		   connected.add(getClient(listConnected.get(i)));
	   }
	   return connected;
  }
  
  public synchronized String getConnectedClientsNames()
  {
	   int size = listConnected.size();
	   String connectList = "";
	   for (int i = 0; i < size; i++) 
	   {
		   connectList+=listConnected.get(i)+"||";
	   }
   if(connectList.endsWith("||"))
    connectList = connectList.substring(0, connectList.length()-2);
   
   return connectList;
  }

  public synchronized String getConnectedClientsKeys()
  {
	   int size = listKeys.size();
	   String connected = "";
	   for (int i = 0; i < size; i++) 
	   {
		   connected+=listKeys.get(i)+"||";
	   }
	   if(connected.endsWith("||"))
	   connected = connected.substring(0, connected.length()-2);	   
	   return connected;
	  }

  public void exit()
  {
   for (Client client : clients) 
   {
	   client.running = false;
   }
   this.running = false;
  }
} 

 class Client extends Thread 
 {
	 private String username;
	 private String publicKey;
	 private Socket socket;
	 private ObjectInputStream inputStream;
	 private ObjectOutputStream outputStream;
	 private boolean running;

  public Client(Socket socket)
  {
	   this.socket = socket;
	   username = ""; 
	   try 
	   {
		   outputStream = new ObjectOutputStream(socket.getOutputStream());
		   inputStream = new ObjectInputStream(socket.getInputStream());
	   } 
	   catch (IOException e)
	   {	    
	   }
  }

  @Override
  public synchronized void start()
  {
	  running = true;
	  super.start();
  }

  public void run() 
  {	    
	  try 
	  {
		   while (running) 
		   {
		     Message message = (Message) inputStream.readObject(); 
		     if(message.getMessageType() == MessageType.STORE_USER)
		     { 
		      if(server.validateUsername(message.getMainMessage()))
		      {
		       this.username = message.getMainMessage();
		       this.publicKey = message.getMainKey();
		
		       listConnected.addElement(username);
		       listKeys.addElement(publicKey);
		       
		       displayMessage(username+" saved.");
		       this.sendMessage(new Message(MessageType.STORE_USER, "SERVER",this.username,"SUCCESS",null));   
		       server.userConnectDisconnect(MessageType.CONNECT_CLIENT, username,publicKey); 
		       
		       this.sendMessage(new Message(MessageType.CONNECTED_CLIENTS, 
		    		   "SERVER", this.username, server.getConnectedClientsNames(),server.getConnectedClientsKeys()));
		      }
		      else
		    	  this.sendMessage(new Message(MessageType.STORE_USER, "SERVER", this.username, "FAILURE",null));      
		     }
		     else if(message.getMessageType() == MessageType.DISCONNECT_CLIENT) 
		      break; 
		     else if(message.getMessageType() == MessageType.DISPLAY_TEXT) 
		     {
		    	 server.sendToAll(message);
		     }
		     else if(message.getMessageType() == MessageType.DISPLAY_SINGLETEXT)
		     {
		      System.out.println("unicast message recieved to forward to "+message.getMessageReceiver());
		      server.sendToOne(message.getMessageReceiver(), message);
		     }
		     else if(message.getMessageType() == MessageType.DISPLAY_MULTITEXT)
		     {
		    	 String[] recievers = message.getMessageReceiver().split(Pattern.quote("||"));
		    	 server.sendToFew(Arrays.asList(recievers), message);
		     }     
		   }	
		}
	   catch (Exception e)
	   {
	    
	   }	
	   disconnect(); 
  }
 
  public void disconnect()
  {
	   try 
	   {  
		   running = false;
		   server.userConnectDisconnect(MessageType.DISCONNECT_CLIENT, username,publicKey);
		   socket.close(); 
	   } 
	   catch (IOException e) 
	   {
   	   }
   server.clients.remove(this);
   listConnected.removeElement(username); 
   displayMessage(username+" disconnected.");
  }
  
  public void sendMessage(Message message) 
  {	  
	  try 
	  {
	   outputStream.writeObject(message);
	   outputStream.flush();
	  } 
	  catch (IOException e)
	  { 
	    displayMessage("Error sending message to "+username+".Disconnecting..");
	    disconnect();
	  }
  }
 }
}
