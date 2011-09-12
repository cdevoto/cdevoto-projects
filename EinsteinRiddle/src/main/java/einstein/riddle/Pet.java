package einstein.riddle;

public enum Pet {
    DOG("dog"),
    BIRD("bird"),
    CAT("cat"),
    HORSE("horse"),
    FISH("fish");
    
	public static Pet [] ALL = {DOG, BIRD, CAT, HORSE, FISH};
    
    
	private String name;

	Pet (String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return name;
	}
    
}
