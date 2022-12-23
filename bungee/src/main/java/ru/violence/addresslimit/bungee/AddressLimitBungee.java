package ru.violence.addresslimit.bungee;

import lombok.SneakyThrows;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.YamlConfiguration;
import ru.violence.addresslimit.bungee.listener.LoginListener;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class AddressLimitBungee extends Plugin {
    @SneakyThrows
    @Override
    public void onEnable() {
        extractDefaultConfig();
        Configuration config = YamlConfiguration.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml"));

        int limit = config.getInt("limit");
        String kickMessage = config.getString("kick-message");

        getProxy().getPluginManager().registerListener(this, new LoginListener(limit, kickMessage));
    }

    @SuppressWarnings({"ResultOfMethodCallIgnored", "ConstantConditions"})
    @SneakyThrows(IOException.class)
    private void extractDefaultConfig() {
        getDataFolder().mkdirs();
        File configFile = new File(getDataFolder(), "config.yml");
        if (configFile.exists()) return;
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("config.yml")) {
            Files.copy(is, configFile.toPath());
        }
    }
}
