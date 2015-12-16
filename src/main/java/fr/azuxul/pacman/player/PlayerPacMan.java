package fr.azuxul.pacman.player;

import fr.azuxul.pacman.entity.Booster;
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

    private int gameCoins, boosterRemainingTime;
    private Booster.BoosterTypes activeBooster;

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

    /**
     * Get active booster of player
     *
     * @return activeBooster
     */
    public Booster.BoosterTypes getActiveBooster() {
        return activeBooster;
    }

    /**
     * Set active booster to player
     *
     * @param activeBooster new active booster
     */
    public void setActiveBooster(Booster.BoosterTypes activeBooster) {
        this.activeBooster = activeBooster;
    }

    /**
     * Get remaining time of active booster
     * If not active booster return -1
     *
     * @return boosterRemainingTime
     */
    public int getBoosterRemainingTime() {
        return boosterRemainingTime;
    }

    /**
     * Set remaining time of active booster
     *
     * @param boosterRemainingTime new boosterRemainingTime
     */
    public void setBoosterRemainingTime(int boosterRemainingTime) {
        this.boosterRemainingTime = boosterRemainingTime;
    }

    /**
     * Update player stats (active effects)
     * 1 update/s
     */
    public void update() {

        if (boosterRemainingTime >= 0) {
            boosterRemainingTime--;
            if (boosterRemainingTime < 0)
                activeBooster = null;
        }
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
