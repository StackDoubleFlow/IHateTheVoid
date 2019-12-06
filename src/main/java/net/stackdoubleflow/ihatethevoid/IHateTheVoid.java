package net.stackdoubleflow.ihatethevoid;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import net.stackdoubleflow.ihatethevoid.event.PlayerPositionPacketHandler;
import org.bukkit.plugin.java.JavaPlugin;

public final class IHateTheVoid extends JavaPlugin {

    private ProtocolManager protocolManager;

    @Override
    public void onEnable() {
        protocolManager = ProtocolLibrary.getProtocolManager();

        protocolManager.addPacketListener(new PlayerPositionPacketHandler(this));
    }

}
