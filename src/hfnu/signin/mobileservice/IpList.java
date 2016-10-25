package hfnu.signin.mobileservice;

import java.util.ArrayList;
import java.util.List;

public class IpList {

	public static List<String> ipList = new ArrayList<String>();

	public static void addIp(String value) {
		ipList.add(value);
	}

	public static List<String> getIpList() {
		return ipList;
	}

	public static void setIpList(List<String> ipList) {
		IpList.ipList = ipList;
	}

}
