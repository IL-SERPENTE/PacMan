package fr.azuxul.pacman;

import net.samagames.api.SamaGamesAPI;
import net.samagames.api.games.IGameProperties;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

/**
 * Utils
 *
 * @author Azuxul
 * @author Rigner
 *
 * @version 1.0
 */
public class Utils {

    private Utils() {
    }

    public static int getChanceForPowerup(String powerupJsonName) {

        IGameProperties gameProperties = SamaGamesAPI.get().getGameManager().getGameProperties();

        return gameProperties.getConfigs().get("powerup-chance").getAsJsonObject().get(powerupJsonName).getAsInt();
    }

    /**
     * Get rules book
     *
     * @return itemStack of rules book
     */
    public static ItemStack getRulesBook() {
        String[] raw = new String[]{
                "\n   ]--------------[" +
                        "\n      &6&lPacMan&0" +
                        "\n    par &lSamaGames&0" +
                        "\n   ]--------------[" +
                        "\n" +
                        "\n &11.&0 But du jeu" +
                        "\n" +
                        "\n &12.&0 PvP" +
                        "\n" +
                        "\n &13.&0 Powerups",

                "\n     &lBut du jeu ?&0\n" +
                        "\n Vous devez récupérer\n le plus" +
                        " de pièces\n possible avant la" +
                        " fin de la\n partie. (plus de\n" +
                        " pièces ou\n temps écoulé)",

                "\n      &lPvp ?&0\n" +
                        "\n Vous pouvez taper" +
                        "\n vos adversaires" +
                        "\n pour leur faire perdre" +
                        "\n des pièces !",

                "\n     &lPowerups &0 " +
                        " Durant la partie,\n certains powerups\n" +
                        " peuvent apparaître !\n Prenez les" +
                        " tous et\n découvrez les !",

                "\n\nJeu développé par :" +
                        "\n\n - &lAzuxul&0" +
                        "\n\n\n\n\n\n\n      SamaGames" +
                        "\n Tout droits réservés."
        };
        String[] colored = new String[raw.length];
        for (int i = 0; i < raw.length; i++)
            colored[i] = ChatColor.translateAlternateColorCodes('&', raw[i]);
        ItemStack item = new ItemStack(Material.WRITTEN_BOOK, 1, (short) 0);
        BookMeta meta = (BookMeta) item.getItemMeta();
        meta.setPages(colored);
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&6&lLivre de règles"));
        meta.setAuthor("Rigner");
        item.setItemMeta(meta);
        return item;
    }
}
