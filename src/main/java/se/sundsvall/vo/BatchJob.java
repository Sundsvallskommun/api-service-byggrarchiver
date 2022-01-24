package se.sundsvall.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;

@Getter
@Setter
public class BatchJob {
    @NotNull
    // TODO - set to @Past ?
    @PastOrPresent
    @Schema(description = "Startdatum på körningen.", format = "date", example = "2021-01-01")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate start;
    @NotNull
    // TODO - set to @Past ?
    @PastOrPresent
    @Schema(description = "Slutdatum på körningen.", format = "date", example = "2021-01-02")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate end;
}
