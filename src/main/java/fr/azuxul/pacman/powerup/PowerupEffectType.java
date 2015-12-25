package fr.azuxul.pacman.powerup;

import org.bukkit.ChatColor;
import org.bukkit.Material;

/**
 * Enum of type of basic powerups
 *
 * @author Azuxul
 * @version 1.0
 */
public enum PowerupEffectType {

    SPEED(ChatColor.AQUA + "Speed", Material.FEATHER, 20),
    DOUBLE_COINS(ChatColor.YELLOW + "Double coins", Material.GOLD_BLOCK, 15),
    COINS_MAGNET(ChatColor.LIGHT_PURPLE + "Coin magnet", Material.EYE_OF_ENDER, 15);

    private final String name;
    private final Material icon;
    private final int duration;

    PowerupEffectType(String name, Material icon, int duration) {
        this.name = name;
        this.icon = icon;
        this.duration = duration;
    }

    /**
     * Get name of powerup effect type
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Used item stack for icon of powerup effect type
     *
     * @return icon
     */
    public Material getIcon() {
        return icon;
    }

    /**
     * Get duration of powerup effect type
     *
     * @return duration
     */
    public int getDuration() {
        return duration;
    }
}
