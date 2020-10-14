package ch.audacus.petmanagement;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class ObjectController implements PetController {

	private ArrayList<Pet> pets;

	public ObjectController() {
		pets = new ArrayList<>();
	}

	@Override
	public ArrayList<Pet> getAllPets() {
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
			}).collect(Collectors.toCollection(ArrayList::new));
		}

		return ID;
	}

	@Override
	public void deletePet(String ID) {
		pets = pets.stream()
				.filter(p -> p.getID() != ID)
				.collect(Collectors.toCollection(ArrayList::new));
	}
}
