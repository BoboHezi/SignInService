package hfnu.signin.mobileservice;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class mobileservice {

	public static void main(String[] args) {
		start();
	}

	public static void start() {
		// 获取所有活跃主机IP
		GetAvailableHost getAvailableHost = new GetAvailableHost();
		getAvailableHost.start();
		// 启动用户服务程序
		new UserService().start();
		// 启动本地数据响应
		new LocalRequest().start();
	}

	// 发送消息至所有的主机
	public static void sendInfo(String number, String location, String time) {

		List<String> valueIP = IpList.getIpList();
		String sendStr = getXML(number, location, time);

		for (int i = 0; i < valueIP.size(); i++) {
			String ip = valueIP.get(i);
			try {
				DatagramSocket server = new DatagramSocket();

				byte[] sendbuf = sendStr.getBytes();

				int port = 5556;
				InetAddress address = InetAddress.getByName(ip);

				DatagramPacket sendPacket = new DatagramPacket(sendbuf,
						sendbuf.length, address, port);

				server.send(sendPacket);
				server.close();
			} catch (SocketException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static String getXML(String number, String location, String time) {
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
		xml = xml + "<recoder id=\"" + number + "\">\n";
		xml = xml + "\t<location>" + location + "</location>\n";
		xml = xml + "\t<time>" + time + "</time>\n";
		xml = xml + "</recoder>";
		return xml;
	}

	static class LocalRequest extends Thread {
		@SuppressWarnings("resource")
		@Override
		public void run() {
			try {
				ServerSocket serverSocket = new ServerSocket(7777);
				Socket clientSocket;
				while (true) {
					clientSocket = serverSocket.accept();
					DataInputStream inputStream = new DataInputStream(
							clientSocket.getInputStream());

					String number = inputStream.readUTF();
					String location = inputStream.readUTF();

					Date date = new Date();
					SimpleDateFormat sdf = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm");
					String time = sdf.format(date);

					sendInfo(number, location, time);

					inputStream.close();
					clientSocket.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
