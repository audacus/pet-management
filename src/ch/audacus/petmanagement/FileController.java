package ch.audacus.petmanagement;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ch.audacus.petmanagement.Pet.Gender;
import ch.audacus.petmanagement.Pet.Species;

public class FileController implements PetController {

	private static final String FILE_PATH = "pets.txt";

	@Override
	public List<Pet> getAllPets() {
		// key -> ID, value -> Pet
		// use linked hash map to keep the ordering
		List<Pet> pets = new LinkedList<>();

		try {
			Stream<String> lines = getFileContent();
			lines.forEach(line -> {
				String[] properties = line.split(Pet.PROPERTIES_SEPARATOR);

				// verify if line contains at least 4 property values
				if (properties.length >= 4) {
					// create pet from first 4 properties
					Pet pet = new Pet(properties[0], // ID
							getSpecies(properties[1]), // species
							getGender(properties[2]), // gender
							properties[3]); // name

					// add pet to map with key = ID
					pets.add(pet);
				}
			});
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return pets;
	}

	@Override
	public String savePet(Pet pet, boolean addingNew) {
		String ID = pet.getID();
		
		try {
			Stream<String> lines = getFileContent();
			List<String> list;
	
			if (addingNew) {
				// add new pet to lines
				list = lines.collect(Collectors.toList());
				list.add(pet.toString());
			} else {
				// replace line if starts with given ID
				list = lines.map(l -> {
					if (l.startsWith(ID)) {
						l = pet.toString();
					}
					return l;
				}).collect(Collectors.toCollection(LinkedList::new));
			}
	
			// write file with altered lines
			write(list);
		} catch (IOException e) {
			System.err.println("could not save pet: " + pet.toString());
			e.printStackTrace();
		}

		return ID;
	}
	
	@Override
	public void deletePet(String ID) {
		try {
			// filter out line that starts with given ID
			write(getFileContent()
					.filter(l -> !l.startsWith(ID))
					.collect(Collectors.toCollection(LinkedList::new)));
		} catch (IOException e) {
			System.err.println("could not delete pet with ID: " + ID);
			e.printStackTrace();
		}
	}

	private static Species getSpecies(String string) {
		switch (string.toLowerCase()) {
		default:
		case "cat":
			return Species.CAT;
		case "dog":
			return Species.DOG;
		case "horse":
			return Species.HORSE;
		}
	}

	private static Gender getGender(String string) {
		switch (string.toLowerCase()) {
		default:
		case "male":
			return Gender.MALE;
		case "female":
			return Gender.FEMALE;
		}
	}

	private static File getFile() throws IOException {
		// read file
		File file = new File(FILE_PATH);
		// create if not exists
		file.createNewFile();
		return file;
	}

	private static Stream<String> getFileContent() throws IOException {
		// filter out blank lines
		return Files.lines(getFile().toPath()).filter(l -> !l.isBlank());
	}
	
	private static void write(List<String> lines) throws IOException {
		Files.write(getFile().toPath(), lines, StandardOpenOption.TRUNCATE_EXISTING);
	}
}
