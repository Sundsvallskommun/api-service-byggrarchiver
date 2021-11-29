package se.sundsvall.casemanagement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(Include.NON_NULL)
@JsonPropertyOrder({ "type", "status", "title", "detail", "instance" })
@Getter
@Setter
@AllArgsConstructor
public class Information {

	private String type;
	private String title;
	private int status;
	private String detail;
	private String instance;

}
