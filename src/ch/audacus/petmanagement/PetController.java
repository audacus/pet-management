package ch.audacus.petmanagement;
import java.util.List;

public interface PetController {

	public List<Pet> getAllPets();
	
	public String savePet(Pet pet, boolean addingNew);
	
	public void deletePet(String ID);
}
