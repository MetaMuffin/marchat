import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.OnMessage;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import java.security.MessageDigest;
import javax.xml.bind.DatatypeConverter;

import org.json.JSONObject;

public class Client {

	public static boolean loggedIn = false;
	final static String uri = "ws://marchat.zapto.org:5555/ws";
	//final static String uri = "ws://25.86.154.196:5555/ws";
    final static CountDownLatch messageLatch = new CountDownLatch(1);
    
    public static String username = "";
    public static String currentChannel = "";
    public static String currentChannelTryToJoin = "";
    
    public static ChatWindow chatWindow;
    public static JSONObject activeChannelList;
    public static Session ServerSession;
    public static boolean connectedToServer = false;
    

    public static void connectToServer() {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            System.out.println("Connecting to " + uri);
            container.connectToServer(MyClientEndpoint.class, URI.create(uri));
            connectedToServer = true;
            //messageLatch.await(100, TimeUnit.SECONDS);
        } catch (DeploymentException /*| InterruptedException */| IOException ex) {
        	System.out.println("connection failed");
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            connectedToServer = false;
        }
    }
    
    public boolean SendLogin(String username, String password) {
    	try {
    		
    		
            String login = "{\"username\":\"" + username + "\",\"password\":\"" + Encoding.hashSHA256(password.getBytes()) + "\",\"anti_replay\":\"" + Encoding.hashSHA256((Encoding.hashSHA256(password.getBytes()) + " " + Instant.now().getEpochSecond()).getBytes()) + "\",\"timestamp\":" + Instant.now().getEpochSecond() + "}";
            String loginB64 = "login:" + Encoding.Base64encode(login);
            
            
            System.out.println("Sending message to endpoint: " + loginB64);
            ServerSession.getBasicRemote().sendText(loginB64);
            
            this.username = username;
            return true;
        } catch (IOException ex) {
            Logger.getLogger(MyClientEndpoint.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } 
    }
    
    public boolean SendRegister(String username, String password) {
    	try {
    		
    		
            String register = "{\"username\":\"" + username + "\",\"password\":\"" + Encoding.hashSHA256(password.getBytes()) + "\"}";
            String registerB64 = "register:" + Encoding.Base64encode(register);
            
            
            System.out.println("Sending message to endpoint: " + registerB64);
            ServerSession.getBasicRemote().sendText(registerB64);
            return true;
        } catch (IOException ex) {
            Logger.getLogger(MyClientEndpoint.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } 
    }
    
    public void SendChannelUserAdd(String username, String channelName) {
    	try {
    	 String channelUserAdd = "{\"username\":\"" + username + "\",\"channel\":\"" + channelName + "\"}";
    	 String channelUserAddB64 = "channel_user_add:" + Encoding.Base64encode(channelUserAdd);
    	 
    	 System.out.println("Sending message to endpoint: " + channelUserAddB64);
    	 
			ServerSession.getBasicRemote().sendText(channelUserAddB64);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public void sendChannelCreate(String name) {
    	try {
       	 String channel_create = "{\"name\":\"" + name + "\"}";
       	 String channel_createAddB64 = "channel_create:" + Encoding.Base64encode(channel_create);
       	 
       	 System.out.println("Sending message to endpoint: " + channel_createAddB64);
       	currentChannelTryToJoin = name;
   		ServerSession.getBasicRemote().sendText(channel_createAddB64);
   		} catch (IOException e) {
   			e.printStackTrace();
   		}
    }
    
    public void SendMessage(String message) {
    	
    	if(message.equals("")) { return; }
    	
    	try {
          	 String msg = "{\"text\":\"" + message + "\"}";
          	 String msgB64 = "message:" + Encoding.Base64encode(msg);
          	 
          	 System.out.println("Sending message to endpoint: " + msgB64);
          	 
      			ServerSession.getBasicRemote().sendText(msgB64);
      		} catch (IOException e) {
      			e.printStackTrace();
      		}
    }
    
    public void SendJoinChannel(String nameOfChannel) {
    	try {
         	 String channelJoin = "{\"name\":\"" + nameOfChannel + "\", \"count\":10, \"offset\": -1}";
         	 String channelJoinB64 = "channel:" + Encoding.Base64encode(channelJoin);
         	 
         	 System.out.println("Sending message to endpoint: " + channelJoinB64);
         	currentChannelTryToJoin = nameOfChannel;
     		ServerSession.getBasicRemote().sendText(channelJoinB64);
     		} catch (IOException e) {
     			e.printStackTrace();
     		}
    }
    
    
    public static void showInfoBox(String title, String msg) {

 		JOptionPane.showMessageDialog(null, msg, title, JOptionPane.INFORMATION_MESSAGE);
    	
    }
    
    public static void showErrorBox(String title, String msg) {

 		JOptionPane.showMessageDialog(null, msg, title, JOptionPane.ERROR_MESSAGE);
    	
    }
    
    
}