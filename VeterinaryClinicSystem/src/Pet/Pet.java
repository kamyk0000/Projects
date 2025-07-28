package Pet;

import Appointment.Appointment;
import Base.*;
import OwnershipPeriod.OwnershipPeriod;
import Person.Owner;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class Pet extends ObjectPlusPlus {
    private String name;
    private String species;
    private String breed;
    private char gender;
    private LocalDate birthDate;
    public int age() {
        return LocalDate.now().getYear() - birthDate.getYear();
    };
    private static Set<String> allChips;
    private String chipID;

    // Roles
    public enum PetOwnershipRoles { Belongs_for, Belonged_for }
    public enum PetAppointmentRoles { Has }

    static {
        ObjectPlusPlus.addRoleName(Pet.class, OwnershipPeriod.class, PetOwnershipRoles.Belongs_for, OwnershipPeriod.OwnershipPetRoles.Is_for);
        ObjectPlusPlus.addRoleName(Pet.class, OwnershipPeriod.class, PetOwnershipRoles.Belonged_for, OwnershipPeriod.OwnershipPetRoles.Was_for);
    }

    // Constructors
    public static Pet create(String name, String species, String breed, char gender, LocalDate birthDate, Owner owner) {
        return create(name, species, breed, gender, birthDate, null, owner);
    }

    public static Pet create(String name, String species, String breed, char gender, LocalDate birthDate, String chipID, Owner owner) {
        if (isValid(name, species, breed, gender, birthDate, chipID, owner)) {
            return new Pet(name, species, breed, gender, birthDate, chipID, owner);
        }
        return null;
    }

    private Pet(String name, String species, String breed, char gender, LocalDate birthDate, String chipID, Owner owner) {
        super();
        this.name = name;
        this.species = species;
        this.breed = breed;
        this.gender = gender;
        this.birthDate = birthDate;
        this.setChipID(chipID);
        try {
            this.linkOwner(owner);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Methods
    /**
     * Remove all links and delete the pet.
     */
    public Pet remove() throws Exception {
        removeOwners(PetOwnershipRoles.Belongs_for, OwnershipPeriod.OwnershipOwnerRoles.Is_for);
        removeOwners(PetOwnershipRoles.Belonged_for, OwnershipPeriod.OwnershipOwnerRoles.Was_for);
        removeAppointments();
        removeFromExtent(this);
        return null;
    }

    /**
     * Remove all links of owners and ownership periods.
     */
    private void removeOwners(Enum<?> petRole, Enum<?> ownershipRole) throws Exception {
        try {
            for (ObjectPlusPlus ownership : getLinks(petRole)) {
                for (ObjectPlusPlus owner : ownership.getLinks(ownershipRole)) {
                    ownership.removeLink(ownershipRole, owner);
                }
                removeLink(petRole, ownership);
                removeFromExtent(ownership);
            }
        } catch (Exception e) {
            System.out.println("No links found for role: " + petRole + ", continuing! " + e.getMessage());
        }
    }

    /**
     * Removes all appointment links (parts).
     */
    private void removeAppointments() throws Exception {
        try {
            for (ObjectPlusPlus appointment : getLinks(PetAppointmentRoles.Has)) {
                ((Appointment) appointment).remove();
            }
        } catch (Exception e) {
            System.out.println("No appointment links found, continuing! " + e.getMessage());
        }
    }

    /**
     * Links the pet to an owner and an ownership period.
     */
    private void linkOwner(Owner owner, OwnershipPeriod ownership) throws Exception {
        addLink(PetOwnershipRoles.Belongs_for, ownership);
        owner.addLink(Owner.OwnerOwnershipRoles.Owns_for, ownership);
    }

    /**
     * Links the pet to an owner and a specified ownership period.
     */
    public void linkOwner(Owner owner, LocalDate dateFrom, LocalDate dateTo) throws Exception {
        if (owner == null) {
            throw new Exception("Owner cannot be null");
        }
        if (owner.checkIfPetBelongsToOwner(this)) {
            throw new Exception("This owner is already linked to the pet");
        }
        OwnershipPeriod ownership = OwnershipPeriod.create(dateFrom, dateTo);
        linkOwner(owner, ownership);
    }

    /**
     * Links the pet to an owner with a default start date of now.
     */
    public void linkOwner(Owner owner) throws Exception {
        linkOwner(owner, LocalDate.now(), null);
    }

    /**
     * Links the pet to an owner with a specified start date.
     */
    public void linkOwner(Owner owner, LocalDate dateFrom) throws Exception {
        linkOwner(owner, dateFrom, null);
    }

    /**
     * Validates all parameters for creating a Pet.
     */
    private static boolean isValid(String name, String species, String breed, char gender, LocalDate birthDate, String chipID, Owner owner) {
        return checkName(name) && checkSpecies(species) && checkBreed(breed) && checkGender(gender) && checkBirthDate(birthDate) && checkChipID(chipID) && owner != null;
    }

    /**
     * Special logic to save static fields
     */
    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject(); // Serialize non-static fields

        // Serialize static fields manually
        oos.writeObject(allChips);
    }

    /**
     * Special logic to load static fields
     */
    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject(); // Deserialize non-static fields

        // Deserialize static fields manually
        allChips = (Set<String>) ois.readObject();
    }

    @Override
    public String toString(){
        String chip = (getChipID() == null ? "" : ("[" + getChipID()) + "] ");
        return chip + getName() + ", " + getSpecies() + " " + getBreed() + " [" + getGender() + "], " + getBirthDate();
    }

    // Setters & Getters
    public static Set<String> getAllChipIDs() {
        if (allChips == null) {
            allChips = new HashSet<>();
        }
        return allChips;
    }

    public static void setAllChipIDs(Set<String> allChips) {
        Pet.allChips = allChips;
    }

    public String getChipID() {
        return chipID;
    }

    public void setChipID(String chipID) {
        if (this.chipID == null)
            this.setChipIDInitial(chipID);
        else throw new IllegalArgumentException("Pet already has a chip ID");
    }

    private void setChipIDInitial(String chipID) {
         if (checkChipID(chipID) && chipID != null) {
             allChips.add(chipID);
         }
         this.chipID = chipID;
    }

    private static boolean checkChipID(String chipID) {
        if (allChips == null) {
            allChips = new HashSet<>();
            }
        if (chipID != null) {
            if (allChips.contains(chipID)) {
                throw new IllegalArgumentException("Duplicate chip ID: " + chipID);
            }
        }
        return true;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        if (checkBirthDate(birthDate)) this.birthDate = birthDate;
    }

    private static boolean checkBirthDate(LocalDate birthDate) {
        if (birthDate == null) {
            throw new IllegalArgumentException("Birthdate cannot be empty");
        } else if (birthDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Birthdate cannot be after current date");
        }
        return true;
    }

    public char getGender() {
        return gender;
    }

    public void setGender(char gender) {
        if(checkGender(gender)) this.gender = gender;
    }

    private static boolean checkGender(char gender) {
        if (!(gender == 'M' || gender == 'F')) {
            throw new IllegalArgumentException("Gender must be 'M' for male or 'F' for female");
        }
        return true;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        if (checkBreed(breed)) this.breed = breed;
    }

    private static boolean checkBreed(String breed) {
        if (breed == null || breed.isEmpty()) {
            throw new IllegalArgumentException("Breed has to be specified");
        }
        return true;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        if (checkSpecies(species)) this.species = species;
    }

    private static boolean checkSpecies(String species) {
        if (species == null || species.isEmpty()) {
            throw new IllegalArgumentException("Species has to be specified");
        }
        return true;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (checkName(name)) this.name = name;
    }

    private static boolean checkName(String name){
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        return true;
    }
}
