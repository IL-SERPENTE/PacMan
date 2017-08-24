package fr.azuxul.pacman.powerup;

import com.google.gson.JsonObject;
import fr.azuxul.pacman.GameManager;
import fr.azuxul.pacman.PacMan;
import fr.azuxul.pacman.player.PlayerPacMan;
import net.samagames.tools.powerups.Powerup;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
public class BasicPowerup implements Powerup {

    private final PowerupEffectType type;
    private final double chance;

    public BasicPowerup(PowerupEffectType type, String jsonIndex, JsonObject jsonObject) {

        this.type = type;
        chance = jsonObject.get(jsonIndex).getAsInt();
    }

    /**
     * When player pickup booster
     *
     * @param player pickup player
     */
    @Override
    public void onPickup(Player player) {

        GameManager gameManager = PacMan.getGameManager();
        PlayerPacMan playerPacMan = gameManager.getPlayer(player.getUniqueId());

        playerPacMan.setActiveBooster(type);
        playerPacMan.setBoosterRemainingTime(type.getDuration());
    }

    /**
     * Name of basic powerup
     *
     * @return name
     */
    @Override
    public String getName() {
        return type.getName();
    }

    /**
     * Used item stack for icon of powerup
     *
     * @return icon
     */
    @Override
    public ItemStack getIcon() {
        return new ItemStack(type.getIcon());
    }

    /**
     * Chance of powerup spawn
     *
     * @return weight
     */
    @Override
    public double getWeight() {
        return chance;
    }

    /**
     * Powerup is special
     *
     * @return false
     */
    @Override
    public boolean isSpecial() {
        return false;
    }
}
