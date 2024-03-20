package ru.violence.addresslimit.velocity.listener;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.connection.PreLoginEvent;
import com.velocitypowered.api.proxy.InboundConnection;
import net.kyori.adventure.text.minimessage.MiniMessage;
import ru.violence.addresslimit.common.ConnectionCounter;

import java.net.InetAddress;

public class LoginListener {
    private final int limit;
    private final String kickMessage;
    private final ConnectionCounter counter = new ConnectionCounter();

    public LoginListener(int limit, String kickMessage) {
        this.limit = limit;
        this.kickMessage = kickMessage;
    }

    @Subscribe(order = PostOrder.FIRST)
    public void onPreLogin(PreLoginEvent event) {
        if (!event.getResult().isAllowed()) return;
        if (counter.getCount(getAddress(event.getConnection())) >= this.limit) {
            event.setResult(PreLoginEvent.PreLoginComponentResult.denied(MiniMessage.miniMessage().deserialize(kickMessage)));
        }
    }

    @Subscribe
    public void onPostLogin(PostLoginEvent event) {
        counter.increment(event.getPlayer().getUniqueId(), getAddress(event.getPlayer()));
    }

    @Subscribe
    public void onDisconnect(DisconnectEvent event) {
        counter.decrement(event.getPlayer().getUniqueId(), getAddress(event.getPlayer()));
    }

    private InetAddress getAddress(InboundConnection connection) {
        return connection.getRemoteAddress().getAddress();
    }
}
