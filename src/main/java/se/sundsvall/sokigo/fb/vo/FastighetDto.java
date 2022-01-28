package se.sundsvall.sokigo.fb.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@ToString
public class FastighetDto {
    private Integer fnr;
    private List<AdressplatsIdentifierDto> grupp;
    private String kommun;
    private String beteckning;
    private String trakt;
    private String beteckningsnummer;
    private String uuid;

}
