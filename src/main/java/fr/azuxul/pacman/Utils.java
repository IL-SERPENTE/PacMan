package fr.azuxul.pacman;

import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Collection;

/**
 * Bukkit and NMS(Net.Minecraft.Server) utils
 *
 * @author Azuxul
 * @version 1.0
 */
public class Utils {

    /**
     * Send hotbar message to player
     *
     * @param player  player receive message
     * @param message message send to player
     */
    public static void sendHotbarMessage(Player player, String message) {
        IChatBaseComponent chatBaseComponent = IChatBaseComponent.ChatSerializer.a(
                "{" +
                        "\"text\": \"" + message + "\"" +
                        "}");

        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutChat(chatBaseComponent, (byte) 2));
    }

    /**
     * Send hotbar message to player collection
     *
     * @param players player collection receive message
     * @param message message send to players
     */
    public static void sendHotbarMessage(Collection<? extends Player> players, String message) {
        IChatBaseComponent chatBaseComponent = IChatBaseComponent.ChatSerializer.a(
                "{" +
                        "\"text\": \"" + message + "\"" +
                        "}");


        for (Player player : players)
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutChat(chatBaseComponent, (byte) 2));
    }
}
