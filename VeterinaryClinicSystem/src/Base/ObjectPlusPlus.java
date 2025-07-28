package Base;

import java.io.Serializable;
import java.util.*;

public abstract class ObjectPlusPlus extends ObjectPlus implements Serializable {

    /** Stores information about all allowed connections. */
    private static Map<Class<?>, Map<Class<?>, EnumMap<? extends Enum<?>, Enum>>> roleNames = new HashMap<>();

    /** Stores information about all connections / links of this object. */
    private Map<Enum, Map<Object, ObjectPlusPlus>> links = new HashMap<>();

    /** Stores information about all parts connected with any objects and classes of that whole objects. */
    private static Map<ObjectPlusPlus, Class<?>> allParts = new HashMap<>();

    public ObjectPlusPlus() {
        super();
    }

    /**
     * Adds a rule for acceptable roles (used for two-way validation and recognition or smth) for two classes with the use of prepared Enums.
     * @param class1 first (left) class for connection
     * @param class2 second (right) class for connection
     * @param roleName role name for the first (left) class
     * @param reverseRoleName role name for the second (right) class
     */
    public static void addRoleName(Class<?> class1, Class<?> class2, Enum<?> roleName, Enum<?> reverseRoleName) {
        roleNames
                .computeIfAbsent(class1, k -> new HashMap<>())
                .computeIfAbsent(class2, f -> new EnumMap<>(roleName.getClass()))
                .put(roleName, reverseRoleName);

        roleNames
                .computeIfAbsent(class2, k -> new HashMap<>())
                .computeIfAbsent(class1, f -> new EnumMap<>(reverseRoleName.getClass()))
                .put(reverseRoleName, roleName);
    }

    /**
     * Main method for link severing (removal) between a specified object / qualifier that works both ways.
     * Used mainly for deletion of part objects, and in deletion of main objects as well.
     * @param roleName role name for which the link was created
     * @param qualifier object / qualifier of an object that links are to be severed from
     * @param counter used for reverse protection
     * @throws Exception
     */
    private void removeLink(Enum<?> roleName, Object qualifier, int counter) throws Exception {
        if (counter < 1) return;

        Map<Object, ObjectPlusPlus> objectLinks = links.get(roleName);
        if (objectLinks == null) {
            throw new Exception("No links for the role: " + roleName);
        }

        ObjectPlusPlus targetObject = objectLinks.remove(qualifier);
        if (targetObject == null) {
            throw new Exception("No link for the qualifier: " + qualifier);
        }

        // Removes part objects
        if (allParts != null && !allParts.isEmpty()) {
            if (allParts.containsKey(qualifier)) {
                if(allParts.get(qualifier) == this.getClass()) {
                    allParts.remove(qualifier);
                    removeFromExtent(targetObject);
                }
            }
        }

        if (objectLinks.isEmpty()) {
            links.remove(roleName);
        }

        Enum<?> reverseRoleName = roleNames.get(this.getClass()).get(targetObject.getClass()).get(roleName);
        targetObject.removeLink(reverseRoleName, this, counter - 1);
    }

    public void removeLink(Enum<?> roleName, Object qualifier) throws Exception {
        removeLink(roleName, qualifier, 2);
    }

    /**
     * Creates a new connection / link between this object and a target object, used also in connecting parts.
     * @param roleName role name for which the link will be created
     * @param targetObject object of an object that links are to be created for
     * @param qualifier qualifier (also can be the same object in case of absence) of an object that links are to be created for
     * @param counter used for reverse protection, overstacking etc.
     */
    private void addLink(Enum<?> roleName, ObjectPlusPlus targetObject, Object qualifier, int counter) {
        if (counter < 1) return;

        // Check if the connection is possible
        if (!roleNames.containsKey(this.getClass())) {
            throw new IllegalArgumentException("Illegal Link: no Links available for the class: " + this.getClass());
        } else if (!roleNames.get(this.getClass()).containsKey(targetObject.getClass())) {
            throw new IllegalArgumentException("Illegal Link: no Links available between classes: " + this.getClass() + " and: " + targetObject.getClass());
        } else if (!roleNames.get(this.getClass()).get(targetObject.getClass()).containsKey(roleName)) {
            throw new IllegalArgumentException("Illegal Link: no Links available between classes: " + this.getClass() + " and: " + targetObject.getClass() + " for role: " + roleName);
        }

        Enum<?> reverseRoleName = roleNames.get(this.getClass()).get(targetObject.getClass()).get(roleName);

        Map<Object, ObjectPlusPlus> objectLinks = links.computeIfAbsent(roleName, k -> new HashMap<>());

        if (!objectLinks.containsKey(qualifier)) {
            objectLinks.put(qualifier, targetObject);
            targetObject.addLink(reverseRoleName, this, this, counter - 1);
        }
    }

    public void addLink(Enum<?> roleName, ObjectPlusPlus targetObject, Object qualifier) {
        addLink(roleName, targetObject, qualifier, 2);
    }

    public void addLink(Enum<?> roleName, ObjectPlusPlus targetObject) {
        addLink(roleName, targetObject, targetObject);
    }

    public void addPart(Enum<?> roleName, ObjectPlusPlus partObject) throws Exception {
        // Check if the part exists somewhere
        if (allParts.containsKey(partObject)) {
            throw new Exception("The part is already connected to a whole!");
        }
        addLink(roleName, partObject);
        // Store adding the object as a part
        allParts.put(partObject, this.getClass());
    }

    public ObjectPlusPlus[] getLinks(Enum<?> roleName) throws Exception {
        Map<Object, ObjectPlusPlus> objectLinks;

        if (!links.containsKey(roleName)) {
            // No links for the role
            throw new Exception("No links for the role: " + roleName);
        }
        objectLinks = links.get(roleName);
        return objectLinks.values().toArray(new ObjectPlusPlus[0]);
    }

    /**
     * Returns a list of linked objects for the specified role.
     * @param roleName the role name
     * @return a list of linked objects
     * @throws Exception
     */
    public <T> List<T> getLinksList(Enum<?> roleName) throws Exception {
        Map<Object, ObjectPlusPlus> objectLinks = links.get(roleName);
        if (objectLinks == null) {
            throw new Exception("No links for the role: " + roleName);
        }

        List<T> resultList = new ArrayList<>();
        for (ObjectPlusPlus opp : objectLinks.values()) {
            resultList.add((T) opp);
        }

        return resultList;
    }

    /**
     * Displays all links of an object for the provided role on the console.
     * @param roleName the role name
     * @throws Exception
     */
    public void showLinks(Enum<?> roleName) throws Exception {
        Map<Object, ObjectPlusPlus> objectLinks = links.get(roleName);
        if (objectLinks == null) {
            throw new Exception("No links for the role: " + roleName);
        }

        System.out.println(this.getClass().getSimpleName() + " links, role '" + roleName + "':");
        objectLinks.values().forEach(obj -> System.out.println("   " + obj));
    }

    /**
     * Used for an easier access to object for a specified role name connected via qualified associations .
     * @param roleName the role name
     * @param qualifier for the qualified association
     * @return a objects connected via a qualifier
     * @throws Exception
     */
    public ObjectPlusPlus getLinkedObject(Enum<?> roleName, Object qualifier) throws Exception {
        Map<Object, ObjectPlusPlus> objectLinks = links.get(roleName);
        if (objectLinks == null || !objectLinks.containsKey(qualifier)) {
            throw new Exception("No link for the role: " + roleName + " or qualifier: " + qualifier);
        }
        return objectLinks.get(qualifier);
    }
}
