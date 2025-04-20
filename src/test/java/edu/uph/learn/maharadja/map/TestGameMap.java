package edu.uph.learn.maharadja.map;

import edu.uph.learn.maharadja.game.Player;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestGameMap {
  @Test
  public void addTerritory_WillCreateASetIfNew() {
    // Given
    Region nusantara = new Region("Nusantara");
    Territory trowulan = new Territory(nusantara, "Trowulan");
    Territory surabaya = new Territory(nusantara, "Surabaya");
    GameMap map = new GameMap();

    // When
    map.addTerritory(trowulan);

    // Then
    Set<Territory> adjacent = map.adjacencyList.get(trowulan);
    assertNotNull(adjacent);

    // When - Re-Adding, Then - not creating a new set
    map.addTerritory(trowulan);
    assertSame(adjacent, map.adjacencyList.get(trowulan));

    // When - Adding different territory, Then - different set
    map.addTerritory(surabaya);
    assertNotNull(map.adjacencyList.get(surabaya));
    assertNotSame(map.adjacencyList.get(trowulan), map.adjacencyList.get(surabaya));
  }

  @Test
  public void addConnection_WillAddBothDirection() {
    // Given
    Region nusantara = new Region("Nusantara");
    Territory malang = new Territory(nusantara, "Malang");
    Territory blambangan = new Territory(nusantara, "Blambangan");
    Territory bali = new Territory(nusantara, "Bali");
    GameMap map = new GameMap();

    // When
    map.addTerritory(malang);
    map.addTerritory(blambangan);
    map.addTerritory(bali);
    map.addConnection(blambangan, bali);

    // Then
    assertTrue(map.adjacencyList.get(malang).isEmpty());
    assertTrue(map.adjacencyList.get(blambangan).contains(bali));
    assertFalse(map.adjacencyList.get(blambangan).contains(malang));
    assertTrue(map.adjacencyList.get(bali).contains(blambangan));
    assertFalse(map.adjacencyList.get(bali).contains(malang));
  }

  @Test
  public void isAttackable_CheckDifferentOwner_AndAdjacent() {
    // Given
    Region nusantara = new Region("Nusantara");
    Player firstPlayer = Player.computer(1);
    Player secondPlayer = Player.computer(2);
    Territory singhasari = givenTerritory(nusantara, "Singhasari", firstPlayer);
    Territory trowulan = givenTerritory(nusantara, "Trowulan", secondPlayer);
    Territory sundaKingdom = givenTerritory(nusantara, "Sunda Kingdom", firstPlayer);
    Region sumatra = new Region("Sumatra");
    Territory lampung = givenTerritory(sumatra, "Lampung", secondPlayer);

    GameMap map = new GameMap();
    map.addTerritory(singhasari);
    map.addTerritory(trowulan);
    map.addTerritory(sundaKingdom);
    map.addTerritory(lampung);

    map.addConnection(singhasari, trowulan);
    map.addConnection(singhasari, sundaKingdom);
    map.addConnection(sundaKingdom, lampung);

    // When, Then - Adjacent and different owner
    assertTrue(map.isAttackable(singhasari, trowulan));
    // When, Then - Same Owner
    assertFalse(map.isAttackable(singhasari, sundaKingdom));
    // When, Then - No direct connection
    assertFalse(map.isAttackable(singhasari, lampung));
  }

  //                ______________________________________________________
  //              /                                             \         \
  //           (2)      (1)          (1)                       (2)       (2)
  //    ----- Aceh -  Lampung - Sunda Kingdom ------------ Trowulan - Surabaya
  //   /       |    /    |                                                 |
  // Barus - Jambi - Palembang                Singhasari -------------   Bali
  //  (1)      (1)    (1)                         (1)                    (2)
  @Test
  public void getShortestDeploymentPath_CheckSameOwner_AndHasConnection() {
    // Given
    Region nusantara = new Region("Nusantara");
    Player firstPlayer = Player.computer(1);
    Player secondPlayer = Player.computer(2);
    Territory singhasari = givenTerritory(nusantara, "Singhasari", firstPlayer);
    Territory trowulan = givenTerritory(nusantara, "Trowulan", secondPlayer);
    Territory surabaya = givenTerritory(nusantara, "Surabaya", secondPlayer);
    Territory bali = givenTerritory(nusantara, "Bali", secondPlayer);
    Territory sundaKingdom = givenTerritory(nusantara, "Sunda Kingdom", firstPlayer);
    Region sumatra = new Region("Sumatra");
    Territory lampung = givenTerritory(sumatra, "Lampung", firstPlayer);
    Territory palembang = givenTerritory(sumatra, "Palembang", firstPlayer);
    Territory jambi = givenTerritory(sumatra, "Jambi", firstPlayer);
    Territory barus = givenTerritory(sumatra, "Barus", firstPlayer);
    Territory aceh = givenTerritory(sumatra, "Aceh", secondPlayer);

    GameMap map = new GameMap();
    map.addTerritory(singhasari);
    map.addTerritory(trowulan);
    map.addTerritory(surabaya);
    map.addTerritory(bali);
    map.addTerritory(sundaKingdom);
    map.addTerritory(lampung);
    map.addTerritory(palembang);
    map.addTerritory(jambi);
    map.addTerritory(barus);
    map.addTerritory(aceh);

    map.addConnection(barus, aceh);
    map.addConnection(barus, jambi);
    map.addConnection(lampung, jambi);
    map.addConnection(jambi, palembang);
    map.addConnection(lampung, palembang);
    map.addConnection(lampung, aceh);
    map.addConnection(aceh, trowulan);
    map.addConnection(aceh, surabaya);
    map.addConnection(bali, surabaya);
    map.addConnection(bali, singhasari);
    map.addConnection(sundaKingdom, trowulan);
    map.addConnection(trowulan, surabaya);
    map.addConnection(sundaKingdom, lampung);

    // When
    assertEquals(
        List.of(sundaKingdom, lampung, jambi, barus),
        map.getShortestDeploymentPath(sundaKingdom, barus)
    );
    assertEquals(
        List.of(lampung, sundaKingdom),
        map.getShortestDeploymentPath(lampung, sundaKingdom)
    );
    assertEquals(
        List.of(),
        map.getShortestDeploymentPath(lampung, singhasari)
    );
    assertEquals(
        List.of(aceh, surabaya, bali),
        map.getShortestDeploymentPath(aceh, bali)
    );
    assertEquals(
        List.of(),
        map.getShortestDeploymentPath(aceh, singhasari)
    );
  }

  private static Territory givenTerritory(
      Region region,
      String name,
      Player owner
  ) {
    Territory territory = new Territory(region, name);
    territory.setOwner(owner);
    return territory;
  }
}
