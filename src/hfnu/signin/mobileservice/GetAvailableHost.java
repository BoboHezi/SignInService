package hfnu.signin.mobileservice;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class GetAvailableHost extends Thread {

	@Override
	public void run() {
		String ip = "192.168.1.";
		while (true) {
			try {
				for (int i = 2; i < 256; i++) {
					String host = ip + i;
					new TestIp(host).start();
				}
				Thread.sleep(1000 * 60 * 5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	static class TestIp extends Thread {

		String ip = null;

		public TestIp(String ip) {
			this.ip = ip;
		}

		@Override
		public void run() {
			InetAddress ia;
			try {
				ia = InetAddress.getByName(ip);
				boolean bool = ia.isReachable(1000 * 10);
				if (bool) {
					IpList.addIp(ip);
					System.out.println("AvailableHost:\t" + ip);
				}

			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
