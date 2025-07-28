import Appointment.Appointment;
import Base.ObjectPlus;
import MedicalProcedure.ByLocation.InClinic;
import MedicalProcedure.ByLocation.InField;
import MedicalProcedure.ByLocation.ProcedureLocation;
import MedicalProcedure.ByType.Immunotherapy;
import MedicalProcedure.ByType.Other;
import MedicalProcedure.ByType.TherapeuticVaccination;
import MedicalProcedure.ByType.Vaccination;
import MedicalProcedure.MedicalProcedure;
import OwnershipPeriod.OwnershipPeriod;
import Person.*;
import Pet.Pet;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

public class Tests {
    public static void main(String[] args) throws Exception {
        System.out.println("Test class");
        testDynamicAndOverlappingPersonInheritance();
        testAttributeAssociationOfOwnersAndPets();
        testAspectAndMultiInheritanceFromAbstractMedicalProcedure();
        testAppointmentClassManipulation();
        testUniqueAttributesSerialization();
        testWholePetPlusPartAppointmentsDeletion();
    }

    /**
     * Overlapping inheritance (as a composition): (Vet <-> Owner OR Intern <-> Owner) XOR Vet <> Intern
     * Tests creating Person objects and child class object creations
     * @throws Exception
     */
    public static void testDynamicAndOverlappingPersonInheritance() throws Exception {
        System.out.println("=========== testDynamicAndOverlappingPersonInheritance ===========");
        //ObjectPlus.readExtents("tests.json");
        // Correct Person creation
        Person person1 = Person.create("John", "Doe", "template, 00-000, address", "XXX-XXX-XXX");
        Person person2 = Person.create("Denise", "John", "template, 00-000, address", "XXX-XXX-XXX");
        // Incorrect Person creation
        try {Person personX = Person.create(null, null, null, null);}
        catch (Exception e) {System.out.println("Exception in Person creation: " + e.getMessage());}
        ObjectPlus.showExtent(Person.class);

        // Correct Vet creation and linking to Person
        Vet vet1 = person1.linkVet(List.of("Surgeon", "Biologist"), LocalDate.now().minusYears(1));

        // Incorrect Vet creation
        try {Vet vetX = person1.linkVet("Surgeon", LocalDate.now().minusYears(3));}
        catch (Exception e) {System.out.println("Exception in Vet creation: " + e.getMessage());}
        try {Vet vetX = person2.linkVet("Surgeon", LocalDate.now().plusMonths(3));}
        catch (Exception e) {System.out.println("Exception in Vet creation: " + e.getMessage());}
        try {Vet vetX = person2.linkVet("", null);}
        catch (Exception e) {System.out.println("Exception in Vet creation: " + e.getMessage());}
        ObjectPlus.showExtent(Vet.class);

        // Correct Intern creation and linking to Person
        Intern intern1 = person2.linkIntern(160, -1);

        // Incorrect Intern creation
        try {Intern internX = person2.linkIntern(250, 10);}
        catch (Exception e) {System.out.println("Exception in Intern creation: " + e.getMessage());}
        try {Intern internX = person1.linkIntern(250, 10);}
        catch (Exception e) {System.out.println("Exception in Intern creation: " + e.getMessage());}
        try {Vet vetX = person2.linkVet("Surgeon", LocalDate.now().minusYears(3));}
        catch (Exception e) {System.out.println("Exception in Intern creation: " + e.getMessage());}
        ObjectPlus.showExtent(Intern.class);

        // Correct Owner creation and linking to Person
        Owner owner1 = person1.linkOwner(null, false);
        Owner owner2 = person2.linkOwner("111-231-211234", true);

        // Incorrect Owner creation
        try {Owner ownerX = person1.linkOwner(null, false);}
        catch (Exception e) {System.out.println("Exception in Owner creation: " + e.getMessage());}
        try {Owner ownerX = person2.linkOwner(null, false);}
        catch (Exception e) {System.out.println("Exception in Owner creation: " + e.getMessage());}
        ObjectPlus.showExtent(Owner.class);

        // Correct promoting Intern to Vet
        ObjectPlus.showExtent(Vet.class);
        ObjectPlus.showExtent(Intern.class);
        Vet vet2 = intern1.getPerson().makeInternIntoVet("Surgeon");
        ObjectPlus.showExtent(Vet.class);
        ObjectPlus.showExtent(Intern.class);

        // Incorrect promoting Intern to Vet
        try {intern1.makeInternIntoVet("Surgeon");}
        catch (Exception e) {System.out.println("Exception in Intern promotion: " + e.getMessage());}
        try {vet1.getPerson().makeInternIntoVet("Surgeon");}
        catch (Exception e) {System.out.println("Exception in Intern promotion: " + e.getMessage());}
        try {ObjectPlus.showExtent(Intern.class);}
        catch (Exception e) {System.out.println("No Intern extent: " + e.getMessage());}
        ObjectPlus.showExtent(Vet.class);

        ObjectPlus.writeExtents("tests.json");
        System.out.println("================================================================\n");
    }

