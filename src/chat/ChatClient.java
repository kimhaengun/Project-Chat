package chat;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

	public class ChatClient extends JFrame {
		private ChatClient chatClient = this;
		private final static String TAG ="ChatClient : ";
		private JButton btnConnect, btnSend;
		private JTextField tfHost, tfChat;
		private JTextArea taChatList;
		private ScrollPane scrollPane;
		private JPanel topPanel, bottomPanel;
		private static final int PORT = 10000;
		
		private Socket socket;
		private PrintWriter writer;
		private BufferedReader reader;
	public ChatClient() {
		init();
		setting();
		batch();
		listener();
		setVisible(true);
		
	}
	private void init() {
		btnConnect = new JButton("connect");
		btnSend = new JButton("send");
		tfHost = new JTextField("127.0.0.1",20);
		tfChat = new JTextField(20);
		taChatList = new JTextArea(10,30); //row, column
		scrollPane = new ScrollPane();
		topPanel = new JPanel();
		bottomPanel = new JPanel();
	}
	private void setting() {
		setTitle("채팅 다대다 클라이언트");
		setSize(350, 500);
		setLocationRelativeTo(null);
		taChatList.setBackground(Color.orange);
		taChatList.setForeground(Color.BLUE);
		
	}
	private void batch() {
		topPanel.add(tfHost);
		topPanel.add(btnConnect);
		bottomPanel.add(tfChat);
		bottomPanel.add(btnSend);
		scrollPane.add(taChatList);
		
		add(topPanel,BorderLayout.NORTH);
		add(scrollPane,BorderLayout.CENTER);
		add(bottomPanel,BorderLayout.SOUTH);
	}
	
	
	private void listener() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				// TODO Auto-generated method stub
				JFrame frame =(JFrame)e.getWindow();
				frame.dispose();
				File f = new File("D:\\Area.txt");
				try {
					String AreaText = taChatList.getText();
					FileWriter fw = new FileWriter(f);
					fw.write(AreaText);
					fw.close();
				} catch (Exception e2) {
					System.out.println(TAG+"파일 저장 실패....");
				}
				System.out.println("파일 저장 종료");
			}
		});
		btnConnect.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				connect();
				btnConnect.setEnabled(false);
			}
		});
		
		
		btnSend.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				send();
			}
		});
	}
	
	
	private void connect() {
		String host = tfHost.getText();
		try {
			socket = new Socket(host,PORT);
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			writer = new PrintWriter(socket.getOutputStream(), true);
			ReaderThread rt = new ReaderThread();
			rt.start();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			System.out.println(TAG+"서버 연결 에러"+e1.getMessage());
		}
	}
	
	private void send() {
		String chat = tfChat.getText();
		//1번. taChatList에 뿌리고
		taChatList.append("[나의 메세지] "+chat+"\n");
		//2번. 서버로 전송
		try {
			writer = new PrintWriter(socket.getOutputStream(), true);
			writer.println(Protocol.ALL+":"+chat);
			System.out.println(TAG+"서버로 전송 되었음"+chat);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//3번 텍스트 필드 챗 비우기
		tfChat.setText("");
	}
	
	class ReaderThread extends Thread{
		@Override
		public void run() {
			//리더를 만든다 while을 돌면서 서버로 부터 메시지를 받아서 taChatlist에 뿌리기
			try {
				reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				writer = new PrintWriter(socket.getOutputStream(), true);
				String input = null;
				while((input=reader.readLine())!=null) {
					taChatList.append(input+"\n");
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new ChatClient();
	}

}
