package chat;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.JTextField;

import chat.MyServerSocket;
import java.awt.Font;

public class MainUI {

	private JFrame frame;
	private JFrame frame2;
	private JTextField chatHistory;
	private JTextField ip;
	private JTextField port;
	private JTextField message;
	
	JButton btnConnect;
	JLabel connectedTo;
	
	//implicit data
	private int nbtn=0;
	
	private static MainUI window;
	private UDPServer udpServer;
	private MyServerSocket sc;
	private MyClientSocket cs;
	private UDPClient udpClient;
	
	private String messageIP;
	private String messagePort;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					window = new MainUI();
					window.frame.setVisible(true);
					window.frame2.setVisible(false);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainUI() {
		initialize();
		/*startTCPServer();
		startUDPServer();
		UDPServerSending();
		TCPServerSending();
		
		UDPClientSending();
		TCPClientSending();*/
	}
	/**
	 * initiate connection
	 */
	private void connectTo(String ip, String port) {
		if(nbtn==0) {  //tcp server
			cs = new MyClientSocket();
			if(cs.GetConnection(ip,port)) { //tcp client connect to tcp server
				System.out.println("tcp connected to:"+ip+","+port);
				connectedTo.setText(ip);
			}else {
				System.out.println("tcp connected failed");
			}
		}else if(nbtn==2) {//udp server
			udpClient = new UDPClient();
			if(udpClient.GetConnection(ip,port)) { //UDP client connect to UDP server
				System.out.println("tcp connected to:"+ip+","+port);
				connectedTo.setText(ip);
			}else {
				System.out.println("tcp connected failed");
			}
		}
		
	}
	
	
	/**
	 * sending message
	 */
	public void Sending(String message) {
		if(nbtn==1) {//tcp client
			System.out.println("tcp client sending..."+message);
			cs.SendInfor(message);
		}else if(nbtn==3) {  //UDP client
			System.out.println("UDP client sending..."+message);
			udpClient.sending(message);
		}else if(nbtn==0) {  //tcp server
			System.out.println("tcp server sending..."+message);
			sc.SendInfor(message,messageIP,messagePort);
		}else if(nbtn==2) {  //udp server
			System.out.println("UDP server sending..."+message);
			udpServer.sending(message, messageIP, messagePort);
		}
	}
	/**
	 * Receiving message
	 */
	public void Receiving() {
		if(nbtn==1) {//tcp client

			new Thread(new Runnable() {
				@Override
				public void run() {
			
				String data = null;
				try {
					String clientAddress = cs.getSocket().getInetAddress().getHostAddress();
					
			        System.out.println("\r\nNew connection from " + clientAddress);
			        
			        BufferedReader in = new BufferedReader(
			                new InputStreamReader(cs.getSocket().getInputStream()));        
			        while ( (data = in.readLine()) != null ) {
			            System.out.println("\r\nMessage from " + clientAddress + ": " + data);
			        }
			        
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				}
				}).start(); 
			
			
		}else if(nbtn==3) {  //UDP client
			new Thread(new Runnable() {
				@Override
				public void run() {
					DatagramSocket receiveSocket = null;
					try {  
			            while(true){  
			                byte[] buf = new byte[1024];  
			                DatagramPacket dp = new DatagramPacket(buf, buf.length);  
			                receiveSocket.receive(dp);  
			                String data = new String(dp.getData(), 0, dp.getLength());  
			                
			                chatHistory.setText(chatHistory.getText()+"\\r\\n "+dp.getAddress().getHostAddress()+":"+data);
			                System.out.println("Other:" + data);  
			            }  
			        } catch (IOException e) {  
			            System.out.println("receive fail");  
			        }
					}
				}).start(); 
		}/*else if(nbtn==0) {  //tcp server
			System.out.println("tcp server sending..."+message);
			sc.SendInfor(message,messageIP,messagePort);
		}else if(nbtn==2) {  //udp server
			System.out.println("UDP server sending..."+message);
			udpServer.sending(message, messageIP, messagePort);
		}*/
	}
	/**
	 * start UDP server
	 */
		public void startUDPServer() {
			Thread th = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					udpServer = new UDPServer(9090);
					
					System.out.println("-- Running UDP Server at " + InetAddress.getLocalHost() + "--");
					ip.setText(InetAddress.getLocalHost().getHostAddress().toString());
					port.setText(9090+"");
			        String msg;

			        while (true) {
			            
			            byte[] buf = new byte[256];
			            DatagramPacket packet = new DatagramPacket(buf, buf.length);
			            
			            // blocks until a packet is received
			            udpServer.getUdpSocket().receive(packet);
			            msg = new String(packet.getData()).trim();
			            
			            messageIP=packet.getAddress().getHostAddress();
			            messagePort= packet.getPort()+"";
			            connectedTo.setText(messageIP);
			            System.out.println("Message from " + packet.getAddress().getHostAddress() + ": " + msg);
			            chatHistory.setText(chatHistory.getText()+"\\r\\n "+messageIP+":"+msg);
			        }
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		});
		th.start();// Thread started	
			
		}
