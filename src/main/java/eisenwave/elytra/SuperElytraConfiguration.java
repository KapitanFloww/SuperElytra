package eisenwave.elytra;

import lombok.extern.java.Log;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Objects;

@Log
public class SuperElytraConfiguration {

    private final static boolean DEFAULT_ENABLED = false;
    private final static int DEFAULT_CHARGE_UP_TIME = 60;
    private final static double DEFAULT_FLAP_BASE = 1.0;
    private final static double DEFAULT_SPEED_MULTIPLIER = 1.0;
    private final static double DEFAULT_LAUNCH_MULTIPLIER = 1.0;

    private final FileConfiguration configuration;

    public SuperElytraConfiguration(FileConfiguration configuration) {
        this.configuration = Objects.requireNonNull(configuration);
        validateConfiguration(this.configuration);
    }

    public double getSpeedMultiplier() {
        return configuration.getDouble("speed_multiplier", DEFAULT_SPEED_MULTIPLIER);
    }

    public double getLaunchMultiplier() {
        return configuration.getDouble("launch_multiplier", DEFAULT_LAUNCH_MULTIPLIER);
    }

    public double getFlapBase() {
        return configuration.getDouble("flap_base", DEFAULT_FLAP_BASE);
    }

    public int getChargeUpTime() {
        return configuration.getInt("chargeup_time", DEFAULT_CHARGE_UP_TIME);
    }

    public boolean getEnabledOnDefault() {
        return configuration.getBoolean("enabled_on_default", DEFAULT_ENABLED);
    }

    private void validateConfiguration(FileConfiguration fileConfiguration) {
        try (final InputStreamReader in = new InputStreamReader(Objects.requireNonNull(SuperElytraPlugin.class.getResourceAsStream("/config.yml")))) {
            final var expectedConfiguration = new YamlConfiguration();
            expectedConfiguration.load(in);

            // Check if any key is missing in actual configuration
            final var missingKeys = new ArrayList<String>();
            final var expectedKeys = expectedConfiguration.getKeys(true);
            final var actualKeys = fileConfiguration.getKeys(true);
            for (String expectedKey : expectedKeys) {
                if (!actualKeys.contains(expectedKey)) {
                    log.severe("Configuration is missing key: " + expectedKey);
                    missingKeys.add(expectedKey);
                }
            }
            if (!missingKeys.isEmpty()) {
                throw new IllegalStateException("Configuration is missing keys: %s".formatted(String.join(", ", missingKeys)));
            }
        } catch (IOException | InvalidConfigurationException ex) {
            log.severe("Could not load config.yml from resource folder: " + ex);
        }
    }
}
