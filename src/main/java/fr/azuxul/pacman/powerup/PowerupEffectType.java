package fr.azuxul.pacman.powerup;

import org.bukkit.ChatColor;
import org.bukkit.Material;

/*
 * This file is part of PacMan.
 *
 * PacMan is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PacMan is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PacMan.  If not, see <http://www.gnu.org/licenses/>.
 */
public enum PowerupEffectType {

    SPEED(ChatColor.AQUA + "Speed", Material.FEATHER, 20),
    JUMP_BOOST(ChatColor.GREEN + "Sauts améliorés", Material.RABBIT_FOOT, 20),
    DOUBLE_GOMMES(ChatColor.YELLOW + "Double gommes", Material.GOLD_BLOCK, 15),
    GOMME_MAGNET(ChatColor.LIGHT_PURPLE + "Gomme magnet", Material.EYE_OF_ENDER, 15);

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
