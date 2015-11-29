package fr.azuxul.pacman;

import net.minecraft.server.v1_8_R3.EntityArmorStand;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftArmorStand;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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

    public static void spawnCoin(Location location) {

        ArmorStand armorStand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND); // Spawn entity in world

        armorStand.setHelmet(new ItemStack(Material.GOLD_BLOCK)); // Set helmet

        EntityArmorStand entity = ((CraftArmorStand) armorStand).getHandle();

        NBTTagCompound nbtTagCompound = new NBTTagCompound();

        // Set nbtTagCompound
        entity.c(nbtTagCompound);
        nbtTagCompound.setBoolean("Small", true); // Set Small
        nbtTagCompound.setBoolean("NoGravity", true); // Set NoGravity
        nbtTagCompound.setBoolean("Invulnerable", true); // Set Invulnerable
        nbtTagCompound.setBoolean("Invisible", true); // Set Invisible
        nbtTagCompound.setInt("DisabledSlots", 31); // Disable slots
        nbtTagCompound.setString("CustomName", ChatColor.GOLD + "Coin"); // Set CustomName
        nbtTagCompound.setBoolean("CustomNameVisible", true); // Set CustomNameVisible
        entity.f(nbtTagCompound);
    }

}
