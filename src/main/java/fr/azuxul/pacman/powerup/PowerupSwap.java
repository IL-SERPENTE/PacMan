package fr.azuxul.pacman.powerup;

import com.google.gson.JsonObject;
import fr.azuxul.pacman.GameManager;
import fr.azuxul.pacman.PacMan;
import fr.azuxul.pacman.player.PlayerPacMan;
import net.samagames.tools.powerups.Powerup;
import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.stream.Collectors;

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
public class PowerupSwap implements Powerup {

    private final ItemStack icon = new ItemStack(Material.ENDER_PEARL);
    private final int chance;

    public PowerupSwap(JsonObject jsonObject) {

        this.chance = jsonObject.get("swap").getAsInt();
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
        List<PlayerPacMan> playerPacManList = gameManager.getPlayerPacManList();

        List<PlayerPacMan> swappablePlayers = playerPacManList.stream().filter(playerFilter -> !playerFilter.equals(playerPacMan) && playerFilter.getPlayerIfOnline() != null).collect(Collectors.toList());

        if (swappablePlayers.isEmpty()) {
            player.sendMessage(gameManager.getCoherenceMachine().getGameTag() + " " + ChatColor.RED + "Il n'y pas de joueur avec qui swap !");
        } else {

            PlayerPacMan playerSwapPacMan = swappablePlayers.get(RandomUtils.nextInt(swappablePlayers.size())); // Get random player

            Player playerSwap = playerSwapPacMan.getPlayerIfOnline();

            Location locationOfPlayerSwap = playerSwap.getLocation();
            Location locationOfPlayer = player.getLocation();

            player.teleport(locationOfPlayerSwap);
            playerSwap.teleport(locationOfPlayer);

            playerSwap.sendMessage(gameManager.getCoherenceMachine().getGameTag() + " " + ChatColor.GREEN + "Vous avez été swap avec un autre joueur");
            player.sendMessage(gameManager.getCoherenceMachine().getGameTag() + " " + ChatColor.GREEN + "Vous avez été swap avec un autre joueur");
        }
    }

    /**
     * Return name of powerup
     *
     * @return "Swap"
     */
    @Override
    public String getName() {
        return ChatColor.DARK_PURPLE + "Swap";
    }

    /**
     * Return used item stack for icon
     *
     * @return Ender pearl
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
     * @return true
     */
    @Override
    public boolean isSpecial() {
        return true;
    }
}
