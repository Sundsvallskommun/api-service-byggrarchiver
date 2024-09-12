package se.sundsvall.byggrarchiver.api.model;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(setterPrefix = "with")
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BatchJob {

	@NotNull
	@PastOrPresent
	@Schema(description = "Startdatum på körningen.", format = "date", example = "2021-01-01")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate start;

	@NotNull
	@PastOrPresent
	@Schema(description = "Slutdatum på körningen.", format = "date", example = "2021-01-02")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate end;

}
