package edu.uph.learn.maharadja.game;

import edu.uph.learn.maharadja.player.Player;

public record GameResult(Player player,
                         boolean victory) {
}
