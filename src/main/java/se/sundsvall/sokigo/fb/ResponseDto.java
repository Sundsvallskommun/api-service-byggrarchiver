package se.sundsvall.sokigo.fb;

import java.util.List;

public class ResponseDto {
    private int statusKod;
    private String statusMeddelande;
    private List<String> fel;
    private List<FastighetDto> data;

    public int getStatusKod() {
        return statusKod;
    }

    public void setStatusKod(int statusKod) {
        this.statusKod = statusKod;
    }

    public String getStatusMeddelande() {
        return statusMeddelande;
    }

    public void setStatusMeddelande(String statusMeddelande) {
        this.statusMeddelande = statusMeddelande;
    }

    public List<String> getFel() {
        return fel;
    }

    public void setFel(List<String> fel) {
        this.fel = fel;
    }

    public List<FastighetDto> getData() {
        return data;
    }

    public void setData(List<FastighetDto> data) {
        this.data = data;
    }

}
