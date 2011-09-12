package einstein.riddle;

public enum HouseColor {
	
	RED("red"),
	GREEN("green"),
	WHITE("white"),
	YELLOW("yellow"),
	BLUE("blue");
	
	public static HouseColor [] ALL = {RED, GREEN, WHITE, YELLOW, BLUE};
	
	
	private String name;

	HouseColor (String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return name;
	}

}
