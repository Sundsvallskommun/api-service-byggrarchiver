package se.sundsvall.sundsvall.archive.vo;

import lombok.Data;

@Data
public class ErrorDetails{
	private String errorMessage;
	private int errorCode;
	private String serviceName;
}