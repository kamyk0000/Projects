package Person;

import Appointment.Appointment;
import Base.ObjectPlusPlus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Vet extends ObjectPlusPlus {
    private List<String> specializations = new ArrayList<>();
    private LocalDate employmentDate;
    public int getExperienceYears() {
        return LocalDate.now().getYear() - employmentDate.getYear();
    }

    // Roles
    public enum VetPersonRoles { Is_Person }
    public enum VetAppointmentRoles { Assigned_to }

    static {
        ObjectPlusPlus.addRoleName(Vet.class, Person.class, Vet.VetPersonRoles.Is_Person, Person.PersonVetRoles.Is_Vet);
    }

    // Constructors
    static Vet create(String specialization, LocalDate employmentDate){
        List<String> list = new ArrayList<>();
        list.add(specialization);
        return create(list, employmentDate);
    }

    static Vet create(List<String> specializations, LocalDate employmentDate){
        if (checkSpecializations(specializations) && checkEmploymentDate(employmentDate)) return new Vet(specializations, employmentDate);
        else return null;
    }

    private Vet(List<String> specializations, LocalDate employmentDate) {
        super();
        this.specializations = specializations;
        this.employmentDate = employmentDate;
    }

    // Methods
    /**
     * Shows Vet specific information.
     */
    public void showInfo() {
        String specs = String.join(", ", specializations);
        System.out.println("Vet, employed on " + employmentDate + ", specializations: " + specs);
    }

    /**
     * Checks if the vet has any appointments.
     */
    public boolean hasVetAppointments() {
        try {
            return getLinks(VetAppointmentRoles.Assigned_to).length > 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Checks if the vet is free during the specified time.
     */
    public boolean isVetFreeOn(LocalDateTime newAppointmentStartTime, LocalDateTime newAppointmentEndTime) {
        if (hasVetAppointments()){
            try {
                ObjectPlusPlus[] appointments = this.getLinks(VetAppointmentRoles.Assigned_to);
                for (ObjectPlusPlus appointment : appointments){
                    LocalDateTime appointmentStartTime = ((Appointment) appointment).getAppointmentDate();
                    LocalDateTime appointmentEndTime = ((Appointment) appointment).getEstimatedEndTime();
                    if (!(newAppointmentEndTime.isBefore(appointmentStartTime) || newAppointmentStartTime.isAfter(appointmentEndTime))) {
                        return false; // There is a conflict
                    }
                    return true;
                }
            } catch (Exception e) {
                return true;
            }
        }
        return true;
    }

    /**
     * Shows the Vet's personal schedule.
     */
    public void showSchedule() throws Exception {
        if (hasVetAppointments()) {
            List<Appointment> futureAppointments = new ArrayList<>();
            for (ObjectPlusPlus appointment : getLinks(VetAppointmentRoles.Assigned_to)) {
                if (((Appointment) appointment).getAppointmentDate().toLocalDate().minusDays(1).isAfter(LocalDate.now())) {
                    futureAppointments.add((Appointment) appointment);
                }
            }
            futureAppointments.sort(Comparator.comparing(Appointment::getAppointmentDate));
            LocalDate date = null;
            System.out.println("Schedule for " + this);
            for (Appointment appointment : futureAppointments) {
                if (date == null || !date.equals(appointment.getAppointmentDate().toLocalDate())) {
                    date = appointment.getAppointmentDate().toLocalDate();
                    System.out.println("=========" + date + "=========");
                }
                System.out.println(appointment.getAppointmentDate().toLocalTime().truncatedTo(ChronoUnit.MINUTES) + " - " + appointment);
            }
        }
        else {
            System.out.println("No vet appointments");
        }
    }

    /**
     * Shows schedule for all Vets.
     */
    public static void showAllSchedules() throws Exception {
        Appointment.showAllSchedules();
    }

    /**
     * Adds new specializations to the vet.
     */
    public void addToSpecializations(String... specialization) {
        this.specializations.addAll(List.of(specialization));
    }

    @Override
    public String toString(){
        String personInfo = "";
        try {
            personInfo = getPerson().toString();
        } catch (Exception ignored){}
        return "[V]: " + personInfo + " " + getSpecializations();
    }

    // Getters & Setters
    public LocalDate getEmploymentDate() {
        return employmentDate;
    }

    private void setEmploymentDate(LocalDate employmentDate) {
        if (checkEmploymentDate(employmentDate)) this.employmentDate = employmentDate;
    }

    private static boolean checkEmploymentDate(LocalDate employmentDate) {
        if (employmentDate == null || employmentDate.isAfter(LocalDate.now())){
            throw new IllegalArgumentException("Employment date has to be specified and be a past date");
        }
        return true;
    }

    public List<String> getSpecializations() {
        return specializations;
    }

    private void setSpecializations(List<String> specializations) {
        if (checkSpecializations(specializations)) this.specializations = specializations;
    }

    private static boolean checkSpecializations(List<String> specializations) {
        if (specializations == null || specializations.isEmpty()) {
            throw new IllegalArgumentException("Vet has to have at least one specialization");
        }
        return true;
    }

    public boolean isOwner() throws Exception {
        return ((Person)this.getLinks(VetPersonRoles.Is_Person)[0]).isOwner();
    }

    public Person getPerson() throws Exception {
        return ((Person)this.getLinks(VetPersonRoles.Is_Person)[0]);
    }
}
