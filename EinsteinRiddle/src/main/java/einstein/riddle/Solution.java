package einstein.riddle;

import java.util.ArrayList;
import java.util.List;

public class Solution {
	
	private List<Person> persons = new ArrayList<Person>();

	public List<Person> getPersons() {
		return persons;
	}

	public void setPersons(List<Person> persons) {
		this.persons = persons;
	}
	
	public void add(Person person) {
		persons.add(person);
	}
	
	public Person getFishOwner () {
		Person fishOwner = null;
		for (Person person : persons) {
			if (person.getPet() == Pet.FISH) {
				fishOwner = person;
				break;
			}
		}
		return fishOwner;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((persons == null) ? 0 : persons.hashCode());
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
		Solution other = (Solution) obj;
		if (persons == null) {
			if (other.persons != null)
				return false;
		} else if (!persons.equals(other.persons))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Solution [persons=" + persons + "]";
	}
	

}
