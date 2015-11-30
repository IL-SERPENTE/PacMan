package fr.azuxul.pacman.player;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * PlayerPacMan
 *
 * @author Azuxul
 * @version 1.0
 */
public class PlayerPacMan implements Comparable<PlayerPacMan> {

    UUID uuid;
    String name;
    int coins;

    public PlayerPacMan(UUID uuid, String name) {

        this.uuid = uuid;
        this.name = name;
    }

    /**
     * Get playerPacMan in list with hir UUID
     *
     * @param playerPacManList list of PacManPlayer
     * @param uuid UUID of player
     * @return PlayerPacMan of UUID player
     */
    public static PlayerPacMan getPlayerPacManInList(List<PlayerPacMan> playerPacManList, UUID uuid) {

        PlayerPacMan result = null;

        for (PlayerPacMan p : playerPacManList) {
            if (p.getUuid().equals(uuid)) {
                result = p;
                break;
            }
        }

        return result;
    }

    /**
     * Get name of playerPacMan
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Get UUID of playerPacMan
     *
     * @return UUID
     */
    public UUID getUuid() {
        return uuid;
    }

    /**
     * Get coins number of playerPacMan
     *
     * @return coins
     */
    public int getCoins() {
        return coins;
    }

    /**
     * Set coins number of playerPacMan
     *
     * @param coins value of coins number
     */
    public void setCoins(int coins) {
        this.coins = coins;
    }

    @Override
    public int compareTo(@Nullable PlayerPacMan comparePlayerPacMan) {

        if (comparePlayerPacMan == null) {
            throw new NullPointerException("The compared object can not be null");
        }
        if (comparePlayerPacMan.getCoins() == this.getCoins()) {
            return 0;
        } else if (comparePlayerPacMan.getCoins() > this.getCoins()) {
            return -1;
        } else {
            return 1;
        }
    }
}
