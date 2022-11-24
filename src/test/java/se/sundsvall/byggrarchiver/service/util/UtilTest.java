package se.sundsvall.byggrarchiver.service.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.byggrarchiver.service.exceptions.ApplicationException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class UtilTest {

    @InjectMocks
    private Util util;

    @Test
    void testByteArrayToBase64() {
        assertNull(util.byteArrayToBase64(null));
    }

    @Test
    void testGetExtensionFromByteArray() throws IOException, ApplicationException {
        File file = new File(getClass().getClassLoader().getResource("File_Without_Extension").getFile());
        Path path = Paths.get(file.getAbsolutePath());
        byte[] bytes = Files.readAllBytes(path);

        var result = util.getExtensionFromByteArray(bytes);
        assertEquals("docx", result);
    }

    @Test
    void testGetExtensionFromByteArrayError() throws IOException {
        File file = new File(getClass().getClassLoader().getResource("Error_File_Without_Extension").getFile());
        Path path = Paths.get(file.getAbsolutePath());
        byte[] bytes = Files.readAllBytes(path);

        var exception = assertThrows(ApplicationException.class, () -> util.getExtensionFromByteArray(bytes));
        assertEquals("Could not guess extension from bytearray", exception.getMessage());
    }

    @Test
    void testGetStringOrEmpty_1() {
        assertEquals("", util.getStringOrEmpty(null));
    }

    @Test
    void testGetStringOrEmpty_2() {
        assertEquals("test", util.getStringOrEmpty("test"));
    }

}
