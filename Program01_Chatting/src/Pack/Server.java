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
	
	// Ŭ���̾�Ʈ ���� ��, ����� Ŭ���̾�Ʈ���� ���� �ؽ�Ʈ ���� ������Ʈ�ϱ�
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
				System.out.println("������ ����");
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}
}

class IOThread extends Thread{ // �д� ó���� �ϴ� ������
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
	
	public void run() { // �����͸� �д� �۾�
		try {
			InputStream inputstream = socket.getInputStream();
			
			while(true) {
				byte[] data = new byte[512];
				int size = inputstream.read(data);
				String s = new String(data, 0, size);
				
				// Ŭ���̾�Ʈ�� �������� Ȯ��
				char check = s.charAt(0); // ù ���ڰ� b���� üũ�ϱ� ����
				synchronized(hashmap) {
					if (check == 'b') { // ù���ڰ� b���,
						exitCheck(s); // ���� Ŭ���̾�Ʈ�� hashmap ���� ���� �� ����� Ŭ�󸮾�Ʈ���� �˸���
					}else { // Ŭ���̾�Ʈ�� ������ �ʾҴٸ�,
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
					StalkBox.appendText("----"+temp+"�� ����----\n");
					new Broadcast().send("----"+temp+"�� ����----\n");
				} catch (Exception e) {
					// TODO: handle exception
				}
				break;
			}
		}
		String serverName = new Server().Sname;
		ScurrentMem.setText(""); // ����â �����
		ScurrentMem.setText(serverName+"���� ������\n"); // ���� ���� �� �ۼ�
		
		key=null;
		value=null;
		itkey = hashmap.keySet().iterator();
		while(itkey.hasNext()) {
			key=itkey.next();
			value = hashmap.get(key);
			try {
				ScurrentMem.setText(value+"���� ������\n");
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		
		synchronized (hashmap) { new Broadcast().loginCehckSend(); }
	}
}

class ConnectThread extends Thread{ // ���Ӹ� �ϴ� ������
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
			System.out.println("���� ���� ���� ����");
			
//			mss.bind(new InetSocketAddress("localhost", 5001));  //���ε�
			mss.bind(new InetSocketAddress(InetAddress.getLocalHost(), 5001));  //���ε�
			System.out.println("���ε� �Ϸ�\n");
			
			while(true) {
				Socket ss = mss.accept();
				InputStream inputstream = ss.getInputStream();
				byte[] data = new byte[512];
				int size = inputstream.read(data);
				String name = new String(data, 0, size);

				synchronized(hashmap) {
					hashmap.put(ss, name);
					System.out.println("���� "+hashmap.size()+"�� �����Ͽ����ϴ�.");
					new IOThread(ss).start();
				}
				
				// ���°� ��ε�ĳ���� �� ���
				
				System.out.println(name+"�� �����̽��ϴ�");
				synchronized(ScurrentMem) { ScurrentMem.appendText(name+"�� ���� ��\n"); }
				synchronized(StalkBox) { 
					StalkBox.appendText("----"+name+"�� ����----\n"); 
					new Broadcast().send("----"+name+"�� ����----\n");
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
        Label labelName = new Label("�̸� : ");
		TextField name = new TextField();
		Button connectBnt = new Button("����");
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
		Button sendBnt = new Button("������");
		sendBnt.setPrefSize(100, 100);
		bottomMenu.setSpacing(10);
		Button exitBnt = new Button("������");
		exitBnt.setPrefSize(100, 100);
		bottomMenu.getChildren().addAll(inputText, sendBnt, exitBnt);
        
        BorderPane borderPane = new BorderPane();
        borderPane.setTop(topMenu);
        borderPane.setCenter(centerMenu);
        borderPane.setBottom(bottomMenu);
        
        //-----------------------------------------------------------------------------------
        StalkBox = talkBox;
        ScurrentMem = currentMem;
        
        connectBnt.setOnAction(new EventHandler<ActionEvent>() { //��ư Ŭ�� �������� �̺�Ʈ
			@Override
			public void handle(ActionEvent arg0) {
				Sname = name.getText(); // �ۼ��� �ؽ�Ʈ�� ������ �� �ִ� �Լ�
				ScurrentMem.appendText(Sname+"�� ���� ��\n"); // ���� ������ + ���ο� �����͸� �߰��ϴ� ������ �Լ�
				hashmap=new HashMap<Socket, String>();
				
				new ConnectThread().start();
			}
		});
        
        sendBnt.setOnAction(new EventHandler<ActionEvent>() { //��ư Ŭ�� �������� �̺�Ʈ
			@Override
			public void handle(ActionEvent arg0) {
				String s = inputText.getText(); // �Է�â�� �Է��� �׽�Ʈ ��������
				talkBox.appendText("["+Sname+"] : "+s+"\n"); // ä�� �ڽ��� �Է��� ���� ����
				inputText.setText(" "); // ������ �Է�â ����
				synchronized (hashmap) { new Broadcast().serverSend(s); } // ������ �Է�â�� �ۼ��� ������ Ŭ���̾�Ʈ���� �Ѹ�
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