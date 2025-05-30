package me.lucyy.givepet;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class GivePet extends JavaPlugin {

    public static final Pattern patternBrackets = Pattern.compile("\\{#[0-9a-fA-F]{6}\\}");
    public static final Pattern pattern = Pattern.compile("#[0-9a-fA-F]{6}");

    private final Map<UUID, TransferAttempt> transferAttempts = new HashMap<>();

    public Map<UUID, TransferAttempt> transferAttempts() {
        return transferAttempts;
    }

    public boolean cancelTransfer(UUID uuid) {
        return transferAttempts.remove(uuid) != null;
    }

    public static String colorMsg(String s) {
        s = removeBrackets(s);
        Matcher match = pattern.matcher(s);
        while (match.find()) {
            String color = s.substring(match.start(), match.end());
            s = s.replace(color, "" + net.md_5.bungee.api.ChatColor.of(color));
            match = pattern.matcher(s);
        }
        return net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&', s);
    }

    private static String removeBrackets(String text) {
        Matcher m = patternBrackets.matcher(text);
        String replaced = text;
        while (m.find()) {
            String hexcode = m.group();
            String fixed = hexcode.substring(2, 8);
            replaced = replaced.replace(hexcode, "#" + fixed);
        }
        return replaced;
    }

    public String getMsg(String key) {
        return colorMsg(getConfig().getString(key));
    }

    @Override
    public void onEnable() {
        FileConfiguration cfg = getConfig();
        cfg.options().copyDefaults(true);
        cfg.addDefault("rightClickPrompt", "&ePlease right click the pet you would like to give");
        cfg.addDefault("playerNotFound", "&cThat player could not be found!");
        cfg.addDefault("cancelFail", "&cYou haven't tried to transfer a pet!");
        cfg.addDefault("cancelSuccess", "&aCancelled transferring a pet!");
        cfg.addDefault("playerLeft", "&cThe player you were trying to give that pet to has since left the server.");
        cfg.addDefault("sentReceiverMsg", "{sender}&a gave you a pet &f{type}&a!");
        cfg.addDefault("sentSenderMsg", "&aYou gave your pet successfully!");
        cfg.addDefault("notOwned", "&cThat's not your pet!");
        cfg.addDefault("selfGive", "&cYou can't give a pet to yourself!");
        saveConfig();

        GivePetCommand cmd = new GivePetCommand(this);

        getCommand("givepet").setExecutor(cmd);
        getCommand("givepet").setTabCompleter(cmd);

        getServer().getPluginManager().registerEvents(new InteractListener(this), this);

    }
}
