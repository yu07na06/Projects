package Pack;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

class Broadcast{
	HashMap<Socket, String> hashmap = null;
	Iterator<Socket> itkey = null;
	TextArea ScurrentMem = null;
	Socket key = null;
	String FLAG = "a";
	
	public Broadcast() {
		this.hashmap = new Server().hashmap;
		this.itkey = hashmap.keySet().iterator();
		this.ScurrentMem = new Server().ScurrentMem;
	}
	
	// 클라이언트 접속 시, 연결된 클라이언트에게 접속 텍스트 영역 업데이트하기
	void loginCehckSend() { 
		String text = null;
		synchronized (ScurrentMem) { text=ScurrentMem.getText(); }
		while(itkey.hasNext()) {
			key=itkey.next();
			try {
				OutputStream outputstream = key.getOutputStream();
				byte[] data = (FLAG+text).getBytes();
				outputstream.write(data);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}
	
	void send(String s) {
		while(itkey.hasNext()) {
			key=itkey.next();
			try {
				OutputStream outputstream = key.getOutputStream();
				byte[] data = s.getBytes();
				outputstream.write(data);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}
	
	void serverSend(String s) {
		String nameTemp = new Server().Sname;
		while(itkey.hasNext()) {
			key=itkey.next();
			try {
				OutputStream outputstream = key.getOutputStream();
				byte[] data = ("["+nameTemp+"] : "+s).getBytes();
				outputstream.write(data);
				System.out.println("데이터 보냄");
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}
}

class IOThread extends Thread{ // 읽는 처리를 하는 스레드
	HashMap<Socket, String> hashmap;
	Socket socket;
	TextArea ScurrentMem;
	TextArea StalkBox;
	
	public IOThread(Socket socket) {
		this.socket=socket;
		this.hashmap=new Server().hashmap;
		this.ScurrentMem = new Server().ScurrentMem;
		this.StalkBox = new Server().StalkBox;
	}
	
	public void run() { // 데이터를 읽는 작업
		try {
			InputStream inputstream = socket.getInputStream();
			
			while(true) {
				byte[] data = new byte[512];
				int size = inputstream.read(data);
				String s = new String(data, 0, size);
				
				// 클라이언트가 나갔는지 확인
				char check = s.charAt(0); // 첫 문자가 b인지 체크하기 위함
				synchronized(hashmap) {
					if (check == 'b') { // 첫문자가 b라면,
						exitCheck(s); // 나간 클라이언트의 hashmap 정보 삭제 및 연결된 클라리언트에게 알리기
					}else { // 클라이언트가 나가지 않았다면,
						synchronized(StalkBox) { StalkBox.appendText(s + "\n"); }
						new Broadcast().send(s);
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	void exitCheck(String s) {
		String temp = s.substring(1);
		Iterator<Socket> itkey = null;
		Socket key=null;
		String value=null;
		
		itkey = hashmap.keySet().iterator();
		while(itkey.hasNext()) {
			key = itkey.next();
			value=hashmap.get(key);
			if (value.equals(temp)) {
				hashmap.remove(key);
				try {
					StalkBox.appendText("----"+temp+"님 퇴장----\n");
					new Broadcast().send("----"+temp+"님 퇴장----\n");
				} catch (Exception e) {
					// TODO: handle exception
				}
				break;
			}
		}
		String serverName = new Server().Sname;
		ScurrentMem.setText(""); // 접속창 지우기
		ScurrentMem.setText(serverName+"님이 접속중\n"); // 서버 접속 중 작성
		
		key=null;
		value=null;
		itkey = hashmap.keySet().iterator();
		while(itkey.hasNext()) {
			key=itkey.next();
			value = hashmap.get(key);
			try {
				ScurrentMem.setText(value+"님이 접속중\n");
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		
		synchronized (hashmap) { new Broadcast().loginCehckSend(); }
	}
}

class ConnectThread extends Thread{ // 접속만 하는 스레드
	HashMap<Socket, String> hashmap;
	TextArea ScurrentMem;
	TextArea StalkBox;
	static ServerSocket mss;
	
	public ConnectThread() {
		this.hashmap = new Server().hashmap;
		this.ScurrentMem = new Server().ScurrentMem;
		this.StalkBox = new Server().StalkBox;
	}
	
	public void run(){
		try {
			mss = new ServerSocket();		
			System.out.println("메인 서버 소켓 생성");
			
//			mss.bind(new InetSocketAddress("localhost", 5001));  //바인딩
			mss.bind(new InetSocketAddress(InetAddress.getLocalHost(), 5001));  //바인딩
			System.out.println("바인딩 완료\n");
			
			while(true) {
				Socket ss = mss.accept();
				InputStream inputstream = ss.getInputStream();
				byte[] data = new byte[512];
				int size = inputstream.read(data);
				String name = new String(data, 0, size);

				synchronized(hashmap) {
					hashmap.put(ss, name);
					System.out.println("현재 "+hashmap.size()+"명 접속하였습니다.");
					new IOThread(ss).start();
				}
				
				// 들어온거 브로드캐스팅 및 출력
				
				System.out.println(name+"님 들어오셨습니다");
				synchronized(ScurrentMem) { ScurrentMem.appendText(name+"님 접속 중\n"); }
				synchronized(StalkBox) { 
					StalkBox.appendText("----"+name+"님 입장----\n"); 
					new Broadcast().send("----"+name+"님 입장----\n");
				}
				synchronized (hashmap) { new Broadcast().loginCehckSend(); }
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}

public class Server extends Application  {
    static String Sname;
    static TextArea StalkBox;
    static TextArea ScurrentMem;
    static HashMap<Socket, String> hashmap;
    
    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setTitle("Server");
        //------------------------------------------------------------------------------------
        
        HBox topMenu = new HBox();
        Label labelName = new Label("이름 : ");
		TextField name = new TextField();
		Button connectBnt = new Button("접속");
		topMenu.setSpacing(15);
		Label labelIP = new Label("IP"+InetAddress.getLocalHost());
		topMenu.getChildren().addAll(labelName, name, connectBnt, labelIP);
        
        HBox centerMenu = new HBox();
		TextArea talkBox = new TextArea();
		talkBox.setMaxSize(600, 500);
		centerMenu.setSpacing(10);
		TextArea currentMem = new TextArea();
		currentMem.setMaxSize(210, 500);
		centerMenu.getChildren().addAll(talkBox, currentMem);
		
		HBox bottomMenu = new HBox();
		TextField inputText = new TextField();
		inputText.setPrefSize(535, 100);
		bottomMenu.setSpacing(100);
		Button sendBnt = new Button("보내기");
		sendBnt.setPrefSize(100, 100);
		bottomMenu.setSpacing(10);
		Button exitBnt = new Button("나가기");
		exitBnt.setPrefSize(100, 100);
		bottomMenu.getChildren().addAll(inputText, sendBnt, exitBnt);
        
        BorderPane borderPane = new BorderPane();
        borderPane.setTop(topMenu);
        borderPane.setCenter(centerMenu);
        borderPane.setBottom(bottomMenu);
        
        //-----------------------------------------------------------------------------------
        StalkBox = talkBox;
        ScurrentMem = currentMem;
        
        connectBnt.setOnAction(new EventHandler<ActionEvent>() { //버튼 클릭 했을때의 이벤트
			@Override
			public void handle(ActionEvent arg0) {
				Sname = name.getText(); // 작성한 텍스트를 가져올 수 있는 함수
				ScurrentMem.appendText(Sname+"님 접속 중\n"); // 기존 데이터 + 새로운 데이터를 추가하는 개념의 함수
				hashmap=new HashMap<Socket, String>();
				
				new ConnectThread().start();
			}
		});
        
        sendBnt.setOnAction(new EventHandler<ActionEvent>() { //버튼 클릭 했을때의 이벤트
			@Override
			public void handle(ActionEvent arg0) {
				String s = inputText.getText(); // 입력창에 입력한 테스트 가져오기
				talkBox.appendText("["+Sname+"] : "+s+"\n"); // 채팅 박스에 입력한 내용 저장
				inputText.setText(" "); // 서버의 입력창 리셋
				synchronized (hashmap) { new Broadcast().serverSend(s); } // 서버가 입력창에 작성한 내용을 클라이언트에게 뿌림
			}
		});
        
        exitBnt.setOnAction(new EventHandler<ActionEvent>() {
        	@Override
        	public void handle(ActionEvent arg0) {
        		try {
					new ConnectThread().mss.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        		primaryStage.close();
        	}
		});
        
        //---------------------------------------------------------------------------------
        Scene scene = new Scene(borderPane, 770, 650);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    public static void main(String[] args) {
        launch();
    }
}