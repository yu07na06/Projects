package Pack;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
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

class ListeningThread extends Thread { // 서버에서 보낸 메세지 읽는 Thread
	Socket cs;
	TextArea talkBox;
	TextArea currentMem;

	public ListeningThread(Socket cs) {
		this.cs = cs;
		this.talkBox = new Client().StalkBox;
		this.currentMem = new Client().ScurrentMem;
	}
	
	public void run() {
		try {
			InputStream inputstream = cs.getInputStream();
			while(true) {
				byte[] data = new byte[512];
				System.out.println("지금 작동 중임");
				int size = inputstream.read(data);
				String s = new String(data, 0, size);
				System.out.println(s+"데이터 받음");
				char check = s.charAt(0);
				if(check == 'a') {
					currentMem.clear();
					currentMem.appendText(s.substring(1) + "\n");
				} else {
					talkBox.appendText(s + "\n");
				}
			}
		} catch (Exception e) {
			System.out.println("종료");
			e.printStackTrace();
		} 
	}
}

public class Client extends Application  {
	 
	Socket cs = new Socket();
    String Cname;
    static TextArea StalkBox;
    static TextArea ScurrentMem;
 
    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setTitle("Client");
        //---------------------------------------------------------------------------------
 
        HBox topMenu = new HBox();
        Label labelName = new Label("이름 : "); // 라벨을 만들기 위해
		TextField name = new TextField();
		Button connectBnt = new Button("접속");
		topMenu.getChildren().addAll(labelName, name, connectBnt);
        
        HBox centerMenu = new HBox();
		TextArea talkBox = new TextArea();
		talkBox.setMaxSize(600, 500);
		TextArea currentMem = new TextArea();
		currentMem.setMaxSize(300, 500);
		centerMenu.getChildren().addAll(talkBox, currentMem);
		
		HBox bottomMenu = new HBox();
		TextField inputText = new TextField();
		inputText.setPrefSize(600, 100);
		Button sendBnt = new Button("보내기");
		sendBnt.setPrefSize(100, 100);
		Button exitBnt = new Button("나가기");
		exitBnt.setPrefSize(100, 100);
		bottomMenu.getChildren().addAll(inputText, sendBnt, exitBnt);
		
		// -----------------------------------------------------------------------------------
		StalkBox = talkBox;
		ScurrentMem = currentMem;
		
		// 서버 접속
		connectBnt.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				try {
//					cs.connect(new InetSocketAddress("localhost",5001));
					cs.connect(new InetSocketAddress("112.162.204.204",5001));
					OutputStream outputStream = cs.getOutputStream();
					Cname = name.getText();
					byte[] data = Cname.getBytes();
					outputStream.write(data);
					System.out.println(Cname + "접속완료");
					new ListeningThread(cs).start();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		// 텍스트 전송 버튼
		sendBnt.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				try {
					OutputStream outputStream = cs.getOutputStream();
					String s = "["+Cname + "] : " + inputText.getText();
					byte[] data = s.getBytes();
					outputStream.write(data);
					System.out.println(inputText.getText() + "전송완료");
					inputText.setText("");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		// 나가기 버튼
		exitBnt.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				try {
					OutputStream outputStream = cs.getOutputStream();
					String s = "b"+Cname;
					byte[] data = s.getBytes();
					outputStream.write(data);
					outputStream.close();
					cs.close();
					primaryStage.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

        BorderPane borderPane = new BorderPane();
        borderPane.setTop(topMenu);
        borderPane.setCenter(centerMenu);
        borderPane.setBottom(bottomMenu);
        
        //---------------------------------------------------------------------------------
        Scene scene = new Scene(borderPane, 850, 650);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

	public static void main(String[] args) {
        launch(args);
    }
}