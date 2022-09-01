package se.sundsvall.byggrarchiver.service;

import generated.sokigo.fb.FastighetDto;
import generated.sokigo.fb.ResponseDtoIEnumerableFastighetDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.ThrowableProblem;
import se.sundsvall.byggrarchiver.integration.fb.FbClient;
import se.sundsvall.byggrarchiver.service.exceptions.ApplicationException;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class FbServiceTest {

    @Mock
    private FbClient fbClient;

    @InjectMocks
    FbService fbService;

    @Test
    void getPropertyInfoByFnr() throws ApplicationException {
        ResponseDtoIEnumerableFastighetDto mockObject = new ResponseDtoIEnumerableFastighetDto();
        FastighetDto mockDto = new FastighetDto();
        mockDto.setUuid(UUID.randomUUID());
        mockDto.setFnr(new Random().nextInt());
        mockObject.setData(List.of(mockDto));
        doReturn(mockObject).when(fbClient).getPropertyInfoByFnr(anyList(), any(), any(), any());

        var result = fbService.getPropertyInfoByFnr(new Random().nextInt());
        assertEquals(mockDto, result);
    }

    @Test
    void getPropertyInfoByFnrEmpty() throws ApplicationException {
        ResponseDtoIEnumerableFastighetDto mockObject = new ResponseDtoIEnumerableFastighetDto();
        doReturn(mockObject).when(fbClient).getPropertyInfoByFnr(anyList(), any(), any(), any());

        var result = fbService.getPropertyInfoByFnr(new Random().nextInt());
        assertNull(result);
    }

    @Test
    void getPropertyInfoByFnrMoreThanOne() {
        ResponseDtoIEnumerableFastighetDto mockObject = new ResponseDtoIEnumerableFastighetDto();
        FastighetDto mockDto_1 = new FastighetDto();
        mockDto_1.setUuid(UUID.randomUUID());
        mockDto_1.setFnr(new Random().nextInt());
        FastighetDto mockDto_2 = new FastighetDto();
        mockDto_2.setUuid(UUID.randomUUID());
        mockDto_2.setFnr(new Random().nextInt());
        mockObject.setData(List.of(mockDto_1, mockDto_2));
        doReturn(mockObject).when(fbClient).getPropertyInfoByFnr(anyList(), any(), any(), any());

        assertThrows(ApplicationException.class, () ->  fbService.getPropertyInfoByFnr(new Random().nextInt()));
    }

    @Test
    void clientError() {
        doThrow(Problem.valueOf(Status.INTERNAL_SERVER_ERROR)).when(fbClient).getPropertyInfoByFnr(anyList(), any(), any(), any());

        int fnr = new Random().nextInt();

        var problem = assertThrows(ThrowableProblem.class, () ->  fbService.getPropertyInfoByFnr(fnr));
        assertEquals(Status.SERVICE_UNAVAILABLE, problem.getStatus());
        assertEquals("Request to fbService.getPropertyInfoByFnr(" + fnr + ") failed.", problem.getDetail());
    }
}
