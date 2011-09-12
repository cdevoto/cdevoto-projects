package einstein.riddle;

public enum Drink {
	
	TEA("tea"),
	COFFEE("coffee"),
	MILK("milk"),
	BEER("beer"),
	WATER("water");
	
	public static Drink [] ALL = {TEA, COFFEE, MILK, BEER, WATER};
    
	private String name;

	Drink (String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return name;
	}
	

}
