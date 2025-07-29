package com.ram.nuitparser.enums;


public enum TelexType {
    // Common Schedule & Operational Telex Types
    ASM,    // adhoc Schedule Message
    SSM,    // Slot/Supplementary Schedule Message
    MVT,    // Movement Message
    LDM,    // Load Message
    ULD,    // Unit Load Device Message
    PSM,    // Passenger Service Message
    CPM,    // Container Pallet Message
    DIV,    // Diversion Message
    DLA,    // Delay Message
    EDL,    // Electronic Data List
    FLM,    // Flight List Message
    MVTDEP, // Departure message (part of MVT)
    MVTARR, // Arrival message (part of MVT)

    // AFTN / Operational Control Messages
    FPL,    // Flight Plan
    CHG,    // Change to flight plan
    DLA_AFTN,  // Delay (AFTN)
    CNL,    // Cancellation
    DEP,    // Departure
    ARR,    // Arrival
    RPL,    // Repetitive flight plan
    RQP,    // Request Position
    RQS,    // Request Supplementary info
    SPL,    // Supplementary Flight Plan

    // Crew & Ground Services
    CREW,   // Crew message
    GHT,    // Ground handling transfer
    EFF,    // Effective time messages

    // Unknown
    UNKNOWN;

    public static TelexType fromString(String type) {
        try {
            return TelexType.valueOf(type.trim().toUpperCase());
        } catch (Exception e) {
            return UNKNOWN;
        }
    }
}
