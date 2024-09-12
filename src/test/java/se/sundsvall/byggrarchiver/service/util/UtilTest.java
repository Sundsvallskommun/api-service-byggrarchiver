package se.sundsvall.byggrarchiver.service.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

import org.junit.jupiter.api.Test;

import se.sundsvall.byggrarchiver.service.exceptions.ApplicationException;
import se.sundsvall.byggrarchiver.util.Util;

class UtilTest {

	@Test
	void testByteArrayToBase64() {
		assertThat(Util.byteArrayToBase64(null)).isNull();
	}

	@Test
	void testGetExtensionFromByteArray() throws IOException, ApplicationException {
		var file = new File(Objects.requireNonNull(getClass().getClassLoader().getResource("File_Without_Extension")).getFile());
		var path = Paths.get(file.getAbsolutePath());
		var bytes = Files.readAllBytes(path);

		assertThat(Util.getExtensionFromByteArray(bytes)).isEqualTo("docx");
	}

	@Test
	void testGetExtensionFromByteArrayError() throws IOException {
		var file = new File(Objects.requireNonNull(getClass().getClassLoader().getResource("Error_File_Without_Extension")).getFile());
		var path = Paths.get(file.getAbsolutePath());
		var bytes = Files.readAllBytes(path);

		assertThatExceptionOfType(ApplicationException.class)
			.isThrownBy(() -> Util.getExtensionFromByteArray(bytes))
			.withMessage("Could not guess extension from bytearray");
	}

	@Test
	void testGetStringOrEmpty_1() {
		assertThat(Util.getStringOrEmpty(null)).isEmpty();
	}

	@Test
	void testGetStringOrEmpty_2() {
		assertThat(Util.getStringOrEmpty("test")).isEqualTo("test");
	}

}