    /**
     * Attribute association: Pet <-> Owner, with attribute table: OwnershipPeriod, with two associations (past and present)
     * Tests creating Pet objects and linking to Owner objects with attribute table OwnershipPeriod
     * @throws Exception
     */
    public static void testAttributeAssociationOfOwnersAndPets() throws Exception {
        System.out.println("=========== testAttributeAssociationOfOwnersAndPets ===========");
        ObjectPlus.readExtents("tests.json");
        Owner owner1 = ObjectPlus.getExtent(Owner.class).get(0);
        Owner owner2 = ObjectPlus.getExtent(Owner.class).get(1);

        // Correct Pet creation
        Pet pet1 = Pet.create("Kitka", "Kot", "Środkowoeuropejski", 'F', LocalDate.now(), "O2W34A", owner1);

        // Incorrect Pet creation
        try {Pet petX = Pet.create("Kitka", "Kot", "Środkowoeuropejski", 'F', LocalDate.now().plusDays(10), owner1);}
        catch (Exception e) {System.out.println("Exception in Pet creation: " + e.getMessage());}
        try {Pet petX = Pet.create(null, null, null, 'X', null, null);}
        catch (Exception e) {System.out.println("Exception in Pet creation: " + e.getMessage());}
        ObjectPlus.showExtent(Pet.class);
        ObjectPlus.showExtent(OwnershipPeriod.class);

        // Correct additional Owner linking
        pet1.linkOwner(owner2, LocalDate.now().minusYears(1));

        owner2.showAllOwnedPets();

        // Incorrect additional Owner linking
        try {pet1.linkOwner(owner1);}
        catch (Exception e) {System.out.println("Exception in Owner linking: " + e.getMessage());}
        try {pet1.linkOwner(null);}
        catch (Exception e) {System.out.println("Exception in Owner linking: " + e.getMessage());}
        try {pet1.linkOwner(owner2, LocalDate.now().plusYears(1));}
        catch (Exception e) {System.out.println("Exception in Owner linking: " + e.getMessage());}
        ObjectPlus.showExtent(OwnershipPeriod.class);

        owner2.showAllOwnedPets();

        // Correct Ownership termination
        owner2.terminateOwnership(pet1);

        // Incorrect Ownership termination
        try {owner2.terminateOwnership(pet1);}
        catch (Exception e) {System.out.println("Exception in Owner unlinking: " + e.getMessage());}
        ObjectPlus.showExtent(OwnershipPeriod.class);

        owner2.showAllOwnedPets();

        ObjectPlus.writeExtents("tests.json");
        System.out.println("================================================================\n");
    }

