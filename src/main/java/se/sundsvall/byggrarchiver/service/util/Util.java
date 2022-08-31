package se.sundsvall.byggrarchiver.service.util;

import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicException;
import net.sf.jmimemagic.MagicMatch;
import net.sf.jmimemagic.MagicMatchNotFoundException;
import net.sf.jmimemagic.MagicParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import se.sundsvall.byggrarchiver.service.exceptions.ApplicationException;

import java.util.Base64;
import java.util.Objects;

@Service
public class Util {

    private final Logger log = LoggerFactory.getLogger(Util.class);

    public String byteArrayToBase64(byte[] byteArray) {
        if (byteArray == null) {
            return null;
        }
        return Base64.getEncoder().encodeToString(byteArray);
    }

    public String getExtensionFromByteArray(byte[] byteArray) throws ApplicationException {
        try {
            MagicMatch magicMatch = Magic.getMagicMatch(byteArray);
            log.info("getExtensionFromByteArray returns: {}", magicMatch.getExtension());
            String result = magicMatch.getExtension();

            if (Objects.isNull(result) || result.isBlank()) {
                throw new MagicMatchNotFoundException();
            } else {
                return result;
            }
        } catch (MagicMatchNotFoundException | MagicException | MagicParseException e) {
            throw new ApplicationException("Could not guess extension from bytearray", e);
        }
    }

    public String getStringOrEmpty(String string) {
        return string != null ? string : "";
    }

}
