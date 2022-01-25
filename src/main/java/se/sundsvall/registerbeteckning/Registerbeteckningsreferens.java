package se.sundsvall.registerbeteckning;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Registerbeteckningsreferens {
    private String beteckningsid;
    private String registerenhet;
    private String beteckning;
}
