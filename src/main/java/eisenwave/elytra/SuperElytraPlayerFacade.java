package eisenwave.elytra;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Objects;

/**
 * The SuperElytra wrapper for a player.
 */
@Getter
@Setter
public class SuperElytraPlayerFacade {

    private final Player delegate;
    private final double launchStrength;
    private final double speedMultiplier;

    private int chargeUpTicks = -1;
    private boolean superElytraEnabled;

    public SuperElytraPlayerFacade(final Player delegate,
                                   double launchStrength,
                                   double speedMultiplier,
                                   boolean superElytraEnabled) {
        this.launchStrength = launchStrength;
        this.speedMultiplier = speedMultiplier;
        this.delegate = Objects.requireNonNull(delegate);
        this.superElytraEnabled = superElytraEnabled;
    }

    public boolean isChargingUp() {
        return chargeUpTicks > -1;
    }

    public void resetChargeUp() {
        this.chargeUpTicks = -1;
    }

    public void startChargeUp() {
        this.chargeUpTicks = 0;
    }

    /**
     * Continue charging-up this player.
     */
    public void chargeUp() {
        this.chargeUpTicks = chargeUpTicks  + 1;
        if (chargeUpTicks <= 20) { // if below 1 second, skip
            return;
        }
        final var location = delegate.getLocation();
        final var world = delegate.getWorld();

        world.spawnParticle(Particle.SMOKE_NORMAL, location, 1, 0.2F, 0.2F, 0.2F, 0.0F); // radius 30
        if (chargeUpTicks % 3 == 0) {
            delegate.playSound(location, Sound.ENTITY_TNT_PRIMED, 0.1F, 0.1F);
            if (chargeUpTicks >= 60) { // 20 ticks 1 seconds -> 3 seconds
                world.spawnParticle(Particle.FLAME, location, 1, 0.4F, 0.1F, 0.4F, 0.01F);
                delegate.playSound(location, Sound.ENTITY_BAT_TAKEOFF, 0.1F, 0.1F);
            }
        }
    }

    /**
     * Perform a liftoff for the given player, after charging-up.
     */
    public void liftoff() {
        final var location = delegate.getLocation();
        final var vector = new Vector(0, launchStrength, 0);
        final var direction = location.getDirection().add(vector);

        delegate.setVelocity(delegate.getVelocity().add(direction));
        delegate.playSound(location, Sound.ENTITY_ENDER_DRAGON_FLAP, 0.1F, 2.0F);
        location.getWorld().spawnParticle(Particle.CLOUD, location, 30, 0.5F, 0.5F, 0.5F, 0.0F);
    }

    /**
     * Boost this player's elytra-flight.
     */
    public void boostElytraFlight() {
        boostElytraFlight(0.0, false);
    }

    /**
     * Boost this player's elytra-flight
     * @param base velocity
     */
    public void boostElytraFlight(double base, boolean superBoost) {
        if (superElytraEnabled) {
            final double speed = base + speedMultiplier;
            final var vector = new Vector(0, delegate.getLocation().getDirection().getY(), 0);
            delegate.setVelocity(delegate.getVelocity().add(vector.multiply(speed)));
        }
        if (superBoost) {
            final var location = delegate.getLocation();
            delegate.playSound(location, Sound.ENTITY_BAT_TAKEOFF, 0.1F, 0.1F);
        }
    }
}
