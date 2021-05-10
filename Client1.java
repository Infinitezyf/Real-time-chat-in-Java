package myQQ;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.html.HTMLEditorKit;


public class Client1 extends JFrame implements Runnable {

	// 文本框，发送信息
	// JTextArea outMessage_textField = new JTextArea(3, 50);
	// 文本框，接收对面发送的信息
	// JTextArea inMessage_textArea = new JTextArea(12, 20);
	// 自身端口
	// 自身接收
	// 自身发送
	int portsend;
	// 发送的服务器端口
	final static int server = 8000;
	final static int friend_server = 8100;
	JButton send_message_button = new JButton("发送信息");
	JButton send_file_button = new JButton("发送文件");
	InetAddress address; // 自身的主机地址 ： 127.0.0.1
	DatagramPacket data = null;
	DatagramSocket mail = null; // 发送者的mail邮箱
	// DatagramSocket mail2 = null; // 接收者的mail邮箱
	DatagramSocket friend_mail = null; // 接收者的mail邮箱
	OutputStream outputStream = null; // 输入输出流 TCP
	InputStream input = null; // 输入输出流

	// 组件
	JMenuBar menuBar = new JMenuBar();
	JMenu menu1 = new JMenu("好友列表");
	JMenu menu2 = new JMenu("发起群聊");
	JMenu menu3 = new JMenu("皮肤");
	JMenu menu4 = new JMenu("个人信息");
	JMenu menu5 = new JMenu("和好友聊天");
	JMenuItem view_friendsItem = new JMenuItem("查看好友");
	JMenuItem add_friendItem = new JMenuItem("添加好友");
	JMenuItem gourpChatItem = new JMenuItem("发起群聊");

	JMenuItem item4 = new JMenuItem("白天模式");
	JMenuItem item5 = new JMenuItem("黑色模式");
	JMenuItem item6 = new JMenuItem("个人信息");

	// 对话框内
	private JScrollPane scrollPane = null;
	private JTextPane text = null;
	private Box box = null; // 放输入组件的容器
	private JButton b_insert = null, b_facial_expression = null, b_file = null; // 插入按钮;清除按钮;插入图片按钮
	private JTextField addText = null; // 文字输入框
	private JComboBox fontName = null, fontSize = null, fontStyle = null, fontColor = null, fontBackColor = null; // 字体名称;字号大小;文字样式;文字颜色;文字背景颜色
	private StyledDocument doc = null;
	private FontAttrib my_time = null;
	private FontAttrib object_time = null;

	// 好友列表
	final int Friend_MAX = 100;
	int friend_number;
	int new_friend_id;
	int[] friend_list = new int[Friend_MAX];
	private JTextPane []messageRecord = new JTextPane[Friend_MAX]; 
	Thread thread;
	
	// 好友
	int myID;
	public int objectID;
	String myname;

	Client1() {
	}

