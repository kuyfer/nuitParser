package org.example;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class CountryDatToJsonConverter {

    public static void main(String[] args) {
        String inputFile = "C:\\Users\\pc\\Desktop\\stage\\rawData\\countries.dat";      // Input file path
        String outputFile = "countries.json";    // Output JSON file

        JSONArray countries = new JSONArray();

        try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] fields = parseCSVLine(line);
                if (fields.length < 3) continue;

                JSONObject country = new JSONObject();
                country.put("iso_code", clean(fields[1]));
                country.put("name", clean(fields[0]));

                countries.put(country);
            }

            try (FileWriter writer = new FileWriter(outputFile)) {
                writer.write(countries.toString(4)); // pretty print
            }

            System.out.println("✅ Countries JSON saved to: " + outputFile);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Handles quoted strings and \N as null
    private static Object clean(String value) {
        if (value == null || value.equals("\\N")) return JSONObject.NULL;
        return value.replaceAll("^\"|\"$", ""); // Strip quotes
    }

    // Safely splits CSV line with commas inside quotes
    private static String[] parseCSVLine(String line) {
        return line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
    }
}
