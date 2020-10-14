package ch.audacus.petmanagement;
import java.util.ArrayList;

public interface PetController {

	public ArrayList<Pet> getAllPets();
	
	public String savePet(Pet pet, boolean addingNew);
	
	public void deletePet(String ID);
}
