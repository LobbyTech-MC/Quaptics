package org.metamechanists.death_lasers.connections.points;

import org.bukkit.Location;
import org.bukkit.Material;
import org.metamechanists.death_lasers.connections.info.ConnectionInfoDisplay;
import org.metamechanists.death_lasers.connections.links.Link;
import org.metamechanists.death_lasers.storage.SerializationUtils;

import java.util.Map;
import java.util.UUID;

public class ConnectionPointInput extends ConnectionPoint {
    public ConnectionPointInput(String name, Location location) {
        super(name, location, Material.RED_CONCRETE, 15, 3);
    }

    private ConnectionPointInput(Link link, final String name, Location location, final int connectedBrightness, final int disconnectedBrightness,
                                 final ConnectionInfoDisplay infoDisplay, UUID blockDisplay, UUID interaction) {
        super(link, name, location, connectedBrightness, disconnectedBrightness, infoDisplay, blockDisplay, interaction);
    }

    @Override
    public void tick() {}

    public static ConnectionPointInput deserialize(Map<String, Object> map) {
        return new ConnectionPointInput(
                (Link) map.get("link"),
                (String) map.get("name"),
                (Location) map.get("location"),
                (int) map.get("connectedBrightness"),
                (int) map.get("disconnectedBrightness"),
                (ConnectionInfoDisplay) map.get("infoDisplay"),
                SerializationUtils.deserializeUUID((Map<String, Object>) map.get("blockDisplay")),
                SerializationUtils.deserializeUUID((Map<String, Object>) map.get("interaction")));
    }
}
