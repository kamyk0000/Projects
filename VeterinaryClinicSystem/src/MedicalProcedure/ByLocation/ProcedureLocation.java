package MedicalProcedure.ByLocation;

import Base.ObjectPlusPlus;
import MedicalProcedure.MedicalProcedure;

public class ProcedureLocation extends ObjectPlusPlus {
    // Roles
    public enum LocationMedicalProcedureRoles {
        Location_of
    }

    static {
        ObjectPlusPlus.addRoleName(ProcedureLocation.class, MedicalProcedure.class, LocationMedicalProcedureRoles.Location_of, MedicalProcedure.MedicalProcedureLocationRoles.Performed_in);
        ObjectPlusPlus.addRoleName(InClinic.class, MedicalProcedure.class, LocationMedicalProcedureRoles.Location_of, MedicalProcedure.MedicalProcedureLocationRoles.Performed_in);
        ObjectPlusPlus.addRoleName(InField.class, MedicalProcedure.class, LocationMedicalProcedureRoles.Location_of, MedicalProcedure.MedicalProcedureLocationRoles.Performed_in);
    }

    // Constructors
    public ProcedureLocation() {
        super();
    }
}
