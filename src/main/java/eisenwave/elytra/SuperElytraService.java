package eisenwave.elytra;

import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SuperElytraService {

    private final double speedMultiplier;
    private final double launchStrength;
    private final boolean enabledOnDefault;
    private final Map<UUID, SuperElytraPlayerFacade> activePlayersMap = new ConcurrentHashMap<>();

    public SuperElytraService(boolean enabledOnDefault, double speedMultiplier, double launchStrength) {
        this.speedMultiplier = speedMultiplier;
        this.enabledOnDefault = enabledOnDefault;
        this.launchStrength = launchStrength;
    }

    public SuperElytraPlayerFacade getElytraUser(final Player player) {
        final var uuid = player.getUniqueId();
        if (activePlayersMap.containsKey(uuid)) {
            return activePlayersMap.get(uuid);
        }
        // Else enable super elytra for given player
        final var elytraPlayer = new SuperElytraPlayerFacade(player, launchStrength, speedMultiplier, enabledOnDefault);
        activePlayersMap.put(uuid, elytraPlayer);
        return elytraPlayer;
    }

    public Collection<SuperElytraPlayerFacade> getElytraUsers() {
        return activePlayersMap.values();
    }

    public void removePlayer(Player player) {
        activePlayersMap.remove(player.getUniqueId());
    }
}
