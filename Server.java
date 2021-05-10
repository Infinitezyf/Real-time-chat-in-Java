package myQQ;

import java.awt.BorderLayout;
import java.awt.Container;
import java.io.*;
import java.net.*;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class Server extends JFrame implements Runnable {
	JTextArea inMessage = new JTextArea(12, 20);
	static final int serverPort = 8000;
	DatagramPacket pack = null;
	int user_number;
	int userid[];
	int user_id_thread;

	Server(int x) {
		super("myWechat Server");
		setBounds(350, 100, 320, 200);
		setVisible(true);
		user_number = x;
		// ---
		JPanel p = new JPanel();

		// ---
		Container con = getContentPane();
		con.add(new JScrollPane(inMessage), BorderLayout.CENTER);
		con.add(p, BorderLayout.NORTH);

		userid = new int[user_number];
		for (int i = 0; i < user_number; i++) {
			userid[i] = 10000 + i;
		}
		// ---
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		validate();
		this.setVisible(true);
		// ---
		/*
		 * Thread thread[] = new Thread [user_number]; for(int i = 0; i<user_number;
		 * i++) { thread[i] = new Thread (this); MyThread1(userid[i]);
		 * thread[i].start(); // 线程负责接收数据 }
		 */
		Thread thread = new Thread(this);
		thread.start();

	}

	public void run() {
		DatagramSocket mail1 = null;
		DatagramSocket mail2 = null;
		try {
			mail1 = new DatagramSocket(serverPort);
		} catch (SocketException e1) {
			e1.printStackTrace();
		}
		byte b[] = new byte[8192];
		try {
			// ---
			pack = new DatagramPacket(b, b.length);
		} catch (Exception e) {
		}
		String alter = null;
		int p = 0;
		this.setVisible(true);

		// 执行循环，得到信息
		// 服务器所起的作用其实是一个整合缓存的作用
		// 解决了客户端->服务器
		while (true) {
			try {
				int length_file = 0;
				int group_sign = 0;
				// 收包：第一个： 端口
				mail1.receive(pack);

				// 分析是谁发的
				int sender = pack.getPort();
				System.out.println(sender);

				// 发给谁1
				String receiver_string = new String(pack.getData(), 0, pack.getLength());
				System.out.println(receiver_string);
				int receiver_id = Integer.parseInt(receiver_string);
				if (receiver_id == 1111)
				{
					group_sign = 1;
				}

				// 收包：第二个：时间
				mail1.receive(pack);
				String time = new String(pack.getData(), 0, pack.getLength());
				
				// 收包： 第三个：话
				mail1.receive(pack);
				String wordString = new String(pack.getData(), 0, pack.getLength());

				InetAddress address = InetAddress.getByName("127.0.0.1");
				
				// 如果是群聊，就给每个人都发一遍
				
				if ( wordString.equals("Image sending!_1_2_3!@#$%^&*()"))
				{
					
					
					// 接包：大小
					mail1.receive(pack);
					String lengthString = new String(pack.getData(), 0, pack.getLength());
					length_file = Integer.parseInt(lengthString);

					// 解包: 名称
					mail1.receive(pack);
					String nameString = new String(pack.getData(), 0, pack.getLength());
					
					// 发给对面
					
					// 先发送时间
					b = (time+"!@#"+sender).getBytes();
					if(group_sign == 1)
					{
						b = (time+"!@#"+sender+"!@#1111").getBytes();
					}
					DatagramPacket data = null;
					if(group_sign == 0)
					{
						data = new DatagramPacket(b, b.length, address, receiver_id);
						mail1.send(data);
					}
					else {
						// 如果是群聊，就给每个人都发一遍
						for (int i = 0; i < user_number ; i++)
						{
							if (userid[i]!=sender) {

								data = new DatagramPacket(b, b.length, address, userid[i]);
								mail1.send(data);
							}
						}
					}

					// 跟对方id的说,有一个文件
					String message = wordString;
					
					b = message.getBytes();
					if(group_sign == 0)
					{
						data = new DatagramPacket(b, b.length, address, receiver_id);
						mail1.send(data);
					}
					else {
						// 如果是群聊，就给每个人都发一遍
						for (int i = 0; i < user_number ; i++)
						{
							if (userid[i]!=sender) {

								data = new DatagramPacket(b, b.length, address, userid[i]);
								mail1.send(data);
							}
						}
					}
					
					
					// 发送文件大小
					// DatagramPacket temp_pack = null;
					
					
					
					b = lengthString.getBytes();
					if(group_sign == 0)
					{
						data = new DatagramPacket(b, b.length, address, receiver_id);
						mail1.send(data);
					}
					else {
						// 如果是群聊，就给每个人都发一遍
						for (int i = 0; i < user_number ; i++)
						{
							if (userid[i]!=sender) {

								data = new DatagramPacket(b, b.length, address, userid[i]);
								mail1.send(data);
							}
						}
					}

					// 发送文件名
					b = nameString.getBytes();
					if(group_sign == 0)
					{
						data = new DatagramPacket(b, b.length, address, receiver_id);
						mail1.send(data);
					}
					else {
						// 如果是群聊，就给每个人都发一遍
						for (int i = 0; i < user_number ; i++)
						{
							if (userid[i]!=sender) {

								data = new DatagramPacket(b, b.length, address, userid[i]);
								mail1.send(data);
							}
						}
					}

					// 加入服务器的提示信息
					inMessage.append(nameString + "\n");
					inMessage.setCaretPosition(inMessage.getText().length());
				}
				
				else if (wordString.equals("File sending!_1_2_3!@#$%^&*()")  ) 
				{
					// 接包：大小
					mail1.receive(pack);
					String lengthString = new String(pack.getData(), 0, pack.getLength());
					length_file = Integer.parseInt(lengthString);

					// 解包: 名称
					mail1.receive(pack);
					String nameString = new String(pack.getData(), 0, pack.getLength());

					String temp_fileName = UUID.randomUUID().toString().replaceAll("-", "");

					String pathsString = "./src/myQQ/server/" + temp_fileName + "_" + nameString;
					File new_file = new File(pathsString);
					OutputStream os = new BufferedOutputStream(new FileOutputStream(pathsString), length_file);

					// 随机生成一个私钥

					int len = 0;
					int sum = 0;
					byte[] buffer = new byte[length_file];
					// 接包：文件
					while (true) {
						// 无数据则开始循环接收数据
						// 接收数据包
						mail1.receive(pack);
						len = pack.getLength();
						String packString = new String(pack.getData(), 0, len);
						buffer = pack.getData();
						if (!packString.equals("!@#$%^&*()")) {
							// 指定接收到数据的长度，可使程序正常接收数据
							os.write(buffer, 0, len);
							os.flush();
						} else {
							break;
						}
						sum += len;
						if (sum == length_file)
						{
							break;
						}
					}
					
					
					// 先发送时间
					b = (time+"!@#"+sender).getBytes();
					if(group_sign == 1)
					{
						b = (time+"!@#"+sender+"!@#1111").getBytes();
					}
					DatagramPacket data = null;
					if(group_sign == 0)
					{
						data = new DatagramPacket(b, b.length, address, receiver_id);
						mail1.send(data);
					}
					else {
						// 如果是群聊，就给每个人都发一遍
						for (int i = 0; i < user_number ; i++)
						{
							if (userid[i]!=sender) {

								data = new DatagramPacket(b, b.length, address, userid[i]);
								mail1.send(data);
							}
						}
					}

					// 跟对方id的说,有一个文件
					String message = wordString;
					
					b = message.getBytes();
					if(group_sign == 0)
					{
						data = new DatagramPacket(b, b.length, address, receiver_id);
						mail1.send(data);
					}
					else {
						// 如果是群聊，就给每个人都发一遍
						for (int i = 0; i < user_number ; i++)
						{
							if (userid[i]!=sender) {

								data = new DatagramPacket(b, b.length, address, userid[i]);
								mail1.send(data);
							}
						}
					}
					
					// 先将文件临时存储至服务器自己的里面
					// 发送文件大小
					// DatagramPacket temp_pack = null;
					b = lengthString.getBytes();
					if(group_sign == 0)
					{
						data = new DatagramPacket(b, b.length, address, receiver_id);
						mail1.send(data);
					}
					else {
						// 如果是群聊，就给每个人都发一遍
						for (int i = 0; i < user_number ; i++)
						{
							if (userid[i]!=sender) {

								data = new DatagramPacket(b, b.length, address, userid[i]);
								mail1.send(data);
							}
						}
					}

					// 发送文件名
					b = nameString.getBytes();
					if(group_sign == 0)
					{
						data = new DatagramPacket(b, b.length, address, receiver_id);
						mail1.send(data);
					}
					else {
						// 如果是群聊，就给每个人都发一遍
						for (int i = 0; i < user_number ; i++)
						{
							if (userid[i]!=sender) {

								data = new DatagramPacket(b, b.length, address, userid[i]);
								mail1.send(data);
							}
						}
					}

					// 加入服务器的提示信息
					inMessage.append(message + "\n");
					inMessage.setCaretPosition(inMessage.getText().length());
					
					// 发送文件
					

					byte []data_bits = new byte[1024];
					
					len = 0;
					InputStream is = new BufferedInputStream(new FileInputStream(pathsString));
					// 创建UDP数据报
					// 进行传送
					if(group_sign == 0)
					{
						
						while ((len = is.read(data_bits)) != -1) {
							pack = new DatagramPacket(data_bits, len, address, receiver_id);
							mail1.send(pack);
							TimeUnit.MICROSECONDS.sleep(10); // 限制传输速度 防止丢包
						}
						
					}
					else {
						// 如果是群聊，就给每个人都发一遍
						while ((len = is.read(data_bits)) != -1) {
							for (int i = 0; i < user_number ; i++)
							{
								if (userid[i]!=sender) {
									pack = new DatagramPacket(data_bits, len, address, userid[i]);
									mail1.send(pack);
									TimeUnit.MICROSECONDS.sleep(10); // 限制传输速度 防止丢包
								}
							}
							
						}
						
					}
					
					message = "!@#$%^&*()";
					b = message.getBytes();
					if(group_sign == 0)
					{
						data = new DatagramPacket(b, b.length, address, receiver_id);
						mail1.send(data);
					}
					else {
						// 如果是群聊，就给每个人都发一遍
						for (int i = 0; i < user_number ; i++)
						{
							if (userid[i]!=sender) {

								data = new DatagramPacket(b, b.length, address, userid[i]);
								mail1.send(data);
							}
						}
					}
					
					// 加入服务器的提示信息
					inMessage.append(message + "\n");
					inMessage.setCaretPosition(inMessage.getText().length());
				} 
				else if (wordString.equals("add_friend_request") ) {
					
					String message = new String();
					message = time + "\n" + wordString;
					inMessage.append(message + "\n");
					inMessage.setCaretPosition(inMessage.getText().length());
					
					message = "1";
					b = message.getBytes();
					DatagramPacket data = null;
					data = new DatagramPacket(b, b.length, address, sender);
					mail1.send(data);
				}
				else {

					String message = new String();
					message = time + "\n" + wordString;
					inMessage.append(message + "\n");
					inMessage.setCaretPosition(inMessage.getText().length());

					// 发送时间
					b = (time+"!@#"+sender).getBytes();
					if(group_sign == 1)
					{
						b = (time+"!@#"+sender+"!@#1111").getBytes();
					}
					DatagramPacket data = null;
					if(group_sign == 0)
					{
						data = new DatagramPacket(b, b.length, address, receiver_id);
						mail1.send(data);
					}
					else {
						// 如果是群聊，就给每个人都发一遍
						for (int i = 0; i < user_number ; i++)
						{
							if (userid[i]!=sender) {

								data = new DatagramPacket(b, b.length, address, userid[i]);
								mail1.send(data);
							}
						}
					}

					// 发送
					b = wordString.getBytes();
					if(group_sign == 0)
					{
						data = new DatagramPacket(b, b.length, address, receiver_id);
						mail1.send(data);
					}
					else {
						// 如果是群聊，就给每个人都发一遍
						for (int i = 0; i < user_number ; i++)
						{
							if (userid[i]!=sender) {

								data = new DatagramPacket(b, b.length, address, userid[i]);
								mail1.send(data);
							}
						}
					}

				}
			} catch (Exception e) {
			}

		}

	}

	public static void main(String args[]) {

		new Server(3);
	}

}
