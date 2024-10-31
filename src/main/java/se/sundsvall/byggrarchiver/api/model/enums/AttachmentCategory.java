package se.sundsvall.byggrarchiver.api.model.enums;

import java.util.Arrays;

import lombok.Getter;

@Getter
public enum AttachmentCategory {

	///////////////////////////////////
	// ByggR
	///////////////////////////////////

	// archiveClassification "A"
	ARIT("ARIT", "A-ritningar", "A"),
	FAS("FAS", "Fasad", "A"),
	FS2("FS2", "Fasad- och sektionsritning", "A"),
	FAP("FAP", "Fasad Plan", "A"),
	FAPL("FAPL", "Fasad Plan Sektion", "A"),
	FPSS("FPSS", "Fasad Plan Sektion Situationsplan", "A"),
	FS("FS", "Fasad sektion", "A"),
	FASSIT("FASSIT", "Fasad Situation", "A"),
	FAS2("FAS2", "Fasadritning", "A"),
	FASSIT2("FASSIT2", "Fasadritning + situationsplan", "A"),
	FOTOMON("FOTOMON", "Fotomontage", "A"),
	FARG("FÄRG", "Färgsättningsförslag", "A"),
	MAST("MAST", "Mastritning", "A"),
	MUR("MUR", "Murritning", "A"),
	MATT("MÅTT", "Måttritning", "A"),
	PERSPEKTIV("PERSPEKTIV", "Perspektivsritning", "A"),
	PLA("PLA", "Plan", "A"),
	PLFA("PLFA", "Plan Fasad", "A"),
	PLFASE("PLFASE", "Plan Fasad Sektion", "A"),
	PLFASESI("PLFASESI", "Plan Fasad Sektion Situationsplan", "A"),
	PLFASI("PLFASI", "Plan Fasad Situationsplan", "A"),
	PLFA2("PLFA2", "Plan- och fasadritning", "A"),
	PFSI2("PFSI2", "Plan- och fasadritning + situationsplan", "A"),
	PLSE2("PLSE2", "Plan- och sektionsritning", "A"),
	PSS2("PSS2", "Plan- och sektionsritning + situationsp.", "A"),
	PLSE("PLSE", "Plan Sektion", "A"),
	PSS("PSS", "Plan Sektion Situation", "A"),
	PLASIT("PLASIT", "Plan Situation", "A"),
	PFS2("PFS2", "Plan, fasad- och sektionsritning", "A"),
	PFSS2("PFSS2", "Plan, fasad, sektion, situation", "A"),
	TEVS("TEVS", "Planbeskrivning", "A"),
	UPLA("UPLA", "Planer", "A"),
	PLAN("PLAN", "Planer", "A"),
	PLANK("PLANK", "Plankritning", "A"),
	PLA2("PLA2", "Planritning", "A"),
	PSI2("PSI2", "Planritning + situationsplan", "A"),
	REL("REL", "Relationsritning", "A"),
	REVRIT("REVRIT", "Reviderade ritning", "A"),
	RITNING("RITNING", "Ritning", "A"),
	TJ("TJ", "Ritningar", "A"),
	RIT("RIT", "Ritningar", "A"),
	SEK("SEK", "Sektion", "A"),
	SEKSIT("SEKSIT", "Sektion Situation", "A"),
	SEKT("SEKT", "Sektioner", "A"),
	SEK2("SEK2", "Sektionsritning", "A"),
	SESI2("SESI2", "Sektionsritning + situationsplan", "A"),
	SKYL("SKYL", "Skyltritning", "A"),
	UPPM("UPPM", "Uppmätningsritning", "A"),
	ANV("ANV", "Utställningshandling", "A"),

