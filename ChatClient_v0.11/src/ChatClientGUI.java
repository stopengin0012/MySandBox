/* チャットクライアント用GUIプログラム */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ChatClientGUI extends JPanel implements Runnable, ActionListener {
	JTextField inputArea; // 入力用テキストフィールド
	JTextArea freeArea; // 出力用テキストエリア
	Client client = null; // Client クラス
	String host = "localhost";
	int port = 28000;
	JTextField hostField, portField;
	Thread thread = null;
	JButton connectBut, closeBut, quitBut;
	JButton privateBut;
	JTextField privateArea;


	public static void main(String args[]) {
		JFrame f = new JFrame("ChatApplet");
		ChatClientGUI chatapplet = new ChatClientGUI();
		chatapplet.setBackground(Color.white);
		chatapplet.setPreferredSize(new Dimension(400,300));
		f.add("Center", chatapplet);
		f.setTitle("SamplePanel");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setResizable(true);
        f.pack();
        f.setVisible(true);

	}

	public ChatClientGUI() {
		// アプレットのレイアウト
		setLayout(new BorderLayout());
		JPanel p = new JPanel();
		JPanel p1 = new JPanel();
		JPanel p2 = new JPanel();
		p.setLayout(new BorderLayout());
		p1.setLayout(new GridLayout(1, 5, 10, 10));
		p1.add(new JLabel("Host = ", JLabel.RIGHT));
		p1.add(hostField = new JTextField(host));
		p1.add(new JLabel("Port = ", JLabel.RIGHT));
		p1.add(portField = new JTextField("" + port));
		p1.add(connectBut = new JButton("Connect"));
		p2.add(closeBut = new JButton("Close"));
		p2.add(quitBut = new JButton("Quit"));
		p.add("North", p1);
		p.add("Center", p2);

		JPanel privateP = new JPanel();
		privateP.setLayout(new BorderLayout());
		privateP.add("West", new JLabel("Person:"));
		privateP.add("Center", privateArea = new JTextField("",5));

		JPanel buttonG = new JPanel();
		buttonG.add(privateBut = new JButton("Private"));

		privateP.add("East",buttonG);

		JPanel message = new JPanel();
		message.setLayout(new BorderLayout());
		message.add("West", new JLabel("Send:"));
		message.add("Center", inputArea = new JTextField("", 30));


		freeArea = new JTextArea();
		JScrollPane scrollpane = new JScrollPane(freeArea,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS , JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollpane.setVisible(true);

		JPanel allMessageP = new JPanel();
		allMessageP.setLayout(new BorderLayout());
		allMessageP.add("North", message);
		allMessageP.add("South", privateP);

		freeArea.setEditable(false);
		add("North", allMessageP);
		add("Center", scrollpane);
		add("South", p);

		// イベント処理の登録
		connectBut.addActionListener(this);
		closeBut.addActionListener(this);
		quitBut.addActionListener(this);
		inputArea.addActionListener(this);
		privateBut.addActionListener(this);
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

	// クライアントのメインルーチン
	public void run() {
		String s;
		while (thread != null) {
			s = client.read(); // メッセージの読みとり
			if (s == null)
				clientClose();
			else
				freeArea.append(s + "\n"); // テキストエリアへ出力
		}
	}

	// 回線の接続を行なう
	public boolean clientOpen() {
		if (client == null) {
			host = hostField.getText();
			port = Integer.valueOf(portField.getText()).intValue();
			;
			client = new Client(host, port); // Clientクラスの呼び出し
			if (client.socket == null) { // 接続失敗？
				System.out.println("Connect Err");
				client = null;
				return false;
			}
			else
				return true;
		}
		else
			return false;
	}

	// 接続の切断を行なう
	public void clientClose() {
		if (client != null) {
			client.close();
			client = null;
			thread = null;
		}
	}

	// メッセージの送信を行う
	public void writeMes(String mes) {
		try {
			client.write(mes);
		} catch (Exception e) {
			System.out.println("送信中に何らかの例外が発生しました");
			e.printStackTrace(System.err);
		}
	}

	// イベント処理
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == connectBut) { // 回線接続を実行
			if (clientOpen())
				start();
		}
		else if (e.getSource() == closeBut) { // 回線切断を実行
			stop();
			clientClose();
		}
		else if (e.getSource() == quitBut) { // アプレットの終了
			stop();
			clientClose();
			System.exit(1);
		} else if(e.getSource() == privateBut) {
			String person = privateArea.getText();
			String message = inputArea.getText();

			char c5 = (char) 05;
			char c6 = (char) 06;

			String mes = String.valueOf(c5)+"sendPerson"
				+String.valueOf(c6)+person+String.valueOf(c6)+message;
			System.out.println(mes);
			writeMes(mes);
		}
		else if (e.getSource() == inputArea && client != null) {
			// テキストフィールド内の文字列をサーバーへ送信する
			writeMes(inputArea.getText());
			inputArea.setText("");
		}
	}
}
