package Base;

import Appointment.Appointment;
import MedicalProcedure.MedicalProcedure;
import Pet.Pet;
import java.io.*;
import java.util.*;

public abstract class ObjectPlus implements Serializable {
    /** Map for all extents. */
    private static Map<Class<?>, List<ObjectPlus>> allExtents = new HashMap<>();

    /**
     * Constructor to add the current object to the extent of its class.
     */
    public ObjectPlus() {
        Class<?> theClass = this.getClass();
        allExtents.computeIfAbsent(theClass, k -> new ArrayList<>()).add(this);
    }

    /**
     * Writes all extents and static attributes to the output stream.
     * @param fileName name for the extensions file to write in
     */
    public static void writeExtents(String fileName) throws IOException {
        try (ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(fileName))) {
            stream.writeObject(allExtents);
            stream.writeObject(MedicalProcedure.getAllCodes());
            stream.writeObject(Pet.getAllChipIDs());
            stream.writeObject(Appointment.getDeleted());
        }

    }

    /**
     * Reads all extents and static attributes from the input stream.
     * @param fileName name for the extensions file to read from
     */
    public static void readExtents(String fileName) throws IOException, ClassNotFoundException {
        try (ObjectInputStream stream = new ObjectInputStream(new FileInputStream(fileName))) {
            allExtents.clear();
            allExtents.putAll((Map<Class<?>, List<ObjectPlus>>) stream.readObject());
            MedicalProcedure.setAllCodes((Set<String>) stream.readObject());
            Pet.setAllChipIDs((Set<String>) stream.readObject());
            Appointment.setDeleted((int) stream.readObject());
        }
    }

    /**
     * Returns the extent of objects for the given class type.
     * @param type the class type
     * @return a list of objects of the given class type
     */
    public static <T> List<T> getExtent(Class<T> type) throws ClassNotFoundException {
        List<ObjectPlus> extent = allExtents.get(type);
        if (extent != null) {
            return (List<T>) extent;
        }
        throw new ClassNotFoundException(String.format("%s. Stored extents: %s", type.toString(), allExtents.keySet()));
    }

    /**
     * Removes an object from its class extent, and whole class type from allExtents if it was the last object.
     * @param object the object to remove
     */
    public static void removeFromExtent(ObjectPlus object) throws Exception {
        Class<?> theClass = object.getClass();
        List<ObjectPlus> extent = allExtents.get(theClass);
        if (extent != null) {
            extent.remove(object);
            if (extent.isEmpty()) {
                allExtents.remove(theClass);
            }
        } else {
            throw new Exception("Unknown class " + theClass);
        }
    }

    /**
     * Displays the extent of the specified class.
     * @param theClass the class to show the extent of
     */
    public static void showExtent(Class<?> theClass) throws Exception {
        List<ObjectPlus> extent = allExtents.get(theClass);
        if (extent == null) {
            throw new Exception("Unknown class " + theClass);
        }

        System.out.println("Extent of the class: " + theClass.getSimpleName());
        extent.forEach(System.out::println);
    }
}