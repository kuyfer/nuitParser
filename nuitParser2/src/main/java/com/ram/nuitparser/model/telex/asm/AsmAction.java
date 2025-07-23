package com.ram.nuitparser.model.telex.asm;

public enum AsmAction {

    NEW, // New flight leg
    CNL, // Cancelled flight
    RIN, // Reinstate flight
    RPL, // Replace flight
    ADM, // Administrative change
    CON, // Consolidation
    EQT, // Equipment change
    FLT, // Flight data change
    RRT, // Route change
    TIM  // Time change
}