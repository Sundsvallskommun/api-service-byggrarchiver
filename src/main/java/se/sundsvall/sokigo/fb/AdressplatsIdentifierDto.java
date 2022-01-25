package se.sundsvall.sokigo.fb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AdressplatsIdentifierDto {
    private Integer adressplatsId;

    public Integer getAdressplatsId() {
        return adressplatsId;
    }

    public void setAdressplatsId(Integer adressplatsId) {
        this.adressplatsId = adressplatsId;
    }
}
