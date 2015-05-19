/*各チャネル用のサーバプログラム*/
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class Channel implements Runnable {
	ChatServerSystem server; // チャットサーバ本体
	Socket socket = null; // ソケット
	BufferedReader input; // 入力用ストリーム
	OutputStreamWriter output; // 出力用ストリーム
	public Thread thread; // チャネルを駆動するためのスレッド
	String handle; // クライアントのハンドル

	// 引数はチャネル番号、ソケット、Server.
	Channel(Socket s, ChatServerSystem cs) {
		server = cs;
		socket = s;
		start();
	}

	public void start() {
		thread = new Thread(this);
		thread.start();
	}

	public void stop() {
		thread = null;
	}

	// クライアントへ文字列を出力する
	synchronized void write(String s) {
		try {
			output.write(s + "\r\n");
			output.flush();
		} catch (IOException e) {
			System.out.println("Write Err");
			close(); // エラーを起こしたら、接続を切断する
		}
	}

	/*
	 *  チャネルのメインルーチン。
	 *  クライアントからの入力を受け付ける
	 */
	public void run() {
		String s;
		try {
			// ソケットから入出力ストリームを得る
			input = new BufferedReader(
					new InputStreamReader(socket.getInputStream()));
			output = new OutputStreamWriter(socket.getOutputStream());
			write("# ようこそ！ Chatサーバーへ．"); // 歓迎の挨拶
			write("# 御名前を入力して下さい．"); // ハンドル名登録
			handle = input.readLine();
			write("# 登録致しました，" + handle + "様．");

			while (thread != null) { // 入力待ちのループ
				s = input.readLine(); // 文字列入力を待つ
				if (s == null)
					close();
				else {

					char c5 = (char) 05;
					char c6 = (char) 06;

					if(s.startsWith(String.valueOf(c5)))
					{
						String[] commandMes = s.split(String.valueOf(c5),-1);
						//String
						for(int i=0;i<commandMes.length;i++){
							System.out.println(i +":"+commandMes[i]);
						}
						System.out.println("_______________________");

						String[] processMes = commandMes[1].split(String.valueOf(c6),-1);

						for(int i=0;i<processMes.length;i++){
							System.out.println("c6:"+i +":"+processMes[i]);
						}
						System.out.println("_______________________");

						processMeth(processMes);


					}else{

						// 全クライアントにブロードキャストする
						server.broadcast(handle + " : " + s);
					}
				}
			}
		} catch (IOException e) {
			close(); // エラーを起こしたら、接続を切断する
		}
	}

	// 接続を切断する
	public void close() {
		try {
			input.close(); // ストリームを閉じる
			output.close();
			socket.close(); // ソケットを閉じる
			socket = null;
			server.broadcast("# 回線切断 :" + handle);
			stop();
		} catch (IOException e) {
			System.out.println("Close Err");
		}
	}


	private void processMeth(String[] cMes){


		if(cMes[0].equals("sendPerson")){//特定の人に贈る機能
			String person = cMes[1];
			String private_message = cMes[2];
			this.server.sendPerson(private_message, person);

		}


	}
}
