package net.stackdoubleflow.ihatethevoid.event;

import com.comphenix.executors.BukkitExecutors;
import com.comphenix.executors.BukkitScheduledExecutorService;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerPositionPacketHandler extends PacketAdapter {

    private final BukkitScheduledExecutorService syncExecutor;

    public PlayerPositionPacketHandler(JavaPlugin plugin) {
        super(plugin, ListenerPriority.HIGHEST, PacketType.Play.Client.POSITION,
                PacketType.Play.Client.POSITION_LOOK);
        this.syncExecutor = BukkitExecutors.newSynchronous(plugin);
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        if (event.getPacketType() != PacketType.Play.Client.POSITION_LOOK &&
                event.getPacketType() != PacketType.Play.Client.POSITION) {
            return;
        }
        StructureModifier<Double> doubles = event.getPacket().getDoubles();
        double x = doubles.read(0);
        double y = doubles.read(1);
        double z = doubles.read(2);
        if (isValid(x) && isValid(y) && isValid(z)) {
            return;
        }
        event.setCancelled(true);
        syncExecutor.execute(() -> {
            fixPlayer(event.getPlayer());
        });
    }

    static boolean isValid(double d) {
        return Double.isFinite(d) && Math.abs(d) < 3.0E7;
    }

    public static void fixPlayer(Player player) {
        player.teleport(player.getWorld().getSpawnLocation());
        player.sendMessage(
                ChatColor.GOLD.toString() + ChatColor.BOLD.toString() +
                        "You were sent back to spawn due to an invalid location.");
    }
}
