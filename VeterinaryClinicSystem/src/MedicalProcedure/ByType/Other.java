package MedicalProcedure.ByType;

import Base.ObjectPlusPlus;
import MedicalProcedure.ByLocation.InClinic;
import MedicalProcedure.ByLocation.InField;
import MedicalProcedure.ByLocation.ProcedureLocation;
import MedicalProcedure.MedicalProcedure;
import Appointment.Appointment;

import java.time.LocalTime;

public class Other extends MedicalProcedure {
    private String description;
    private double additionalCost;

    // Roles
    static {
        ObjectPlusPlus.addRoleName(Other.class, ProcedureLocation.class, MedicalProcedureLocationRoles.Performed_in, ProcedureLocation.LocationMedicalProcedureRoles.Location_of);
        ObjectPlusPlus.addRoleName(Other.class, InClinic.class, MedicalProcedureLocationRoles.Performed_in, ProcedureLocation.LocationMedicalProcedureRoles.Location_of);
        ObjectPlusPlus.addRoleName(Other.class, InField.class, MedicalProcedureLocationRoles.Performed_in, ProcedureLocation.LocationMedicalProcedureRoles.Location_of);
        ObjectPlusPlus.addRoleName(Other.class, Appointment.class, MedicalProcedureAppointmentRoles.Included_for, Appointment.AppointmentMedicalProcedureRoles.Includes);
    }

    // Constructors
    public static Other create(MedicalProcedure procedure, String description, double additionalCost) {
        return create(procedure.getName(), procedure.getCode(), procedure.getBaseCost(), procedure.getEstimatedLength(), description, additionalCost);
    }

    public static Other create(String name, String code, double baseCost, LocalTime estimatedLength, String description, double additionalCost) {
        if (checkDescription(description)) return new Other(name, code, baseCost, estimatedLength, description, additionalCost);
        return null;
    }

    private Other(String name, String code, double baseCost, LocalTime estimatedLength, String description, double additionalCost) {
        super(name, code, baseCost, estimatedLength);
        this.setDescription(description);
        this.setAdditionalCost(additionalCost);
    }

    // Methods
    @Override
    public String toString(){
        return "O" + super.toString() + ", " + getDescription();
    }

    @Override
    public double getTotalCost() {
        return (super.getBaseCost() + getDistanceCost() + getAdditionalCost()) * getDiscount();
    }

    // Getters & Setters
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (checkDescription(description)) this.description = description;
    }

    public static boolean checkDescription(String description) {
        if (description == null || description.isEmpty()) {
            throw new IllegalArgumentException("Description cannot be missing");
        }
        return true;
    }

    public double getAdditionalCost() {
        return additionalCost;
    }

    public void setAdditionalCost(double additionalCost) {
        if (additionalCost < 0) additionalCost = 0;
        this.additionalCost = additionalCost;
    }
}
