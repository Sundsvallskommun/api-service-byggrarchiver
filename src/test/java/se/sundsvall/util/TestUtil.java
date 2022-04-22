package se.sundsvall.util;

import javax.enterprise.context.ApplicationScoped;
import java.util.Random;

@ApplicationScoped
public class TestUtil {
    public static final Random RANDOM = new Random();
}
