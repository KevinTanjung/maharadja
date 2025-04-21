package edu.uph.learn.maharadja.ui.components;

import edu.uph.learn.maharadja.map.Region;
import edu.uph.learn.maharadja.map.Territory;
import edu.uph.learn.maharadja.ui.components.PlayerTab.FXTerritory;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TestPlayerTabFXTerritory {
  @Test
  public void sortByTroopNumbers() {
    Region region = new Region("Sumatra");
    Territory territory1 = new Territory(region, "Lamuri");
    territory1.deployTroop(10);
    Territory territory2 = new Territory(region, "Barus");
    territory2.deployTroop(5);
    Territory territory3 = new Territory(region, "Toba Highlands");
    territory3.deployTroop(7);

    List<FXTerritory> territoryList = new ArrayList<>();
    territoryList.add(new FXTerritory(territory1));
    territoryList.add(new FXTerritory(territory2));
    territoryList.add(new FXTerritory(territory3));

    Collections.sort(territoryList);

    assertEquals(territory1.getName(), territoryList.getFirst().getName());
    assertEquals(territory3.getName(), territoryList.get(1).getName());
    assertEquals(territory2.getName(), territoryList.get(2).getName());
  }
}
