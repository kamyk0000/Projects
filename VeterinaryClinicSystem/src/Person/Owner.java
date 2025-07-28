package Person;

import Base.ObjectPlus;
import Base.ObjectPlusPlus;
import OwnershipPeriod.OwnershipPeriod;
import Pet.Pet;
import java.time.LocalDate;
import java.util.List;

public class Owner extends ObjectPlusPlus {
    private String NIP;
    private boolean isBreeder;

    // Roles
    public enum OwnerOwnershipRoles { Owns_for, Owned_for }
    public enum OwnerPersonRoles { Is_Person }

    static {
        ObjectPlusPlus.addRoleName(Owner.class, Person.class, OwnerPersonRoles.Is_Person, Person.PersonOwnerRoles.Is_Owner);
        ObjectPlusPlus.addRoleName(Owner.class, OwnershipPeriod.class, OwnerOwnershipRoles.Owns_for, OwnershipPeriod.OwnershipOwnerRoles.Is_for);
        ObjectPlusPlus.addRoleName(Owner.class, OwnershipPeriod.class, OwnerOwnershipRoles.Owned_for, OwnershipPeriod.OwnershipOwnerRoles.Was_for);
    }

    // Constructors
    static Owner create(String NIP, boolean isBreeder) {
        return new Owner(NIP, isBreeder);
    }

    private Owner(String NIP, boolean isBreeder) {
        super();
        this.setNIP(NIP);
        this.setBreeder(isBreeder);
    }

    // Methods
    /**
     * Shows Owner specific information.
     */
    public void showInfo() {
        String nip = (NIP == null || NIP.isEmpty()) ? "" : ", " + NIP;
        String breeder = isBreeder ? ", Breeder" : "";
        System.out.println("Owner" + breeder + nip);
    }

    /**
     * Looks for and returns a specific OwnershipPeriod period for a specified Pet
     * @param pet Pet in question
     * @return Ownership in question
     */
    public OwnershipPeriod getOwnershipForPet(Pet pet) {
        try {
            List<OwnershipPeriod> ownershipPeriods = getLinksList(OwnerOwnershipRoles.Owns_for);
            for (OwnershipPeriod period : ownershipPeriods) {
                Pet petForPeriod = (Pet) period.getLinks(OwnershipPeriod.OwnershipPetRoles.Is_for)[0];
                if (pet.equals(petForPeriod)) {
                    return period;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    /**
     * Check if the pet belongs to the owner.
     */
    public boolean checkIfPetBelongsToOwner(Pet pet) {
        try {
            return getOwnershipForPet(pet) != null;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Ends a current ownership of a pet, and sets it to a past ownership with a current date.
     * @param pet Pet in question
     * @throws Exception
     */
    public void terminateOwnership(Pet pet) throws Exception {
        terminateOwnership(pet, null);
    }

    /**
     * Ends a current ownership of a pet, and sets it to a past ownership with a specified end date.
     * @param pet Pet in question
     * @param dateTo the date of ownership termination
     * @throws Exception
     */
    public void terminateOwnership(Pet pet, LocalDate dateTo) throws Exception {
        OwnershipPeriod period = getOwnershipForPet(pet);
        if (period != null) {
            pet.removeLink(Pet.PetOwnershipRoles.Belongs_for, period);
            this.removeLink(OwnerOwnershipRoles.Owns_for, period);
            period.setDateTo((dateTo == null) ? LocalDate.now() : dateTo);
            pet.addLink(Pet.PetOwnershipRoles.Belonged_for, period);
            this.addLink(OwnerOwnershipRoles.Owned_for, period);
        }
    }

    /**
     * Show currently owned pets.
     */
    public void showCurrentOwnedPets() {
        try {
            List<OwnershipPeriod> ownershipPeriods = getLinksList(OwnerOwnershipRoles.Owns_for);
            if (ownershipPeriods.isEmpty()) {
                System.out.println("No currently owned pets!");
                return;
            }
            System.out.println("Currently owned pets: ");
            for (OwnershipPeriod period : ownershipPeriods) {
                for (ObjectPlus pet : period.getLinks(OwnershipPeriod.OwnershipPetRoles.Is_for)) {
                    System.out.println(pet);
                }
            }
        } catch (Exception e) {
            System.out.println("No currently owned pets!");
        }
    }

    /**
     * Show all owned pets (current and past).
     */
    public void showAllOwnedPets() {
        showCurrentOwnedPets();
        try {
            List<OwnershipPeriod> ownershipPeriods = getLinksList(OwnerOwnershipRoles.Owned_for);
            if (ownershipPeriods.isEmpty()) {
                System.out.println("No previous owned pets found!");
                return;
            }
            System.out.println("Previously owned pets: ");
            for (OwnershipPeriod period : ownershipPeriods) {
                for (ObjectPlus pet : period.getLinks(OwnershipPeriod.OwnershipPetRoles.Was_for)) {
                    System.out.println(pet);
                }
            }
        } catch (Exception e) {
            System.out.println("No previous owned pets found!");
        }
    }

    @Override
    public String toString(){
        String personInfo = "";
        try {
            personInfo = getPerson().toString();
        } catch (Exception ignored){}
        return "[O]: " + personInfo;
    }

    // Getters & Setters
    public Person getPerson() throws Exception {
        return ((Person)this.getLinks(OwnerPersonRoles.Is_Person)[0]);
    }

    public String getNIP() {
        return NIP;
    }

    public void setNIP(String NIP) {
        this.NIP = NIP;
    }

    public boolean isBreeder() {
        return isBreeder;
    }

    public void setBreeder(boolean breeder) {
        isBreeder = breeder;
    }
}
