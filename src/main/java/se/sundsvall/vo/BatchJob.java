package se.sundsvall.vo;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@Setter
public class BatchJob {
    @NotNull
    private LocalDate start;
    private LocalDate end;
}
