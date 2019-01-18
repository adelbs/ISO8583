package org.adelbs.iso8583.payload;

public class RevertResult {
	
	private int revertEndPosition = 0;
	private Object resultantObject;
	
	public RevertResult(int revertEndPosition, Object resultantObject) {
		super();
		this.revertEndPosition = revertEndPosition;
		this.resultantObject = resultantObject;
	}

	public int getRevertEndPosition() {
		return revertEndPosition;
	}

	public void setRevertEndPosition(int revertEndPosition) {
		this.revertEndPosition = revertEndPosition;
	}

	public Object getResultantObject() {
		return resultantObject;
	}

	public void setResultantObject(Object resultantObject) {
		this.resultantObject = resultantObject;
	}	
}
