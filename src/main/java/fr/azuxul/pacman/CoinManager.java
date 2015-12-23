package fr.azuxul.pacman;

import net.samagames.api.games.Status;

/**
 * Coin manager
 *
 * @author Azuxul
 * @version 1.0
 */
public class CoinManager {

    private final GameManager gameManager;
    private int remainingGlobalCoins, globalCoins;

    public CoinManager(GameManager gameManager) {

        this.gameManager = gameManager;
    }

    /**
     * Get number of global coins remaining
     *
     * @return remainingGlobalCoins
     */
    public int getRemainingGlobalCoins() {
        return remainingGlobalCoins;
    }

    /**
     * Set number of global coins remaining
     *
     * @param remainingGlobalCoins global coins remaining
     */
    public void setRemainingGlobalCoins(int remainingGlobalCoins) {

        Status status = gameManager.getStatus();

        this.remainingGlobalCoins = remainingGlobalCoins;

        // If remaining coins is equals to 0 and is not end
        if (remainingGlobalCoins <= 0 && !status.equals(Status.FINISHED)) {

            gameManager.getServer().getOnlinePlayers().forEach(p -> gameManager.getScoreboard().sendScoreboardToPlayer(p, status)); // Update scoreboard
            gameManager.end(); // End
        }
    }

    public int getGlobalCoins() {
        return globalCoins;
    }

    /**
     * Set number of global coins
     *
     * @param globalCoins global coins
     */
    public void setGlobalCoins(int globalCoins) {
        this.remainingGlobalCoins = globalCoins;
        this.globalCoins = globalCoins;
    }
}