	Client1(String q, int number) {
		// ---
		super(q);
		setResizable(false);
		myID = number;

		try {
			address = InetAddress.getByName("127.0.0.1");
			mail = new DatagramSocket(myID); // 发送者的mail邮箱
			// friend_mail = new DatagramSocket(); // 好友申请
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		portsend = myID;
		friend_number = 1;
		// 默认好友
		if (myID == 10000)
		{
			friend_list[0] = 10001;
			friend_list[1] = 10002;
			friend_number++;
		}
		else {
			friend_list[0] = 10000;
		}

		// TODO:增加上方的菜单栏：选项分别为：
		// 2. 发起群聊 
		// 3. 更换模式（深夜模式）
		// 4. 空判断 1

		setJMenuBar(menuBar);
		menuBar.add(menu1);
		menu1.addSeparator();
		menuBar.add(menu2);
		menu2.addSeparator();
		menuBar.add(menu3);
		menu3.addSeparator();
		menuBar.add(menu4);
		menuBar.add(menu5);
		menu4.addSeparator();

		menu1.add(view_friendsItem);
		menu1.add(add_friendItem);

		menu2.add(gourpChatItem);
		menu3.add(item4);
		menu3.add(item5);

		menu4.add(item6);

		// 查看好友
		view_friendsItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				String buttonname = e.getActionCommand();
				if (buttonname.equals("查看好友")) {
					if (friend_number == 0) {
						JOptionPane.showMessageDialog(null, "抱歉，您没有好友。快去添加好友吧~");
						return;
					}
					JFrame jFrame = new JFrame("选择对话好友");// 定义一个窗体

					JRadioButton[] friend = new JRadioButton[friend_number];// 定义一个单选按钮
					ButtonGroup group = new ButtonGroup();
					JPanel panel = new JPanel();
					for (int i = 0; i < friend_number; i++) {
						// error 解决
						String a = friend_list[i] + "";
						friend[i] = new JRadioButton(a);
						panel.add(friend[i]);
						group.add(friend[i]);
						friend[i].addActionListener(this);
					}
					jFrame.setLocationRelativeTo(null);
					jFrame.setContentPane(panel);
					jFrame.setSize(330, 80);// 设置窗体大小
					jFrame.setVisible(true);
				} else {
					// saveAsObj();
					objectID = Integer.parseInt(buttonname);
					readFromObj();
					JOptionPane.showMessageDialog(null, "切换对话好友：" + buttonname);
					menu5.setText("和好友" + objectID + "聊天");
				}
			}

		});

		gourpChatItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, "切换群聊！");
				menu5.setText("和群聊聊天");
				objectID = 1111;
				readFromObj();
			}

		});
		// 添加好友
		add_friend();

		setBounds(200, 100, 740, 360);
		setVisible(true);


		JTextPane pane = new JTextPane();
		construct_textpane();
		
		this.add(pane,BorderLayout.CENTER);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		validate();

		
		my_time = new FontAttrib();
		object_time = new FontAttrib();

		// 创建新线程
		thread = new Thread(this);
		thread.start(); // 线程负责接收数据
	}

	private void construct_textpane() {
		try { // 使用Windows的界面风格
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (Exception e) {
			e.printStackTrace();
		}

		text = new JTextPane();
		text.setEditable(false);
		doc = text.getStyledDocument(); // 获得JTextPane的Document
		scrollPane = new JScrollPane(text);
		scrollPane.setPreferredSize(new Dimension(400, 400));
		addText = new JTextField(18);
		String[] str_name = { "宋体", "黑体", "Dialog", "Gulim" };
		String[] str_Size = { "12", "14", "18", "22", "30", "40" };
		String[] str_Style = { "常规", "斜体", "粗体", "粗斜体" };
		String[] str_Color = { "黑色", "红色", "蓝色", "黄色", "绿色" };
		String[] str_BackColor = { "无色", "灰色", "淡红", "淡蓝", "淡黄", "淡绿" };
		fontName = new JComboBox(str_name); // 字体名称
		fontSize = new JComboBox(str_Size); // 字号
		fontStyle = new JComboBox(str_Style); // 样式
		fontColor = new JComboBox(str_Color); // 颜色
		fontBackColor = new JComboBox(str_BackColor); // 背景颜色
		b_insert = new JButton("发送"); // 发送
		b_facial_expression = new JButton("表情"); // 清除
		b_file = new JButton("文件"); // 插入图片

		b_insert.addActionListener(new ActionListener() { // 插入文字的事件
			public void actionPerformed(ActionEvent e) {
				if (send_message() == 1)
				{
					insert(getFontAttrib());
					addText.setText("");
					saveAsObj();
				}
			}
		});

		b_facial_expression.addActionListener(new ActionListener() { // 表情事件
			public void actionPerformed(ActionEvent arg0) {
				// 表情包设置
				// 表情包也是发送文件，但是限定为png等形式
				
				try {
					
					File file = send_image();
					// 应该插入一个文件的标识，还有名称
					if (file != null)
					{
						insertIcon(file,"image"); // 插入图片
						saveAsObj();
					}
					
				} catch (IOException e) {
					
					e.printStackTrace();
				} catch (InterruptedException e) {
					
					e.printStackTrace();
				}
			}
		});

		b_file.addActionListener(new ActionListener() { // 插入文件事件
			public void actionPerformed(ActionEvent arg0) {
				
				try {
				
					File file = send_file();
					// 应该插入一个文件的标识，还有名称
					if (file != null) {

						insertIcon(file,"finished"); // 插入图片

						saveAsObj();
					}
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		});
		box = Box.createVerticalBox(); // 竖结构
		Box box_1 = Box.createHorizontalBox(); // 横结构
		Box box_2 = Box.createHorizontalBox(); // 横结构
		box.add(box_1);
		box.add(Box.createVerticalStrut(8)); // 两行的间距
		box.add(box_2);
		box.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8)); // 8个的边距
		// 开始将所需组件加入容器

		box_1.add(new JLabel("字体：")); // 加入标签
		box_1.add(fontName); // 加入组件
		box_1.add(Box.createHorizontalStrut(8)); // 间距
		box_1.add(new JLabel("样式："));
		box_1.add(fontStyle);
		box_1.add(Box.createHorizontalStrut(8));
		box_1.add(new JLabel("字号："));
		box_1.add(fontSize);
		box_1.add(Box.createHorizontalStrut(8));
		box_1.add(new JLabel("颜色："));
		box_1.add(fontColor);
		box_1.add(Box.createHorizontalStrut(8));
		box_1.add(new JLabel("背景："));
		box_1.add(fontBackColor);
		box_1.add(Box.createHorizontalStrut(8));
		box_1.add(b_file);
		box_2.add(addText);
		box_2.add(Box.createHorizontalStrut(8));
		box_2.add(b_insert);
		box_2.add(Box.createHorizontalStrut(8));
		box_2.add(b_facial_expression);
		this.getRootPane().setDefaultButton(b_insert); // 默认回车按钮
		this.getContentPane().add(scrollPane);
		this.getContentPane().add(box, BorderLayout.SOUTH);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		pack();
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		addText.requestFocus();
	}

	private void insertIcon(File file, String s) {

		doc = text.getStyledDocument(); // 获得JTextPane的Document;
		String filenameString = file.getName();
		text.setCaretPosition(doc.getLength()); // 设置插入位置
		JButton fileButton = new JButton(filenameString);

		fileButton.addActionListener(new ActionListener() { // 持续监听
			public void actionPerformed(ActionEvent e) {
				System.out.print("打开文件");
				// 打开
		        Desktop desktop = Desktop.getDesktop();
		        if(file.exists() && s .equals( "finished"))
					try {
						desktop.open(file);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

			}
		});
		// 设置监听
		ImageIcon fileIcon = null;
		int width = 20,height = 20;	//这是图片和JLable的宽度和高度
		if (s .equals("finished") )
			fileIcon = new ImageIcon("./src/myQQ/icon/file.png");
		else if (s .equals("no")  )
			fileIcon = new ImageIcon("./src/myQQ/icon/file_unfinished.png");
		else if(s.equals("image")) 
		{
			fileIcon = new ImageIcon(file.getPath());
			width = 300;
			height = 300;
		}
	
		fileIcon.setImage(fileIcon.getImage().getScaledInstance(width, height,Image.SCALE_FAST ));//可以用下面三句代码来代替
		fileButton.setIcon(fileIcon);
		
		if(s .equals("finished") || s .equals("no"))
			text.insertComponent(fileButton);
		else if(s.equals("image"))
			  text.insertIcon(fileIcon); // 插入图片
		
		insert(new FontAttrib()); // 这样做可以换行
	}

	private void insert(FontAttrib attrib) {
		try { // 插入文本

			doc = text.getStyledDocument(); // 获得JTextPane的Document;;
			doc.insertString(doc.getLength(), attrib.getText() + "\n", attrib.getAttrSet());
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
	
	private FontAttrib get_time_FontAttrib(String time, int mode)
	{
		FontAttrib att = new FontAttrib();
		// 先利用
		
		
		if (mode == 1)
		{
			att.setText(time + " 你：");
			att.setColor(new Color(255, 0, 0));
		}
		else if(mode == 2)
		{
			att.setText(time + " 对方：");
			att.setColor(new Color(0, 255, 0));
		}
		
		att.setName("宋体");
		att.setSize(12);
		att.setStyle(FontAttrib.BOLD);
		return att;
	}

	private FontAttrib getFontAttrib(String []attrib) {
		// 重载
		FontAttrib att = new FontAttrib();
		att.setText(attrib[5]);
		att.setName(attrib[0]);
		att.setSize(Integer.parseInt(attrib[1]));

		// 设置
		String temp_style = (String) attrib[2];
		if (temp_style.equals("常规")) {
			att.setStyle(FontAttrib.GENERAL);
		} else if (temp_style.equals("粗体")) {
			att.setStyle(FontAttrib.BOLD);
		} else if (temp_style.equals("斜体")) {
			att.setStyle(FontAttrib.ITALIC);
		} else if (temp_style.equals("粗斜体")) {
			att.setStyle(FontAttrib.BOLD_ITALIC);
		}
		
		// 设置
		String temp_color = (String) attrib[4];
		if (temp_color.equals("黑色")) {
			att.setColor(new Color(0, 0, 0));
		} else if (temp_color.equals("红色")) {
			att.setColor(new Color(255, 0, 0));
		} else if (temp_color.equals("蓝色")) {
			att.setColor(new Color(0, 0, 255));
		} else if (temp_color.equals("黄色")) {
			att.setColor(new Color(255, 255, 0));
		} else if (temp_color.equals("绿色")) {
			att.setColor(new Color(0, 255, 0));
		}
		
		// 设置
		String temp_backColor = (String) attrib[3];
		if (!temp_backColor.equals("无色")) {
			if (temp_backColor.equals("灰色")) {
				att.setBackColor(new Color(200, 200, 200));
			} else if (temp_backColor.equals("淡红")) {
				att.setBackColor(new Color(255, 200, 200));
			} else if (temp_backColor.equals("淡蓝")) {
				att.setBackColor(new Color(200, 200, 255));
			} else if (temp_backColor.equals("淡黄")) {
				att.setBackColor(new Color(255, 255, 200));
			} else if (temp_backColor.equals("淡绿")) {
				att.setBackColor(new Color(200, 255, 200));
			}
		}
		return att;
	
	}
	private FontAttrib getFontAttrib() {
		FontAttrib att = new FontAttrib();
		att.setText(addText.getText());
		att.setName((String) fontName.getSelectedItem());
		att.setSize(Integer.parseInt((String) fontSize.getSelectedItem()));

		// 设置
		String temp_style = (String) fontStyle.getSelectedItem();
		if (temp_style.equals("常规")) {
			att.setStyle(FontAttrib.GENERAL);
		} else if (temp_style.equals("粗体")) {
			att.setStyle(FontAttrib.BOLD);
		} else if (temp_style.equals("斜体")) {
			att.setStyle(FontAttrib.ITALIC);
		} else if (temp_style.equals("粗斜体")) {
			att.setStyle(FontAttrib.BOLD_ITALIC);
		}
		
		// 设置
		String temp_color = (String) fontColor.getSelectedItem();
		if (temp_color.equals("黑色")) {
			att.setColor(new Color(0, 0, 0));
		} else if (temp_color.equals("红色")) {
			att.setColor(new Color(255, 0, 0));
		} else if (temp_color.equals("蓝色")) {
			att.setColor(new Color(0, 0, 255));
		} else if (temp_color.equals("黄色")) {
			att.setColor(new Color(255, 255, 0));
		} else if (temp_color.equals("绿色")) {
			att.setColor(new Color(0, 255, 0));
		}
		
		// 设置
		String temp_backColor = (String) fontBackColor.getSelectedItem();
		if (!temp_backColor.equals("无色")) {
			if (temp_backColor.equals("灰色")) {
				att.setBackColor(new Color(200, 200, 200));
			} else if (temp_backColor.equals("淡红")) {
				att.setBackColor(new Color(255, 200, 200));
			} else if (temp_backColor.equals("淡蓝")) {
				att.setBackColor(new Color(200, 200, 255));
			} else if (temp_backColor.equals("淡黄")) {
				att.setBackColor(new Color(255, 255, 200));
			} else if (temp_backColor.equals("淡绿")) {
				att.setBackColor(new Color(200, 255, 200));
			}
		}
		return att;
	}

	private class FontAttrib {
		public static final int GENERAL = 0; // 常规
		public static final int BOLD = 1; // 粗体
		public static final int ITALIC = 2; // 斜体
		public static final int BOLD_ITALIC = 3; // 粗斜体
		private SimpleAttributeSet attrSet = null; // 属性集
		private String text = null, name = null; // 要输入的文本和字体名称
		private int style = 0, size = 0; // 样式和字号
		private Color color = null, backColor = null; // 文字颜色和背景颜色

		public FontAttrib() {
			
		}

		public SimpleAttributeSet getAttrSet() {
			attrSet = new SimpleAttributeSet();
			if (name != null) {
				StyleConstants.setFontFamily(attrSet, name);
			}
			if (style == FontAttrib.GENERAL) {
				StyleConstants.setBold(attrSet, false);
				StyleConstants.setItalic(attrSet, false);
			} else if (style == FontAttrib.BOLD) {
				StyleConstants.setBold(attrSet, true);
				StyleConstants.setItalic(attrSet, false);
			} else if (style == FontAttrib.ITALIC) {
				StyleConstants.setBold(attrSet, false);
				StyleConstants.setItalic(attrSet, true);
			} else if (style == FontAttrib.BOLD_ITALIC) {
				StyleConstants.setBold(attrSet, true);
				StyleConstants.setItalic(attrSet, true);
			}
			StyleConstants.setFontSize(attrSet, size);
			if (color != null) {
				StyleConstants.setForeground(attrSet, color);
			}
			if (backColor != null) {
				StyleConstants.setBackground(attrSet, backColor);
			}
			return attrSet;
		}

		public void setAttrSet(SimpleAttributeSet attrSet) {
			this.attrSet = attrSet;
		}

		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}

		public Color getColor() {
			return color;
		}

		public void setColor(Color color) {
			this.color = color;
		}

		public Color getBackColor() {
			return backColor;
		}

		public void setBackColor(Color backColor) {
			this.backColor = backColor;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getSize() {
			return size;
		}

		public void setSize(int size) {
			this.size = size;
		}

		public int getStyle() {
			return style;
		}

		public void setStyle(int style) {
			this.style = style;
		}
	}

	public void run() {
		while (true) {
			// 接收数据

			int this_now_object = objectID;
			DatagramPacket pack = null;
			byte b[] = new byte[8192];
			try {
				// 首先接收时间
				pack = new DatagramPacket(b, b.length);
				mail.receive(pack);
				this_now_object = objectID;
				int sender = pack.getPort();
				if (sender == friend_server)
				{
					String result = new String(pack.getData(), 0, pack.getLength());
					if (result .equals("1")) {
						JOptionPane.showMessageDialog(null, "添加好友成功！");
						friend_list[friend_number] = new_friend_id;
						friend_number++;
					} else if (result.equals("2")) {
						JOptionPane.showMessageDialog(null, "查无此人！");
					} else if (result .equals("3")) {
						JOptionPane.showMessageDialog(null, "已经是你的好友！");
					}
					continue;
				}
				
				// this_now_object = objectID;
				String timeString = new String(pack.getData(), 0, pack.getLength());
				String []suffix = timeString.split("!@#");
				// 群聊
				saveAsObj();
				if (suffix.length == 3)
				{
					// 群聊
					
					// 更换群聊的场景
					objectID = 1111;
				}
				else {
					objectID = Integer.parseInt(suffix[1]); // sender
				}
				if (this_now_object != objectID)
					readFromObj();
				insert(get_time_FontAttrib(timeString, 2));

				// 再接收格式、文字信息
				pack = new DatagramPacket(b, b.length);
				mail.receive(pack);
				String formatString = new String(pack.getData(), 0, pack.getLength());
				
				// 
				System.out.println(myID + "-"+formatString);
				
				
				// 解码
				// 新建一个类
				// 对方发过来的 用2
				
				if (formatString .equals( "Image sending!_1_2_3!@#$%^&*()"))
				{
					int length_file;
					// 接包：大小
					mail.receive(pack);
					System.out.println(myID + "-"+formatString);
					String lengthString = new String(pack.getData(), 0, pack.getLength());
					length_file = Integer.parseInt(lengthString);

					// 解包: 名称
					mail.receive(pack);
					System.out.println(myID + "-"+formatString);
					String nameString = new String(pack.getData(), 0, pack.getLength());
					System.out.println(myID + "-"+nameString);
					
					String baseDir = "./src/myQQ/image";
					File baseDir_fold = new File(baseDir);
					File[] files = baseDir_fold.listFiles();
					
					int i;
					for (i = 0; i < files.length; i++)
					{
						if(files[i].getName().contains(nameString))
						{
							// 发回文件名
							break;
						}
					}
					
					insertIcon(files[i], "image");
				}
				
				else if (
							formatString .equals( "File sending!_1_2_3!@#$%^&*()")
						)
				{
					int length_file;
					// 接包：大小
					mail.receive(pack);
					System.out.println(myID + "-"+formatString);
					String lengthString = new String(pack.getData(), 0, pack.getLength());
					length_file = Integer.parseInt(lengthString);

					// 解包: 名称
					mail.receive(pack);
					System.out.println(myID + "-"+formatString);
					String nameString = new String(pack.getData(), 0, pack.getLength());
					System.out.println(myID + "-"+nameString);


					String pathsString = "./src/myQQ/" + myID + "/" + nameString;
					File new_file = new File(pathsString);
					System.out.println(myID + "-"+pathsString);
					OutputStream os = new BufferedOutputStream(new FileOutputStream(pathsString), length_file);
					System.out.println(myID + "-"+formatString);
					int len = 0;
					byte[] buffer = new byte[length_file];
					System.out.println(myID + "-"+formatString);
					//////////////////////////////
					// 加入一个icon
					if (formatString .equals( "File sending!_1_2_3!@#$%^&*()"))
							insertIcon(new_file,"no");
					
					////////////////////////////
					System.out.println(myID + "-"+formatString);
					
					// 接包：文件
					while (true) {
						// 无数据则开始循环接收数据
						// 接收数据包
						mail.receive(pack);
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
					}
					
					// 更换标识
					//////////////////////////////
					System.out.println(myID + "-"+formatString);
					doc = text.getStyledDocument(); // 获得JTextPane的Document;
					if (formatString .equals( "File sending!_1_2_3!@#$%^&*()"))
						insertIcon(new_file,"finished");
					else 
						insertIcon(new_file,"image");
					////////////////////////////

				}
				else {
					suffix = formatString.split("!@#");
					insert(getFontAttrib(suffix));

					pack = null;
				}
			} 
			catch (Exception e) {
				
			}
			if (this_now_object != objectID)
			{
				readFromObj();
				saveAsObj();
				objectID = this_now_object;
				//System.out.println(this_now_object+"this_now_object2");
				readFromObj();
			}
			doc = text.getStyledDocument();
		}
	}

	// 发送好友请求
	public int add_friend_request(int id) {
		DatagramPacket pack = null;
		byte b[] = null;
		try {

			new_friend_id = id;
			

			b = "add_friend_request".getBytes();
			pack = new DatagramPacket(b, b.length, address, friend_server); // 一个给服务器server的包
			// 自身端口 10000 -> 8000
			mail.send(pack);
			
			// 发送对方端口号
			b = (id + "").getBytes();
			pack = new DatagramPacket(b, b.length, address, friend_server); // 一个给服务器server的包

			mail.send(pack);
			
		} catch (Exception e) {
			System.err.print(e);
		}
		return 1;
	}

	public static void main(String args[]) {
//		new Client1("客户端1", 10000);
	}

	private int send_message() {
		

		// 得到att 的字体等数据
		String word = addText.getText();
		if (word .equals(""))
		{
			return 0;
		}

		// 发送了时间数据，并且得到了时间的字符串
		String time_string = send_pre_infomation();
		
		// 发送完了时间数据，自己创建一个
		insert(get_time_FontAttrib(time_string, 1));
		String name = (String) fontName.getSelectedItem();
		String size = (String) fontSize.getSelectedItem();
		String style = (String) fontStyle.getSelectedItem();
		String backColor = (String) fontBackColor.getSelectedItem();
		String color = (String) fontColor.getSelectedItem();
		
		// 单击按钮发送数据
		String message = name + "!@#"
				+ size + "!@#"
				+ style + "!@#"
				+ backColor + "!@#"
				+ color + "!@#"
				+ word;
		byte b[] = message.getBytes();

		// 数据
		data = new DatagramPacket(b, b.length, address, server); // 一个给服务器server的包

		try {
			mail.send(data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return 1;
	}

	private String send_pre_infomation() {
		// 将对方端口和时间一起发给对面
		
		// 获取时间
		SimpleDateFormat sdf = new SimpleDateFormat();
		sdf.applyPattern("MM-dd HH:mm:ss");
		Date date = new Date();
		String time_string = sdf.format(date);
		byte[] time_bytes = time_string.getBytes();
		byte[] friend_port = (objectID + "").getBytes();

		try {
			// 发送对方端口
			data = new DatagramPacket(friend_port, friend_port.length, address, server); // 一个给服务器server的包
			mail.send(data);

			// 时间发给server端口
			data = new DatagramPacket(time_bytes, time_bytes.length, address, server); // 一个给服务器server的包
			mail.send(data);

		} catch (Exception e) {
			System.err.print(e);
		}

		return time_string;
	}

	private File send_image() throws IOException, InterruptedException {
		
		JFileChooser fileChooser = new JFileChooser(new File("./src/myQQ/image"));
		File file = null;
		String fileNameString = null;
		
		fileChooser.setAcceptAllFileFilterUsed(false);
        //限制文件只能显示PNG\JPG格式的图片
        FileNameExtensionFilter filter = new FileNameExtensionFilter("PNG and JPG images", "png", "jpg");
        fileChooser.addChoosableFileFilter(filter);

        int state = fileChooser.showOpenDialog(null);// 显示文件选择框
        if (state == fileChooser.APPROVE_OPTION) {

			// 发送时间包
			String time_string = send_pre_infomation();
			insert(get_time_FontAttrib(time_string, 1));
			
			// 总共发了1 + 1 + n + 1 个包
			// 1. 提示信息 : "File sending!"
			// 2. 文件名称
			// 2. 文件大小
			// 3. (n) 文件本体
			// 4. 文件的传输结束标志

			file = fileChooser.getSelectedFile();
			// System.out.println(file.getPath());

			// 得到文件大小
			long file_length = file.length();

			// 发送之前，先发给对方提示，提示文件的发送
			byte[] tipsBytes = "Image sending!_1_2_3!@#$%^&*()".getBytes();
			DatagramPacket temp_pack = new DatagramPacket(tipsBytes, tipsBytes.length, address, server);
			mail.send(temp_pack);

			// 发送文件大小
			tipsBytes = ("" + file_length).getBytes();
			temp_pack = new DatagramPacket(tipsBytes, tipsBytes.length, address, server);
			mail.send(temp_pack);

			// 发送文件名
			fileNameString = file.getName();
			byte[] fileNameBytes = fileNameString.getBytes();
			DatagramPacket fileNamePack = new DatagramPacket(fileNameBytes, fileNameBytes.length, address, server);
			mail.send(fileNamePack);

			// 发送文件
			/*
			InputStream is = new BufferedInputStream(new FileInputStream(file.getPath()));

			byte[] data = new byte[1024];

			int len = 0;
			// 创建UDP数据报
			// 进行传送
			while ((len = is.read(data)) != -1) {
				temp_pack = new DatagramPacket(data, len, address, server);
				mail.send(temp_pack);
				TimeUnit.MICROSECONDS.sleep(1); // 限制传输速度 防止丢包
			}

			// 发送完毕标识符
			tipsBytes = "!@#$%^&*()".getBytes();
			temp_pack = new DatagramPacket(tipsBytes, tipsBytes.length, address, server);
			mail.send(temp_pack);
			*/

			System.out.println("发送文件完毕");

			// TODO: 在聊天窗口打印出来
			//inMessage_textArea.append("你：" + time_string + "\n" + "发送了一份文件：" + "\n" + file.getPath() + "\n");

			return file;
		}
		else {
			return null;
		}
		
	}
	
	
	private File send_file() throws IOException, InterruptedException {
		
		
		JFileChooser fileChooser = new JFileChooser(new File("."));
		String fileNameString = null;
		int state = fileChooser.showOpenDialog(null);// 显示文件选择框
		File file = null;
		if (state == fileChooser.APPROVE_OPTION) {

			// 发送时间包
			String time_string = send_pre_infomation();
			insert(get_time_FontAttrib(time_string, 1));
			
			// 总共发了1 + 1 + n + 1 个包
			// 1. 提示信息 : "File sending!"
			// 2. 文件名称
			// 2. 文件大小
			// 3. (n) 文件本体
			// 4. 文件的传输结束标志

			file = fileChooser.getSelectedFile();
			// System.out.println(file.getPath());

			// 得到文件大小
			long file_length = file.length();

			// 发送之前，先发给对方提示，提示文件的发送
			byte[] tipsBytes = "File sending!_1_2_3!@#$%^&*()".getBytes();
			DatagramPacket temp_pack = new DatagramPacket(tipsBytes, tipsBytes.length, address, server);
			mail.send(temp_pack);

			// 发送文件大小
			tipsBytes = ("" + file_length).getBytes();
			temp_pack = new DatagramPacket(tipsBytes, tipsBytes.length, address, server);
			mail.send(temp_pack);

			// 发送文件名
			fileNameString = file.getName();
			byte[] fileNameBytes = fileNameString.getBytes();
			DatagramPacket fileNamePack = new DatagramPacket(fileNameBytes, fileNameBytes.length, address, server);
			mail.send(fileNamePack);

			// 发送文件
			InputStream is = new BufferedInputStream(new FileInputStream(file.getPath()));

			byte[] data = new byte[1024];

			int len = 0;
			// 创建UDP数据报
			// 进行传送
			while ((len = is.read(data)) != -1) {
				temp_pack = new DatagramPacket(data, len, address, server);
				mail.send(temp_pack);
				TimeUnit.MICROSECONDS.sleep(1); // 限制传输速度 防止丢包
			}

			// 发送完毕标识符
			tipsBytes = "!@#$%^&*()".getBytes();
			temp_pack = new DatagramPacket(tipsBytes, tipsBytes.length, address, server);
			mail.send(temp_pack);

			System.out.println("发送文件完毕");

			// TODO: 在聊天窗口打印出来
			//inMessage_textArea.append("你：" + time_string + "\n" + "发送了一份文件：" + "\n" + file.getPath() + "\n");

			return file;
		}
		else {
			return null;
		}
	}	

	private void add_friend() {
		add_friendItem.addActionListener(new ActionListener() {
			JFrame jFrame = new JFrame("搜索好友");// 定义一个窗体
			JTextField textF = new JTextField("", 10);

			public void actionPerformed(ActionEvent e) {
				String buttonname = e.getActionCommand();

				if (buttonname.equals("添加好友")) {

					textF.setBackground(Color.pink);
					JButton jb = new JButton("确认");
					JPanel panel = new JPanel();
					panel.add(jb);
					panel.add(textF);
					jb.addActionListener(this);
					jFrame.setLocationRelativeTo(null);
					jFrame.setSize(330, 80);// 设置窗体大小
					jFrame.setContentPane(panel);
					jFrame.setVisible(true);
				} else {
					String friend_id_new = textF.getText();
					add_friend_request(Integer.parseInt(friend_id_new));
				}
			}
		});
	}


    
    public void saveAsObj() {
    	 
		try {
			File writeF = new File("./src/myQQ/record/"+myID+"/"+objectID);
			if (!writeF.exists()) {
				writeF.createNewFile();
			}
			StyledDocument doc = (StyledDocument) text.getDocument();
			FileOutputStream fos = new FileOutputStream("./src/myQQ/record/"+myID+"/"+objectID);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(doc);
			oos.flush();
			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void readFromObj() {
		try {
			File writeF = new File("./src/myQQ/record/"+myID+"/"+objectID);
			if (!writeF.exists()) {
				writeF.createNewFile();
			}
			FileInputStream fis = new FileInputStream("./src/myQQ/record/"+myID+"/"+objectID);
			ObjectInputStream ois = new ObjectInputStream(fis);
			StyledDocument doc_1 = (StyledDocument) ois.readObject();
			ois.close();
			text.setStyledDocument(doc_1);
			// doc = doc_1;
			// TODO : BUG
			validate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	
	}
    
}
