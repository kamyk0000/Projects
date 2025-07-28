package MedicalProcedure.ByLocation;

import MedicalProcedure.MedicalProcedure;

public class InField extends ProcedureLocation {
    private String address;
    private double distanceFromClinic;
    private static final double costPerKm = 2.0;
    public double getAddedCosts() {
        return costPerKm * distanceFromClinic * ((toClinicTransportation) ? 2 : 1);
    }
    private boolean toClinicTransportation;

    // Roles

    // Constructors
    public static InField create(String address, MedicalProcedure procedure) throws Exception {
        return create(address, 0, false, procedure);
    }

    public static InField create(String address, double distanceFromClosestClinic, boolean toClinicTransportation, MedicalProcedure procedure) throws Exception {
        if (address != null && procedure != null && checkDistanceFromClinic(distanceFromClosestClinic))return new InField(address, distanceFromClosestClinic, toClinicTransportation, procedure);
        else throw new Exception("Invalid address or procedure");
    }

    private InField(String address, double distanceFromClosestClinic, boolean toClinicTransportation, MedicalProcedure procedure) throws Exception {
        super();
        this.setAddress(address);
        this.setDistanceFromClinic(distanceFromClosestClinic);
        this.toClinicTransportation = toClinicTransportation;
        procedure.linkLocation(this);
    }

    // Getters & Setters
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        if (address == null || address.isEmpty()) {
            throw new IllegalArgumentException("Address cannot be null or empty");
        }
        this.address = address;
    }

    public double getDistanceFromClinic() {
        return distanceFromClinic;
    }

    public void setDistanceFromClinic(double distanceFromClinic) {
        this.distanceFromClinic = distanceFromClinic;
    }

    private static boolean checkDistanceFromClinic(double distanceFromClinic) {
        if (distanceFromClinic < 0) {
            throw new IllegalArgumentException("Distance From Clinic cannot be negative");
        }
        return true;
    }

    public boolean isToClinicTransportation() {
        return toClinicTransportation;
    }

    public void setToClinicTransportation(boolean toClinicTransportation) {
        this.toClinicTransportation = toClinicTransportation;
    }
}
