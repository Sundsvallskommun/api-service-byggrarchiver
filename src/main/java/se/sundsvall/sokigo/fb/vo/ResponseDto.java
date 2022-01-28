package se.sundsvall.sokigo.fb.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ResponseDto {
    private int statusKod;
    private String statusMeddelande;
    private List<String> fel;
    private List<FastighetDto> data;

}
