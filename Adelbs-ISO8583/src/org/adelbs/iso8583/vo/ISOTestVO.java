package org.adelbs.iso8583.vo;

public class ISOTestVO {

	private String configFile;
	
	public ISOTestVO(String configFile) {
		this.configFile = configFile;
	}
	
	public String getConfigFile() {
		return configFile;
	}

	public void setConfigFile(String configFile) {
		this.configFile = configFile;
	}

	public String toXML(boolean fullDocument) {
		StringBuffer xmlMessage = new StringBuffer();

		if (fullDocument) {
			xmlMessage.append("<?xml version=\"1.0\" ?>\n\n ");
			xmlMessage.append("<document>\n\n");
		}
		
		xmlMessage.append("<test-iso config-file=\"").append(getConfigFile()).append("\" />\n\n");

		if (fullDocument)
			xmlMessage.append("\n\n</document>");

		return xmlMessage.toString();
	}
}
