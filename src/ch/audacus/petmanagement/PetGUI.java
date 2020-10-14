package ch.audacus.petmanagement;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.LinkedList;
import java.util.stream.Collectors;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import layout.SpringUtilities;

public class PetGUI {

	// button actions
	private static final String ACTION_LOAD_PETS = "load pets";
	private static final String ACTION_DELETE_PET = "delete pet";
	private static final String ACTION_NEW_PET = "new pet";
	private static final String ACTION_CANCEL = "cancel";
	private static final String ACTION_SAVE_PET = "save pet";
	private static final String ACTION_PREVIOUS = "previous";
	private static final String ACTION_NEXT = "next";

	// controller
	private PetController controller;

	private ArrayList<Pet> pets;
	// contains all IDs
	private ArrayList<String> order;
	// index of current item
	private int index;
	// flag if adding new pet
	private boolean addingNew;

	// ordering
	private String previous;
	private String current;
	private String next;

	// value fields
	private JLabel valueID;
	private JComboBox<Pet.Species> valueSpecies;
	private JComboBox<Pet.Gender> valueGender;
	private JTextField valueName;

	// buttons
	private JButton buttonLoadPets;
	private JButton buttonDeletePet;
	private JButton buttonNewPet;
	private JButton buttonSavePet;
	private JButton buttonPrevious;
	private JButton buttonNext;

	public PetGUI(PetController controller) {
		this.controller = controller;
		init();
	}