	// archiveClassification "D"
	ANM("ANM", "Anmälan", "D"),
	ANMA("ANMÄ", "Anmälan av kontrollansvarig", "D"),
	ANS("ANS", "Ansökan om bygglov", "D"),
	ANSFO("ANSFÖ", "Ansökan om förhandsbesked", "D"),
	ANSM("ANSM", "Ansökan om marklov", "D"),
	ANSR("ANSR", "Ansökan om rivningslov", "D"),
	ANSS("ANSS", "Ansökan om strandskyddsdispens", "D"),
	BEGLST("BEGLST", "Begäran från länsstyrelsen", "D"),
	BERBSA("BERBSA", "Beräkning byggsanktionsavgift", "D"),
	BLST("BLST", "Beslut från Länsstyrelsen", "D"),
	OMPLA("OMPLÄ", "Beslut omprövning Länsstyrelsen", "D"),
	BULL("BULL", "Bullerutredning", "D"),
	DEB("DEB", "Debiteringsblad", "D"),
	DEL("DEL", "Delgivning", "D"),
	DELK("DELK", "Delgivningskvitto", "D"),
	DELSLU("DELSLU", "Delslutbesked", "D"),
	DELSTA("DELSTA", "Delstartbesked", "D"),
	DOM("DOM", "Dom", "D"),
	ENER("ENER", "Energibalansberäkning", "D"),
	ENEDEK("ENEDEK", "Energideklaration", "D"),
	FAST("FAST", "Fastställd kontrollplan", "D"),
	FOLJREVRIT("FÖLJREVRIT", "Följebrev reviderad ritning", "D"),
	FORG2("FÖRG2", "Förhandsgranskningsblad", "D"),
	GODFA("GODFÄ", "Godkännande från fastighetsägare", "D"),
	GRAM("GRAM", "Grannemedgivande", "D"),
	INFOSS("INFOSS", "Info inför start- och slutbesked", "D"),
	INTSLUT("INTSLUT", "Interimistiskt slutbesked", "D"),
	KM("KM", "Kontrollmeddelande", "D"),
	MOTBKR("MOTBKR", "Mottagningsbekräftelse", "D"),
	OVK("OVK", "OVK-protokoll", "D"),
	PM("PM", "PM", "D"),
	PMINN("PMINN", "Påminnelse", "D"),
	PROARB("PROARB", "Protokoll arbetsplatsbesök", "D"),
	PROTAU("PROTAU", "Protokoll AU", "D"),
	PROTKS("PROTKS", "Protokoll KS", "D"),
	PROTPLU("PROTPLU", "Protokoll PLU", "D"),
	PROTSBN("PROTSBN", "Protokoll SBN", "D"),
	PROSS("PROSS", "Protokoll slutsamråd", "D"),
	PROTS("PROTS", "Protokoll tekniskt samråd", "D"),
	REMISS("REMISS", "Remiss", "D"),
	REMS("REMS", "Remissvar", "D"),
	RATT("RÄTT", "Rättidsprövning", "D"),
	SKP("SKP", "Signerad kontrollplan", "D"),
	SBES("SBES", "Slutbesked", "D"),
	STAB("STAB", "Startbesked", "D"),
	SVAR("SVAR", "Svar", "D"),
	SVAR2ar("SVAR2år", "Svar 2-årsbrev", "D"),
	TJA("TJÄ", "Tjänsteskrivelse", "D"),
	UNDER("UNDER", "Underrättelsesvar", "D"),
	ARB("ÄRB", "Ärendeblad", "D"),
	OVER("ÖVER", "Överklagandeskrivelse", "D"),
	ADRESS("ADRESS", "Adressblad", "D"),
	ANSUPA("ANSUPA", "Anmälan utan personnummer", "D"),
	ANNO("ANNO", "Annons", "D"),
	ANSF("ANSF", "Ansökan om förhandsbesked", "D"),
	ANSSL("ANSSL", "Ansökan om slutbesked", "D"),
	ANSUP("ANSUP", "Ansökan utan personnummer", "D"),
	ANKVU("ANKVU", "Antikvariskt utlåtande", "D"),
	ARBI("ARBI", "Arbetstagarintyg", "D"),
	BEHA("BEHA", "Atomutskick handläggare tilldelad", "D"),
	AVPLAN("AVPLAN", "Avvecklingsplan", "D"),
	BANK("BANK", "Bankgaranti", "D"),
	BEGSTART("BEGSTART", "Begäran om startbesked", "D"),
	BEK("BEK", "Bekräftelse", "D"),
	BEKMOTANS("BEKMOTANS", "Bekräftelse mottagen ansökan", "D"),
	BEMO("BEMÖ", "Bemötande", "D"),
	BESKA("BESKA", "Besöksrapporter KA", "D"),
	BESLUT("BESLUT", "Beslut", "D"),
	BIL("BIL", "Bilaga", "D"),
	BRS("BRS", "Brandskiss", "D"),
	BRAB("BRAB", "Brandskyddsbeskrivning", "D"),
	BRAD("BRAD", "Brandskyddsdokumentation", "D"),
	BROS("BROS", "Broschyr", "D"),
	DPH("DPH", "Detaljplankarta/detaljplanhandling", "D"),
	DETALJ("DETALJ", "Detaljritning", "D"),
	DHBHUR("DHBHUR", "Du har fått bygglov/ Hur man överklagar", "D"),
	ELD("ELD", "Elda rätt", "D"),
	EPOS("EPOS", "Epost", "D"),
	EXRIT("EXRIT", "Exempelritning", "D"),
	FAKTU("FAKTU", "Fakturaunderlag", "D"),
	FAKTUS("FAKTUS", "Fakturaunderlag sanktionsavgift", "D"),
	FOTO("FOTO", "Foto", "D"),
	FUM("FUM", "Fullmakt", "D"),
	FSF("FSF", "Färdigställandeförsäkring", "D"),
	FOLJ("FÖLJ", "Följebrev", "D"),
	FORB("FÖRB", "Förhandsbesked", "D"),
	FORK("FÖRK", "Förslag till kontrollplan", "D"),
	FORR("FÖRR", "Förslag till rivningsplan", "D"),
	FORGARBO("FÖRGARBO", "Försäkringsbrev Gar-Bo", "D"),
	UROR("URÖR", "Genomförandebeskrivning", "D"),
	GRA("GRA", "Grannhörande", "D"),
	GRAN("GRAN", "Granskningsblad", "D"),
	GBLAD("GBLAD", "Granskningsblad", "D"),
	HISSINT("HISSINT", "Hissintyg", "D"),
	HUR("HUR", "Hur man överklagar", "D"),
	ARK("ARK", "Illustration/ perspektiv", "D"),
	INTFAK("INTFAK", "Internfakturaunderlag", "D"),
	INTY("INTY", "Intyg", "D"),
	KLA("KLA", "Klassningsplan", "D"),
	KOMP("KOMP", "Kompletteringsföreläggande", "D"),
	KONT("KONT", "Kontrollansvarig", "D"),
	KPLAN("KPLAN", "Kontrollplan PBL", "D"),
	RAPP("RAPP", "Kontrollrapport", "D"),
	KVAL("KVAL", "Kvalitetsansvarig", "D"),
	LUFT("LUFT", "Luftflödesprotokoll", "D"),
	LUTE("LUTE", "Lufttäthetstest", "D"),
	MAIL("MAIL", "Mail", "D"),
	MAPL("MAPL", "Markplaneringsritning", "D"),
	MATINV("MATINV", "Materialinventering", "D"),
	MEDDEL("MEDDEL", "Meddelanden", "D"),
	MIRP("MIRP", "Miljöinventering/ rivningsplan", "D"),
	MINN("MINN", "Minnesanteckningar", "D"),
	POIT("POIT", "PoIT", "D"),
	PRESENTA("PRESENTA", "Presentation", "D"),
	PRES("PRES", "Prestandadeklaration", "D"),
	KPV("KPV", "Programsamrådshandling", "D"),
	PROT("PROT", "Protokoll", "D"),
	PAMINNTB("PÅMINNTB", "Påminnelse tidsbegränsat lov", "D"),
	RAP("RAP", "Rapport", "D"),
	REMUA("REMUA", "Remiss utan adress", "D"),
	RUE("RUE", "Remissvar utan erinran", "D"),
	HBB("HBB", "Ritningsförteckning", "D"),
	RIVA("RIVA", "Rivningsanmälan", "D"),
	RIVP("RIVP", "Rivningsplan", "D"),
	SAK("SAK", "Sakkunnigintyg", "D"),
	SAKUT("SAKUT", "Sakkunnigutlåtande brand", "D"),
	KPR("KPR", "Samrådshandling", "D"),
	KP("KP", "Samrådsredogörelse del 1", "D"),
	KR("KR", "Samrådsredogörelse del 2", "D"),
	SIN("SIN", "Signerad kontrollplan", "D"),
	SKR("SKR", "Skrivelse", "D"),
	KA("KA", "Skrivelser", "D"),
	SKY("SKY", "Skyddsrumsförfrågan", "D"),
	SLUT("SLUT", "Slutbevis", "D"),
	SCB("SCB", "Statistikblankett SCB", "D"),
	STIM("STIM", "Stimulansbidrag", "D"),
	SAF("SÅF", "Svar på åtgärdsföreläggande", "D"),
	TEBY("TEBY", "Teknisk beskrivning", "D"),
	TEKN("TEKN", "Teknisk beskrivning brf", "D"),
	TEKRAP("TEKRAP", "Teknisk rapport", "D"),
	TILL("TILL", "Tillsynsbesiktning", "D"),
	TILLVR("TILLVR", "Tillverkningsritning", "D"),
	SBN("SBN", "Tjänsteskrivelse till nämnden", "D"),
	SAKNAS("SAKNAS", "Typen saknades vid konverteringen", "D"),
	UND("UND", "Underlag situationsplan", "D"),
	UKP("UKP", "Underlag till kontrollplan", "D"),
	UKR("UKR", "Underlag till rivningsplan", "D"),
	UNDUT("UNDUT", "Underrättelse", "D"),
	UBGARBO("UBGARBO", "Uppdragsbekräftelse", "D"),
	UTBEU("UTBEU", "Utbetalningsunderlag", "D"),
	UTSK("UTSK", "Utskick", "D"),
	UTSKP("UTSKP", "Påminnelseutskick", "D"),
	UTSKS("UTSKS", "Svar utskick", "D"),
	BRAU("BRAU", "Utförandekontroll brandskydd", "D"),
	UKA("UKA", "Utlåtande KA", "D"),
	ATG("ÅTG", "Åtgärdsföreläggande", "D"),

