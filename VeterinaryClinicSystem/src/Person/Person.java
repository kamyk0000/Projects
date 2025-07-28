package Person;

import Base.ObjectPlusPlus;
import java.time.LocalDate;
import java.util.List;

public class Person extends ObjectPlusPlus {
    private String firstName;
    private String secondName;
    private String lastName;
    private String address;
    private String phone;

    private boolean isIntern;
    private boolean isVet;
    private boolean isOwner;

    // Roles
    public enum PersonOwnerRoles { Is_Owner }
    public enum PersonVetRoles { Is_Vet }
    public enum PersonInternRoles { Is_Intern }

    static {
        ObjectPlusPlus.addRoleName(Person.class, Owner.class, PersonOwnerRoles.Is_Owner, Owner.OwnerPersonRoles.Is_Person);
        ObjectPlusPlus.addRoleName(Person.class, Vet.class, PersonVetRoles.Is_Vet, Vet.VetPersonRoles.Is_Person);
        ObjectPlusPlus.addRoleName(Person.class, Intern.class, PersonInternRoles.Is_Intern, Intern.InternPersonRoles.Is_Person);
    }

    // Constructors
    public static Person create(String firstName, String lastName, String address, String phone) throws Exception {
        return create(firstName, null, lastName, address, phone);
    }

    public static Person create(String firstName, String secondName, String lastName, String address, String phone) throws Exception {
        if (checkFirstName(firstName) && checkLastName(lastName) && checkAddress(address) && checkPhone(phone)) {
            return new Person(firstName, secondName, lastName, address, phone);
        }
        return null;
    }

    private Person(String firstName, String secondName, String lastName, String address, String phone) {
        super();
        this.firstName = firstName;
        this.secondName = (secondName!=null && secondName.isEmpty()) ? null : secondName;
        this.lastName = lastName;
        this.address = address;
        this.phone = phone;
    }

    // Methods

    /**
     * Show detailed information about the person.
     */
    public void showInfo() throws Exception {
        System.out.println(this + "\n" + address + "\n" + phone);
        if (isIntern) {
            getIntern().showInfo();
        }
        if (isVet) {
            getVet().showInfo();
        }
        if (isOwner) {
            getOwner().showInfo();
        }
    }

    /**
     * Validates all parameters for creating a Person.
     */
    private static boolean isValid(String firstName, String lastName, String address, String phone) {
        return checkFirstName(firstName) && checkLastName(lastName) && checkAddress(address) && checkPhone(phone);
    }

    @Override
    public String toString(){
        return getFirstName() + " " + getSecondName() + getLastName();
    }

    /**
     * Creates and links an Intern to the Person.
     */
    public Intern linkIntern(double internshipHours, double hoursWorked) throws Exception {
        if (isIntern) {
            throw new Exception("This Person is already a Intern!");
        }
        if (isVet) {
            throw new Exception("This Person is a Vet, a Vet cannot become an Intern!");
        }
        Intern intern = Intern.create(internshipHours, hoursWorked, 0);
        addPart(PersonInternRoles.Is_Intern, intern);
        isIntern = true;
        return intern;
    }

    /**
     * Creates and links an Vet to the Person.
     */
    public Vet linkVet(String specialization, LocalDate employmentDate) throws Exception {
        return linkVet(List.of(specialization), employmentDate);
    }

    public Vet linkVet(List<String> specialization, LocalDate employmentDate) throws Exception {
        if (isIntern) {
            throw new Exception("This Person is an Intern, if you want to make them a Vet use makeInternIntoVet() method!");
        }
        if (isVet) {
            throw new Exception("This Person is already a Vet!");
        }
        Vet vet = Vet.create(specialization, employmentDate);
        addPart(PersonVetRoles.Is_Vet, vet);
        isVet = true;
        return vet;
    }

