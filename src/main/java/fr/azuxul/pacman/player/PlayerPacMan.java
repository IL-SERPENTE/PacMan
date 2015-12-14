package fr.azuxul.pacman.player;

import net.samagames.api.games.GamePlayer;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

/**
 * PlayerPacMan
 *
 * @author Azuxul
 * @version 1.0
 */
public class PlayerPacMan extends GamePlayer implements Comparable<PlayerPacMan> {

    int gameCoins;

    public PlayerPacMan(Player player) {

        super(player);
    }

    /**
     * Get gameCoins number of playerPacMan
     *
     * @return gameCoins
     */
    public int getGameCoins() {
        return gameCoins;
    }

    /**
     * Set gameCoins number of playerPacMan
     *
     * @param gameCoins value of gameCoins number
     */
    public void setGameCoins(int gameCoins) {
        this.gameCoins = gameCoins;
    }

    @Override
    public int compareTo(@Nullable PlayerPacMan comparePlayerPacMan) {

        if (comparePlayerPacMan == null) {
            throw new NullPointerException("The compared object can not be null");
        }
        if (comparePlayerPacMan.getGameCoins() == this.getGameCoins()) {
            return 0;
        } else if (comparePlayerPacMan.getGameCoins() > this.getGameCoins()) {
            return -1;
        } else {
            return 1;
        }
    }
}
