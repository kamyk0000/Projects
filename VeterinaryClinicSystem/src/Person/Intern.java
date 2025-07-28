package Person;

import Appointment.Appointment;
import Base.ObjectPlusPlus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class Intern extends ObjectPlusPlus {
    private double internshipHours;
    private double hoursWorked;
    private double grade;

    // Roles
    public enum InternPersonRoles { Is_Person }
    public enum InternAppointmentRoles { Assists_in }

    static {
        ObjectPlusPlus.addRoleName(Intern.class, Person.class, Intern.InternPersonRoles.Is_Person, Person.PersonInternRoles.Is_Intern);
    }

    // Constructors
    static Intern create(double internshipHours, double hoursWorked, double grade){
        return new Intern(internshipHours, hoursWorked, grade);
    }

    private Intern(double internshipHours, double hoursWorked, double grade) {
        super();
        this.setInternshipHours(internshipHours);
        this.setHoursWorked(hoursWorked);
        this.setGrade(grade);
    }

    // Methods
    /**
     * Shows Owner specific information.
     */
    public void showInfo() {
        System.out.println("Intern, worked for: " + getHoursWorked() + " hours, grade: " + getGrade());
    }

    /**
     * Checks if the intern has any appointments.
     */
    public boolean hasInternAppointments() {
        try {
            return (this.getLinks(InternAppointmentRoles.Assists_in)).length > 0;
        } catch (Exception e) {
            return false;
        }
    }
    /**
     * Checks if the intern is free or occupied (assists in an appointment) during the specified time.
     */
    public boolean isInternFreeOn(LocalDateTime newAppointmentStartTime, LocalDateTime newAppointmentEndTime) {
        if (hasInternAppointments()){
            try {
                List<Appointment> appointments = getLinksList(InternAppointmentRoles.Assists_in);
                for (Appointment appointment : appointments){
                    LocalDateTime appointmentStartTime = appointment.getAppointmentDate();
                    LocalDateTime appointmentEndTime = appointment.getEstimatedEndTime();
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
     * Shows the estimated end date of the internship.
     */
    public void showEstimatedInternshipEndDate() {
        double days = (internshipHours - hoursWorked) / 8.0;
        LocalDate updatedDate = LocalDate.now().plusDays((long) days);
        System.out.println("Estimated date of internship end: " + updatedDate);
    }

    /**
     * Adds hours to the worked hours.
     */
    public void addHours(double hours) {
        hoursWorked += hours;
    }

    public void makeInternIntoVet(String spec) throws Exception {
        this.getPerson().makeInternIntoVet(spec);
    }

    @Override
    public String toString(){
        String personInfo = "";
        try {
            personInfo = getPerson().toString();
        } catch (Exception ignored){}
        return "[I]: " + personInfo;
    }

    // Getters & Setters
    public Person getPerson() throws Exception {
        return ((Person)this.getLinks(InternPersonRoles.Is_Person)[0]);
    }

    public double getInternshipHours() {
        return internshipHours;
    }

    private void setInternshipHours(double internshipHours) {
        if (internshipHours < 0) internshipHours = 0;
        this.internshipHours = internshipHours;
    }

    public double getHoursWorked() {
        return hoursWorked;
    }

    private void setHoursWorked(double hoursWorked) {
        if (hoursWorked < 0) hoursWorked = 0;
        this.hoursWorked = hoursWorked;
    }

    public double getGrade() {
        return grade;
    }

    public void setGrade(double grade) {
        if (grade < 0) grade = 0;
        this.grade = grade;
    }
}