    /**
     * Creates and links an Owner to the Person.
     */
    public Owner linkOwner(String NIP, boolean isBreeder) throws Exception {
        if (isOwner) {
            throw new Exception("This Person is already an Owner!");
        }
        Owner owner = Owner.create(NIP, isBreeder);
        addPart(PersonOwnerRoles.Is_Owner, owner);
        isOwner = true;
        return owner;
    }

    /**
     * Example of dynamic inheritance; Promotes an Intern to a Vet.
     */
    public Vet makeInternIntoVet(String specialization) throws Exception {
        if (isVet) {
            throw new Exception("This person is already a vet!");
        }
        if (!isIntern) {
            throw new Exception("This person is not an intern!");
        }
        if (specialization == null || specialization.isEmpty()) {
            throw new Exception("This intern must have a specialization to become a vet!");
        }
        Intern intern = getIntern();
        removeLink(PersonInternRoles.Is_Intern, intern);
        Vet vet = Vet.create(specialization, LocalDate.now());
        addPart(PersonVetRoles.Is_Vet, vet);
        isVet = true;
        isIntern = false;
        return vet;
    }

    // Intern methods
    public void showEstimatedInternshipEndDate() throws Exception {
        if (!isIntern) {
            throw new Exception("This person is not an intern!");
        }
        getIntern().showEstimatedInternshipEndDate();
    }

    public void addHours(double hours) throws Exception {
        if (!isIntern) {
            throw new Exception("This person is not an intern!");
        }
        getIntern().addHours(hours);
    }

    // Vet methods
    public void showSchedule() throws Exception {
        if (!isVet) {
            throw new Exception("This person is not a vet!");
        }
        getVet().showSchedule();
    }

    public void addToSpecializations(String... specializations) throws Exception {
        if (!isVet) {
            throw new Exception("This person is not a vet!");
        }
        getVet().addToSpecializations(specializations);
    }

    public static void showAllSchedules() throws Exception {
        Vet.showAllSchedules();
    }

    // Owner methods
    public void showCurrentOwnedPets() throws Exception {
        if (!isOwner) {
            throw new Exception("This person is not an owner!");
        }
        getOwner().showCurrentOwnedPets();
    }

    public void showAllOwnedPets() throws Exception {
        if (!isOwner) {
            throw new Exception("This person is not an owner!");
        }
        getOwner().showAllOwnedPets();
    }

    // Getters & Setters
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        if (checkFirstName(firstName)) this.firstName = firstName;
    }

    private static boolean checkFirstName(String firstName) {
        if (firstName == null || firstName.isEmpty()) {
            throw new IllegalArgumentException("First name cannot be null or empty!");
        }
        return true;
    }

    public String getSecondName() {
        return (secondName == null) ? "" : secondName + " ";
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        if (checkLastName(lastName)) this.lastName = lastName;
    }

    private static boolean checkLastName(String lastName) {
        if (lastName == null || lastName.isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be null or empty!");
        }
        return true;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        if (checkAddress(address)) this.address = address;
    }

    private static boolean checkAddress(String address) {
        if (address == null || address.isEmpty()) {
            throw new IllegalArgumentException("Address cannot be null or empty!");
        }
        return true;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        if (checkPhone(phone)) this.phone = phone;
    }

    private static boolean checkPhone(String phone) {
        if (phone == null || phone.isEmpty()) {
            throw new IllegalArgumentException("Phone cannot be null or empty!");
        }
        return true;
    }

    public boolean isIntern() {
        return isIntern;
    }

    public boolean isVet() {
        return isVet;
    }

    public boolean isOwner() {
        return isOwner;
    }

    public Intern getIntern() throws Exception {
        return ((Intern)this.getLinks(PersonInternRoles.Is_Intern)[0]);
    }

    public Vet getVet() throws Exception {
        return ((Vet)this.getLinks(PersonVetRoles.Is_Vet)[0]);
    }

    public Owner getOwner() throws Exception {
        return ((Owner)this.getLinks(PersonOwnerRoles.Is_Owner)[0]);
    }
}

