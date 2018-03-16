package chat;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
public class UDPServer {
    private DatagramSocket udpSocket;
    private int port;
 
    public DatagramSocket getUdpSocket() {
		return udpSocket;
	}
	public void setUdpSocket(DatagramSocket udpSocket) {
		this.udpSocket = udpSocket;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	
	 public void sending(String s,String ip, String port) {
		 try {
    	 DatagramPacket p = new DatagramPacket(
                 s.getBytes(), s.getBytes().length, InetAddress.getByName(ip), Integer.parseInt(port));
             
				this.getUdpSocket().send(p);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
    }
	
	public UDPServer(int port) throws SocketException, IOException {
        this.setPort(port);
        this.setUdpSocket(new DatagramSocket(this.getPort()));
    }
    private void listen() throws Exception {
        System.out.println("-- Running Server at " + InetAddress.getLocalHost() + "--");
        String msg;
        
        while (true) {
            
            byte[] buf = new byte[256];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            
            // blocks until a packet is received
            this.getUdpSocket().receive(packet);
            msg = new String(packet.getData()).trim();
            
            System.out.println(
                "Message from " + packet.getAddress().getHostAddress() + ": " + msg);
        }
    }
    
    public static void main(String[] args) throws Exception {
        UDPServer client = new UDPServer(Integer.parseInt(args[0]));
        client.listen();
    }
}
