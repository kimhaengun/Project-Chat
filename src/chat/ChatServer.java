package chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class ChatServer {
	private static final String TAG="ChatServer : ";
	private ServerSocket serverSocket;
	private Vector<ClientInfo> vc; //����� Ŭ���̾�Ʈ Ŭ����(����)�� ��� �÷���
	
	public ChatServer() {
		try {
			vc = new Vector<>();
			serverSocket = new ServerSocket(10000);
			System.out.println(TAG+"Ŭ���̾�Ʈ ���� ��� ��~");
			//���� �������� ����.
			while(true) {
				Socket socket = serverSocket.accept();
				System.out.println(TAG+"Ŭ���̾�Ʈ ��û ����");
				ClientInfo clinetInfo = new ClientInfo(socket);
				clinetInfo.start();
				vc.add(clinetInfo);	
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	class ClientInfo extends Thread{
		Socket socket;
		BufferedReader reader;
		PrintWriter writer; // BufferedWriter�� �ٸ� ���� �������� �Լ��� �������ش�.
		public ClientInfo(Socket socket) { //������������ ������ ����
			this.socket=socket;
			try {
				reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				writer = new PrintWriter(socket.getOutputStream(), true);
			} catch (Exception e) {
				System.out.println("���� ���� ����"+e.getMessage());
			}
		}
		
		//���� : Ŭ���̾�Ʈ�� ���� ���� �޽����� ��� Ŭ���̾�Ʈ���� �缱�� ���ش�.
		@Override
		public void run() {
			//�޽����� �о ����.
			try {
				String input = null;
				while((input = reader.readLine())!=null) {
					String gubun[] = input.split(":");
					System.out.println(TAG+"input�� ����2 : "+gubun[1]);
					for(ClientInfo clientInfo : vc) {
						clientInfo.writer.println(input);
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			
		}
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new ChatServer();
	}//end of main

}
