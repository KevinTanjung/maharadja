package edu.uph.learn.maharadja.player;

import edu.uph.learn.maharadja.map.Territory;

import java.util.Map;

public interface Bot {
  Map<Territory, Integer> decideTroopDraft(int numOfTroops);
}
