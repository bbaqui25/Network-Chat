/*
 * Networked Chat with RSA Encryption/Decryption
 * Project 5
 * CS 342 - Fall 2017
 * 
 * - Margi Katwala
 * - Bushra Baqui
 * - Aditya Sinha
 * 
 * **ClientDriver.java**  
 */

import java.awt.BorderLayout;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Menu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.omg.CORBA.portable.InputStream;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.PublicKey;

@SuppressWarnings("serial")
public class ClientDriver extends JFrame 
{	
	private static final Font TEXT_FONT = new Font("Times new Roman", Font.PLAIN, 15);
	private static final Font BUTTON_FONT = new Font(Font.SERIF, Font.BOLD, 15);
	private static final Border OUTLINE_BORDER = BorderFactory.createLineBorder(new Color(139,0,139), 3, true);
	
	JPanel panelConnect, chatPanel, chooseUserPanel;
	JTextField  IPText, portText, userText, mainMessage , firstPrime, secondPrime;
	JTextArea chatTextArea;
	JButton  connectButton, selectClientButton, allClientButton, exitButton, userChooseButton;
	JList<String> listConnected;
	DefaultListModel<String> listAllConnected;
	JMenu file = new JMenu("File");

	private MessageListener messageListener; 
	private String userName; 

	public ClientDriver() 
	{
		super("Client"); 	
		
		// creates the menu panel
		JPanel topPanel = new JPanel(new GridLayout(2, 0, 20, 20));
		JMenuBar menuBar = new JMenuBar();
		JMenu file = new JMenu("Menu");
		menuBar.setForeground(Color.BLUE);
	
		panelConnect = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
		panelConnect.add(menuBar);
		
		// creating the menu items
		JMenuItem open = new JMenuItem("Load File");
		JMenuItem help = new JMenuItem("Help ");
		JMenuItem about = new JMenuItem("About");
	
		file.add(help);
		file.add(about);
		menuBar.add(file);
		//create text box for IP address and Port number
		JLabel IPLabel = new JLabel("IP Address");
		IPLabel.setFont(TEXT_FONT);
		IPLabel.setForeground(Color.BLACK);
		panelConnect.add(IPLabel);
		IPText = new JTextField(20);
		IPText.setFont(TEXT_FONT);
		IPText.setForeground(Color.BLACK);
		panelConnect.add(IPText);
		
		JLabel portLabel = new JLabel("Port");
		portLabel.setFont(TEXT_FONT);
		portLabel.setForeground(Color.BLACK);
		panelConnect.add(portLabel);
		portText = new JTextField(20);
		portText.setFont(TEXT_FONT);
		portText.setForeground(Color.BLACK);
		panelConnect.add(portText);

		connectButton = new JButton("Connect");
		connectButton.setFont(BUTTON_FONT);
		
		// action listener for the connect button
		connectButton.addActionListener(new ActionListener() 
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{

				new Thread(new Runnable() 
				{ 
					public void run()
					{
						try 
						{
							connectToserver(IPText.getText(), Integer.parseInt(portText.getText())); 
							connectPanelSwitch(false);
							chooseUsernamePanelSwitch(true); 

						} catch (NumberFormatException e1)
						{ 
							showMessageDialog("Port number should be an integer.");
						} 
						catch (IOException e2)
						{ 
							showMessageDialog("Error: Cannot Connect to server.");
						}

					}
				}).start();

			}
		});
		
		panelConnect.add(connectButton);
		panelConnect.setBorder(OUTLINE_BORDER);
		topPanel.add(panelConnect);
	
		chooseUserPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,40,40));
		// Panel for inputing username and two prime keys
		JLabel userLabel = new JLabel("Choose Username");
		JLabel PrimeNum1 = new JLabel("Prime Num 1");
		JLabel PrimeNum2 = new JLabel("Prime Num 2");

		userLabel.setFont(TEXT_FONT);
		userLabel.setForeground(Color.BLACK);
		chooseUserPanel.add(userLabel);
		firstPrime= new JTextField(5);
		secondPrime= new JTextField(5);
		userText = new JTextField(20);
		userText.setFont(TEXT_FONT);
		userText.setForeground(Color.BLACK);
		chooseUserPanel.add(userText);
		chooseUserPanel.add(PrimeNum1);
		chooseUserPanel.add(firstPrime);
		chooseUserPanel.add(PrimeNum2);
		chooseUserPanel.add(secondPrime);
		userChooseButton = new JButton("Enter");
		userChooseButton.setFont(BUTTON_FONT);
		
		// action listener for help menu item
		help.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent ev) 
			{
				JOptionPane.showMessageDialog(help,
						"How to Use the Network Chat: "
							+ "\n 1. Connect to the server using the IP address and Port number "
							+ "\n 2. Choose username and two prime numbers to secure your message between clients ."
							+ "\n If you enter -1 for both prime numbers you will allow to choose file to create the random numbers "
							+ "\n 3. If you want to send message to specific select Send to Select else Send to All "
							+ "\n 4.How to Use Menu :"
							+ "\n       Load File: Lets you load the file of prime numbers which will generate the prime key randomly. "
							+ "\n       Help: It will help you to familarzie with the game "
							+ "\n       About: Project Managment Team "
							+ "\n 5. To exit the chat press Exit in the bottom corner ");
			}
		});
		
		about.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ev) 	
			{
				JOptionPane.showMessageDialog(help,
						" Class CS - 342 Project  5"
						+ "\n Project Manged by "
						+ "\n  1. Margi Katwala "
						+ "\n  2. Aditya Sinha "
						+ "\n  3.Bushra Baqui "
						+ "\n  ");
			}
		});
		// After the connection is done 
		userChooseButton.addActionListener(new ActionListener() 
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				String username = userText.getText(); 
				LargeInteger publicKey;
				publicKey= null;
				
				boolean errorFlag = false;
				List<Long> numbers = new ArrayList<Long>();					

				LargeInteger primeNum = new LargeInteger(firstPrime.getText());

				if (LargeInteger.compare(primeNum, new LargeInteger(-1)) == 0) 
				{
					try
						{
							BufferedReader in;
							File selectedFile;
							JFileChooser openFile = new JFileChooser();
							FileReader reader = null;
							int returnValue = openFile.showOpenDialog(null);
							
							if (returnValue == openFile.APPROVE_OPTION)
							{
								returnValue= 1;
							} 
							else
								returnValue =0;
								selectedFile = openFile.getSelectedFile();
							
							if(selectedFile.canRead() && selectedFile.exists())
							{
								
									reader = new FileReader(selectedFile);								
							}
						// reads the buffer
						in = new BufferedReader(reader);
						String inputLine =in.readLine();
						int lineNum = 0;
						while(inputLine!=null) 
						{
							
							lineNum++;
							StringTokenizer tokenNum = new StringTokenizer(inputLine);
							inputLine = in.readLine();
							
						}
						in.close();
						}
						catch(Exception ex)
						{						
						}
					primeNum = RSA.random_prime(50);
				}
				
				if (!new BigInteger(primeNum.toString()).isProbablePrime(10)) 
				{
					JOptionPane.showMessageDialog(null, primeNum + " is not prime. Please try again");
					errorFlag = true;
				}

				LargeInteger q1 = new LargeInteger(secondPrime.getText());
				
				if (LargeInteger.compare(q1, new LargeInteger(-1)) == 0)
				{
					try 
					{
						BufferedReader in;
						// lets the user choose the file for the random primes
						File selectedFile;
						JFileChooser openFile = new JFileChooser();
						FileReader reader = null;
						int returnVal = openFile.showOpenDialog(null);
						if (returnVal == openFile.APPROVE_OPTION)
						{
							returnVal= 1;
						} else
							returnVal =0;
						selectedFile = openFile.getSelectedFile();
						
						if(selectedFile.canRead() && selectedFile.exists())
						{							
							reader = new FileReader(selectedFile);						
						}
						
						in = new BufferedReader(reader);
						String inputLine =in.readLine();
						int LineNumber = 0;
						
						while(inputLine!=null) 
						{							
							LineNumber++;
							StringTokenizer newToken = new StringTokenizer(inputLine);
							inputLine = in.readLine();							
						}
						in.close();
					}						
					catch(Exception ex) 
					{						
					}
					q1 = RSA.random_prime(50);
				}
				if (!new BigInteger(q1.toString()).isProbablePrime(10)) 
				{
					JOptionPane.showMessageDialog(null, q1 + " is not prime. Please try again");
					errorFlag = true;
				}
				if (!errorFlag) 
				{
					@SuppressWarnings("unused")
					RSA.Key key = RSA.generateKeys(primeNum, q1);
					JOptionPane.showMessageDialog(null, "Successfully created keys!");
				
					messageListener.sendMessage(new Message(MessageType.STORE_USER, username,  "SERVER",  username,null));
					userChooseButton.setEnabled(false); 
				}
				else 
				{
					JOptionPane.showMessageDialog(null,"Please try again");
				}
			}
		});
		// Adding items to the chat box 
		chooseUserPanel.add(userChooseButton);
		chooseUserPanel.setBorder(OUTLINE_BORDER);
		topPanel.add(chooseUserPanel);

		getContentPane().add(topPanel,  BorderLayout.NORTH);
	
		chatPanel = new JPanel(new BorderLayout(5, 5));
		chatTextArea = new JTextArea(20, 40);
		chatTextArea.setEditable(false);
		chatTextArea.setBorder(OUTLINE_BORDER);
		chatTextArea.setFont(new Font(Font.MONOSPACED, Font.ITALIC, 15));
		chatTextArea.setForeground(Color.BLACK);

		JScrollPane spChat = new JScrollPane(chatTextArea);
		spChat.setBorder(new EmptyBorder(20, 20, 0, 20));
		chatPanel.add(spChat, BorderLayout.CENTER);

		listAllConnected = new DefaultListModel<>();
		listConnected = new JList<>(listAllConnected);
		listConnected.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		JScrollPane spConnected = new JScrollPane(listConnected);
		
		spConnected.setFont(TEXT_FONT);
		spConnected.setBorder(BorderFactory.createTitledBorder(OUTLINE_BORDER, 
				"Connected Clients",TitledBorder.CENTER, TitledBorder.DEFAULT_JUSTIFICATION, TEXT_FONT, Color.BLACK));
		chatPanel.add(spConnected, BorderLayout.WEST);
		
		JPanel sendMessagePane = new JPanel(new FlowLayout(FlowLayout.LEFT, 20 ,10));

		mainMessage = new JTextField(40);
		mainMessage.setFont(TEXT_FONT);
		mainMessage.setForeground(Color.BLACK);
		mainMessage.setBorder(OUTLINE_BORDER);
		sendMessagePane.add(mainMessage);
		
		selectClientButton = new JButton("Send to Selected");
		selectClientButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				String messageContent = mainMessage.getText();
				if(messageContent.length() == 0)
				{ 
					showMessageDialog("Please enter some text");
					return;
				}

				List<String> recipients = listConnected.getSelectedValuesList();

				if(recipients.size() == 0)
				{ 
					showMessageDialog("Please choose atleast one recipient");
					return;
				}

				else if(recipients.size() == 1)
				{ 
					Message message = new Message(MessageType.DISPLAY_SINGLETEXT, userName, recipients.get(0), messageContent,null);
					System.out.println("unicast message sent to "+recipients.get(0));
					displayMessage(message.toString()); 
					
					messageListener.sendMessage(message); 
					mainMessage.setText(""); 
					listConnected.clearSelection(); 
				}

				else if(recipients.size() > 1)
				{ 					
					String recipientsNames = "";
					for (String recipient : recipients) 
					{
						recipientsNames+=recipient+"||";
					}
				
					recipientsNames = recipientsNames.substring(0,recipientsNames.length()-2);
			
					Message message = new Message(MessageType.DISPLAY_MULTITEXT, userName,  recipientsNames,  messageContent,null);
					messageListener.sendMessage(message);
					displayMessage(message.toString());
					mainMessage.setText("");
					listConnected.clearSelection();
				}
			}
		});
		sendMessagePane.add(selectClientButton);

		allClientButton = new JButton("Send to All");
		allClientButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				String content = mainMessage.getText();
				RSA.Key key = RSA.getlocalKey();
				ArrayList<LargeInteger> econtent = RSA.encrypt(content, key.n, key.e, 4);
				if(content.length() == 0)
				{ 
					showMessageDialog("Please enter some text");
					return;
				}
				
				@SuppressWarnings("unused")
				ArrayList<LargeInteger> sum = econtent;
				
				if(listAllConnected.size() == 0)
				{ 
					showMessageDialog("Cannot send the message. No users connected");
					return;
				}
				
				Message message = new Message(MessageType.DISPLAY_TEXT, userName, "ALL", content,null);
				messageListener.sendMessage(message);
				displayMessage(message.toString());
				mainMessage.setText(""); 
			}
		});
		sendMessagePane.add(allClientButton);
	
		exitButton = new JButton("Exit");
		exitButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{				
				messageListener.sendMessage(new Message(MessageType.DISCONNECT_CLIENT, userName, "SERVER", userName, null));
				System.exit(0);
			}
		});
		sendMessagePane.add(exitButton);

		chatPanel.add(sendMessagePane, BorderLayout.SOUTH);
		getContentPane().add(chatPanel, BorderLayout.CENTER);

		connectPanelSwitch(true);
		chooseUsernamePanelSwitch(false);
		chatPanelSwitch(false);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		IPText.setText("127.0.0.1");
		portText.setText("8585");
	}
	
	private void connectPanelSwitch(boolean isEnabled) 
	{
		IPText.setEnabled(isEnabled);
		portText.setEnabled(isEnabled);
		connectButton.setEnabled(isEnabled);
	}

	private void chooseUsernamePanelSwitch(boolean isEnabled)
	{
		userText.setEnabled(isEnabled);
		userChooseButton.setEnabled(isEnabled);
	}
	
	private void chatPanelSwitch(boolean isEnabled)
	{
		chatTextArea.setEnabled(isEnabled);
		mainMessage.setEnabled(isEnabled);
		selectClientButton.setEnabled(isEnabled);
		allClientButton.setEnabled(isEnabled);
		exitButton.setEnabled(isEnabled);
		listConnected.setEnabled(isEnabled);
		
		if(isEnabled == false)
			listAllConnected.clear();
	}

	private void connectToserver(String ip, int port) throws IOException
	{
		Socket socket = new Socket(ip, port);
		messageListener = new MessageListener(socket);
		messageListener.start(); 
	}

	public static void main(String[] args) 
	{
		new ClientDriver();
	}

	public void displayMessage(String message)
	{
		chatTextArea.append(message+"\n");
	}
	
	public void showMessageDialog(String message)
	{
		JOptionPane.showMessageDialog(this, message);
	}
	
	public class MessageListener extends Thread
	{
		private ObjectInputStream inputStream;
		private ObjectOutputStream outputStream;
		private boolean running;

		public MessageListener(Socket socket) throws IOException
		{
			inputStream = new ObjectInputStream(socket.getInputStream());
			outputStream = new ObjectOutputStream(socket.getOutputStream());
			outputStream.flush();
			running = true;
		}

		public void run()
		{
			try 
			{
				while (running)
				{
					Message message = (Message) inputStream.readObject(); 
					
					if(message.getMessageType() == MessageType.DISPLAY_SINGLETEXT
							|| message.getMessageType() == MessageType.DISPLAY_MULTITEXT 
							||message.getMessageType() == MessageType.DISPLAY_TEXT)
					{ 
						displayMessage(message.toString());
					}
					else if(message.getMessageType() == MessageType.STORE_USER)
					{
						if(message.getMainMessage().equals("SUCCESS")){
							displayMessage("Username choosen successfully");
							userName = message.getMessageReceiver();
							chooseUsernamePanelSwitch(false);
							chatPanelSwitch(true);
						}
						else
						{ 						
							showMessageDialog("Error: Please choose a different username.");
							userChooseButton.setEnabled(true);
						}
					}
					else if(message.getMessageType() == MessageType.CONNECTED_CLIENTS)
					{ 
						String[] connected = message.getMainMessage().split(Pattern.quote("||"));
						for (String client : connected) 
						{
							if(!client.equals(userName))
							{
								listAllConnected.addElement(client);
								displayMessage(client+" connected.");
							}
						}
					}
					else if(message.getMessageType() == MessageType.CONNECT_CLIENT)
					{ 
						listAllConnected.addElement(message.getMainMessage());
						displayMessage(message.getMainMessage()+" connected.");
					}

					else if(message.getMessageType() == MessageType.DISCONNECT_CLIENT)
					{ 
						listAllConnected.removeElement(message.getMainMessage());
						displayMessage(message.getMainMessage()+" disconnected.");
					}
				}
			} 
			catch (Exception e)
			{
				showMessageDialog("Error: Connection to server lost. Please try reconnecting.");

			}
			chatPanelSwitch(false);
			chooseUsernamePanelSwitch(false);
			connectPanelSwitch(true);
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
				showMessageDialog("Error: Cannot send message to server. Try Reconnecting..");
				chatPanelSwitch(false);
				chooseUsernamePanelSwitch(false);
				connectPanelSwitch(true);
			}
		}
	}
}