/**
 * start TCP server
 */
	public void startTCPServer() {
		Thread th = new Thread(new Runnable() {
			@Override
			public void run() {
				String data = null;
				try {   //listening  for incoming message
					sc = new MyServerSocket("");
					sc = sc.startService();
					//ip & port
					ip.setText(sc.getSocketAddress().getHostAddress());
					port.setText(sc.getPort()+"");
					
					sc.setClientSocket(sc.getServer().accept());
					String clientAddress = sc.getClientSocket().getInetAddress().getHostAddress();
					
					 messageIP=sc.getClientSocket().getInetAddress().getHostAddress();
			         messagePort= sc.getClientSocket().getPort()+"";
			         connectedTo.setText(messageIP);
			         
			        System.out.println("\r\nNew connection from " + clientAddress);
			        
			        BufferedReader in = new BufferedReader(
			                new InputStreamReader(sc.getClientSocket().getInputStream()));        
			        while ( (data = in.readLine()) != null ) {
			            System.out.println("\r\nMessage from " + clientAddress + ": " + data);
			            chatHistory.setText(chatHistory.getText()+"\\r\\n "+messageIP+":"+data);
			        }
			        
				} catch (IOException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		});
		th.start();// Thread started	
        
		
	}
	
	/**
	 * frame change
	 */
	public void changeFrame(int x) {
		/*frame.hide();
		frame2.show();*/
		window.frame.setVisible(false);
		window.frame2.setVisible(true);
		
		if(x==0 || x==2) {
			btnConnect.setEnabled(false);
			ip.setEditable(false);
			port.setEditable(false);
		}else if(x==1 || x==3) {
			btnConnect.setEnabled(true);
			ip.setEditable(true);
			port.setEditable(true);
		}
	}
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		
		frame = new JFrame();
		frame.getContentPane().setFont(new Font("Tahoma", Font.PLAIN, 14));
		frame.setBounds(100, 100, 600, 403);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame2 = new JFrame();
		frame2.setBounds(100, 100, 600, 419);
		frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JButton btnServer = new JButton("TCPServer");
		btnServer.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnServer.setBounds(88, 175, 135, 23);
		
		btnServer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {  //TCP server btn
				nbtn=0;
				changeFrame(nbtn);
				startTCPServer();
			}
		});
		
		JButton btnTcpclient = new JButton("TCPClient");
		btnTcpclient.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnTcpclient.setBounds(278, 175, 145, 23);
		btnTcpclient.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {  //tcp client btn
				nbtn=1;
				changeFrame(nbtn);
			}
		});
		
		
		
		JButton btnUdpserver = new JButton("UDPServer");
		btnUdpserver.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnUdpserver.setBounds(88, 124, 135, 23);
		btnUdpserver.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {  //UDP server btn
				nbtn=2;
				changeFrame(nbtn);
				startUDPServer();
				
			}
		});
		
		JButton btnClient = new JButton("UDPClient");
		btnClient.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnClient.setBounds(278, 124, 145, 23);
		btnClient.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {  //UDP client btn
				nbtn=3;
				changeFrame(nbtn);
			}
		});
		
		
		
		
		
		JLabel lblChooseOneTo = new JLabel("Choose one to begin:");
		lblChooseOneTo.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblChooseOneTo.setBounds(88, 67, 160, 23);
		
		chatHistory = new JTextField();
		chatHistory.setBounds(34, 65, 507, 248);
		chatHistory.setColumns(10);
		
		ip = new JTextField();
		ip.setBounds(87, 12, 97, 20);
		ip.setColumns(10);
		
		port = new JTextField();
		port.setBounds(231, 12, 71, 20);
		port.setColumns(10);
		
		JLabel lblPort = new JLabel("port:");
		lblPort.setBounds(188, 15, 33, 14);
		
		JLabel lblIpaddr = new JLabel("IP_addr:");
		lblIpaddr.setBounds(34, 15, 58, 14);
		
		btnConnect = new JButton("connect");
		btnConnect.setBounds(338, 11, 86, 23);
		btnConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				connectTo(ip.getText(),port.getText());
			}

			
		});
		
		message = new JTextField();
		message.setBounds(34, 324, 230, 20);
		message.setColumns(10);
		
		JButton btnSend = new JButton("send");
		btnSend.setBounds(321, 323, 71, 23);
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Sending(message.getText());
			}
		});
		frame.getContentPane().setLayout(null);
		frame2.getContentPane().setLayout(null);
		frame2.getContentPane().add(chatHistory);
		frame2.getContentPane().add(ip);
		frame2.getContentPane().add(port);
		frame2.getContentPane().add(lblPort);
		frame2.getContentPane().add(lblIpaddr);
		frame2.getContentPane().add(btnConnect);
		frame2.getContentPane().add(message);
		frame2.getContentPane().add(btnSend);
		
		JLabel lblConnectedTo = new JLabel("connected to:");
		lblConnectedTo.setBounds(34, 43, 86, 14);
		frame2.getContentPane().add(lblConnectedTo);
		
		connectedTo = new JLabel("");
		connectedTo.setBounds(135, 43, 129, 14);
		frame2.getContentPane().add(connectedTo);
		
		frame.getContentPane().add(lblChooseOneTo);
		frame.getContentPane().add(btnUdpserver);
		frame.getContentPane().add(btnClient);
		frame.getContentPane().add(btnServer);
		frame.getContentPane().add(btnTcpclient);
	}
}
