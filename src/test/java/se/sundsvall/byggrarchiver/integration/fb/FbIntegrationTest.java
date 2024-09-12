package se.sundsvall.byggrarchiver.integration.fb;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.zalando.problem.Status.SERVICE_UNAVAILABLE;
import static se.sundsvall.byggrarchiver.testutils.TestUtil.randomInt;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.ThrowableProblem;

import se.sundsvall.byggrarchiver.service.exceptions.ApplicationException;

import generated.se.sundsvall.arendeexport.ArendeFastighet;
import generated.se.sundsvall.arendeexport.Fastighet;
import generated.sokigo.fb.FastighetDto;
import generated.sokigo.fb.ResponseDtoIEnumerableFastighetDto;

@ExtendWith(MockitoExtension.class)
class FbIntegrationTest {

	@Mock
	private FbClient mockFbClient;

	@InjectMocks
	private FbIntegration fbIntegration;

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

		final var arendeFastighetList = List.of(arendeFastighet1, arendeFastighet2);

		when(mockFbClient.getPropertyInfoByFnr(List.of(123))).thenReturn(new ResponseDtoIEnumerableFastighetDto().data(List.of(fastighetDto)));

		//Act
		final var result = fbIntegration.getFastighet(arendeFastighetList);

		//Assert and verify
		verify(mockFbClient).getPropertyInfoByFnr(List.of(123));

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
		final var result = fbIntegration.getFastighet(arendeFastighetList);

		//Assert and verify
		verifyNoInteractions(mockFbClient);

		assertThat(result.getFastighetsbeteckning()).isNull();
		assertThat(result.getTrakt()).isNull();
		assertThat(result.getObjektidentitet()).isNull();
	}

	@Test
	void getPropertyInfoByFnr() throws ApplicationException {
		final var fastighetDto = new FastighetDto()
			.uuid(UUID.randomUUID())
			.fnr(randomInt());
		final var responseDto = new ResponseDtoIEnumerableFastighetDto()
			.data(List.of(fastighetDto));

		when(mockFbClient.getPropertyInfoByFnr(anyList())).thenReturn(responseDto);

		final var result = fbIntegration.getPropertyInfoByFnr(12345);

		assertThat(result).isEqualTo(fastighetDto);

		verify(mockFbClient, times(1)).getPropertyInfoByFnr(anyList());
		verifyNoMoreInteractions(mockFbClient);
	}

	@Test
	void getPropertyInfoByFnr_Empty() throws ApplicationException {
		when(mockFbClient.getPropertyInfoByFnr(anyList()))
			.thenReturn(new ResponseDtoIEnumerableFastighetDto().data(List.of()));

		final var result = fbIntegration.getPropertyInfoByFnr(45678);

		assertThat(result).isNull();

		verify(mockFbClient, times(1)).getPropertyInfoByFnr(anyList());
		verifyNoMoreInteractions(mockFbClient);
	}

	@Test
	void getPropertyInfoByFnr_MoreThanOne() {
		final var dto1 = new FastighetDto();
		dto1.setUuid(UUID.randomUUID());
		dto1.setFnr(randomInt());

		final var dto2 = new FastighetDto();
		dto2.setUuid(UUID.randomUUID());
		dto2.setFnr(randomInt());

		when(mockFbClient.getPropertyInfoByFnr(anyList()))
			.thenReturn(new ResponseDtoIEnumerableFastighetDto().data(List.of(dto1, dto2)));

		assertThatExceptionOfType(ApplicationException.class)
			.isThrownBy(() -> fbIntegration.getPropertyInfoByFnr(15151));

		verify(mockFbClient, times(1)).getPropertyInfoByFnr(anyList());
		verifyNoMoreInteractions(mockFbClient);
	}

	@Test
	void getPropertyInfoByFnr_ClientError() {
		when(mockFbClient.getPropertyInfoByFnr(anyList()))
			.thenThrow(Problem.valueOf(Status.INTERNAL_SERVER_ERROR));

		final int fnr = randomInt();

		assertThatExceptionOfType(ThrowableProblem.class)
			.isThrownBy(() -> fbIntegration.getPropertyInfoByFnr(fnr))
			.satisfies(throwableProblem -> {
				assertThat(throwableProblem.getStatus()).isEqualTo(SERVICE_UNAVAILABLE);
				assertThat(throwableProblem.getDetail()).isEqualTo("Request to fbService.getPropertyInfoByFnr(" + fnr + ") failed.");
			});

		verify(mockFbClient, times(1)).getPropertyInfoByFnr(anyList());
		verifyNoMoreInteractions(mockFbClient);
	}

}
