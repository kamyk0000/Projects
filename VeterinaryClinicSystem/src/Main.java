import Base.*;
import MedicalProcedure.ByLocation.*;
import MedicalProcedure.ByType.*;
import Person.*;
import Pet.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        String EXTENT_FILE_NAME = "extents.json";

        System.out.println("Hello world! This is repository creator!");

        Person person1 = Person.create("John", "Doe", "4234 Stanley Avenue, NY City", "706-279-1379");
        Person person2 = Person.create("Denise", "John", "4051 Holly Street, Dalton", "706-279-1379");
        Person person3 = Person.create("Vanessa", "Image", "394 Derek Drive, Medina", "330-391-8091");
        Person person4 = Person.create("Anthony", "Buck", "4708 Melm Street, Rhode Island", "401-262-7994");
        Person person5 = Person.create("Connor", "Webb", "395 Derek Drive, Medina", "330-461-4310");
        Person person6 = Person.create("Boden", "Lucero", "4408 Melm Street, Rhode Island", "401-639-2288");

        Vet vet1 = person1.linkVet(List.of("Surgeon", "Biologist"), LocalDate.now().minusYears(1));
        Vet vet2 = person2.linkVet(List.of("Surgeon", "Anaesthesiologist"), LocalDate.now().minusYears(1));
        Vet vet3 = person3.linkVet(List.of("Microbiologist", "Biologist"), LocalDate.now().minusYears(1));
        Vet vet4 = person4.linkVet(List.of("Surgeon"), LocalDate.now().minusYears(1));

        Intern intern1 = person5.linkIntern(160, -1);
        Intern intern2 = person6.linkIntern(160, 100);

        Owner owner1 = person1.linkOwner(null, false);
        Owner owner2 = person2.linkOwner("111-231-211234", true);
        Owner owner3 = person5.linkOwner(null, true);

        Pet pet1 = Pet.create("Dominguez", "Cat", "Persian", 'M', LocalDate.of(2010, 2, 14), owner1);
        Pet pet2 = Pet.create("Kaden", "Cat", "Maine Coon", 'F', LocalDate.of(2012, 5, 12), owner1);
        Pet pet3 = Pet.create("Dustin", "Dog", "Pug", 'M', LocalDate.of(2020, 6, 29), owner2);
        Pet pet4 = Pet.create("Devon", "Dog", "Golden Retriever", 'M', LocalDate.of(2000, 1, 3), owner1);
        Pet pet5 = Pet.create("Snow", "Cat", "Siamese", 'F', LocalDate.of(2015, 10, 16), owner1);
        Pet pet6 = Pet.create("Kirk", "Bird", "Parrot", 'F', LocalDate.of(2011, 12, 23), owner2);
        Pet pet7 = Pet.create("Enzo", "Lizard", "Gekko", 'M', LocalDate.of(2017, 3, 2), owner3);
        Pet pet8 = Pet.create("Pruitt", "Cow", "Common", 'F', LocalDate.of(2019, 7, 12), owner3);

        Other other1 = Other.create("Checkup", "OC01", 150, LocalTime.of(1, 0), "A simple checkup", 0);
        Other other2 = Other.create("Neuter", "NA01", 250, LocalTime.of(3, 0), "Standard neuter procedure", 50);
        Other other3 = Other.create("Fracture repair", "FR01", 150, LocalTime.of(0, 30), "Designed fracture restoration", 0);

        Immunotherapy immunotherapy1 = Immunotherapy.create(other1, "IU01", "Unspecified", "Unspecified");
        Immunotherapy immunotherapy2 = Immunotherapy.create("Cancer treatment", "CAN5", 500, LocalTime.of(2, 30), "Various cancer types", "Regular radiotherapy");

        Vaccination vaccination1 = Vaccination.create("Vaccine for HPV", "HPV1", 100, LocalTime.of(0, 20), "HPV-16", "Made by Taruna", 50 );
        Vaccination vaccination2 = Vaccination.create("Vaccine for Parvoviridae", "PAV1", 200, LocalTime.of(0, 20), "Parvoviridae bacteria", "Manufactured by Bazn", 10 );

        TherapeuticVaccination therapeuticVaccination1 = TherapeuticVaccination.create("Prophylactic Rabies vaccine", "RAB1", 150, LocalTime.of(0, 15), "Rabies", "Yearly doses", "Rabies virus", "Made by Mordena", 20, 360);
        TherapeuticVaccination therapeuticVaccination2 = TherapeuticVaccination.create(vaccination1, "HPV2",  "HPV", "Yearly doses", 60);
        TherapeuticVaccination therapeuticVaccination3 = TherapeuticVaccination.create(immunotherapy1, "IM01",  "Unspecified", "Unspecified", 0, 0);
        TherapeuticVaccination therapeuticVaccination4 = TherapeuticVaccination.create(immunotherapy2, vaccination2, "IVA1", 160);

        ProcedureLocation pl1 = InClinic.create(0.1, therapeuticVaccination1);
        ProcedureLocation pl2 = InField.create("template, 00-000, address", 10, true, other1);

        ObjectPlusPlus.writeExtents(EXTENT_FILE_NAME);
        System.out.println("It just works");
    }
}