	// archiveClassification "GU"
	GEO("GEO", "Geotekniska handling", "GU"),

	// archiveClassification "K"
	GRUNDP("GRUNDP", "Grundplan", "K"),
	GRUNDR("GRUNDR", "Grundritning", "K"),
	KOND("KOND", "Konstruktionsdokument", "K"),
	UKON("UKON", "Konstruktionshandling", "K"),
	KONR("KONR", "Konstruktionsritning", "K"),
	STOMR("STOMR", "Stomritningar", "K"),
	TAPL("TAPL", "Takplan", "K"),
	TSR("TSR", "Takstolsritning", "K"),

	// archiveClassification "S"
	KART("KART", "Karta", "S"),
	NYKA("NYKA", "Nybyggnadskarta", "S"),
	SITU("SITU", "Situationsplan", "S"),
	TOMTPLBE("TOMTPLBE", "Tomtplatsbestämning", "S"),

	// archiveClassification "VVS"
	VAH("VAH", "VA-handling", "VVS"),
	VENT("VENT", "Ventilationshandling", "VVS"),
	UVEN("UVEN", "Ventilationsritning", "VVS"),
	VS("VS", "VS-handling", "VVS"),
	VVSH("VVSH", "VVS-handling", "VVS"),

	///////////////////////////////////
	// Ecos
	///////////////////////////////////
	ANMALAN_LIVSMEDELSANLAGGNING("ANMALAN_LIVSMEDELSANLAGGNING", "3AD42CEE-C09E-401B-ABE8-0CD5D03FE6B4"),