	private void createAndShowGUI() {
		JFrame frame = new JFrame("Pets");
		JPanel panel = new JPanel(new SpringLayout());

		// button click listener
		ActionListener buttonListener = new ButtonListener();
		// value field change listener
		EventListener valueListener = new ValueListener();

		// controller
		JLabel labelController = new JLabel(this.controller.getClass().getSimpleName());
		panel.add(labelController);
		// spacer
		panel.add(new JLabel(""));
		
		// operation buttons
		// load pets
		buttonLoadPets = new JButton("load pets");
		buttonLoadPets.setVerticalAlignment(AbstractButton.CENTER);
		buttonLoadPets.setMnemonic(KeyEvent.VK_L);
		buttonLoadPets.setActionCommand(ACTION_LOAD_PETS);
		buttonLoadPets.addActionListener(buttonListener);
		panel.add(buttonLoadPets);
		// delete pet
		buttonDeletePet = new JButton("delete pet");
		buttonDeletePet.setVerticalAlignment(AbstractButton.CENTER);
		buttonDeletePet.setMnemonic(KeyEvent.VK_D);
		buttonDeletePet.setActionCommand(ACTION_DELETE_PET);
		buttonDeletePet.addActionListener(buttonListener);
		panel.add(buttonDeletePet);
		// new pet
		buttonNewPet = new JButton();
		buttonNewPet.setVerticalAlignment(AbstractButton.CENTER);
		buttonNewPet.addActionListener(buttonListener);
		panel.add(buttonNewPet);
		// save pet
		buttonSavePet = new JButton("save pet");
		buttonSavePet.setVerticalAlignment(AbstractButton.CENTER);
		buttonSavePet.setMnemonic(KeyEvent.VK_S);
		buttonSavePet.setActionCommand(ACTION_SAVE_PET);
		buttonSavePet.addActionListener(buttonListener);
		panel.add(buttonSavePet);

		// ID
		JLabel labelID = new JLabel("ID: ", JLabel.TRAILING);
		valueID = new JLabel("<do something>");
		labelID.setLabelFor(valueID);
		panel.add(labelID);
		panel.add(valueID);

		// species
		JLabel labelSpecies = new JLabel("Species: ", JLabel.TRAILING);
		valueSpecies = new JComboBox<>(Pet.Species.values());
		valueSpecies.addActionListener((ActionListener) valueListener);
		labelSpecies.setLabelFor(valueSpecies);
		panel.add(labelSpecies);
		panel.add(valueSpecies);

		// gender
		JLabel labelGender = new JLabel("Gender: ", JLabel.TRAILING);
		valueGender = new JComboBox<>(Pet.Gender.values());
		valueGender.addActionListener((ActionListener) valueListener);
		labelGender.setLabelFor(valueGender);
		panel.add(labelGender);
		panel.add(valueGender);

		// name
		JLabel labelName = new JLabel("Name: ", JLabel.TRAILING);
		valueName = new JTextField();
		valueName.getDocument().addDocumentListener((DocumentListener) valueListener);
		labelName.setLabelFor(valueName);
		panel.add(labelName);
		panel.add(valueName);

		// navigation buttons
		// previous
		buttonPrevious = new JButton("< previous");
		buttonPrevious.setVerticalAlignment(AbstractButton.CENTER);
		buttonPrevious.setMnemonic(KeyEvent.VK_LEFT);
		buttonPrevious.setActionCommand(ACTION_PREVIOUS);
		buttonPrevious.addActionListener(buttonListener);
		panel.add(buttonPrevious);
		// next
		buttonNext = new JButton("next >");
		buttonNext.setVerticalAlignment(AbstractButton.CENTER);
		buttonNext.setMnemonic(KeyEvent.VK_RIGHT);
		buttonNext.setActionCommand(ACTION_NEXT);
		buttonNext.addActionListener(buttonListener);
		panel.add(buttonNext);

		// grid layout
		SpringUtilities.makeCompactGrid(panel,
				8, 2, // rows, columns
				6, 6, // initX, initY
				6, 6); // xPad, yPad

		// window options
		panel.setOpaque(true);
		frame.setContentPane(panel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		loadPets();
	}

	private void resetFields() {
		boolean canEdit = order.size() > 0 || addingNew;

		// ensure load pets is enabled
		buttonLoadPets.setEnabled(true);
		// reset new pet button
		buttonNewPet.setText("new pet");
		buttonNewPet.setMnemonic(KeyEvent.VK_N);
		buttonNewPet.setActionCommand(ACTION_NEW_PET);
		// always disable save button -> never a pet loaded yet
		buttonSavePet.setEnabled(false);

		// clear values
		valueSpecies.setSelectedItem(null);
		valueSpecies.setEnabled(canEdit);

		valueGender.setSelectedItem(null);
		valueGender.setEnabled(canEdit);

		valueName.setText("");
		valueName.setEnabled(canEdit);

		buttonPrevious.setEnabled(false);
		buttonNext.setEnabled(canEdit);

		updateButtons();
	}

	private void loadPets() {
		// reset ordering
		order.clear();
		previous = null;
		current = null;
		next = null;

		// load all pets
		pets = controller.getAllPets();
		// setup ordering
		// get all IDs from the pets
		order.addAll(pets.stream()
				.map(p -> p.getID())
				.collect(Collectors.toCollection(LinkedList::new)));
		if (order.size() > 0) {
			next = order.get(0);
			index = -1;
		} else {
			valueID.setText("<no pets found>");
		}

		debug(new String[] {
			"--- load pets ---",
			"pets: " + pets.size()
		});

		resetFields();
		loadNextPet();
	}

	private void loadPet(String ID) {
		// get pet from ID
		Pet pet = pets.stream()
				.filter(p -> p.getID() == ID)
				.findFirst().orElse(null);
		
		// check if pet was found in list
		if (pet != null) {

			// set values
			valueID.setText(pet.getID());
			valueSpecies.setSelectedItem(pet.getSpecies());
			valueGender.setSelectedItem(pet.getGender());
			valueName.setText(pet.getName());
	
			// update current pet (only needed if loading directly and not via previous or next buttons)
			current = ID;
		} else {
			System.err.println("could not find pet with ID: " + ID);
		}

		updateOrder();
		updateButtons();

		debug(new String[] {
			"--- load pet ---",
			"index: " + index,
			"previous: " + previous,
			"current: " + current,
			"next: " + next
		});
	}

	private void newPet() {
		addingNew = true;

		resetFields();

		// disable button
		buttonLoadPets.setEnabled(false);
		buttonDeletePet.setEnabled(false);
		buttonPrevious.setEnabled(false);
		buttonNext.setEnabled(false);

		// use new pet button as cancel button
		buttonNewPet.setText("cancel");
		buttonNewPet.setMnemonic(KeyEvent.VK_C);
		buttonNewPet.setActionCommand(ACTION_CANCEL);

		valueID.setText("<new pet>");
	}

	private void cancel() {
		addingNew = false;
		loadPets();
	}

	private void savePet() {
		Pet pet;

		// get values
		Pet.Species species = (Pet.Species) valueSpecies.getSelectedItem();
		Pet.Gender gender = (Pet.Gender) valueGender.getSelectedItem();
		String name = valueName.getText();

		debug(new String[] {
			"--- save pet ---",
			"ID: " + valueID.getText(),
			"species " + species,
			"gender: " + gender,
			"name: " + name
		});

		// create pet with or without generating new ID
		if (addingNew) {
			pet = new Pet(species, gender, name);
		} else {
			pet = new Pet(current, species, gender, name);
		}

		// save pet
		String ID = controller.savePet(pet, addingNew);
		// load pets
		loadPets();
		// load saved pet
		loadPet(ID);

		addingNew = false;
	}

	private void deletePet() {
		if (current != null) {
			controller.deletePet(current);
			loadPets();
		}
	}

	private void updateOrder() {
		// set index to current pet
		index = order.indexOf(current);

		// check if has new previous
		int indexPrevious = index - 1;
		previous = indexPrevious >= 0 ? order.get(indexPrevious) : null;
		// check if has new next
		int indexNext = index + 1;
		next = order.size() > indexNext ? order.get(indexNext) : null;
	}

	private void updateButtons() {
		buttonDeletePet.setEnabled(current != null);
		// enable / disable buttons depending on having previous and/or next
		buttonPrevious.setEnabled(previous != null);
		buttonNext.setEnabled(next != null);
	}

	private void loadPreviousPet() {
		if (previous != null) {
			// set new current
			current = previous;
			loadPet(current);
		}
	}

	private void loadNextPet() {
		if (next != null) {
			// set new next
			current = next;
			loadPet(current);
		}
	}

	private void init() {
		// initialize order to check size on layout building
		order = new ArrayList<>();
		// set default value to prevent saving existing pets as new pets
		addingNew = false;

		// set system look and feel
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

		// setup window
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}

	private void debug(String[] lines) {
		for (String line : lines) {
			System.out.println(line);
		}
	}

	private class ButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			switch (e.getActionCommand()) {
			case ACTION_LOAD_PETS:
				loadPets();
				break;
			case ACTION_DELETE_PET:
				deletePet();
				break;
			case ACTION_NEW_PET:
				newPet();
				break;
			case ACTION_CANCEL:
				cancel();
				break;
			case ACTION_SAVE_PET:
				savePet();
				break;
			case ACTION_PREVIOUS:
				loadPreviousPet();
				break;
			case ACTION_NEXT:
				loadNextPet();
				break;
			}
		}
	}

	private class ValueListener implements ActionListener, DocumentListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			checkValues();
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			checkValues();
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			checkValues();
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			checkValues();
		}

		private void checkValues() {
			buttonSavePet.setEnabled(valueSpecies.getSelectedItem() != null
					&& valueGender.getSelectedItem() != null
					&& valueName.getText().length() > 0);
		}
	}
}
