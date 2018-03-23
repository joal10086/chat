package chat;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import java.awt.Font;

public class MainUI {

	private JFrame frame;
	private JFrame frame2;
	private JTextArea chatHistory;
	private JTextField ip;
	private JTextField port;
	private JTextField message;
	
	JButton btnConnect;
	JLabel connectedTo;
	
	//implicit data
	private int nbtn=0;
	String host = "";
	
	private static MainUI window;
	private DatagramSocket UDPServerSocket;
	private DatagramSocket UDPClientSocket;
	private ServerSocket TCPServerSocket;
	private Socket TCPClientSocket;
	  
	private  PrintStream serverOut;
	private  PrintStream clientOut;
	
	
	private String messageIP;
	private String messagePort;
	public String receivedMsg = null;
	
private boolean test=false;
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
	}
	/**
	 * initiate connection
	 */
	private void connectTo(String ip, String port) {
		System.out.println("connected to:"+ip+","+port);
		messageIP=ip;
		messagePort=port;		
		connectedTo.setText(messageIP+":"+messagePort);
		if(nbtn==1) {  //tcp client
			try {
				TCPClientSocket = new Socket(InetAddress.getByName(ip), Integer.parseInt(port));
				Receiving();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} 
		}else if(nbtn==3) {//udp client
			try {
				UDPClientSocket  =new DatagramSocket(3030);
				Receiving();  //listening after initiate UDP client 
			} catch (SocketException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	
	/**
	 * sending message
	 * @throws IOException 
	 */
	public void Sending(String message) throws IOException {
		if(nbtn==1) {//tcp client
			System.out.println("tcp client sending..."+message);
			
			Thread th = new Thread(new Runnable() {
				@Override
				public void run() {
					boolean check = true;
		            diaplayMsg(host,message);
		            while(check){
		            	System.out.println("sendng to ip="+TCPClientSocket.getInetAddress()+",port="+TCPClientSocket.getPort());
		            	if(test) {
		            		connectTo(ip.getText(),port.getText());
		            	}
		            	test=true;
		            	try {
							clientOut = new PrintStream(TCPClientSocket.getOutputStream());
						} catch (IOException e) {
							e.printStackTrace();
						}
						clientOut.println(message);
						System.out.println("sendn");
		                
						check=false;
		            }
				}});
			th.start();
		}else if(nbtn==3) {  //UDP client
			System.out.println("UDP client sending..."+message);
			Thread th = new Thread(new Runnable() {
				@Override
				public void run() {
					try{
			            DatagramPacket packet = null;
			            boolean check = true;
			            diaplayMsg(host,message);
			            while(check){
			                packet =  new DatagramPacket(message.getBytes(),message.getBytes().length,InetAddress.getByName(messageIP),Integer.valueOf(messagePort));
			                UDPClientSocket.send(packet);
			                
			                check=false;
			            }    
			        }catch(Exception e){
			            e.printStackTrace();
			        }
				}});
			th.start();
		}else if(nbtn==0) {  //tcp server
			System.out.println("tcp server sending..."+message);
			Thread th = new Thread(new Runnable() {
				@Override
				public void run() {
					diaplayMsg(host,message);
					serverOut.println(message+"\n"); 
				}});
			th.start();
		}else if(nbtn==2) {  //udp server
			System.out.println("UDP server sending..."+message);
			Thread th = new Thread(new Runnable() {
				@Override
				public void run() {
					try{
			            DatagramPacket packet = null;
			            boolean check = true;
			            diaplayMsg(host,message);
			            while(check){
			                packet =  new DatagramPacket(message.getBytes(),message.getBytes().length,InetAddress.getByName(messageIP),Integer.valueOf(messagePort));
			                UDPServerSocket.send(packet);
			                check=false;
			            }    
			        }catch(Exception e){
			            e.printStackTrace();
			        }
				}});
			th.start();
			
		}
		
	}
	/**
	 * Receiving message
	 */
	public void Receiving() {
		if(nbtn==1) {//tcp client
			//new  thread for TCP client listening to the coming message
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {   //listening  for incoming message
							
					        while(true){  
					            try{  
					                BufferedReader buf = new BufferedReader(new InputStreamReader(TCPClientSocket.getInputStream()));  
					                clientOut = new PrintStream(TCPClientSocket.getOutputStream());
					               
					                receivedMsg =  buf.readLine();
					                clientOut.println("echo,"+receivedMsg);
				                    
				                    messageIP=TCPClientSocket.getInetAddress().getHostAddress();
							        messagePort= TCPClientSocket.getPort()+"";
							        
							        System.out.println("message received from:ip="+TCPClientSocket.getInetAddress()+",port="+TCPClientSocket.getPort());
					    			
							        connectedTo.setText(messageIP+":"+messagePort);
									diaplayMsg(messageIP,receivedMsg);
					            }catch(Exception e){  
					                e.printStackTrace();  
					            } 
					        }  
					        
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}).start(); 
		}else if(nbtn==3) {  //UDP client
			//new  thread for UDP client listening the coming message
			new Thread(new Runnable() {
				@Override
				public void run() {
					try{
			            byte[] buffer = new byte[1024];    
			            DatagramPacket packet =  new DatagramPacket(buffer,buffer.length);   //new packet for receiving message
			            while(true){
			            	UDPClientSocket.receive(packet);
			            	receivedMsg = new String(packet.getData()).trim();
			                
			                messageIP=packet.getAddress().getHostAddress();
				            messagePort= packet.getPort()+"";
				            
				            connectedTo.setText(messageIP+":"+messagePort);
				    		diaplayMsg(messageIP,receivedMsg);
			            }
			        }catch(Exception e){
			            e.printStackTrace();
			        }
					}
				}).start(); 
		}
	}
	/**
	 * display message in textArea
	 */
	public void diaplayMsg(String ip,String msg) {
		// clear content in textArea if content line reaches MAX (eg, 10)
		if(chatHistory.getLineCount()<10) {  
			chatHistory.setText(chatHistory.getText()+"\r\n "+messageIP+":"+msg);
		}else {
			chatHistory.setText(messageIP+":"+msg);
			
		}
	}
	/**
	 * start UDP server
	 */
		public void startUDPServer() {
			Thread th = new Thread(new Runnable() {
			@Override
			public void run() {
				try{
		            byte[] buffer = new byte[1024];    
		            DatagramPacket packet =  new DatagramPacket(buffer,buffer.length);   //packet receiving msg
		            while(true){
		            	UDPServerSocket.receive(packet);
		                String msg = new String(packet.getData()).trim();
		                
		                messageIP=packet.getAddress().getHostAddress();
			            messagePort= packet.getPort()+"";
			            connectedTo.setText(messageIP+":"+messagePort);
			            System.out.println("Message from " + packet.getAddress().getHostAddress() + ": " + msg);
			            diaplayMsg(messageIP,msg);
		            }
		        }catch(Exception e){
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
				try {   //listening  for incoming message
			      //ip & port
					ip.setText(TCPServerSocket.getInetAddress().getHostAddress());
					port.setText(TCPServerSocket.getLocalPort()+"");
					
			        Socket client = null;  
			        boolean f = true;  
			        while(f){  
			            client = TCPServerSocket.accept();  
			            try{  
			                 
			                BufferedReader buf = new BufferedReader(new InputStreamReader(client.getInputStream()));  
		                    String str =  buf.readLine();
		                    
			                serverOut = new PrintStream(client.getOutputStream()); 
			                
			                System.out.println("message received from:ip="+client.getInetAddress()+",port="+client.getPort());
			    			
		                    
		                    messageIP=client.getInetAddress().getHostAddress();
					        messagePort= client.getPort()+"";
					        connectedTo.setText(messageIP+":"+messagePort);
					        
				            diaplayMsg(messageIP,str);
			            }catch(Exception e){  
			                e.printStackTrace();  
			            } 
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
		//change UI
		window.frame.setVisible(false);
		window.frame2.setVisible(true);
		
		InetAddress loc;
		try {
			loc = InetAddress.getLocalHost();
			host = loc.getHostAddress();
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
		if(x==0 || x==2) {
			if(x==0) {// TCP server 
				frame2.setTitle("TCP Server");
				try {
			        TCPServerSocket =new ServerSocket(0, 1, InetAddress.getByName(host));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else { // UDP server 
				frame2.setTitle("UDP Server");
				try {
					UDPServerSocket=new DatagramSocket(9090);
					ip.setText(host);
					port.setText(9090+"");
				} catch (SocketException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			btnConnect.setEnabled(false);
			ip.setEditable(false);
			port.setEditable(false);
		}else if(x==1 || x==3) {
			if(x==1) {
				frame2.setTitle("TCP Client");
			}else {
				frame2.setTitle("UDP Client");
			}
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
		
		chatHistory = new JTextArea();
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
				try {
					Sending(message.getText());
				} catch (IOException e) {
					e.printStackTrace();
				}
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
