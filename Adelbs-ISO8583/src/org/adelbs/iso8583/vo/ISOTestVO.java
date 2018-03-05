package org.adelbs.iso8583.vo;

public class ISOTestVO {

	private String configFile;
	private boolean isRequestSync;
	private boolean isResponseSync;
	
	public ISOTestVO(String configFile, boolean isRequestSync, boolean isResponseSync) {
		this.configFile = configFile;
		this.isRequestSync = isRequestSync;
		this.isResponseSync = isResponseSync;
	}
	
	public String getConfigFile() {
		return configFile;
	}

	public void setConfigFile(String configFile) {
		this.configFile = configFile;
	}

	public boolean isRequestSync() {
		return isRequestSync;
	}

	public void setRequestSync(boolean isRequestSync) {
		this.isRequestSync = isRequestSync;
	}

	public boolean isResponseSync() {
		return isResponseSync;
	}

	public void setResponseSync(boolean isResponseSync) {
		this.isResponseSync = isResponseSync;
	}

	public String toXML(boolean fullDocument) {
		StringBuffer xmlMessage = new StringBuffer();

		if (fullDocument) {
			xmlMessage.append("<?xml version=\"1.0\" ?>\n\n ");
			xmlMessage.append("<document>\n\n");
		}
		
		xmlMessage.append("<test-iso config-file=\"").append(getConfigFile()).append("\" ").
		append("request-sync=\"").
		append(isRequestSync() ? "true" : "false").append("\" ").
		append("response-sync=\"").
		append(isResponseSync() ? "true" : "false").append("\"/>\n\n");

		if (fullDocument)
			xmlMessage.append("\n\n</document>");

		return xmlMessage.toString();
	}
}
