import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatServerSystem implements Runnable {
	final int MAX_CHANNELS = 256; // 最大チャネル数
	Channel channel[] = new Channel[MAX_CHANNELS];
	ServerSocket serversocket; // 接続受け付け用ServerSocket
	int port; // ポート番号
	Thread thread;
	int num = 0;

	public static void main(String args[]) {
		if (args.length < 1) {
			System.out.println("デフォルトのポート28000で起動します");
			System.out.println("%java Server 28000");
			new ChatServerSystem(28000);
		} else {
			int p = Integer.parseInt(args[0]);
			new ChatServerSystem(p);
		}
	}

	public ChatServerSystem(int port) {
		this.port = port;
		this.start();
	}

	// メインサーバーの切断を行なう
	public void serverClose() {
		try {
			System.out.println("Server#Close()");
			serversocket.close();
			serversocket = null;
		} catch (IOException e) {
			e.printStackTrace(System.err);
		}
	}

	// 全チャネルの切断を行なう
	public void clientClose() {
		System.out.println("Server#Close " + channel.length);
		for (int i = 0; i < channel.length; i++) {
			System.out.println("Server#Close(" + i + ")");
			channel[i].close();
			channel[i] = null;
		}
	}

	public void quit() {
		clientClose();
		serverClose();
		System.exit(1);
	}

	public void start() {
		if (thread == null) {
			thread = new Thread(this);
			thread.start();
		}
	}

	public void stop() {
		if (thread != null) {
			thread = null;
		}
	}

	public void run() {
		int i;
		try {
			serversocket = new ServerSocket(port); // メインサーバー開放
			while (true) { // 空いているチャネルを探す
				for (i = 0; i < MAX_CHANNELS; i++) {
					if (channel[i] == null || channel[i].thread == null) {
						break;
					}
				}
				if (i == MAX_CHANNELS) // 最大のクライアント数なら終了
					return;
				Socket socket = serversocket.accept(); // 接続待ち
				channel[i] = new Channel(socket, this);// 新チャネル作成
			}
		} catch (IOException e) {
			System.out.println("Server Err!");
			return;
		}
	}

	// 全チャネルへブロードキャスト
	synchronized void broadcast(String message) {
		num++;
		System.out.println(num + ": " + message);
		for (int i = 0; i < MAX_CHANNELS; i++) {
			if (channel[i] != null && channel[i].socket != null) {
				channel[i].write(message);
			}
		}
	}

	//個人へのメッセージを送信するメソッド
	synchronized void sendPerson(String message, String h) {
		num++;
		System.out.println(num + ": " + message);
		for (int i = 0; i < MAX_CHANNELS; i++) {
			if (channel[i] != null && channel[i].socket != null) {

				if(channel[i].handle.equals(h)){
					channel[i].write("private:"+message);
				}

				//channel[i].write(message);
			}
		}
	}

}