    /**
     * Many-Aspect Inheritance: InClinic, InField (by localization) -> MedicalProcedure <- (by type) Other, Immunotherapy, Vaccine, etc.
     * Multi Inheritance: Immunotherapy <- TherapeuticImmunology -> Vaccination
     * Tests inheritances of MedicalProcedure objects and classes
     * @throws Exception
     */
    public static void testAspectAndMultiInheritanceFromAbstractMedicalProcedure() throws Exception {
        System.out.println("========= testAspectAndMultiInheritanceFromAbstractMedicalProcedure =========");
        ObjectPlus.readExtents("tests.json");

        // Correct Procedure creation
        Other other1 = Other.create("Checkup", "OC01", 150, LocalTime.of(1, 0), "A simple checkup", 0);
        Immunotherapy immunotherapy1 = Immunotherapy.create(other1, "IU01", "Unspecified", "Unspecified");
        Vaccination vaccination1 = Vaccination.create("Vaccine for HPV", "HPV1", 100, LocalTime.of(0, 20), "HPV-16", "Made by Taruna", 50 );

        // Incorrect Procedure creation
        try {Other.create(null, null, -1, null, null, -1);}
        catch (Exception e) {System.out.println("Exception in Medical Procedure creation: " + e.getMessage());}
        try {Immunotherapy.create(immunotherapy1, "IU01", "Unspecified", "Unspecified");}
        catch (Exception e) {System.out.println("Exception in Medical Procedure creation: " + e.getMessage());}
        ObjectPlus.showExtent(Other.class);
        ObjectPlus.showExtent(Immunotherapy.class);
        ObjectPlus.showExtent(Vaccination.class);

        // Correct TherapeuticVaccination (Multiclass inheritance object) creation
        TherapeuticVaccination therapeuticVaccination1 = TherapeuticVaccination.create("Prophylactic Rabies vaccine", "RAB1", 150, LocalTime.of(0, 15), "Rabies", "Yearly doses", "Rabies virus", "Made by Mordena", 20, 360);
        TherapeuticVaccination therapeuticVaccination2 = TherapeuticVaccination.create(vaccination1, "HPV2",  "HPV", "Yearly doses", 60);
        TherapeuticVaccination therapeuticVaccination3 = TherapeuticVaccination.create(immunotherapy1, "IM01",  "Unspecified", "Unspecified", 0, 0);
        TherapeuticVaccination therapeuticVaccination4 = TherapeuticVaccination.create(immunotherapy1, vaccination1, "IVA1", 160);

        // Incorrect TherapeuticVaccination (Multiclass inheritance object) creation
        try {TherapeuticVaccination.create(null, null, null,  -1);}
        catch (Exception e) {System.out.println("Exception in Medical Procedure creation: " + e.getMessage());}
        try {TherapeuticVaccination.create(immunotherapy1, vaccination1, "IVA1", 160);}
        catch (Exception e) {System.out.println("Exception in Medical Procedure creation: " + e.getMessage());}
        ObjectPlus.showExtent(TherapeuticVaccination.class);

        // Correct aspect addition
        ProcedureLocation pl1 = InClinic.create(0.1, therapeuticVaccination1);
        ProcedureLocation pl2 = InField.create("template, 00-000, address", 10, true, other1);

        // Incorrect aspect addition
        try {InField.create(null, therapeuticVaccination3);}
        catch (Exception e) {System.out.println("Exception in Procedure Location creation: " + e.getMessage());}
        ObjectPlus.showExtent(InClinic.class);
        ObjectPlus.showExtent(InField.class);

        ObjectPlus.writeExtents("tests.json");
        System.out.println("================================================================\n");
    }

    public static void testAppointmentClassManipulation() throws Exception {
        System.out.println("============== testAppointmentClassManipulation ==============");
        ObjectPlus.readExtents("tests.json");
        Pet pet1 = ObjectPlus.getExtent(Pet.class).get(0);
        Vet vet1 = ObjectPlus.getExtent(Vet.class).get(0);
        Vet vet2 = ObjectPlus.getExtent(Vet.class).get(1);
        Person person1 = Person.create("Vanessa", "Image", "template, 00-000, address", "XXX-XXX-XXX");
        Intern intern1 = person1.linkIntern(0, 100);
        MedicalProcedure mp1 = ObjectPlus.getExtent(Other.class).get(0);
        MedicalProcedure mp2 = ObjectPlus.getExtent(Immunotherapy.class).get(0);
        MedicalProcedure mp3 = ObjectPlus.getExtent(Vaccination.class).get(0);

        // Correct Appointment creation
        Appointment ap1 = Appointment.create(pet1, mp1, vet2, LocalDateTime.now().plusDays(6), "Note!");
        Appointment ap2 = Appointment.create(pet1, List.of(mp1, mp2, mp3), List.of(vet2), intern1, LocalDateTime.now().plusDays(2).minusHours(4), "Note!!!!!!!!!");
        Appointment ap3 = Appointment.create(pet1, mp1, vet1, LocalDateTime.now().minusDays(6), "Note!");
        Appointment ap4 = Appointment.create(pet1, List.of(mp1, mp2, mp3), List.of(vet2), intern1, LocalDateTime.now().minusDays(6).minusHours(4), "Note!!!!!!!!!");

        // Incorrect Appointment creation including checking Vets/Intern availability
        try {Appointment.create(pet1, mp2, vet1, LocalDateTime.now().plusDays(6), "TempX");}
        catch (Exception e) {System.out.println("Exception in Appointment creation: " + e.getMessage());}
        try {Appointment.create(pet1, mp2, vet1, LocalDateTime.now().minusDays(6), "TempX");}
        catch (Exception e) {System.out.println("Exception in Appointment creation: " + e.getMessage());}
        try {Appointment.create(null, mp2, null, LocalDateTime.now().plusDays(6), null);}
        catch (Exception e) {System.out.println("Exception in Appointment creation: " + e.getMessage());}
        try {Appointment.create(pet1, List.of(mp1), List.of(vet1), intern1, LocalDateTime.now().plusDays(2).minusHours(5), "X");}
        catch (Exception e) {System.out.println("Exception in Appointment creation: " + e.getMessage());}
        ObjectPlus.showExtent(Appointment.class);

        Appointment.showRaport();
        Appointment.showAllSchedules();
        Appointment.showAllAppointmentsForPeriod(LocalDate.now().minusDays(3), LocalDate.now().plusDays(10));
        Appointment.showAllAppointmentsForDate(LocalDate.now().plusDays(2));

        // Correct Appointment reschedule
        ap1.reschedule(List.of(vet1), null, LocalDateTime.now().plusDays(10));

        // Incorrect Appointment reschedule
        try {ap1.reschedule(List.of(vet1), null, LocalDateTime.now().minusDays(2));}
        catch (Exception e) {System.out.println("Exception in Appointment rescheduling: " + e.getMessage());}
        try {ap1.reschedule(List.of(vet1), null, LocalDateTime.now().plusDays(2));}
        catch (Exception e) {System.out.println("Exception in Appointment rescheduling: " + e.getMessage());}

        Appointment.showAllAppointmentsForPeriod(LocalDate.now().plusDays(10), LocalDate.now().plusDays(20));

        ap4.printToFile();

        ObjectPlus.showExtent(Appointment.class);
        ap2.remove();
        ObjectPlus.showExtent(Appointment.class);
        Appointment.showRaport();

        ObjectPlus.writeExtents("tests.json");
        System.out.println("================================================================\n");
    }

