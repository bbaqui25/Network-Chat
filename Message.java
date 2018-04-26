/*
 * Networked Chat with RSA Encryption/Decryption
 * Project 5
 * CS 342 - Fall 2017
 * 
 * - Margi Katwala
 * - Bushra Baqui
 * - Aditya Sinha
 * 
 * **Message.java**  
 */

import java.io.Serializable;

public class Message implements Serializable 
{
	 private MessageType messageType;
	 private String messageSender;
	 private String messageReceiver;
	 private String mainKey;
	 private String mainMessage;

	 public Message(MessageType type, String sender, String reciever, String content, String key) 
	 {
		  this.messageType = type;
		  this.messageSender = sender;
		  this.messageReceiver = reciever;
		  this.mainMessage = content;
		  this.mainKey = key;		  
	 }
	 
	 public MessageType getMessageType() 
	 {
		 return messageType;
	 }
	 
	 public String getMessageSender() 
	 {
		 return messageSender;
	 }
	
	 public String getMainMessage() 
	 {
		 return mainMessage;
	 }
	
	 public String getMessageReceiver() 
	 {
		 return messageReceiver;
	 }
	 
	 public String getMainKey() 
	 {
		 return mainKey;
	 }
 
	 @Override
	 public String toString() 
	 {
		  return messageSender+": "+mainMessage;
	 }
}
