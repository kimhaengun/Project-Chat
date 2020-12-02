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
	private Vector<ClientInfo> vc; //연결된 클라이언트 클래스(소켓)을 담는 컬렉션
	
	public ChatServer() {
		try {
			vc = new Vector<>();
			serverSocket = new ServerSocket(10000);
			System.out.println(TAG+"클라이언트 연결 대기 중~");
			//메인 스레드의 역할.
			while(true) {
				Socket socket = serverSocket.accept();
				System.out.println(TAG+"클라이언트 요청 받음");
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
		PrintWriter writer; // BufferedWriter와 다른 점은 내려쓰기 함수를 지원해준다.
		public ClientInfo(Socket socket) { //콤포지션으로 소켓을 받음
			this.socket=socket;
			try {
				reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				writer = new PrintWriter(socket.getOutputStream(), true);
			} catch (Exception e) {
				System.out.println("서버 연결 실패"+e.getMessage());
			}
		}
		
		//역할 : 클라이언트로 부터 받은 메시지를 모든 클라이언트에게 재선송 해준다.
		@Override
		public void run() {
			//메시지를 읽어서 써줌.
			try {
				String input = null;
				while((input = reader.readLine())!=null) {
					String gubun[] = input.split(":");
					System.out.println(TAG+"input을 읽음2 : "+gubun[1]);
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
