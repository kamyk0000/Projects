package MedicalProcedure.ByType;

import Appointment.Appointment;
import Base.ObjectPlusPlus;
import MedicalProcedure.ByLocation.InClinic;
import MedicalProcedure.ByLocation.InField;
import MedicalProcedure.ByLocation.ProcedureLocation;
import MedicalProcedure.MedicalProcedure;

import java.time.LocalTime;

public class TherapeuticVaccination extends Immunotherapy implements IVaccination {
    private int nextTreatmentAfterMinDays; //? totalInjections?
    private String againstPathogen;
    private String vaccineInfo;
    private double vaccinePrice;

    // Roles
    static {
        ObjectPlusPlus.addRoleName(TherapeuticVaccination.class, ProcedureLocation.class, MedicalProcedureLocationRoles.Performed_in, ProcedureLocation.LocationMedicalProcedureRoles.Location_of);
        ObjectPlusPlus.addRoleName(TherapeuticVaccination.class, InClinic.class, MedicalProcedureLocationRoles.Performed_in, ProcedureLocation.LocationMedicalProcedureRoles.Location_of);
        ObjectPlusPlus.addRoleName(TherapeuticVaccination.class, InField.class, MedicalProcedureLocationRoles.Performed_in, ProcedureLocation.LocationMedicalProcedureRoles.Location_of);
        ObjectPlusPlus.addRoleName(TherapeuticVaccination.class, Appointment.class, MedicalProcedureAppointmentRoles.Included_for, Appointment.AppointmentMedicalProcedureRoles.Includes);
    }

    // Constructors
    public static TherapeuticVaccination create(Immunotherapy immunotherapy, Vaccination vaccination, String code, int nextTreatmentAfterMinDays) {
        return create(immunotherapy.getName(), code, immunotherapy.getBaseCost(), immunotherapy.getEstimatedLength(), immunotherapy.getForDisease(), immunotherapy.getTreatmentPlan(), vaccination.getAgainstPathogen(), vaccination.getVaccineInfo(), vaccination.getVaccinePrice(), nextTreatmentAfterMinDays);
    }

    public static TherapeuticVaccination create(Vaccination vaccination, String code, String forDisease, String treatmentPlan, int nextTreatmentAfterMinDays) {
        return create(vaccination.getName(), code, vaccination.getBaseCost(), vaccination.getEstimatedLength(), forDisease, treatmentPlan, vaccination.getAgainstPathogen(), vaccination.getVaccineInfo(), vaccination.getVaccinePrice(), nextTreatmentAfterMinDays);
    }

    public static TherapeuticVaccination create(Immunotherapy immunotherapy, String code, String againstPathogen, String vaccineInfo, double vaccinePrice, int nextTreatmentAfterMinDays) {
        return create(immunotherapy.getName(), code, immunotherapy.getBaseCost(), immunotherapy.getEstimatedLength(), immunotherapy.getForDisease(), immunotherapy.getTreatmentPlan(), againstPathogen, vaccineInfo, vaccinePrice, nextTreatmentAfterMinDays);
    }

    public static TherapeuticVaccination create(MedicalProcedure procedure, String code, String forDisease, String treatmentPlan, String againstPathogen, String vaccineInfo, double vaccinePrice, int nextTreatmentAfterMinDays) {
        return create(procedure.getName(), code, procedure.getBaseCost(), procedure.getEstimatedLength(), forDisease, treatmentPlan, againstPathogen, vaccineInfo, vaccinePrice, nextTreatmentAfterMinDays);
    }

    public static TherapeuticVaccination create(String name, String code, double baseCost, LocalTime estimatedLength, String forDisease, String treatmentPlan, String againstPathogen, String vaccineInfo, double vaccinePrice, int nextTreatmentAfterMinDays) {
        if (isValid(forDisease, treatmentPlan, againstPathogen, vaccineInfo, code)) return new TherapeuticVaccination(name, code, baseCost, estimatedLength, forDisease, treatmentPlan, againstPathogen, vaccineInfo, vaccinePrice, nextTreatmentAfterMinDays);
        return null;
    }

    private TherapeuticVaccination(String name, String code, double baseCost, LocalTime estimatedLength, String forDisease, String treatmentPlan, String againstPathogen, String vaccineInfo, double vaccinePrice, int nextTreatmentAfterMinDays) {
        super(name, code, baseCost, estimatedLength, forDisease, treatmentPlan);
        this.setAgainstPathogen(againstPathogen);
        this.setVaccineInfo(vaccineInfo);
        this.setVaccinePrice(vaccinePrice);
        this.setNextTreatmentAfterMinDays(nextTreatmentAfterMinDays);
    }

    // Methods
    @Override
    public double getTotalCost() {
        return (getBaseCost() + getDistanceCost() + getVaccinePrice()) * getDiscount(); // * totalinjections?
    }

    private static boolean isValid(String forDisease, String treatmentPlan, String againstPathogen, String vaccineInfo, String code) {
        return checkForDisease(forDisease) && checkTreatmentPlan(treatmentPlan) && checkAgainstPathogen(againstPathogen) && checkVaccineInfo(vaccineInfo) && checkCode(code);
    }

    @Override
    public String toString(){
        return "TV" + "[" + getCode() + "]: " + getName() + " against: " + getAgainstPathogen() + ", vaccine: " + getVaccineInfo();
    }

    // Getters & Setters
    public static boolean checkTreatmentPlan(String treatmentPlan) {
        return Immunotherapy.checkTreatmentPlan(treatmentPlan);
    }

    public static boolean checkForDisease(String forDisease) {
        return Immunotherapy.checkForDisease(forDisease);
    }

    public int getNextTreatmentAfterMinDays() {
        return nextTreatmentAfterMinDays;
    }

    public void setNextTreatmentAfterMinDays(int nextTreatmentAfterMinDays) {
        if (nextTreatmentAfterMinDays < 0) nextTreatmentAfterMinDays = 0;
        this.nextTreatmentAfterMinDays = nextTreatmentAfterMinDays;
    }

    public static boolean checkAgainstPathogen(String againstPathogen) {
        return Vaccination.checkAgainstPathogen(againstPathogen);
    }

    @Override
    public String getAgainstPathogen() {
        return this.againstPathogen;
    }

    @Override
    public void setAgainstPathogen(String againstPathogen) {
        if (againstPathogen == null || againstPathogen.isEmpty()) {
            throw new IllegalArgumentException("Pathogen has to be specified");
        }
        this.againstPathogen = againstPathogen;
    }

    public static boolean checkVaccineInfo(String vaccineInfo) {
        return Vaccination.checkVaccineInfo(vaccineInfo);
    }

    @Override
    public String getVaccineInfo() {
        return this.vaccineInfo;
    }

    @Override
    public void setVaccineInfo(String vaccineInfo) {
        if (vaccineInfo == null || vaccineInfo.isEmpty()) {
            throw new IllegalArgumentException("Vaccine Info cannot be empty");
        }
        this.vaccineInfo = vaccineInfo;
    }

    @Override
    public double getVaccinePrice() {
        return this.vaccinePrice;
    }

    @Override
    public void setVaccinePrice(double vaccinePrice) {
        if (vaccinePrice < 0) vaccinePrice = 0;
        this.vaccinePrice = vaccinePrice;
    }
}
