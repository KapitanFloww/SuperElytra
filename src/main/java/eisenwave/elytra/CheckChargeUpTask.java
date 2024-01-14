package eisenwave.elytra;

import java.util.Objects;

public class CheckChargeUpTask implements Runnable {

    private final SuperElytraService service;

    public CheckChargeUpTask(SuperElytraService service) {
        this.service = Objects.requireNonNull(service);
    }

    @Override
    public void run() {
        service.getElytraUsers().forEach(elytraUser -> {
            final var player = elytraUser.getDelegate();
            if (player.isGliding() || player.isFlying()) {
                return;
            }
            // If player is already charging up - continue with charge-up
            if (elytraUser.isChargingUp()) {
                elytraUser.chargeUp();
            }
        });
    }
}
