
package se.tekis.arende;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the se.tekis.arende package. 
 * &lt;p&gt;An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _ArendeIntressent_QNAME = new QName("www.tekis.se/arende", "arendeIntressent");
    private final static QName _HandelseIntressent_QNAME = new QName("www.tekis.se/arende", "handelseIntressent");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: se.tekis.arende
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Dokument }
     * 
     */
    public Dokument createDokument() {
        return new Dokument();
    }

    /**
     * Create an instance of {@link ArendeIntressent }
     * 
     */
    public ArendeIntressent createArendeIntressent() {
        return new ArendeIntressent();
    }

    /**
     * Create an instance of {@link HandelseIntressent }
     * 
     */
    public HandelseIntressent createHandelseIntressent() {
        return new HandelseIntressent();
    }

    /**
     * Create an instance of {@link Intressent }
     * 
     */
    public Intressent createIntressent() {
        return new Intressent();
    }

    /**
     * Create an instance of {@link ArrayOfIntressentKommunikation }
     * 
     */
    public ArrayOfIntressentKommunikation createArrayOfIntressentKommunikation() {
        return new ArrayOfIntressentKommunikation();
    }

    /**
     * Create an instance of {@link IntressentKommunikation }
     * 
     */
    public IntressentKommunikation createIntressentKommunikation() {
        return new IntressentKommunikation();
    }

    /**
     * Create an instance of {@link IntressentAttention }
     * 
     */
    public IntressentAttention createIntressentAttention() {
        return new IntressentAttention();
    }

    /**
     * Create an instance of {@link ArrayOfAktorbehorighet }
     * 
     */
    public ArrayOfAktorbehorighet createArrayOfAktorbehorighet() {
        return new ArrayOfAktorbehorighet();
    }

    /**
     * Create an instance of {@link Aktorbehorighet }
     * 
     */
    public Aktorbehorighet createAktorbehorighet() {
        return new Aktorbehorighet();
    }

    /**
     * Create an instance of {@link Fakturaadress }
     * 
     */
    public Fakturaadress createFakturaadress() {
        return new Fakturaadress();
    }

    /**
     * Create an instance of {@link Arende }
     * 
     */
    public Arende createArende() {
        return new Arende();
    }

    /**
     * Create an instance of {@link HandlaggareBas }
     * 
     */
    public HandlaggareBas createHandlaggareBas() {
        return new HandlaggareBas();
    }

    /**
     * Create an instance of {@link HandlaggareIdentity }
     * 
     */
    public HandlaggareIdentity createHandlaggareIdentity() {
        return new HandlaggareIdentity();
    }

    /**
     * Create an instance of {@link ArrayOfArendeIntressent }
     * 
     */
    public ArrayOfArendeIntressent createArrayOfArendeIntressent() {
        return new ArrayOfArendeIntressent();
    }

    /**
     * Create an instance of {@link ArrayOfHandelse }
     * 
     */
    public ArrayOfHandelse createArrayOfHandelse() {
        return new ArrayOfHandelse();
    }

    /**
     * Create an instance of {@link Handelse }
     * 
     */
    public Handelse createHandelse() {
        return new Handelse();
    }

    /**
     * Create an instance of {@link Beslut }
     * 
     */
    public Beslut createBeslut() {
        return new Beslut();
    }

    /**
     * Create an instance of {@link ArrayOfHandelseHandling }
     * 
     */
    public ArrayOfHandelseHandling createArrayOfHandelseHandling() {
        return new ArrayOfHandelseHandling();
    }

    /**
     * Create an instance of {@link HandelseHandling }
     * 
     */
    public HandelseHandling createHandelseHandling() {
        return new HandelseHandling();
    }

    /**
     * Create an instance of {@link Handling }
     * 
     */
    public Handling createHandling() {
        return new Handling();
    }

    /**
     * Create an instance of {@link DokumentFil }
     * 
     */
    public DokumentFil createDokumentFil() {
        return new DokumentFil();
    }

    /**
     * Create an instance of {@link ArrayOfHandelseIntressent }
     * 
     */
    public ArrayOfHandelseIntressent createArrayOfHandelseIntressent() {
        return new ArrayOfHandelseIntressent();
    }

    /**
     * Create an instance of {@link ArrayOfBevakning }
     * 
     */
    public ArrayOfBevakning createArrayOfBevakning() {
        return new ArrayOfBevakning();
    }

    /**
     * Create an instance of {@link Bevakning }
     * 
     */
    public Bevakning createBevakning() {
        return new Bevakning();
    }

    /**
     * Create an instance of {@link ArrayOfAbstractArendeObjekt }
     * 
     */
    public ArrayOfAbstractArendeObjekt createArrayOfAbstractArendeObjekt() {
        return new ArrayOfAbstractArendeObjekt();
    }

    /**
     * Create an instance of {@link ArendeBelagenhetAdress }
     * 
     */
    public ArendeBelagenhetAdress createArendeBelagenhetAdress() {
        return new ArendeBelagenhetAdress();
    }

    /**
     * Create an instance of {@link BelagenhetAdress }
     * 
     */
    public BelagenhetAdress createBelagenhetAdress() {
        return new BelagenhetAdress();
    }

    /**
     * Create an instance of {@link RegByggnad }
     * 
     */
    public RegByggnad createRegByggnad() {
        return new RegByggnad();
    }

    /**
     * Create an instance of {@link PrelRegByggnad }
     * 
     */
    public PrelRegByggnad createPrelRegByggnad() {
        return new PrelRegByggnad();
    }

    /**
     * Create an instance of {@link GeomType }
     * 
     */
    public GeomType createGeomType() {
        return new GeomType();
    }

    /**
     * Create an instance of {@link PointGeomType }
     * 
     */
    public PointGeomType createPointGeomType() {
        return new PointGeomType();
    }

    /**
     * Create an instance of {@link Fastighet }
     * 
     */
    public Fastighet createFastighet() {
        return new Fastighet();
    }

    /**
     * Create an instance of {@link PrelFastighet }
     * 
     */
    public PrelFastighet createPrelFastighet() {
        return new PrelFastighet();
    }

    /**
     * Create an instance of {@link PrelDetaljPlan }
     * 
     */
    public PrelDetaljPlan createPrelDetaljPlan() {
        return new PrelDetaljPlan();
    }

    /**
     * Create an instance of {@link TillsynsOmrade }
     * 
     */
    public TillsynsOmrade createTillsynsOmrade() {
        return new TillsynsOmrade();
    }

    /**
     * Create an instance of {@link GenericOmrade }
     * 
     */
    public GenericOmrade createGenericOmrade() {
        return new GenericOmrade();
    }

    /**
     * Create an instance of {@link GraevOmrade }
     * 
     */
    public GraevOmrade createGraevOmrade() {
        return new GraevOmrade();
    }

    /**
     * Create an instance of {@link PrelBelagenhetAdress }
     * 
     */
    public PrelBelagenhetAdress createPrelBelagenhetAdress() {
        return new PrelBelagenhetAdress();
    }

    /**
     * Create an instance of {@link ArendeRegByggnad }
     * 
     */
    public ArendeRegByggnad createArendeRegByggnad() {
        return new ArendeRegByggnad();
    }

    /**
     * Create an instance of {@link ArendeOmrade }
     * 
     */
    public ArendeOmrade createArendeOmrade() {
        return new ArendeOmrade();
    }

    /**
     * Create an instance of {@link ArrayOfArendeBelagenhetAdress }
     * 
     */
    public ArrayOfArendeBelagenhetAdress createArrayOfArendeBelagenhetAdress() {
        return new ArrayOfArendeBelagenhetAdress();
    }

    /**
     * Create an instance of {@link ArrayOfArendeRegByggnad }
     * 
     */
    public ArrayOfArendeRegByggnad createArrayOfArendeRegByggnad() {
        return new ArrayOfArendeRegByggnad();
    }

    /**
     * Create an instance of {@link ArendeFastighet }
     * 
     */
    public ArendeFastighet createArendeFastighet() {
        return new ArendeFastighet();
    }

    /**
     * Create an instance of {@link ArendePrelFastighet }
     * 
     */
    public ArendePrelFastighet createArendePrelFastighet() {
        return new ArendePrelFastighet();
    }

    /**
     * Create an instance of {@link ArrayOfString }
     * 
     */
    public ArrayOfString createArrayOfString() {
        return new ArrayOfString();
    }

    /**
     * Create an instance of {@link Remiss }
     * 
     */
    public Remiss createRemiss() {
        return new Remiss();
    }

    /**
     * Create an instance of {@link ArendeHandlaggare }
     * 
     */
    public ArendeHandlaggare createArendeHandlaggare() {
        return new ArendeHandlaggare();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArendeIntressent }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ArendeIntressent }{@code >}
     */
    @XmlElementDecl(namespace = "www.tekis.se/arende", name = "arendeIntressent")
    public JAXBElement<ArendeIntressent> createArendeIntressent(ArendeIntressent value) {
        return new JAXBElement<ArendeIntressent>(_ArendeIntressent_QNAME, ArendeIntressent.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link HandelseIntressent }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link HandelseIntressent }{@code >}
     */
    @XmlElementDecl(namespace = "www.tekis.se/arende", name = "handelseIntressent")
    public JAXBElement<HandelseIntressent> createHandelseIntressent(HandelseIntressent value) {
        return new JAXBElement<HandelseIntressent>(_HandelseIntressent_QNAME, HandelseIntressent.class, null, value);
    }

}