	ANMALAN_ENSKILT_AVLOPP("ANMALAN_ENSKILT_AVLOPP", "E9F85119-9E94-47AD-B531-BB91EF75368A"),
	ANSOKAN_ENSKILT_AVLOPP("ANSOKAN_ENSKILT_AVLOPP", "296B51FA-C77B-42E7-AFBE-F0A74CAE4FD2"),
	ANMALAN_ANDRING_AVLOPPSANLAGGNING("ANMALAN_ANDRING_AVLOPPSANLAGGNING", "3FBEECCA-099D-4E51-8FFA-D023AF79017D"),
	ANMALAN_ANDRING_AVLOPPSANORDNING("ANMALAN_ANDRING_AVLOPPSANORDNING", "52E2898B-D780-4EB5-B9CA-24842714E6DF"),

	ANMALAN_VARMEPUMP("ANMALAN_VARMEPUMP", "ACCC629C-4D26-4466-9DFD-578DB746D119"),
	ANSOKAN_TILLSTAND_VARMEPUMP_MINDRE_AN_100KW("ANSOKAN_TILLSTAND_VARMEPUMP_MINDRE_AN_100KW", "3F6DBE03-DB41-47AA-A56A-ECD87C8133B1"),

	ANMALAN_HALSOSKYDDSVERKSAMHET("ANMALAN_HALSOSKYDDSVERKSAMHET", "EA5D5EBE-DCBE-4EAA-A2B9-8662B128BD96"),

	SITUATIONSPLAN("SITUATIONSPLAN", "9288F033-8E1A-48AE-858F-CB7345F81359"),

	SKRIVELSE("SKRIVELSE", "A06E65AD-E4B1-4B84-BCC6-7843CDE6B0A1"),

	MEDICAL_CONFIRMATION("MEDICAL_CONFIRMATION", "Läkarintyg"),
	POLICE_REPORT("POLICE_REPORT", "Polisanmälan"),
	PASSPORT_PHOTO("PASSPORT_PHOTO", "Passfoto"),
	SIGNATURE("SIGNATURE", "Namnunderskrift"),
	POWER_OF_ATTORNEY("POWER_OF_ATTORNEY", "Fullmakt");

	private final String code;

	private final String description;

	private final String archiveClassification;

	AttachmentCategory(final String code, final String description) {
		this(code, description, null);
	}

	AttachmentCategory(final String code, final String description, final String archiveClassification) {
		this.code = code;
		this.description = description;
		this.archiveClassification = archiveClassification;
	}

	public static AttachmentCategory fromCode(final String code) {
		return Arrays.stream(AttachmentCategory.values())
			.filter(attachmentCategory -> attachmentCategory.code.equals(code))
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("No attachment category '" + code + "'"));
	}
}
