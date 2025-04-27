package edu.uph.learn.maharadja.player;

import edu.uph.learn.maharadja.map.Territory;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface Bot {
  Map<Territory, Integer> decideTroopDraft(int numOfTroops);

  Optional<TroopMovementDecision> decideTerritoryFortification(List<Territory> deployableTerritories);
}
