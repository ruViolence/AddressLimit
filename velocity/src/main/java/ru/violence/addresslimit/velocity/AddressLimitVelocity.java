package ru.violence.addresslimit.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.SneakyThrows;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.yaml.YAMLConfigurationLoader;
import ru.violence.addresslimit.velocity.listener.LoginListener;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Plugin(id = "addresslimit", name = "AddressLimit", version = BuildConstants.VERSION)
public class AddressLimitVelocity {
    private final ProxyServer proxy;
    private final File dataFolder;

    @Inject
    public AddressLimitVelocity(ProxyServer proxy, @DataDirectory Path dataFolder) {
        this.proxy = proxy;
        this.dataFolder = dataFolder.toFile();
    }

    @SneakyThrows
    @Subscribe
    public void onProxyInit(ProxyInitializeEvent event) {
        extractDefaultConfig();
        ConfigurationNode config = YAMLConfigurationLoader.builder().setFile(new File(dataFolder, "config.yml")).build().load();

        int limit = config.getNode("limit").getInt();
        String kickMessage = config.getNode("kick-message").getString();

        proxy.getEventManager().register(this, new LoginListener(limit, kickMessage));
    }

    @SuppressWarnings({"ResultOfMethodCallIgnored", "ConstantConditions"})
    @SneakyThrows(IOException.class)
    private void extractDefaultConfig() {
        dataFolder.mkdirs();
        File configFile = new File(dataFolder, "config.yml");
        if (!configFile.exists()) {
            try (InputStream cfgStream = getClass().getClassLoader().getResourceAsStream("config.yml")) {
                Files.copy(cfgStream, configFile.toPath());
            }
        }
    }
}
