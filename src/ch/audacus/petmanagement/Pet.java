package ch.audacus.petmanagement;

import java.util.UUID;

public class Pet {
	
	public static final String PROPERTIES_SEPARATOR = ";";

	public enum Species {
		DOG, CAT, HORSE
	};

	public enum Gender {
		MALE, FEMALE
	}

	private String ID;
	private Species species;
	private Gender gender;
	private String name;

	public Pet(Species species, Gender gender, String name) {
		this(UUID.randomUUID().toString().substring(0, 8), species, gender, name);
	}
	
	public Pet(String ID, Species species, Gender gender, String name) {
		this.ID = ID;
		this.species = species;
		this.gender = gender;
		this.name = name;
	}
	
	@Override
	public String toString() {
		return String.join(PROPERTIES_SEPARATOR, this.ID, this.species.toString(), this.gender.toString(), this.name);
	}

	public Species getSpecies() {
		return species;
	}

	public void setSpecies(Species species) {
		this.species = species;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getID() {
		return ID;
	}
	
	public void setID(String ID) {
		this.ID = ID;
	}
}