import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class Client {
	Socket socket = null; // 接続用Socket
	BufferedReader input = null; // 入力用ストリーム
	OutputStreamWriter output = null; // 出力用ストリーム
	Thread thread = null;

	public Client(String host, int port) {
		try {
			socket = new Socket(host, port);
			// ソケットから入出力ストリームを得る
			input = new BufferedReader(
					new InputStreamReader(socket.getInputStream()));
			output = new OutputStreamWriter(socket.getOutputStream());
		} catch (IOException e) {
			socket = null;
			System.out.println("Client Err!");
		}
	}

	// メッセージ送信
	public boolean write(String Message) {
		try {
			output.write(Message + "\r\n");
			output.flush();
		} catch (Exception e) {
			System.out.println("Miss Send");
			return false;
		}
		return true;
	}

	// メッセージ受信
	public String read() {
		String readString = null;
		try {
			readString = input.readLine();
		} catch (Exception e) {
			System.out.println("Can't read message !!!");
		}
		return readString;
	}

	// 接続を切断する
	public void close() {
		try {
			System.out.println("Client#Close()");
			socket.close(); // ソケットを閉じる
			socket = null;
		} catch (IOException e) {
			e.printStackTrace(System.err);
		}
	}
}
