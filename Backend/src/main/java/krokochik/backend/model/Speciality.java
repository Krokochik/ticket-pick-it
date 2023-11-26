package krokochik.backend.model;

public enum Speciality {
    ALLERG("Allergist/Immunologist"),
    ANESTH("Anesthesiologist"),
    CARDIO("Cardiologist"),
    DERMA("Dermatologist"),
    ENDO("Endocrinologist"),
    GASTRO("Gastroenterologist"),
    HEMA("Hematologist"),
    IDS("Infectious Disease Specialist"),
    NEPHRO("Nephrologist"),
    ONCO("Oncologist"),
    OPHTA("Ophthalmologist"),
    ENTS("Otorhinolaryngologist"),
    PSYCHIATRIST("Psychiatrist"),
    UROLOGIST("Urologist");

    private final String fullName;

    Speciality(String value) {
        fullName = value;
    }

    public String fullName() {
        return fullName;
    }
}
