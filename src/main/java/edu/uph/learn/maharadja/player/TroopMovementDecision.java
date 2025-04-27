package edu.uph.learn.maharadja.player;

import edu.uph.learn.maharadja.map.Territory;

public record TroopMovementDecision(Territory from,
                                    Territory to,
                                    int numOfTroops,
                                    int value) {
}
