package fr.azuxul.pacman.powerup;

import fr.azuxul.pacman.GameManager;
import fr.azuxul.pacman.PacMan;
import fr.azuxul.pacman.Utils;
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

/**
 * Swap powerup
 *
 * @author Azuxul
 * @version 1.0
 */
public class PowerupSwap implements Powerup {

    private final ItemStack icon = new ItemStack(Material.ENDER_PEARL);
    private final int chance = Utils.getChanceForPowerup("swap");

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
