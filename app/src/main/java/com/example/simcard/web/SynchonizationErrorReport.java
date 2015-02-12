package com.example.simcard.web;

public class SynchonizationErrorReport {

	StringBuffer stringBuffer;
	final static String enterString = "\n";

	public SynchonizationErrorReport() {
		stringBuffer = new StringBuffer();
	}

	public void add(Exception e) {
		if (stringBuffer.length() > 0) {
			stringBuffer.append(enterString);
		}
		stringBuffer.append(e.getLocalizedMessage());
		stringBuffer.append(enterString);

	}

	@Override
	public String toString() {
		return stringBuffer.toString();
	}

}
