package eisenwave.elytra;

import eisenwave.elytra.command.ElytraModeCommand;
import lombok.extern.java.Log;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

@Log
public class SuperElytraPlugin extends JavaPlugin {

    private final static String PERMISSION_LAUNCH = "superelytra.launch";
    private final static String PERMISSION_GLIDE = "superelytra.glide";

    private static final double BASE_SPEED = 0.05;
    private static final double BASE_LAUNCH = 3.0;
    
    @Override
    public void onEnable() {

        // Init configuration
        saveDefaultConfig();
        final var configuration = new SuperElytraConfiguration(getConfig());

        // Init services
        final var service = new SuperElytraService(
                configuration.getEnabledOnDefault(),
                configuration.getSpeedMultiplier() * BASE_SPEED,
                configuration.getLaunchMultiplier() * BASE_LAUNCH);

        // Init commands
        Objects.requireNonNull(getCommand("elytramode")).setExecutor(new ElytraModeCommand(service));

        // Init listeners
        final var listener = new SuperElytraListener(configuration.getChargeUpTime(), PERMISSION_GLIDE, PERMISSION_LAUNCH, service, configuration.getFlapBase());
        getServer().getPluginManager().registerEvents(listener, this);

        // Init scheduled tasks
        getServer().getScheduler().runTaskTimer(this, new CheckChargeUpTask(service), 0, 1); // check every tick

        log.info("Plugin enabled");
    }
}
