package einstein.riddle;


public class Person {
	
	private Nationality nationality;
	private HouseColor houseColor;
	private int housePosition;
	private Pet pet;
	private Drink drink;
	private Smoke smoke;
	
	public Nationality getNationality() {
		return nationality;
	}
	public void setNationality(Nationality nationality) {
		this.nationality = nationality;
	}
	public HouseColor getHouseColor() {
		return houseColor;
	}
	public void setHouseColor(HouseColor houseColor) {
		this.houseColor = houseColor;
	}
	public int getHousePosition() {
		return housePosition;
	}
	public void setHousePosition(int housePosition) {
		this.housePosition = housePosition;
	}
	public Pet getPet() {
		return pet;
	}
	public void setPet(Pet pet) {
		this.pet = pet;
	}
	public Drink getDrink() {
		return drink;
	}
	public void setDrink(Drink drink) {
		this.drink = drink;
	}
	public Smoke getSmoke() {
		return smoke;
	}
	public void setSmoke(Smoke smoke) {
		this.smoke = smoke;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((drink == null) ? 0 : drink.hashCode());
		result = prime * result
				+ ((houseColor == null) ? 0 : houseColor.hashCode());
		result = prime * result + housePosition;
		result = prime * result
				+ ((nationality == null) ? 0 : nationality.hashCode());
		result = prime * result + ((pet == null) ? 0 : pet.hashCode());
		result = prime * result + ((smoke == null) ? 0 : smoke.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Person other = (Person) obj;
		if (drink != other.drink)
			return false;
		if (houseColor != other.houseColor)
			return false;
		if (housePosition != other.housePosition)
			return false;
		if (nationality != other.nationality)
			return false;
		if (pet != other.pet)
			return false;
		if (smoke != other.smoke)
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "Person [nationality=" + nationality + ", houseColor="
				+ houseColor + ", housePosition=" + housePosition + ", pet="
				+ pet + ", drink=" + drink + ", smoke=" + smoke + "]";
	}
	
	public boolean disjoint(Person person) {
		return nationality != person.nationality && houseColor != person.houseColor && housePosition != person.housePosition && pet != person.pet && drink != person.drink && smoke != person.smoke;
	}
	

}
