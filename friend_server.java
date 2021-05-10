package myQQ;

import java.awt.BorderLayout;
import java.awt.Container;
import java.io.*;
import java.net.*;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


public class friend_server extends JFrame
	implements Runnable
{
	JTextArea inMessage = new JTextArea(12,20); 
	static final int friend_server_port = 8100;
	static final int USER_MAX = 50;
	static final int FRIEND_MAX = 20;
	DatagramPacket pack = null;
	
	InetAddress address; 
	int user_number;
	Vector userid;
	Vector []user_friend = new Vector [USER_MAX];
	
	friend_server() {
		
	}
	friend_server(int x)
	{
		super("friend");
		setBounds(350,100,320,200);
    	setVisible(true);
    	user_number = x;
    	// ---
    	JPanel p = new JPanel();    
    	
    	// ---
    	Container con=getContentPane();
    	con.add(new JScrollPane(inMessage),BorderLayout.CENTER);
    	con.add(p,BorderLayout.NORTH);
    	
    	userid = new Vector();
    	for (int i = 0; i < user_number; i++)
    	{
    		user_friend[i] = new Vector();
    		userid.add(10000+i);
    		if (i == 0)
    			user_friend[i].add(10001);
    		else if (i == 1){
    			user_friend[i].add(10000);
			}
    	}
    	
    	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	validate();
    	this.setVisible(true);
    	Thread thread = new Thread (this);
		thread.start(); 
    	
	}
	public void run()
	{
		DatagramSocket mail1 = null;       
    	try {
			mail1 = new DatagramSocket(friend_server_port);
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	byte b[]=new byte[8192];
    	try
    	{
    		// ---
    		address = InetAddress.getByName("127.0.0.1");
    		pack = new DatagramPacket(b,b.length);
    	}
    	catch(Exception e){}
    	
    	//执行循环，得到信息
    	while(true)
    	{
    		try 
    		{
        		
        		// 收包：第一个： 端口
        		mail1.receive(pack);
    			
        		// 分析是谁发的
       			int sender = pack.getPort();
       			// System.out.print(sender);
       			
       			// 发给谁
       			String receiver_string = new String(pack.getData(),0,pack.getLength());
       			if (receiver_string .equals("add_friend_request"))
       			{
       				mail1.receive(pack);
       				String new_one_string = new String(pack.getData(),0,pack.getLength());
       				int new_one_id = Integer.parseInt(new_one_string);
       				
       				int index_of_sender = userid.indexOf(sender);
       				int index_of_new_one = userid.indexOf(new_one_id);
       				
       				// 已经有了该好友
       				if(user_friend[index_of_sender].contains(new_one_id))
       				{
       					// 发回信息：3
       					b = "3".getBytes();
       					DatagramPacket data = new DatagramPacket(b,b.length,address,sender); // 一个给服务器server的包
       	        		//自身端口 10000 -> 8000
       	        		mail1.send(data);
       				}
       				else if (index_of_new_one == -1)
       				{
       					// 发回信息：2
       					b = "2".getBytes();
       					DatagramPacket data = new DatagramPacket(b,b.length,address,sender); // 一个给服务器server的包
       	        		//自身端口 10000 -> 8000
       	        		mail1.send(data);
       				}
       				else if (!user_friend[index_of_sender].contains(new_one_id)) {

       					// 先发送好友申请给新增的好友
       					b = "1".getBytes();
       					DatagramPacket data = new DatagramPacket(b,b.length,address,sender); // 一个给服务器server的包
       	        		//自身端口 10000 -> 8000
       	        		mail1.send(data);
       					
       					// 添加进去
       	        		user_friend[index_of_sender].add(new_one_id);
       					
       				}
       			}
       			
   	     	   	
    		}
    		catch ( Exception e) {}
     
     	   	
    	}
	     	   	
			
     }
	public static void main(String args[])
    {
	
    	new friend_server (3);
    }
	
}
