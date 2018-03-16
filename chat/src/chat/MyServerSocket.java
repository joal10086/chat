package chat;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
public class MyServerSocket {
    private ServerSocket server;
    private Socket clientSocket;
    
	
	public Socket getClientSocket() {
		return clientSocket;
	}
	public void setClientSocket(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}
	public MyServerSocket(String bindAddr) throws Exception {
        this.setServer(new ServerSocket(0, 1, InetAddress.getByName(bindAddr)));
    }
    public InetAddress getSocketAddress() {
        return this.server.getInetAddress();
    }
    
    public int getPort() {
        return this.server.getLocalPort();
    }
    
    public static void main(String[] args) throws Exception {
       
    }
	public MyServerSocket startService() throws Exception {
		 MyServerSocket app = new MyServerSocket(null);
	        
	        System.out.println("\r\nRunning Server: " + 
	                "Host=" + app.getSocketAddress().getHostAddress() + 
	                " Port=" + app.getPort());
	        
       
		return app;
	}
	@SuppressWarnings("resource")
	public String SendInfor(String s,String ip, String port) {
		PrintWriter out;
		try {
			out = new PrintWriter(new Socket(InetAddress.getByName(ip),Integer.parseInt(port)).getOutputStream(), true);
			out.println(s);
	        out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public ServerSocket getServer() {
		return server;
	}
	public void setServer(ServerSocket server) {
		this.server = server;
	}
}