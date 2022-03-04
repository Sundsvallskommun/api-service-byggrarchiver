package se.sundsvall.sundsvall.archive.vo;

import lombok.Data;

@Data
public class ArchiveResponse{
	private String archiveId;
	private ErrorDetails errorDetails;
}