    /**
     * Unique attributes: Pet -> ChipIDs, Appointment -> Deleted appointments, Medical Procedure -> codes
     * Tests loading and saving (serializing / deserializing) static attributes
     * @throws Exception
     */
    public static void testUniqueAttributesSerialization() throws Exception {
        System.out.println("============= testUniqueAttributesSerialization =============");
        ObjectPlus.readExtents("tests.json");

        System.out.println("Pet codes: " + Pet.getAllChipIDs());
        System.out.println("Deleted appointments: " + Appointment.getDeleted());
        System.out.println("Medical procedure codes: " + MedicalProcedure.getAllCodes());
        System.out.println("Clinic address: " + InClinic.getClinicAddress());

        Set<String> petCodes = Pet.getAllChipIDs();
        int deleted = Appointment.getDeleted();
        Set<String> procedureCodes = MedicalProcedure.getAllCodes();

        petCodes.add("TEST_CODE");
        deleted ++;
        procedureCodes.add("TEST_CODE");

        Pet.setAllChipIDs(petCodes);
        Appointment.setDeleted(deleted);
        MedicalProcedure.setAllCodes(procedureCodes);
        InClinic.setClinicAddress("Test address");

        ObjectPlus.writeExtents("tests.json");
        ObjectPlus.readExtents("tests.json");

        System.out.println("Pet codes: " + Pet.getAllChipIDs());
        System.out.println("Deleted appointments: " + Appointment.getDeleted());
        System.out.println("Medical procedure codes: " + MedicalProcedure.getAllCodes());
        System.out.println("Clinic address: " + InClinic.getClinicAddress());

        ObjectPlus.writeExtents("tests.json");
        System.out.println("================================================================\n");
    }
    /**
     * Whole <- Part composition: Pet <- Appointment
     * Tests creation and deletion (for garbage collector?) of part type objects
     * @throws Exception
     */
    public static void testWholePetPlusPartAppointmentsDeletion() throws Exception {
        System.out.println("============ testWholePetPlusPartAppointmentsDeletion ============");
        ObjectPlus.readExtents("tests.json");
        ObjectPlus.showExtent(Pet.class);
        ObjectPlus.showExtent(Appointment.class);

        Pet pet1 = ObjectPlus.getExtent(Pet.class).get(0);
        pet1 = pet1.remove();

        System.out.println("Pet after removing " + pet1);
        System.out.println("If Pet after removal is null, and all references have been deleted garbage collector can now delete the object entirely");
        try {ObjectPlus.showExtent(Pet.class);}
        catch (Exception e) {System.out.println("No pets in extents: " + e.getMessage());}
        try {ObjectPlus.showExtent(Appointment.class);}
        catch (Exception e) {System.out.println("No appointments in extents: " + e.getMessage());}


        ObjectPlus.writeExtents("tests.json");
        System.out.println("================================================================\n");
    }

}
