package se.sundsvall.sokigo.fb.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class AdressplatsIdentifierDto {
    private Integer adressplatsId;
}
