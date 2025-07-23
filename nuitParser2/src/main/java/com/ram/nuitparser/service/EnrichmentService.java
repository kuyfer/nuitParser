package com.ram.nuitparser.service;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.ram.nuitparser.model.enrichment.Aircraft;
import com.ram.nuitparser.model.enrichment.Airline;
import com.ram.nuitparser.model.enrichment.AirportExtended;
import com.ram.nuitparser.model.enrichment.Country;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.*;

@Service
public class EnrichmentService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Map<String, Airline> airlinesByIcao = new HashMap<>();
    private Map<String, AirportExtended> airportsByIata = new HashMap<>();
    private Map<String, Aircraft> aircraftByIata = new HashMap<>();
    private Map<String, Country> countriesByCode = new HashMap<>();

    @PostConstruct
    public void init() {
        loadAirlines();
        loadAirports();
        loadAircraft();
        loadCountries();
    }

    private void loadAirlines() {
        try (InputStream input = getClass().getResourceAsStream("/data/airlines.json")) {
            List<Airline> airlines = objectMapper.readValue(input, new TypeReference<>() {});
            for (Airline airline : airlines) {
                if (airline.getIcao() != null) {
                    airlinesByIcao.put(airline.getIcao(), airline);
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to load airlines: " + e.getMessage());
        }
    }

    private void loadAirports() {
        try (InputStream input = getClass().getResourceAsStream("/data/airportsExtended.json")) {
            List<AirportExtended> airports = objectMapper.readValue(input, new TypeReference<>() {});
            for (AirportExtended airport : airports) {
                if (airport.getIata() != null) {
                    airportsByIata.put(airport.getIata(), airport);
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to load airports: " + e.getMessage());
        }
    }

    private void loadAircraft() {
        try (InputStream input = getClass().getResourceAsStream("/data/aircraft.json")) {
            List<Aircraft> aircraftList = objectMapper.readValue(input, new TypeReference<>() {});
            for (Aircraft aircraft : aircraftList) {
                if (aircraft.getIataCode() != null) {
                    aircraftByIata.put(aircraft.getIataCode(), aircraft);
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to load aircraft data: " + e.getMessage());
        }
    }

    private void loadCountries() {
        try (InputStream input = getClass().getResourceAsStream("/data/countries.json")) {
            List<Country> countries = objectMapper.readValue(input, new TypeReference<>() {});
            for (Country country : countries) {
                countriesByCode.put(country.getIso_code(), country);
            }
        } catch (Exception e) {
            System.err.println("Failed to load countries: " + e.getMessage());
        }
    }


    public Optional<Airline> getAirlineByIcao(String icao) {
        return Optional.ofNullable(airlinesByIcao.get(icao));
    }

    public Optional<AirportExtended> getAirportByIata(String iata) {
        return Optional.ofNullable(airportsByIata.get(iata));
    }

    public Optional<Aircraft> getAircraftByIata(String iata) {
        return Optional.ofNullable(aircraftByIata.get(iata));
    }

    public Optional<Country> getCountryByCode(String code) {
        return Optional.ofNullable(countriesByCode.get(code));
    }
}
