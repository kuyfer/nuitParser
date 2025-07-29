package com.ram.nuitparser.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ram.nuitparser.model.enrichment.Aircraft;
import com.ram.nuitparser.model.enrichment.Airline;
import com.ram.nuitparser.model.enrichment.AirportExtended;
import com.ram.nuitparser.model.enrichment.Country;
import com.ram.nuitparser.model.telex.asm.AsmMessage;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.*;

@Service
public class EnrichmentService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    // Changed to Lists since JSON files contain arrays
    private List<Airline> airlines = new ArrayList<>();
    private List<AirportExtended> airports = new ArrayList<>();
    private List<Aircraft> aircraftList = new ArrayList<>();
    private List<Country> countries = new ArrayList<>();

    @PostConstruct
    public void init() {
        loadAirlines();
        loadAirports();
        loadAircraft();
        loadCountries();
    }

    private void loadAirlines() {
        try (InputStream input = getClass().getResourceAsStream("/data/airlines.json")) {
            if (input != null) {
                airlines = objectMapper.readValue(input, new TypeReference<List<Airline>>() {});
            } else {
                System.err.println("Airlines JSON file not found");
            }
        } catch (Exception e) {
            System.err.println("Failed to load airlines: " + e.getMessage());
        }
    }

    private void loadAirports() {
        try (InputStream input = getClass().getResourceAsStream("/data/airportsExtended.json")) {
            if (input != null) {
                airports = objectMapper.readValue(input, new TypeReference<List<AirportExtended>>() {});
            } else {
                System.err.println("Airports JSON file not found");
            }
        } catch (Exception e) {
            System.err.println("Failed to load airports: " + e.getMessage());
        }
    }

    private void loadAircraft() {
        try (InputStream input = getClass().getResourceAsStream("/data/aircraft.json")) {
            if (input != null) {
                aircraftList = objectMapper.readValue(input, new TypeReference<List<Aircraft>>() {});
            } else {
                System.err.println("Aircraft JSON file not found");
            }
        } catch (Exception e) {
            System.err.println("Failed to load aircraft data: " + e.getMessage());
        }
    }

    private void loadCountries() {
        try (InputStream input = getClass().getResourceAsStream("/data/countries.json")) {
            if (input != null) {
                countries = objectMapper.readValue(input, new TypeReference<List<Country>>() {});
            } else {
                System.err.println("Countries JSON file not found");
            }
        } catch (Exception e) {
            System.err.println("Failed to load countries: " + e.getMessage());
        }
    }

    // Lookup methods
    public Optional<Airline> getAirlineByIata(String iata) {
        return airlines.stream()
                .filter(airline -> iata != null && iata.equals(airline.getIata()))
                .findFirst();
    }

    public Optional<AirportExtended> getAirportByIata(String iata) {
        return airports.stream()
                .filter(airport -> iata != null && iata.equals(airport.getIata()))
                .findFirst();
    }

    public Optional<Aircraft> getAircraftByIata(String iata) {
        return aircraftList.stream()
                .filter(aircraft -> iata != null && iata.equals(aircraft.getIataCode()))
                .findFirst();
    }

    public Optional<Country> getCountryByCode(String code) {
        return countries.stream()
                .filter(country -> code != null && code.equals(country.getIso_code()))
                .findFirst();
    }

    // ADD THIS MISSING METHOD
    public void enrich(AsmMessage message) {
        if (message == null) {
            System.err.println("Enrichment skipped: null message");
            return;
        }

        // Enrich airline information
        if (message.getFlightDesignator() != null && message.getFlightDesignator().length() >= 2) {
            String airlineCode = message.getFlightDesignator().substring(0, 2);
            getAirlineByIata(airlineCode).ifPresent(airline -> {
                message.setAirlineName(airline.getName());
                message.setAirlineCountry(airline.getCountry());
            });
        }

        // Enrich departure airport
        if (message.getDepartureAirport() != null) {
            getAirportByIata(message.getDepartureAirport()).ifPresent(airport -> {
                message.setDepartureAirportName(airport.getName());
                message.setDepartureTimezone(airport.getTzDatabaseTimezone());
            });
        }

        // Enrich arrival airport
        if (message.getArrivalAirport() != null) {
            getAirportByIata(message.getArrivalAirport()).ifPresent(airport -> {
                message.setArrivalAirportName(airport.getName());
                message.setArrivalTimezone(airport.getTzDatabaseTimezone());
            });
        }

        // Aircraft enrichment would go here if needed
    }
}