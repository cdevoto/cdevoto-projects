package einstein.riddle;

public enum Smoke {
	
	PALLMALL("PallMall"),
	DUNHILL("Dunhill"),
	BLENDS("Blends"),
	BLUEMASTER("BlueMaster"),
	PRINCE("Prince");
	
	public static Smoke [] ALL = {PALLMALL, DUNHILL, BLENDS, BLUEMASTER, PRINCE};
    
	private String name;

	Smoke (String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return name;
	}
	

}
