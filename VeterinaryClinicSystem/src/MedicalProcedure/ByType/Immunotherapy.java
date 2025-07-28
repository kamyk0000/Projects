package MedicalProcedure.ByType;

import Appointment.Appointment;
import Base.ObjectPlusPlus;
import MedicalProcedure.ByLocation.InClinic;
import MedicalProcedure.ByLocation.InField;
import MedicalProcedure.ByLocation.ProcedureLocation;
import MedicalProcedure.MedicalProcedure;

import java.time.LocalTime;

public class Immunotherapy extends MedicalProcedure {
    private String forDisease;
    private String treatmentPlan;

    // Roles
    static {
        ObjectPlusPlus.addRoleName(Immunotherapy.class, ProcedureLocation.class, MedicalProcedureLocationRoles.Performed_in, ProcedureLocation.LocationMedicalProcedureRoles.Location_of);
        ObjectPlusPlus.addRoleName(Immunotherapy.class, InClinic.class, MedicalProcedureLocationRoles.Performed_in, ProcedureLocation.LocationMedicalProcedureRoles.Location_of);
        ObjectPlusPlus.addRoleName(Immunotherapy.class, InField.class, MedicalProcedureLocationRoles.Performed_in, ProcedureLocation.LocationMedicalProcedureRoles.Location_of);
        ObjectPlusPlus.addRoleName(Immunotherapy.class, Appointment.class, MedicalProcedureAppointmentRoles.Included_for, Appointment.AppointmentMedicalProcedureRoles.Includes);
    }

    // Constructors
    public static Immunotherapy create(MedicalProcedure procedure, String code, String forDisease, String treatmentPlan) {
        return create(procedure.getName(), code, procedure.getBaseCost(), procedure.getEstimatedLength(), forDisease, treatmentPlan);
    }

    public static Immunotherapy create(String name, String code, double baseCost, LocalTime estimatedLength, String forDisease, String treatmentPlan) {
        if (isValid(forDisease, treatmentPlan, code)) return new Immunotherapy(name, code, baseCost, estimatedLength, forDisease, treatmentPlan);
        return null;
    }

    Immunotherapy(String name, String code, double baseCost, LocalTime estimatedLength, String forDisease, String treatmentPlan) {
        super(name, code, baseCost, estimatedLength);
        this.setTreatmentPlan(treatmentPlan);
        this.setForDisease(forDisease);
    }

    // Methods
    @Override
    public double getTotalCost() {
        return (getBaseCost() + getDistanceCost()) * getDiscount();
    }

    private static boolean isValid(String forDisease, String treatmentPlan, String code) {
        return checkForDisease(forDisease) && checkTreatmentPlan(treatmentPlan) && checkCode(code);
    }

    @Override
    public String toString(){
        return "I" + super.toString() + " for disease: " + getForDisease();
    }

    // Getters & Setters
    public String getForDisease() {
        return forDisease;
    }

    public void setForDisease(String forDisease) {
        if (checkForDisease(forDisease)) this.forDisease = forDisease;
    }

    public static boolean checkForDisease(String forDisease) {
        if (forDisease == null || forDisease.isEmpty()) {
            throw new IllegalArgumentException("Disease needs to be specified");
        }
        return true;
    }

    public String getTreatmentPlan() {
        return treatmentPlan;
    }

    public void setTreatmentPlan(String treatmentPlan) {
        if (checkTreatmentPlan(treatmentPlan)) this.treatmentPlan = treatmentPlan;
    }

    public static boolean checkTreatmentPlan(String treatmentPlan) {
        if (treatmentPlan == null || treatmentPlan.isEmpty()) {
            throw new IllegalArgumentException("Treatment Plan needs to be specified");
        }
        return true;
    }
}
