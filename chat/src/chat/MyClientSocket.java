package chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
public class MyClientSocket {
    private Socket socket;
    public Socket getSocket() {
		return socket;
	}
	public void setSocket(Socket socket) {
		this.socket = socket;
	}
	
    public static void main(String[] args) throws Exception {}
    
	public boolean GetConnection(String ip, String port) {
		boolean r = false;
		try {
			this.setSocket(new Socket(
			            InetAddress.getByName(ip), 
			            Integer.parseInt(port)));
			
			 System.out.println("\r\nConnected to: " + this.getSocket().getInetAddress());
	            r=true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	        
	       
            
		return r;
	}
	public String SendInfor(String s) {
		PrintWriter out;
		try {
			out = new PrintWriter(this.getSocket().getOutputStream(), true);
			out.println(s);
	        out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
         
		return null;
	}
	public String ReceiveInfor(String s) {
		String data = null;
		try {
			String clientAddress = this.getSocket().getInetAddress().getHostAddress();
			
	        System.out.println("\r\nNew connection from " + clientAddress);
	        
	        BufferedReader in = new BufferedReader(
	                new InputStreamReader(this.getSocket().getInputStream()));        
	        while ( (data = in.readLine()) != null ) {
	            System.out.println("\r\nMessage from " + clientAddress + ": " + data);
	        }
	        
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}   
}