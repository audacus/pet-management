package ch.audacus.petmanagement;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class ObjectController implements PetController {

	// key -> ID, value -> Pet
	private List<Pet> pets;

	public ObjectController() {
		// use linked hash map to keep the ordering
		pets = new LinkedList<>();
	}

	@Override
	public List<Pet> getAllPets() {
		return pets;
	}

	@Override
	public String savePet(Pet pet, boolean addingNew) {
		// overwrite or add new pet
		String ID = pet.getID();
		if (addingNew) {
			// add pet to list
			pets.add(pet);
		} else {
			// search for pet with ID of given pet and replace it
			pets = pets.stream().map(p -> {
				if (p.getID() == ID) {
					p = pet;
				}
				return p;
			}).collect(Collectors.toCollection(LinkedList::new));
		}

		return ID;
	}

	@Override
	public void deletePet(String ID) {
		pets = pets.stream()
				.filter(p -> p.getID() != ID)
				.collect(Collectors.toCollection(LinkedList::new));
	}
}
