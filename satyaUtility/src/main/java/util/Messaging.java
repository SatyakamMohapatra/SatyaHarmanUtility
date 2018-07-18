package util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Messaging {
	private Map<String, List<MessageInfo>> message = new HashMap<String, List<MessageInfo>>();

	public Map<String, List<MessageInfo>> getMessageMap() {
		return message;
	}

	public List<MessageInfo> getMessage(String dimensionName) {
		return message.get(dimensionName);
	}

	public void setMessage(String dimensionName, List<MessageInfo> messageInfo) {
		this.message.put(dimensionName, messageInfo);
	}
	
	public void setMessage(String dimensionName, MessageInfo messageInfo) {
		List<MessageInfo> messageInfoList = message.get(dimensionName);
		if (messageInfoList == null) {
			messageInfoList = new ArrayList<MessageInfo>();
		}
		if (!messageInfoList.contains(messageInfo)) {
			messageInfoList.add(messageInfo);
		}
		message.put(dimensionName, messageInfoList);
	}
}
