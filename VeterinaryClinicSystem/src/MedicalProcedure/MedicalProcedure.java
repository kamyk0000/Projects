package MedicalProcedure;

import Base.ObjectPlusPlus;
import MedicalProcedure.ByLocation.InClinic;
import MedicalProcedure.ByLocation.InField;
import MedicalProcedure.ByLocation.ProcedureLocation;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

public abstract class MedicalProcedure extends ObjectPlusPlus {
    private String name;
    private double baseCost;
    private LocalTime estimatedLength;
    private String code;
    private static Set<String> allCodes;

    // Roles
    public enum MedicalProcedureLocationRoles { Performed_in }
    public enum MedicalProcedureAppointmentRoles { Included_for }

    static {
        ObjectPlusPlus.addRoleName(MedicalProcedure.class, ProcedureLocation.class, MedicalProcedureLocationRoles.Performed_in, ProcedureLocation.LocationMedicalProcedureRoles.Location_of);
        ObjectPlusPlus.addRoleName(MedicalProcedure.class, InClinic.class, MedicalProcedureLocationRoles.Performed_in, ProcedureLocation.LocationMedicalProcedureRoles.Location_of);
        ObjectPlusPlus.addRoleName(MedicalProcedure.class, InField.class, MedicalProcedureLocationRoles.Performed_in, ProcedureLocation.LocationMedicalProcedureRoles.Location_of);
    }

    // Constructors (in this case, pseudo-constructor)
    public MedicalProcedure(String name, String code, double baseCost, LocalTime estimatedLength) {
        super();
        this.setName(name);
        this.setBaseCost(baseCost);
        this.setEstimatedLength(estimatedLength);
        this.setCode(code);
    }

    // Methods
    /**
     * Links a location as a part for the medical procedure.
     */
    public void linkLocation(ProcedureLocation location) throws Exception {
        addPart(MedicalProcedureLocationRoles.Performed_in, location);
    }

    /**
     * Abstract method that returns total cost of a procedure.
     * @return total cost of a procedure
     */
    public abstract double getTotalCost();

    /**
     * Special logic to save static fields
     */
    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject(); // Serialize non-static fields

        // Serialize static fields manually
        oos.writeObject(allCodes);
    }

    /**
     * Special logic to load static fields
     */
    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject(); // Deserialize non-static fields

        // Deserialize static fields manually
        allCodes = (Set<String>) ois.readObject();
    }

    @Override
    public String toString(){
        return "[" + getCode() + "]: " + getName();
    }

    // Getters & Setters
    public static Set<String> getAllCodes() {
        if (allCodes == null) {
            allCodes = new HashSet<>();
        }
        return allCodes;
    }

    public static void setAllCodes(Set<String> allCodes) {
        MedicalProcedure.allCodes = allCodes;
    }

    public static boolean checkCode(String code) {
        if (code.isEmpty() || code == null){
            throw new IllegalArgumentException("Code cannot be empty");
        }
        if (allCodes == null) {
            allCodes = new HashSet<>();
        } else if (allCodes.contains(code)) {
            throw new IllegalArgumentException("Code already exists");
        }
        return true;
    }

    private void setCode(String code) {
        if (checkCode(code)) {
            this.code = code;
            allCodes.add(code);
        }
    }

    public String getCode() {
        if (allCodes == null) {
            throw new IllegalArgumentException("There are no medical procedure codes");
        }
        return code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        this.name = name;
    }

    public double getDiscount() {
        try {
            ObjectPlusPlus location = getLinks(MedicalProcedureLocationRoles.Performed_in)[0];
            if (location.getClass() == InClinic.class) {
                 return 1.0 - ((InClinic) location).getInClinicDiscount();
            }
        } catch (Exception ignored) {}
        return 1.0;
    }

    public double getDistanceCost() {
        try {
            ObjectPlusPlus location = getLinks(MedicalProcedureLocationRoles.Performed_in)[0];
            if (location.getClass() == InField.class) {
                return ((InField) location).getAddedCosts();
            }
        } catch (Exception ignored) {}
        return 0;
    }

    public double getBaseCost() {
        return baseCost;
    }

    public void setBaseCost(double baseCost) {
        if (baseCost < 0) baseCost = 0;
        this.baseCost = baseCost;
    }

    public LocalTime getEstimatedLength() {
        return estimatedLength;
    }

    public void setEstimatedLength(LocalTime estimatedLength) {
        if (estimatedLength == null) {
            estimatedLength = LocalTime.of(0, 0);
        }
        this.estimatedLength = estimatedLength;
    }

}
