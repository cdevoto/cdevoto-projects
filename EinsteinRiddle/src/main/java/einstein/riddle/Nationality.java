package einstein.riddle;


public enum Nationality {
	
	BRIT("Brit"),
	SWEDE("Swede"),
	DANE("Dane"),
	NORWEGIAN("Norwegian"),
	GERMAN("German");
	
	public static Nationality [] ALL = {BRIT, SWEDE, DANE, NORWEGIAN, GERMAN};
	
	private String name;

	Nationality (String name) {
		this.name = name;
	}
	
	public String toString() {
		return name;
	}

}
