package eisenwave.elytra;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.util.Objects;

public class SuperElytraListener implements Listener {

    private final SuperElytraService superElytraService;
    private final String permissionGlide;
    private final String permissionLaunch;
    private final int requiredChargeUpTime;
    private final double flapBase;

    public SuperElytraListener(final int requiredChargeUpTime,
                               final String permissionGlide,
                               final String permissionLaunch,
                               final SuperElytraService superElytraService,
                               final double flapBase
    ) {
        this.requiredChargeUpTime = requiredChargeUpTime;
        this.permissionGlide = permissionGlide;
        this.permissionLaunch = permissionLaunch;
        this.superElytraService = Objects.requireNonNull(superElytraService);
        this.flapBase = flapBase;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        superElytraService.removePlayer(event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        final var player = event.getPlayer();

        // Return if player is not gliding or does not have valid permission
        if (!player.isGliding() || !player.hasPermission(permissionGlide)) {
            return;
        }

        // Else, boost elytra flight
        final var elytraUser = superElytraService.getElytraUser(player);
        elytraUser.boostElytraFlight();
    }

    @EventHandler(ignoreCancelled = true)
    public void onToggleSneak(PlayerToggleSneakEvent event) {
        final var player = event.getPlayer();

        if (player.isFlying()) {
            return;
        }
        final var chestPlate = player.getEquipment().getChestplate();
        if (chestPlate == null || chestPlate.getType() != Material.ELYTRA) {
            return;
        }
        if (!player.hasPermission(permissionLaunch)) {
            return;
        }
        
        final var elytraUser = superElytraService.getElytraUser(player);
        // Player is gliding, boost extra
        if (player.isGliding()) {
            boostElytra(event, elytraUser);
            return;
        }
        // Player is on ground, charge elytra
        charge(event, elytraUser);
    }

    private void boostElytra(PlayerToggleSneakEvent event, SuperElytraPlayerFacade playerFacade) {
        if (event.isSneaking()) {
            playerFacade.boostElytraFlight(flapBase, true); // while in flight, boost
        }
    }

    private void charge(PlayerToggleSneakEvent event, SuperElytraPlayerFacade elytraUser) {
        // player -> sneaking
        if (event.isSneaking()) {
            elytraUser.startChargeUp();
            return;
        }

        // player -> released sneaking
        // If sneak released and charge-up-time reached
        if (elytraUser.getChargeUpTicks() >= requiredChargeUpTime) {
            elytraUser.liftoff();
        }
        elytraUser.resetChargeUp();
    }
    
}
