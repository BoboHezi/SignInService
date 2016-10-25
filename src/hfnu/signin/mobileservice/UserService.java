package hfnu.signin.mobileservice;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Stack;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class UserService extends Thread {

	public static ServerSocket serverSocket;
	public static Socket clientSocket;
	public static DataInputStream inputStream;
	public static DataOutputStream outputStream;

	public static String username;
	public static String password;

	@Override
	public void run() {
		try {
			serverSocket = new ServerSocket(5555);
			System.out.println("Listening On Port 5555!");

			while (true) {
				clientSocket = serverSocket.accept();
				InetAddress ip = clientSocket.getInetAddress();
				int port = clientSocket.getPort();
				System.out.println(ip + ":" + port + "\tConnected!");

				if (ip.toString().equals("/127.0.0.1")) {
					System.out.println("本地主机连接!");
					continue;
				}

				inputStream = new DataInputStream(clientSocket.getInputStream());
				outputStream = new DataOutputStream(
						clientSocket.getOutputStream());

				String requestXML = inputStream.readUTF();

				boolean resu = validate(requestXML);
				outputStream.writeBoolean(resu);

				inputStream.close();
				outputStream.close();
				clientSocket.close();

			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
	}

	private boolean validate(String requestXML)
			throws ParserConfigurationException, SAXException, IOException {
		purseData(requestXML);

		if (username.equals(password))
			return true;
		else
			return false;
	}

	private void purseData(String value) throws ParserConfigurationException,
			SAXException, IOException {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser = factory.newSAXParser();
		saxParser.parse(new ByteArrayInputStream(value.getBytes()),
				new myHandler());
	}

	class myHandler extends DefaultHandler {
		private Stack<String> stack = new Stack<String>();
		private String id;
		private String username;
		private String password;

		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			stack.push(qName);
			for (int i = 0; i < attributes.getLength(); i++) {
				id = attributes.getValue(i);
			}
		}

		@Override
		public void characters(char[] ch, int start, int length)
				throws SAXException {
			String tag = stack.peek();
			if (tag.equals("username"))
				username = new String(ch, start, length);
			else if (tag.equals("password"))
				password = new String(ch, start, length);
		}

		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			stack.pop();
		}

		@Override
		public void endDocument() throws SAXException {
			System.out.println("\tID:" + id + "\n\tUserName:" + username
					+ "\n\tPassWord:" + password);
			UserService.username = this.username;
			UserService.password = this.password;
		}

	}

}
