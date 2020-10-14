package ch.audacus.petmanagement;

public class PetManagement {
	
	public PetManagement(PetController controller) {
		new PetGUI(controller);
	}
	
	public static void main(String[] args) {
		new PetManagement(new ObjectController());
//		new PetManagement(new FileController());
	}
}
