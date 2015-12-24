package fr.azuxul.pacman.powerup;

import fr.azuxul.pacman.GameManager;
import fr.azuxul.pacman.PacMan;
import fr.azuxul.pacman.player.PlayerPacMan;
import net.samagames.tools.powerups.Powerup;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Basic powwerup class
 *
 * @author Azuxul
 * @version 1.0
 */
public class BasicPowerup implements Powerup {

    private final PowerupEffectType type;
    private final double chance;

    public BasicPowerup(PowerupEffectType type, double chance) {

        this.type = type;
        this.chance = chance;
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
