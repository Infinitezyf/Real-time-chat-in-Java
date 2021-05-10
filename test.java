package myQQ;

public class test {

	
	// TODO:
	// 好友申请
	public static void main(String args[])
    {
		// 好友数目：
		int user_number = 3;
		for (int i = 0; i < user_number; i++)
		{
			new Client1("客户端"+(i+10000)+"",i+10000);
		}
		new Server(user_number);
		new friend_server(user_number);
    }
	
}