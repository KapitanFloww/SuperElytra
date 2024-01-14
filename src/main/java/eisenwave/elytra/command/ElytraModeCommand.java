package eisenwave.elytra.command;

import eisenwave.elytra.SuperElytraService;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ElytraModeCommand implements CommandExecutor, TabCompleter {
    
    private static final String PREFIX_ERR = ChatColor.RED + "[SuperElytra] " + ChatColor.RESET;
    private static final String PREFIX_MSG = ChatColor.BLUE + "[SuperElytra] " + ChatColor.RESET;
    private static final String ERROR_BAD_PLAYER = PREFIX_ERR + "Only players can toggle their elytra mode";
    private static final String MSG_ON = PREFIX_MSG + "You enabled enhanced flight";
    private static final String MSG_OFF = PREFIX_MSG + "You disabled enhanced flight";
    
    private final SuperElytraService elytraService;
    
    public ElytraModeCommand(SuperElytraService elytraService) {
        this.elytraService = Objects.requireNonNull(elytraService);
    }
    
    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command command, @NotNull String label, String[] args) {
        if (args.length != 1) {
            return false;
        }
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ERROR_BAD_PLAYER);
            return true;
        }

        final var elytraUser = elytraService.getElytraUser(player);
        switch (args[0]) {
            case "normal": {
                elytraUser.setSuperElytraEnabled(false);
                player.sendMessage(MSG_OFF);
                return true;
            }
            case "super": {
                elytraUser.setSuperElytraEnabled(true);
                player.sendMessage(MSG_ON);
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (args.length != 1) {
            return null;
        }

        final String arg = args[0].toLowerCase();
        if (arg.isEmpty()) {
            return Arrays.asList("normal", "super");
        }

        else if ("normal".startsWith(arg))
            return Collections.singletonList("normal");
        else if ("super".startsWith(arg))
            return Collections.singletonList("super");
        else
            return Collections.emptyList();
    }
    
}
