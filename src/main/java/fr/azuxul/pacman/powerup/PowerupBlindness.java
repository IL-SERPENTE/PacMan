package fr.azuxul.pacman.powerup;

import com.google.gson.JsonObject;
import fr.azuxul.pacman.GameManager;
import fr.azuxul.pacman.PacMan;
import fr.azuxul.pacman.player.PlayerPacMan;
import net.samagames.tools.powerups.Powerup;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

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
public class PowerupBlindness implements Powerup {

    private final ItemStack icon = new ItemStack(Material.INK_SACK);
    private final int chance;

    public PowerupBlindness(JsonObject jsonObject) {

        this.chance = jsonObject.get("blindness").getAsInt();
    }

    /**
     * When player pickup booster
     *
     * @param player pickup player
     */
    @Override
    public void onPickup(Player player) {

        GameManager gameManager = PacMan.getGameManager();

        for (PlayerPacMan playerPacMan : gameManager.getPlayerPacManList()) {

            Player p = playerPacMan.getPlayerIfOnline();

            if (p != null && !p.equals(player))
                p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 120, 1));
        }

        gameManager.getServer().broadcastMessage(gameManager.getCoherenceMachine().getGameTag() + " " + ChatColor.GOLD + player.getName() + ChatColor.GRAY + " vient de lancer de la poudre aveuglante !");
    }

    /**
     * Return name of powerup
     *
     * @return "Poudre aveuglante"
     */
    @Override
    public String getName() {
        return ChatColor.DARK_GRAY + "Poudre aveuglante";
    }

    /**
     * Return used item stack for icon
     *
     * @return Ink sack
     */
    @Override
    public ItemStack getIcon() {
        return icon;
    }

    /**
     * Return chance of spawn
     *
     * @return chance of spawn in game.json
     */
    @Override
    public double getWeight() {
        return chance;
    }

    /**
     * Return is special
     *
     * @return false
     */
    @Override
    public boolean isSpecial() {
        return false;
    }
}
