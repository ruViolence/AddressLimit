package ru.violence.addresslimit.bungee.listener;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import ru.violence.addresslimit.common.ConnectionCounter;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public class LoginListener implements Listener {
    private final int limit;
    private final String kickMessage;
    private final ConnectionCounter counter = new ConnectionCounter();

    public LoginListener(int limit, String kickMessage) {
        this.limit = limit;
        this.kickMessage = kickMessage;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLogin(LoginEvent event) {
        if (event.isCancelled()) return;
        if (counter.getCount(getAddress(event.getConnection())) >= this.limit) {
            event.setCancelled(true);
            event.setCancelReason(TextComponent.fromLegacyText(kickMessage));
        }
    }

    @EventHandler
    public void onPostLogin(PostLoginEvent event) {
        counter.increment(event.getPlayer().getUniqueId(), getAddress(event.getPlayer()));
    }

    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent event) {
        counter.decrement(event.getPlayer().getUniqueId(), getAddress(event.getPlayer()));
    }

    private InetAddress getAddress(Connection connection) {
        return ((InetSocketAddress) connection.getSocketAddress()).getAddress();
    }
}
