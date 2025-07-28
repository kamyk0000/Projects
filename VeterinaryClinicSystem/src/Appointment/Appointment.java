package Appointment;

import Base.ObjectPlus;
import Base.ObjectPlusPlus;
import MedicalProcedure.ByLocation.InClinic;
import MedicalProcedure.ByType.Immunotherapy;
import MedicalProcedure.ByType.Other;
import MedicalProcedure.ByType.TherapeuticVaccination;
import MedicalProcedure.ByType.Vaccination;
import MedicalProcedure.MedicalProcedure;
import OwnershipPeriod.OwnershipPeriod;
import Person.*;
import Pet.*;
import java.io.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Appointment extends ObjectPlusPlus {
    private LocalDateTime appointmentDate;
    public LocalDateTime getEstimatedEndTime() throws Exception {
        return getEstimatedEndTimeFor(this.appointmentDate, this.getLinks(AppointmentMedicalProcedureRoles.Includes));
    }
    public enum State {
        Scheduled, In_Progress, Completed, Canceled, Flagged
    }
    private State state;
    private String note;
    private static int deleted;

    // Roles
    public enum AppointmentPetRoles { For }
    public enum AppointmentMedicalProcedureRoles { Includes }
    public enum AppointmentVetRoles { Has_Vet_assigned }
    public enum AppointmentInternRoles { Has_assist_from }

    static {
        ObjectPlusPlus.addRoleName(Appointment.class, Pet.class, AppointmentPetRoles.For, Pet.PetAppointmentRoles.Has);
        ObjectPlusPlus.addRoleName(Appointment.class, Vet.class, AppointmentVetRoles.Has_Vet_assigned, Vet.VetAppointmentRoles.Assigned_to);
        ObjectPlusPlus.addRoleName(Appointment.class, Intern.class, AppointmentInternRoles.Has_assist_from, Intern.InternAppointmentRoles.Assists_in);
        ObjectPlusPlus.addRoleName(Appointment.class, Other.class, AppointmentMedicalProcedureRoles.Includes, MedicalProcedure.MedicalProcedureAppointmentRoles.Included_for);
        ObjectPlusPlus.addRoleName(Appointment.class, Vaccination.class, AppointmentMedicalProcedureRoles.Includes, MedicalProcedure.MedicalProcedureAppointmentRoles.Included_for);
        ObjectPlusPlus.addRoleName(Appointment.class, Immunotherapy.class, AppointmentMedicalProcedureRoles.Includes, MedicalProcedure.MedicalProcedureAppointmentRoles.Included_for);
        ObjectPlusPlus.addRoleName(Appointment.class, TherapeuticVaccination.class, AppointmentMedicalProcedureRoles.Includes, MedicalProcedure.MedicalProcedureAppointmentRoles.Included_for);
    }

    // Constructors
    public static Appointment create(Pet pet, MedicalProcedure medicalProcedure, Vet vet, LocalDateTime appointmentDate, String note) throws Exception {
        if (pet == null) {
            throw new IllegalArgumentException("Please select a pet");
        }
        if (vet == null) {
            throw new IllegalArgumentException("Please select a Vet");
        }
        return create(pet, List.of(medicalProcedure), List.of(vet), null, appointmentDate, note);
    }

    public static Appointment create(Pet pet, List<MedicalProcedure> medicalProcedures, List<Vet> vets, Intern intern, LocalDateTime appointmentDate, String note) throws Exception {
        if (medicalProcedures.isEmpty()) {
            throw new IllegalArgumentException("No MedicalProcedure assigned to a Appointment");
        }
        if (pet == null) {
            throw new IllegalArgumentException("Please select a pet");
        }
        if (intern != null) checkIntern(intern, medicalProcedures, appointmentDate);
        if (checkVets(vets, medicalProcedures, appointmentDate, pet) && checkAppointmentDate(appointmentDate)) {
            return new Appointment(pet, medicalProcedures, vets, intern, appointmentDate, note);
        }
        return null;
    }

    private Appointment(Pet pet, List<MedicalProcedure> medicalProcedures, List<Vet> vets, Intern intern, LocalDateTime appointmentDate, String note) throws Exception {
        super();
        if (appointmentDate.isBefore(LocalDateTime.now())) {
            for (Vet vet : vets) {
                if (checkIfPetBelongsToVet(pet, vet)) {
                    setState(State.Flagged);
                    break;
                } else {
                    setState(State.Completed);
                }
            }
        } else setState(State.Scheduled);
        this.appointmentDate = appointmentDate;
        this.note = note;
        this.linkPet(pet);
        for (MedicalProcedure medicalProcedure : medicalProcedures) {
            this.addLink(AppointmentMedicalProcedureRoles.Includes, medicalProcedure, medicalProcedure.getCode());
        }
        this.assignVets(vets);
        this.assignIntern(intern);
    }

    // Methods

    /**
     * Prints the information about Appointment to a .html file in a pattern of an invoice.
     * @throws Exception
     */
    public void printToFile() throws Exception {
        Pet pet = (Pet) this.getLinks(AppointmentPetRoles.For)[0];
        ObjectPlusPlus[] procedures = this.getLinks(AppointmentMedicalProcedureRoles.Includes);
        Owner owner = (Owner) pet.getLinks(Pet.PetOwnershipRoles.Belongs_for)[0].getLinks(OwnershipPeriod.OwnershipOwnerRoles.Is_for)[0];


        try (BufferedWriter writer = new BufferedWriter(new FileWriter(this.hashCode() + "invoice.html"))) {
            writer.write(
                    "<!DOCTYPE html>\n" +
                            "<html>\n" +
                            "<head>\n" +
                            "<meta charset=\"utf-8\" />\n" +
                            "<title>" + this.hashCode() + " invoice</title>\n"
            );
            writer.write("""
                    <style>
                    \t\t\t.invoice-box {
                    \t\t\t\tmax-width: 800px;
                    \t\t\t\tmargin: auto;
                    \t\t\t\tpadding: 30px;
                    \t\t\t\tborder: 1px solid #eee;
                    \t\t\t\tbox-shadow: 0 0 10px rgba(0, 0, 0, 0.15);
                    \t\t\t\tfont-size: 16px;
                    \t\t\t\tline-height: 24px;
                    \t\t\t\tfont-family: 'Helvetica Neue', 'Helvetica', Helvetica, Arial, sans-serif;
                    \t\t\t\tcolor: #555;
                    \t\t\t}

                    \t\t\t.invoice-box table {
                    \t\t\t\twidth: 100%;
                    \t\t\t\tline-height: inherit;
                    \t\t\t\ttext-align: left;
                    \t\t\t}

                    \t\t\t.invoice-box table td {
                    \t\t\t\tpadding: 5px;
                    \t\t\t\tvertical-align: top;
                    \t\t\t}

                    \t\t\t.invoice-box table tr td:nth-child(2) {
                    \t\t\t\ttext-align: right;
                    \t\t\t}

                    \t\t\t.invoice-box table tr.top table td {
                    \t\t\t\tpadding-bottom: 20px;
                    \t\t\t}

                    \t\t\t.invoice-box table tr.top table td.title {
                    \t\t\t\tfont-size: 45px;
                    \t\t\t\tline-height: 45px;
                    \t\t\t\tcolor: #333;
                    \t\t\t}

                    \t\t\t.invoice-box table tr.information table td {
                    \t\t\t\tpadding-bottom: 40px;
                    \t\t\t}

                    \t\t\t.invoice-box table tr.heading td {
                    \t\t\t\tbackground: #eee;
                    \t\t\t\tborder-bottom: 1px solid #ddd;
                    \t\t\t\tfont-weight: bold;
                    \t\t\t}

                    \t\t\t.invoice-box table tr.details td {
                    \t\t\t\tpadding-bottom: 20px;
                    \t\t\t}

                    \t\t\t.invoice-box table tr.item td {
                    \t\t\t\tborder-bottom: 1px solid #eee;
                    \t\t\t}

                    \t\t\t.invoice-box table tr.item.last td {
                    \t\t\t\tborder-bottom: none;
                    \t\t\t}

                    \t\t\t.invoice-box table tr.total td:nth-child(2) {
                    \t\t\t\tborder-top: 2px solid #eee;
                    \t\t\t\tfont-weight: bold;
                    \t\t\t}

                    \t\t\t@media only screen and (max-width: 600px) {
                    \t\t\t\t.invoice-box table tr.top table td {
                    \t\t\t\t\twidth: 100%;
                    \t\t\t\t\tdisplay: block;
                    \t\t\t\t\ttext-align: center;
                    \t\t\t\t}

                    \t\t\t\t.invoice-box table tr.information table td {
                    \t\t\t\t\twidth: 100%;
                    \t\t\t\t\tdisplay: block;
                    \t\t\t\t\ttext-align: center;
                    \t\t\t\t}
                    \t\t\t}

                    \t\t\t/** RTL **/
                    \t\t\t.invoice-box.rtl {
                    \t\t\t\tdirection: rtl;
                    \t\t\t\tfont-family: Tahoma, 'Helvetica Neue', 'Helvetica', Helvetica, Arial, sans-serif;
                    \t\t\t}

                    \t\t\t.invoice-box.rtl table {
                    \t\t\t\ttext-align: right;
                    \t\t\t}

                    \t\t\t.invoice-box.rtl table tr td:nth-child(2) {
                    \t\t\t\ttext-align: left;
                    \t\t\t}
                    \t\t</style>
                    \t</head>
                    """
            );
            writer.write(
                    "<body>\n" +
                            "<div class=\"invoice-box\">\n" +
                            "<table cellpadding=\"0\" cellspacing=\"0\">\n" +
                            "<tr class=\"top\">\n" +
                            "<td colspan=\"2\">\n" +
                            "<table>\n" +
                            "<tr>\n" +
                            "<td class=\"title\">\n" +
                            "<img\n" +
                            "src=\"/images/clinic_banner.png\"\n" +
                            "style=\"width: 100%; max-width: 300px\"\n" +
                            "/>\n" +
                            "</td>\n" +
                            "\n" +
                            "<td>\n" +
                            "Invoice #: " + ((int)(Math.random()*10000)) + "/" + LocalDate.now().getYear() + "<br />\n" +
                            "Created: " + LocalDate.now() + "<br />\n" +
                            "Due: " + LocalDate.now().plusMonths(3).plusDays(12) + "\n" +
                            "</td>\n" +
                            "</tr>\n" +
                            "</table>\n" +
                            "</td>\n" +
                            "</tr>\n" +
                            "\n" +
                            "<tr class=\"information\">\n" +
                            "<td colspan=\"2\">\n" +
                            "<table>\n" +
                            "<tr>\n" +
                            "<td>\n" +
                            "MEOW Veterinary Clinic, Inc.<br />\n" +
                            InClinic.getClinicAddress() + "<br />\n" +
                            "</td>\n" +
                            "\n" +
                            "<td>\n" +
                            owner.getPerson() + "<br />\n" +
                            owner.getPerson().getAddress() + "<br />\n" +
                            owner.getPerson().getPhone() + "<br />\n" +
                            ((owner.getNIP() == null) ? "" : owner.getNIP() + "<br />\n") +
                            "</td>\n" +
                            "</tr>\n" +
                            "</table>\n" +
                            "</td>\n" +
                            "</tr>\n" +
                            "\n" +
                            "<tr class=\"heading\">\n" +
                            "<td>Pet details</td>\n" +
                            "</tr>\n" +
                            "\n" +
                            "<tr class=\"details\">\n" +
                            "<td>" + pet + "</td>\n" +
                            "</tr>\n" +
                            "\t\t\t\t<tr class=\"heading\">\n" +
                            "\t\t\t\t\t<td>Item</td>\n" +
                            "\n" +
                            "\t\t\t\t\t<td>Price</td>\n" +
                            "\t\t\t\t</tr>\n" +
                            "\n"
            );
            double total = 0;
            for (int i = 0; i < procedures.length - 1; i++) {
                double totalCosts = ((MedicalProcedure)procedures[i]).getTotalCost();
                total += totalCosts;
                writer.write("\n" +
                        "\t\t\t\t<tr class=\"item\">\n" +
                        "\t\t\t\t\t<td>" + procedures[i] + "</td>\n" +
                        "\n" +
                        "\t\t\t\t\t<td>" + totalCosts + " PLN</td>\n" +
                        "\t\t\t\t</tr>\n");

            }
            double lastCost = ((MedicalProcedure)procedures[procedures.length - 1]).getTotalCost();
            total += lastCost;
            writer.write("\t\t\t\t<tr class=\"item last\">\n" +
                    "\t\t\t\t\t<td>" + procedures[procedures.length - 1] + "</td>\n" +
                    "\n" +
                    "\t\t\t\t\t<td>" + lastCost + " PLN</td>\n" +
                    "\t\t\t\t</tr>\n" +
                    "\n" +
                    "\t\t\t\t<tr class=\"total\">\n" +
                    "\t\t\t\t\t<td></td>\n" +
                    "\n" +
                    "\t\t\t\t\t<td>Total: " + total + " PLN</td>\n" +
                    "\t\t\t\t</tr>\n" +
                    "</table>\n" +
                    "\t\t</div>\n" +
                    "\t</body>\n" +
                    "</html>");
        }
    }

    /**
     * Check whether a Pet belongs to a Vet
     */
    public static boolean checkIfPetBelongsToVet(Pet pet, Vet vet) throws Exception {
        if (vet.isOwner()) {
            return vet.getPerson().getOwner().checkIfPetBelongsToOwner(pet);
        }
        return false;
    }

    /**
     * Calls a Vet method to check if Vet is available for new Appointment
     */
    public boolean isVetFreeOn(Vet vet, LocalDateTime appointmentDate) throws Exception {
        return vet.isVetFreeOn(appointmentDate, getEstimatedEndTime());
    }

    /**
     * Whole logic of removing appointments due to them being a part of Pet objects
     */
    public void remove() throws Exception {
        try {
            for (ObjectPlusPlus medicalProcedure : getLinks(AppointmentMedicalProcedureRoles.Includes)) {
                removeLink(AppointmentMedicalProcedureRoles.Includes, ((MedicalProcedure) medicalProcedure).getCode());
            }
        } catch (Exception e) {
            System.out.println("No medical procedure links found, continuing! " + e.getMessage());
        }
        try {
            for (ObjectPlusPlus vet : getLinks(AppointmentVetRoles.Has_Vet_assigned)) {
                removeLink(AppointmentVetRoles.Has_Vet_assigned, vet);
            }
        } catch (Exception e) {
            System.out.println("No vet links found, continuing! " + e.getMessage());
        }
        try {
            for (ObjectPlusPlus intern : getLinks(AppointmentInternRoles.Has_assist_from)) {
                removeLink(AppointmentInternRoles.Has_assist_from, intern);
            }
        } catch (Exception e) {
            System.out.println("No intern links found, continuing! " + e.getMessage());
        }
        try {
            for (ObjectPlusPlus pet : getLinks(AppointmentPetRoles.For)) {
                removeLink(AppointmentPetRoles.For, pet);
            }
        } catch (Exception e) {
            System.out.println("No pet links found, continuing! " + e.getMessage());
        }
        removeFromExtent(this);
        deleted++;
    }

    /**
     * Rescheduling an Appointment for a different Vet/s or Date or both
     */
    public void reschedule(List<Vet> vets, Intern intern, LocalDateTime newAppointmentDate) throws Exception {
        if (newAppointmentDate.isBefore(LocalDateTime.now())) throw new IllegalArgumentException("Cannot reschedule to past");
        if(intern!=null) {
            if (!intern.isInternFreeOn(newAppointmentDate, getEstimatedEndTime())) throw new IllegalArgumentException("Intern: " + intern + ", is already assigned to an appointment at specified time");
        }
        for (Vet vet : vets){
            if(!isVetFreeOn(vet, newAppointmentDate)) {
             throw new IllegalArgumentException("Vet: " + vet + ", is already assigned to an appointment at specified time");
            }
        }
        ObjectPlusPlus[] vetsList = this.getLinks(AppointmentVetRoles.Has_Vet_assigned);
        for (ObjectPlusPlus obj : vetsList) {
            removeLink(AppointmentVetRoles.Has_Vet_assigned, obj);
        }
        try {
            removeLink(AppointmentInternRoles.Has_assist_from, getLinks(AppointmentInternRoles.Has_assist_from)[0]);
            this.assignIntern(intern);
        } catch (Exception ignored) {}
        this.setAppointmentDate(newAppointmentDate);
        this.assignVets(vets);
    }

    /**
     * Displays stats of all Appointment states (including deleted ones)
     */
    public static void showRaport() throws ClassNotFoundException {
        List<Appointment> appointments = getAllAppointments();
        int scheduled = 0, inProgress = 0, completed = 0, cancelled = 0, flagged = 0;

        for (Appointment appointment : appointments){
            switch (appointment.getState()) {
                case Canceled -> cancelled++;
                case Completed -> completed++;
                case Scheduled -> scheduled++;
                case In_Progress -> inProgress++;
                case Flagged -> flagged++;
            }
        }

        System.out.println("Appointments rapport:" +
                "\nScheduled: " + scheduled +
                "\nIn progress: " + inProgress +
                "\nCompleted: " + (completed + flagged) +
                ((flagged > 0) ? "\n\tIncluding flagged: " + flagged : "") +
                "\nCancelled: " + (cancelled + deleted));
    }

    /**
     * Displays info of all Appointment for a certain time date
     */
    public static void showAllAppointmentsForDate(LocalDate date) throws ClassNotFoundException {
        showAllAppointmentsForPeriod(date, null);
    }

    /**
     * Displays info of all Appointment for a certain time period
     */
    public static void showAllAppointmentsForPeriod(LocalDate dateFrom, LocalDate dateTo) throws ClassNotFoundException {
        List<Appointment> appointments = getAllAppointments();
        LocalDateTime dFrom = dateFrom.atStartOfDay();
        LocalDateTime dTo = null;
        if (dateTo == null) {
            dTo = dFrom.plusDays(1);
        } else dTo = dateTo.atStartOfDay();

        System.out.println("Appointments for: " + dFrom.toLocalDate() + " to " + dTo.toLocalDate() + ": ");
        for (Appointment appointment : appointments) {
            if (appointment.getAppointmentDate().isAfter(dFrom) && appointment.getAppointmentDate().isBefore(dTo)){
                System.out.println(appointment + " : " + appointment.getAppointmentDate().truncatedTo(ChronoUnit.MINUTES));
            }
        }
    }

    /**
     * Displays info of all future Appointments as a schedule for Vets
     */
    public static void showAllSchedules() throws Exception {
        List<Appointment> futureAppointments = ObjectPlus.getExtent(Appointment.class).stream()
                .filter(appointment -> appointment.getAppointmentDate().toLocalDate().isAfter(LocalDateTime.now().toLocalDate()))
                .sorted(Comparator.comparing(Appointment::getAppointmentDate)).toList();
        LocalDate date = null;
        System.out.println("Schedule for all");
        for (Appointment appointment : futureAppointments) {
            if (date == null || !date.equals(appointment.getAppointmentDate().toLocalDate())) {
                date = appointment.getAppointmentDate().toLocalDate();
                System.out.println("=========" + date + "=========");
            }
            for (ObjectPlusPlus vet : appointment.getLinks(AppointmentVetRoles.Has_Vet_assigned)) {
                System.out.println(appointment.getAppointmentDate().toLocalTime().truncatedTo(ChronoUnit.MINUTES) + " " + vet + " | " + appointment);
            }
        }
    }

    /**
     * Special logic to save static fields
     */
    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject(); // Serialize non-static fields

        // Serialize static fields manually
        oos.writeObject(deleted);
    }

    /**
     * Special logic to load static fields
     */
    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject(); // Deserialize non-static fields

        // Deserialize static fields manually
        deleted = (int) ois.readObject();
    }

    @Override
    public String toString(){
        return "Appointment[" + this.hashCode() + "]: " + getState();
    }

    // Getters & Setters
    public static void setDeleted(int deleted) {
        Appointment.deleted = deleted;
    }

    public static int getDeleted() {
        return Appointment.deleted;
    }

    public static List<Appointment> getAllAppointments() throws ClassNotFoundException {
        Iterable<Appointment> iterable = ObjectPlus.getExtent(Appointment.class);
        List<Appointment> appointments = new ArrayList<>();
        iterable.forEach(appointments::add);
        return appointments;
    }

    private static boolean checkState(LocalDateTime appointmentDate) throws Exception {
        return true;
    }

    public void assignVets(List<Vet> vets) throws Exception {
        for (Vet vet : vets) {
            if (vet.isVetFreeOn(this.appointmentDate, getEstimatedEndTime())) {
                this.addLink(AppointmentVetRoles.Has_Vet_assigned, vet);
            } else throw new IllegalArgumentException(vet + ", is already assigned to an appointment at specified time");
        }
    }

    private static boolean checkVets(List<Vet> vets, List<MedicalProcedure> procedures, LocalDateTime appointmentDate, Pet pet) throws Exception {
        if (vets.size() > 2 || vets.isEmpty()) {
            throw new IllegalArgumentException("There must be 1 - 2 Vets assigned to a Appointment");
        }
        for (Vet vet : vets) {
            if (!vet.isVetFreeOn(appointmentDate, getEstimatedEndTimeFor(appointmentDate, procedures))) {
            throw new IllegalArgumentException(vet + ", is already assigned to an appointment at specified time");
            } else if (appointmentDate.isAfter(LocalDateTime.now())) {
                if(checkIfPetBelongsToVet(pet, vet)){
                    throw new IllegalArgumentException(vet + ", is an owner of this pet and cannot be assigned to the future procedure");
                }
            }
        }
        return true;
    }

    public void assignIntern(Intern intern) throws Exception {
        if (intern != null) {
            if (intern.isInternFreeOn(this.appointmentDate, getEstimatedEndTime())) {
                this.addLink(AppointmentInternRoles.Has_assist_from, intern);
            } else throw new IllegalArgumentException(intern + ", is already assigned to an appointment at specified time");
        }
    }

    private static boolean checkIntern(Intern intern, List<MedicalProcedure> procedures, LocalDateTime appointmentDate) throws Exception {
        if (intern.isInternFreeOn(appointmentDate, getEstimatedEndTimeFor(appointmentDate, procedures))) {
            return true;
            } else throw new IllegalArgumentException(intern + ", is already assigned to an appointment at specified time");
    }

    public void linkPet(Pet pet) throws Exception {
        if (pet != null) pet.addPart(Pet.PetAppointmentRoles.Has, this);
        else throw new Exception("Pet cannot be null");
    }

    public LocalDateTime getAppointmentDate() {
        return appointmentDate;
    }

    public static LocalDateTime getEstimatedEndTimeFor(LocalDateTime dateTime, ObjectPlusPlus[] procedures) throws Exception {
        List<MedicalProcedure> medicalProcedures = new ArrayList<>();
        for (ObjectPlusPlus procedure : procedures) {
            medicalProcedures.add((MedicalProcedure) procedure);
        }
        return getEstimatedEndTimeFor(dateTime, medicalProcedures);
    }

    public static LocalDateTime getEstimatedEndTimeFor(LocalDateTime dateTime, List<MedicalProcedure> procedures) throws Exception {
        LocalDateTime appointmentEndDate = null;
        for (MedicalProcedure procedure : procedures) {
            appointmentEndDate = dateTime.plus(Duration.ofHours((procedure).getEstimatedLength().getHour())
                    .plus(Duration.ofMinutes((procedure).getEstimatedLength().getMinute())));
        }
        return appointmentEndDate;
    }

    public void setAppointmentDate(LocalDateTime appointmentDate) {
        if (checkAppointmentDate(appointmentDate)) this.appointmentDate = appointmentDate;
    }

    private static boolean checkAppointmentDate(LocalDateTime appointmentDate) {
        if (appointmentDate == null) {
            throw new IllegalArgumentException("Appointment Date cannot be missing");
        }
        return true;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        if(checkState(state)) this.state = state;
    }

    private static boolean checkState(State state) {
        if (state == null) {
            throw new IllegalArgumentException("State cannot be null");
        }
        return true;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        if (note == null || note.isEmpty()) {
            throw new IllegalArgumentException("Note cannot be empty");
        }
        this.note = note;
    }
}
