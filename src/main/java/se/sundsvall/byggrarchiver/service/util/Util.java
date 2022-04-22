package se.sundsvall.byggrarchiver.service.util;

import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicMatch;
import org.jboss.logging.Logger;
import se.sundsvall.byggrarchiver.service.exceptions.ApplicationException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Base64;

@ApplicationScoped
public class Util {

    @Inject
    Logger log;

    public String byteArrayToBase64(byte[] byteArray) {
        if (byteArray == null) {
            return null;
        }
        return Base64.getEncoder().encodeToString(byteArray);
    }

    public String getExtensionFromByteArray(byte[] byteArray) throws ApplicationException {
        try {
            MagicMatch magicMatch = Magic.getMagicMatch(byteArray);
            log.info("getExtensionFromByteArray returns: " + magicMatch.getExtension());
            return magicMatch.getExtension();
        } catch (Exception e) {
            throw new ApplicationException("Could not guess extension from bytearray", e);
        }
    }

    public String getStringOrEmpty(String string) {
        return string != null ? string : "";
    }

}
