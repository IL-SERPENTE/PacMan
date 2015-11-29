package fr.azuxul.pacman.player;

import java.util.List;
import java.util.UUID;

/**
 * PlayerPacMan
 *
 * @author Azuxul
 * @version 1.0
 */
public class PlayerPacMan {

    UUID uuid;
    String name;
    int coins;

    public PlayerPacMan(UUID uuid, String name) {

        this.uuid = uuid;
        this.name = name;
    }

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

    public String getName() {
        return name;
    }

    public UUID getUuid() {
        return uuid;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }
}
