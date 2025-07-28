package MedicalProcedure.ByLocation;

import MedicalProcedure.MedicalProcedure;

public class InClinic extends ProcedureLocation {
    public static String clinicAddress = "Warszawa, 05-023, ul.Akacjowa 12/3";
    public double inClinicDiscount;

    // Roles

    // Constructors
    public static InClinic create(double inClinicDiscount, MedicalProcedure procedure) throws Exception {
        if (checkInClinicDiscount(inClinicDiscount) && procedure != null) return new InClinic(inClinicDiscount, procedure);
        else return null;
    }

    private InClinic(double inClinicDiscount, MedicalProcedure procedure) throws Exception {
        super();
        this.inClinicDiscount = inClinicDiscount;
        procedure.linkLocation(this);
    }

    // Getters & Setters
    public static String getClinicAddress() {
        return clinicAddress;
    }

    public static void setClinicAddress(String clinicAddress) {
        InClinic.clinicAddress = clinicAddress;
    }

    public double getInClinicDiscount() {
        return inClinicDiscount;
    }

    public void setInClinicDiscount(double inClinicDiscount) {
        if (checkInClinicDiscount(inClinicDiscount)) this.inClinicDiscount = inClinicDiscount;
    }

     public static boolean checkInClinicDiscount(double inClinicDiscount) {
         if (inClinicDiscount < 0 || inClinicDiscount > 1){
             throw new IllegalArgumentException("inClinicDiscount must be between 0 and 1 (0% - 100%)");
         }
         return true;
     }
}
