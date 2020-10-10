package ch.audacus.petmanagement;
import java.util.Map;

public interface PetController {

	public Map<String, Pet> getAllPets();
	
	public String savePet(Pet pet, boolean addingNew);
	
	public void deletePet(String ID);
}