/*

        Other procedure1 = Other.create("Other", "S123NA2", 100, LocalTime.of(1, 12), "Description", 50);
        Immunotherapy procedure2 = Immunotherapy.create("Covid treeatment", "COV1910", 10, LocalTime.of(2, 20), "Covid-19", "Regular visits");
        TherapeuticVaccination procedure3 = TherapeuticVaccination.create(procedure2, "COV1212", "Covid", "Vaccine from moderna", 200, 2);

        ProcedureLocation pl1 = InClinic.create(0.1, procedure1);

        procedure1.showLinks(MedicalProcedure.MedicalProcedureLocationRoles.Performed_in);
        pl1.showLinks(ProcedureLocation.LocationMedicalProcedureRoles.Location_of);

        Appointment ap1 = Appointment.create(pet1, procedure3, v1, LocalDateTime.now().minusDays(6), "Note!");

        ap1.showLinks(Appointment.AppointmentVetRoles.Has_Vet_assigned);
        ap1.showLinks(Appointment.AppointmentMedicalProcedureRoles.Includes);
        ap1.showLinks(Appointment.AppointmentPetRoles.For);

        //ap1.setState(Appointment.State.Completed);

        Appointment ap2 = Appointment.create(pet2, List.of(procedure1, procedure2), List.of(v1), i1, LocalDateTime.now().plusDays(2).minusHours(4), "Note!!!!!!!!!");

        ap2.showLinks(Appointment.AppointmentVetRoles.Has_Vet_assigned);
        ap2.showLinks(Appointment.AppointmentInternRoles.Has_assist_from);
        ap2.showLinks(Appointment.AppointmentMedicalProcedureRoles.Includes);
        ap2.showLinks(Appointment.AppointmentPetRoles.For);

        Appointment.showRaport();

        Appointment.showAllAppointmentsForPeriod(LocalDate.now().minusDays(3), LocalDate.now().plusDays(3));

        Appointment.showAllAppointmentsForDate(LocalDate.now().plusDays(2));

        ObjectPlus.showExtent(Appointment.class);
        ObjectPlus.showExtent(TherapeuticVaccination.class);
        ObjectPlus.showExtent(Immunotherapy.class);
        ObjectPlus.showExtent(Other.class);

        System.out.println(procedure1.getTotalCost());

        ObjectPlus.showExtent(Pet.class);
        ObjectPlus.showExtent(Appointment.class);

        pet1.remove();

        ObjectPlus.showExtent(Pet.class);
        ObjectPlus.showExtent(Appointment.class);

        ObjectPlusPlus.writeExtents(EXTENT_FILE_NAME);

        ObjectPlusPlus.readExtents(EXTENT_FILE_NAME);
        //ObjectPlusPlus.showExtent(Pet.class);
        ObjectPlusPlus.showExtent(TherapeuticVaccination.class);

        Iterable<TherapeuticVaccination> vths = ObjectPlus.getExtent(TherapeuticVaccination.class);
        for (TherapeuticVaccination vv : vths) {
            System.out.println(vv.getCode());
        }
        //v1.showSchedule();

        ap2.printToFile();

        Appointment.showAllSchedules();

         */