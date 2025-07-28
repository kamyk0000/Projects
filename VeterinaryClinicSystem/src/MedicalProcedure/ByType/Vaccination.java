package MedicalProcedure.ByType;

import Appointment.Appointment;
import Base.ObjectPlusPlus;
import MedicalProcedure.ByLocation.InClinic;
import MedicalProcedure.ByLocation.InField;
import MedicalProcedure.ByLocation.ProcedureLocation;
import MedicalProcedure.MedicalProcedure;

import java.time.LocalTime;

public class Vaccination extends MedicalProcedure implements IVaccination {
    private String againstPathogen;
    private String vaccineInfo;
    private double vaccinePrice;

    // Roles
    static {
        ObjectPlusPlus.addRoleName(Vaccination.class, ProcedureLocation.class, MedicalProcedureLocationRoles.Performed_in, ProcedureLocation.LocationMedicalProcedureRoles.Location_of);
        ObjectPlusPlus.addRoleName(Vaccination.class, InClinic.class, MedicalProcedureLocationRoles.Performed_in, ProcedureLocation.LocationMedicalProcedureRoles.Location_of);
        ObjectPlusPlus.addRoleName(Vaccination.class, InField.class, MedicalProcedureLocationRoles.Performed_in, ProcedureLocation.LocationMedicalProcedureRoles.Location_of);
        ObjectPlusPlus.addRoleName(Vaccination.class, Appointment.class, MedicalProcedureAppointmentRoles.Included_for, Appointment.AppointmentMedicalProcedureRoles.Includes);
    }

    // Constructors
    public static Vaccination create(MedicalProcedure procedure, String code, String againstPathogen, String vaccineInfo, double vaccinePrice) {
        return create(procedure.getName(), code, procedure.getBaseCost(), procedure.getEstimatedLength(), againstPathogen, vaccineInfo, vaccinePrice);
    }

    public static Vaccination create(String name, String code, double baseCost, LocalTime estimatedLength, String againstPathogen, String vaccineInfo, double vaccinePrice) {
        if(isValid(againstPathogen, vaccineInfo, code)) return new Vaccination(name, code, baseCost, estimatedLength, againstPathogen, vaccineInfo, vaccinePrice);
        return null;
    }

    private Vaccination(String name, String code, double baseCost, LocalTime estimatedLength, String againstPathogen, String vaccineInfo, double vaccinePrice) {
        super(name, code, baseCost, estimatedLength);
        setAgainstPathogen(againstPathogen);
        setVaccineInfo(vaccineInfo);
        setVaccinePrice(vaccinePrice);
    }

    // Methods
    @Override
    public double getTotalCost() {
        return (super.getBaseCost() + getDistanceCost() + getVaccinePrice()) * getDiscount();
    }

    private static boolean isValid(String againstPathogen, String vaccineInfo, String code) {
        return checkVaccineInfo(vaccineInfo) && checkAgainstPathogen(againstPathogen) && checkCode(code);
    }

    @Override
    public String toString(){
        return "V" + super.toString() + " against: " + getAgainstPathogen();
    }

    // Getters & Setters
    @Override
    public String getAgainstPathogen() {
        return this.againstPathogen;
    }

    @Override
    public void setAgainstPathogen(String againstPathogen) {
        if (checkAgainstPathogen(againstPathogen)) this.againstPathogen = againstPathogen;
    }

    public static boolean checkAgainstPathogen(String againstPathogen) {
        if (againstPathogen == null || againstPathogen.isEmpty()) {
            throw new IllegalArgumentException("Pathogen has to be specified");
        }
        return true;
    }

    @Override
    public String getVaccineInfo() {
        return this.vaccineInfo;
    }

    @Override
    public void setVaccineInfo(String vaccineInfo) {
        if (checkVaccineInfo(vaccineInfo)) this.vaccineInfo = vaccineInfo;
    }

    public static boolean checkVaccineInfo(String vaccineInfo) {
        if (vaccineInfo == null || vaccineInfo.isEmpty()) {
            throw new IllegalArgumentException("Vaccine Info cannot be empty");
        }
        return true;
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
