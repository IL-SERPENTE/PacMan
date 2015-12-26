package fr.azuxul.pacman.powerup;

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

/**
 * Blindness powerup
 *
 * @author Azuxul
 * @version 1.0
 */
public class PowerupBlindness implements Powerup {

    private final ItemStack icon = new ItemStack(Material.INK_SACK);

    @Override
    public void onPickup(Player player) {

        GameManager gameManager = PacMan.getGameManager();

        for (PlayerPacMan playerPacMan : gameManager.getPlayerPacManList()) {

            Player p = playerPacMan.getPlayerIfOnline();

            if (p != null && !p.equals(player))
                p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 120, 1));
        }

        gameManager.getServer().broadcastMessage(ChatColor.GOLD + player.getName() + ChatColor.BLACK + " vient de lancer de la poudre aveuglante !");
    }

    @Override
    public String getName() {
        return ChatColor.DARK_GRAY + "Poudre aveuglante";
    }

    @Override
    public ItemStack getIcon() {
        return icon;
    }

    @Override
    public double getWeight() {
        return 9;
    }

    @Override
    public boolean isSpecial() {
        return false;
    }
}
