package fr.azuxul.pacman;

import net.minecraft.server.v1_8_R3.EntityArmorStand;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftArmorStand;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Collection;

/**
 * Utils
 *
 * @author Azuxul
 * @version 1.0
 */
public class Utils {

    public static void sendHotbarMessage(Player player, String message) {
        IChatBaseComponent chatBaseComponent = IChatBaseComponent.ChatSerializer.a(
                "{" +
                        "\"text\": \"" + message + "\"" +
                        "}");

        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutChat(chatBaseComponent, (byte) 2));
    }

    public static void sendHotbarMessage(Collection<? extends Player> players, String message) {
        IChatBaseComponent chatBaseComponent = IChatBaseComponent.ChatSerializer.a(
                "{" +
                        "\"text\": \"" + message + "\"" +
                        "}");


        for (Player player : players)
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutChat(chatBaseComponent, (byte) 2));
    }

    public static ArmorStand spawnCoin(Location location, boolean noGravity) {

        ArmorStand armorStand = (ArmorStand) location.getWorld().spawnEntity(location.add(0, -0.2, 0), EntityType.ARMOR_STAND); // Spawn entity in world

        armorStand.setHelmet(new ItemStack(Material.GOLD_BLOCK)); // Set helmet

        EntityArmorStand entity = ((CraftArmorStand) armorStand).getHandle();

        NBTTagCompound nbtTagCompound = new NBTTagCompound();

        // Set nbtTagCompound
        entity.c(nbtTagCompound);
        nbtTagCompound.setBoolean("Small", true); // Set Small
        nbtTagCompound.setBoolean("NoGravity", noGravity); // Set NoGravity
        nbtTagCompound.setBoolean("Invulnerable", true); // Set Invulnerable
        nbtTagCompound.setBoolean("Invisible", true); // Set Invisible
        nbtTagCompound.setInt("DisabledSlots", 31); // Disable slots
        entity.f(nbtTagCompound);

        return armorStand;
    }

    public static void spawnPlayerDropedCoin(Location location, Vector direction) {

        ArmorStand armorStand = spawnCoin(location, false); // Spawn normal coin with gravity
        GameManager gameManager = PacMan.getGameManager();

        armorStand.setVelocity(direction); // Set velocity

        gameManager.getServer().getScheduler().runTaskLater(gameManager.getPlugin(), armorStand::remove, 300); // After 15s kill coin
    }

}
