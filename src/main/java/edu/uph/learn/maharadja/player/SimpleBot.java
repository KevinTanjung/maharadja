package edu.uph.learn.maharadja.player;

import edu.uph.learn.maharadja.map.Territory;

import java.util.HashMap;
import java.util.Map;

public class SimpleBot extends Player implements Bot {
  public SimpleBot(int i) {
    super("Computer " + i);
    computer = true;
    setColor(PLAYER_COLORS.get(i-1));
  }

  @Override
  public Map<Territory, Integer> decideTroopDraft(int numOfTroops) {
    Map<Territory, Integer> draftTroops = new HashMap<>();
    int remainingTroops = numOfTroops;
    for (Territory territory : getTerritories()) {
      if (remainingTroops <= 0) break;
      draftTroops.putIfAbsent(territory, 0);
      draftTroops.put(territory, draftTroops.get(territory) + 1);
      remainingTroops--;
    }
    return draftTroops;
  }
}
