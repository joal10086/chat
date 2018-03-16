package chat;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;
public class UDPClient {
    private DatagramSocket udpSocket;
    private InetAddress serverAddress;
    private int port;
    
    public DatagramSocket getUdpSocket() {
		return udpSocket;
	}
	public void setUdpSocket(DatagramSocket udpSocket) {
		this.udpSocket = udpSocket;
	}
	public InetAddress getServerAddress() {
		return serverAddress;
	}
	public void setServerAddress(InetAddress serverAddress) {
		this.serverAddress = serverAddress;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public boolean GetConnection(String ip, String port) {
		boolean r = false;
		try {
			this.setServerAddress(InetAddress.getByName(ip));
	        this.setPort(Integer.valueOf(port));
	        this.setUdpSocket(new DatagramSocket(this.getPort()));
	        r=true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	        
	       
            
		return r;
	}
    public static void main(String[] args) throws NumberFormatException, IOException {        
       /* UDPClient sender = new UDPClient(args[0], Integer.parseInt(args[1]));
        System.out.println("-- Running UDP Client at " + InetAddress.getLocalHost() + " --");
        sender.start();*/
    }
    
    public void sending(String s) {
    	 DatagramPacket p = new DatagramPacket(
                 s.getBytes(), s.getBytes().length, this.getServerAddress(), this.getPort());
             
             try {
				this.getUdpSocket().send(p);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
    }
}
