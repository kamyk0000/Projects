package MedicalProcedure.ByType;

public interface IVaccination {
    double getTotalCost();
    String getAgainstPathogen();
    void setAgainstPathogen(String againstPathogen);
    String getVaccineInfo();
    void setVaccineInfo(String vaccineInfo);
    double getVaccinePrice();
    void setVaccinePrice(double vaccinePrice);
}
