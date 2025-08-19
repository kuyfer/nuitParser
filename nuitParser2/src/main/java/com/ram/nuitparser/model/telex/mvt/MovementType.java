package com.ram.nuitparser.model.telex.mvt;

public enum MovementType {
    DEP,  // Departure
    ARR,  // Arrival
    DIV,  // Diversion
    CAN,  // Cancellation
    DLY;  // Delay

    public static MovementType fromCode(String code) {
        return switch(code) {
            case "DEP" -> DEP;
            case "ARR" -> ARR;
            case "DIV" -> DIV;
            case "CAN" -> CAN;
            case "DLY" -> DLY;
            default -> null;
        };
    }
}