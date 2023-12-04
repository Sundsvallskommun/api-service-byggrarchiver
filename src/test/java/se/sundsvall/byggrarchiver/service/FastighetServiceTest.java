package se.sundsvall.byggrarchiver.service;

import generated.se.sundsvall.arendeexport.ArendeFastighet;
import generated.se.sundsvall.arendeexport.Fastighet;
import generated.sokigo.fb.FastighetDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.byggrarchiver.integration.fb.FbIntegration;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FastighetServiceTest {

	@Mock
	private FbIntegration fbIntegration;

	@InjectMocks
	private FastighetService fastighetService;

	@Test
	void getFastighet() throws Exception {
		//Arrange
		final var fastighet1 = new Fastighet();
		fastighet1.setFnr(123);
		final var arendeFastighet1 = new ArendeFastighet().withArHuvudObjekt(true).withFastighet(fastighet1);

		final var arendeFastighet2 = new ArendeFastighet().withArHuvudObjekt(false);

		final var fastighetDto = new FastighetDto();
		fastighetDto.setFnr(123);
		fastighetDto.setKommun("Sundsvall");
		fastighetDto.setBeteckning("Beteckning");
		fastighetDto.setTrakt("Trakt");
		fastighetDto.setUuid(UUID.randomUUID());
		when(fbIntegration.getPropertyInfoByFnr(123)).thenReturn(new FastighetDto());

		final var arendeFastighetList = List.of(arendeFastighet1, arendeFastighet2);

		when(fbIntegration.getPropertyInfoByFnr(123)).thenReturn(fastighetDto);

		//Act
		final var result = fastighetService.getFastighet(arendeFastighetList);

		//Assert and verify
		verify(fbIntegration).getPropertyInfoByFnr(123);

		assertThat(result.getFastighetsbeteckning()).isEqualTo(fastighetDto.getKommun() + " " + fastighetDto.getBeteckning());
		assertThat(result.getTrakt()).isEqualTo(fastighetDto.getTrakt());
		assertThat(result.getObjektidentitet()).isEqualTo(fastighetDto.getUuid().toString());
	}

	@Test
	void getFastighetWhenNoHuvudObjekt() throws Exception {
		//Arrange
		final var fastighet1 = new Fastighet();
		fastighet1.setFnr(123);
		final var arendeFastighet1 = new ArendeFastighet().withArHuvudObjekt(false).withFastighet(fastighet1);

		final var arendeFastighet2 = new ArendeFastighet().withArHuvudObjekt(false);

		final var arendeFastighetList = List.of(arendeFastighet1, arendeFastighet2);

		//Act
		final var result = fastighetService.getFastighet(arendeFastighetList);

		//Assert and verify
		verifyNoInteractions(fbIntegration);

		assertThat(result.getFastighetsbeteckning()).isNull();
		assertThat(result.getTrakt()).isNull();
		assertThat(result.getObjektidentitet()).isNull();
	}

}
