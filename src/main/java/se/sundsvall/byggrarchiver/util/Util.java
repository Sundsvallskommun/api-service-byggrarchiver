package se.sundsvall.byggrarchiver.util;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.Base64;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.sundsvall.byggrarchiver.service.exceptions.ApplicationException;

import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicException;
import net.sf.jmimemagic.MagicMatchNotFoundException;
import net.sf.jmimemagic.MagicParseException;

public final class Util {

    private static final Logger LOG = LoggerFactory.getLogger(Util.class);

    private Util() { }

    public static String byteArrayToBase64(final byte[] byteArray) {
        if (byteArray == null) {
            return null;
        }
        return Base64.getEncoder().encodeToString(byteArray);
    }

    public static String getExtensionFromByteArray(final byte[] byteArray) throws ApplicationException {
        try {
            var magicMatch = Magic.getMagicMatch(byteArray);
            LOG.info("getExtensionFromByteArray returns: {}", magicMatch.getExtension());
            var result = magicMatch.getExtension();

            if (isBlank(result)) {
                throw new MagicMatchNotFoundException();
            } else {
                return result;
            }
        } catch (MagicMatchNotFoundException | MagicException | MagicParseException e) {
            throw new ApplicationException("Could not guess extension from bytearray", e);
        }
    }

    public static String getStringOrEmpty(final String string) {
        return Optional.ofNullable(string).orElse("");
    }
}
