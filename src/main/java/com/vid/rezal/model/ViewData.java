package com.vid.rezal.model;

import com.vid.rezal.utility.ErrorCons;

public class ViewData<T> {

	private String error;
	private String response = ErrorCons.success;
	private T data;
	private int errorCode = 100;
	
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public String getResponse() {
		return response;
	}
	public void setResponse(String response) {
		this.response = response;
	}
	public T getData() {
		return data;
	}
	public void setData(T data) {
		this.data = data;
	}
	public int getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	
	
}
