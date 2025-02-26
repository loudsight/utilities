package com.loudsight.useful.entity.utils;

import com.loudsight.useful.helper.logging.LoggingHelper;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertiesToYamlConverter {
    private static final LoggingHelper LOGGER = LoggingHelper.wrap(PropertiesToYamlConverter.class);

    public static void main(String[] args) {
        // Specify the input and output file paths
        String propertiesFilePath = "c:/dev/code/victory2025/webapps/backendserver/src/main/config/loudsight.net.properties";
        String yamlFilePath = "c:/dev/code/victory2025/webapps/backendserver/src/main/config/loudsight.net.yaml";

        try {
            // Convert properties file to Map
            Map<String, Object> propertiesMap = propertiesToMap(propertiesFilePath);

            Map<String, Object> propertiesMapOfMap = new HashMap<>();
            propertiesToMapOfMaps(propertiesMap, propertiesMapOfMap);

            // Save Map to YAML file
            saveYaml(propertiesMapOfMap, yamlFilePath);

            LOGGER.logInfo("Conversion complete! YAML file saved as: {}", yamlFilePath);
        } catch (IOException e) {
            LOGGER.logError("Error during conversion", e);
        }
    }

    private static void propertiesToMapOfMaps(Map<String, Object> from, Map<String, Object> to) {
        from.forEach((k, v) -> {
            propertiesToMapOfMaps(k, v, to);
        });
    }

    private static void propertiesToMapOfMaps(String k, Object v, Map<String, Object> to) {

//        BiFunction<Map<String, Object>, Map.Entry<String, Object>, Map<String, Object>> accumulator = (a, b) -> {
            var x = k.split("\\.", 2);
            if (x.length > 1) {
                to.compute(x[0], (m, n) -> {
                    if (n == null) {
                        n = new HashMap<>();
                    }
                    propertiesToMapOfMaps(x[1], v, (Map)n);
                    return n;
                });
            } else {
                to.put(k, v.toString());
            }
    }
    // Convert .properties file to a Map
    private static Map<String, Object> propertiesToMap(String propertiesFilePath) throws IOException {
        Properties properties = new Properties();
        
        // Load properties file
        try (FileInputStream inputStream = new FileInputStream(propertiesFilePath)) {
            properties.load(inputStream);
        }
        
        // Convert Properties to a Map
        return (Map) properties;
    }

    // Save the Map to a YAML file
    private static void saveYaml(Map<String, Object> propertiesMap, String yamlFilePath) throws IOException {
        // Setup YAML options for pretty printing
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK); // Block style (indented)
        Yaml yaml = new Yaml(options);

        // Write the Map as a YAML file
        try (FileWriter writer = new FileWriter(yamlFilePath)) {
            yaml.dump(propertiesMap, writer);
        }
    }
}
