package OwnershipPeriod;

import Base.ObjectPlusPlus;
import java.time.LocalDate;

public class OwnershipPeriod extends ObjectPlusPlus {
    private LocalDate dateFrom;
    private LocalDate dateTo;

    // Roles
    public enum OwnershipOwnerRoles { Is_for, Was_for }
    public enum OwnershipPetRoles { Is_for, Was_for }

    // Constructors
    public static OwnershipPeriod create(LocalDate dateFrom, LocalDate dateTo) {
        return new OwnershipPeriod(dateFrom, dateTo);
    }

    public OwnershipPeriod (LocalDate dateFrom, LocalDate dateTo) {
        this.setDateFrom(dateFrom);
        this.setDateTo(dateTo);
    }

    // Methods
    @Override
    public String toString() {
        String info = (getDateTo() == null) ? " and has not ended yet!" : " and has ended " + getDateTo();
        return "Ownership started: " + getDateFrom() + info;
    }


    // Getters & Setters
    public LocalDate getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(LocalDate dateFrom) {
        if (dateFrom == null) {
            dateFrom = LocalDate.now();
        } else if (dateFrom.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Ownership cannot start in the future");
        }
        this.dateFrom = dateFrom;
    }

    public LocalDate getDateTo() {
        return dateTo;
    }

    public void setDateTo(LocalDate dateTo) {
        if (dateTo != null) {
            if (dateTo.isBefore(dateFrom)) {
                throw new IllegalArgumentException("Ownership cannot end before starting");
            }
        }
        this.dateTo = dateTo;
    }
}
