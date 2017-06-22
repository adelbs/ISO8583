package org.adelbs.iso8583.vo;

public class ISOTestVO {

	private String configFile;
	private boolean isRequestSync;
	private boolean isResponseSync;
	
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

}
