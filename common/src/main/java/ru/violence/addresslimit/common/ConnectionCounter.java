package ru.violence.addresslimit.common;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ConnectionCounter {
    private final Map<InetAddress, Set<UUID>> counterMap = new HashMap<>();

    public synchronized void increment(UUID playerUniqueId, InetAddress address) {
        counterMap.compute(address, (a, uuids) -> {
            if (uuids == null) uuids = new HashSet<>();
            uuids.add(playerUniqueId);
            return uuids;
        });
    }

    public synchronized void decrement(UUID playerUniqueId, InetAddress address) {
        counterMap.computeIfPresent(address, (a, uuids) -> {
            uuids.remove(playerUniqueId);
            return uuids.isEmpty() ? null : uuids;
        });
    }

    public synchronized int getCount(InetAddress address) {
        Set<UUID> uuids = counterMap.get(address);
        return uuids != null ? uuids.size() : 0;
    }
}
