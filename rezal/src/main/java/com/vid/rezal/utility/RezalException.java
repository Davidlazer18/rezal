package com.vid.rezal.utility;

public class RezalException extends Exception {

	private static final long serialVersionUID = 1L;
	public String errorMsg;
	public String errorCode;

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String message, String errorCode) {
		this.errorCode = errorCode;
	}

	public RezalException(String message) {
		super(message);
		setErrorMsg(message);
	}

	public RezalException(String message, Throwable throwable) {
		super(message, throwable);
	}

}

