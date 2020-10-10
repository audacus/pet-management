package ch.audacus.petmanagement;

import java.util.LinkedHashMap;
import java.util.Map;

public class ObjectController implements PetController {

	// key -> ID, value -> Pet
	private Map<String, Pet> pets;

	public ObjectController() {
		// use linked hash map to keep the ordering
		pets = new LinkedHashMap<>();
	}

	@Override
	public Map<String, Pet> getAllPets() {
		return pets;
	}

	@Override
	public String savePet(Pet pet, boolean addingNew) {
		// overwrite or add new pet
		String ID = pet.getID();
		pets.put(ID, pet);

		return ID;
	}

	@Override
	public void deletePet(String ID) {
		pets.remove(ID);
	}
}
