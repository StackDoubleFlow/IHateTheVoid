package net.stackdoubleflow.ihatethevoid.event;

import com.comphenix.executors.BukkitExecutors;
import com.comphenix.executors.BukkitScheduledExecutorService;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerPositionPacketHandler extends PacketAdapter {

    private JavaPlugin plugin;
    private BukkitScheduledExecutorService syncExecutor;

    public PlayerPositionPacketHandler(JavaPlugin plugin) {
        super(plugin, ListenerPriority.HIGHEST, PacketType.Play.Client.POSITION,
                PacketType.Play.Client.POSITION_LOOK);
        this.plugin = plugin;
        this.syncExecutor = BukkitExecutors.newSynchronous(plugin);
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.POSITION_LOOK ||
                event.getPacketType() == PacketType.Play.Client.POSITION) {
            PacketContainer packet = event.getPacket();
            double x = packet.getDoubles().read(0);
            double y = packet.getDoubles().read(1);
            double z = packet.getDoubles().read(2);
            if(!Double.isFinite(x) || !Double.isFinite(y) || !Double.isFinite(z) ||
                    Math.abs(x) >= 3.2E7 || Math.abs(z) >= 3.2E7 || Math.abs(y) >= 3.2E7) {
                event.setCancelled(true);
                syncExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        event.getPlayer().teleport(event.getPlayer().getWorld().getSpawnLocation());
                        event.getPlayer().sendMessage(ChatColor.GOLD.toString() + ChatColor.BOLD.toString() +
                                "You were sent back to spawn due to an invalid location.");
                    }
                });
            }
        }
    }
}
