package com.ram.nuitparser.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ram.nuitparser.model.enrichment.*;
import com.ram.nuitparser.model.telex.TelexMessage;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.*;

@Service
public class EnrichmentService {
    private static final Logger logger = LoggerFactory.getLogger(EnrichmentService.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    private List<Airline> airlines = new ArrayList<>();
    private List<AirportExtended> airports = new ArrayList<>();
    private List<Aircraft> aircraftList = new ArrayList<>();
    private List<Country> countries = new ArrayList<>();

    @PostConstruct
    public void init() {
        logger.info("Initializing EnrichmentService data loading");
        loadAirlines();
        loadAirports();
        loadAircraft();
        loadCountries();
        logger.info("Enrichment data loaded: {} airlines, {} airports, {} aircraft, {} countries",
                airlines.size(), airports.size(), aircraftList.size(), countries.size());
    }

    private void loadAirlines() {
        logger.debug("Loading airlines dataset");
        try (InputStream input = getClass().getResourceAsStream("/data/airlines.json")) {
            if (input != null) {
                airlines = objectMapper.readValue(input, new TypeReference<>() {
                });
                logger.info("Loaded {} airline records", airlines.size());
            } else {
                logger.error("Airlines JSON file not found in classpath");
            }
        } catch (Exception e) {
            logger.error("Failed to load airlines dataset: {}", e.getMessage(), e);
        }
    }

    private void loadAirports() {
        logger.debug("Loading airports dataset");
        try (InputStream input = getClass().getResourceAsStream("/data/airportsExtended.json")) {
            if (input != null) {
                airports = objectMapper.readValue(input, new TypeReference<>() {
                });
                logger.info("Loaded {} airport records", airports.size());
            } else {
                logger.error("Airports JSON file not found in classpath");
            }
        } catch (Exception e) {
            logger.error("Failed to load airports dataset: {}", e.getMessage(), e);
        }
    }

    private void loadAircraft() {
        logger.debug("Loading aircraft dataset");
        try (InputStream input = getClass().getResourceAsStream("/data/aircraft.json")) {
            if (input != null) {
                aircraftList = objectMapper.readValue(input, new TypeReference<>() {
                });
                logger.info("Loaded {} aircraft records", aircraftList.size());
            } else {
                logger.error("Aircraft JSON file not found in classpath");
            }
        } catch (Exception e) {
            logger.error("Failed to load aircraft dataset: {}", e.getMessage(), e);
        }
    }

    private void loadCountries() {
        logger.debug("Loading countries dataset");
        try (InputStream input = getClass().getResourceAsStream("/data/countries.json")) {
            if (input != null) {
                countries = objectMapper.readValue(input, new TypeReference<>() {
                });
                logger.info("Loaded {} country records", countries.size());
            } else {
                logger.error("Countries JSON file not found in classpath");
            }
        } catch (Exception e) {
            logger.error("Failed to load countries dataset: {}", e.getMessage(), e);
        }
    }

    public Optional<Airline> getAirlineByIata(String iata) {
        logger.debug("Looking up airline by IATA: {}", iata);
        return airlines.stream()
                .filter(airline -> iata != null && iata.equals(airline.getIata()))
                .findFirst();
    }

    public Optional<AirportExtended> getAirportByIata(String iata) {
        logger.debug("Looking up airport by IATA: {}", iata);
        return airports.stream()
                .filter(airport -> iata != null && iata.equals(airport.getIata()))
                .findFirst();
    }

    public Optional<Aircraft> getAircraftByIata(String iata) {
        logger.debug("Looking up aircraft by IATA: {}", iata);
        return aircraftList.stream()
                .filter(aircraft -> iata != null && iata.equals(aircraft.getIataCode()))
                .findFirst();
    }

    public Optional<Country> getCountryByCode(String code) {
        logger.debug("Looking up country by code: {}", code);
        return countries.stream()
                .filter(country -> code != null && code.equals(country.getIso_code()))
                .findFirst();
    }

    public void enrich(TelexMessage message) {
        if (message == null) {
            logger.warn("Enrichment skipped: null message received");
            return;
        }

        logger.info("Starting enrichment for flight: {}", message.getFlightDesignator());

        // Enrich airline information
        String designator = message.getFlightDesignator();
        if (designator != null && designator.length() >= 2) {
            String airlineCode = designator.substring(0, 2);
            logger.debug("Extracted airline code: {}", airlineCode);

            getAirlineByIata(airlineCode).ifPresentOrElse(airline -> {
                message.setAirlineName(airline.getName());
                message.setAirlineCountry(airline.getCountry());
                logger.debug("Enriched airline: {} - {}", airlineCode, airline.getName());
            }, () -> logger.warn("Airline not found for code: {}", airlineCode));
        }

        // Enrich departure airport
        String depAirport = message.getDepartureAirport();
        if (depAirport != null) {
            getAirportByIata(depAirport).ifPresentOrElse(airport -> {
                message.setDepartureAirportName(airport.getName());
                message.setDepartureTimezone(airport.getTzDatabaseTimezone());
                logger.debug("Enriched departure airport: {}", airport.getName());
            }, () -> logger.warn("Departure airport not found for IATA: {}", depAirport));
        }

        // Enrich arrival airport
        String arrAirport = message.getArrivalAirport();
        if (arrAirport != null) {
            getAirportByIata(arrAirport).ifPresentOrElse(airport -> {
                message.setArrivalAirportName(airport.getName());
                message.setArrivalTimezone(airport.getTzDatabaseTimezone());
                logger.debug("Enriched arrival airport: {}", airport.getName());
            }, () -> logger.warn("Arrival airport not found for IATA: {}", arrAirport));
        }

        logger.info("Completed enrichment for flight: {}", designator);
    }
}