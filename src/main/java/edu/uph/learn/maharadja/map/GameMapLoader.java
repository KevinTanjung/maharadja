package edu.uph.learn.maharadja.map;

import edu.uph.learn.maharadja.common.Color;
import edu.uph.learn.maharadja.game.GameState;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static edu.uph.learn.maharadja.common.ObjectMapperUtil.OBJECT_MAPPER;

/**
 * Helper to load the map from a JSON file into our desired data structure stored in {@link GameMap}.
 */
public class GameMapLoader {
  private static final Logger LOG = LoggerFactory.getLogger(GameMapLoader.class);
  private static final ClassLoader CLASS_LOADER = GameMapLoader.class.getClassLoader();

  public static GameMap load(MapType mapType) {
    try (InputStream inputStream = CLASS_LOADER.getResourceAsStream("maps/" + mapType.getFileName())) {
      JsonFile file = OBJECT_MAPPER.readValue(inputStream, JsonFile.class);
      //LOG.info("Parsed region: {}", OBJECT_MAPPER.writeValueAsString(file));
      Map<String, Territory> territoryByName = new HashMap<>();
      GameMap gameMap = new GameMap(file.columns, file.rows);

      for (RegionFile regionFile : file.regions) {
        Region region = new Region(
            regionFile.name,
            regionFile.bonusTroops,
            regionFile.color
        );
        gameMap.addRegion(region);
        for (TerritoryFile territoryFile : regionFile.getTerritories()) {
          Territory territory = new Territory(
              region,
              territoryFile.name,
              territoryFile.q,
              territoryFile.r
          );
          territoryByName.put(territoryFile.name, territory);
          gameMap.addTerritory(territory);
        }
      }

      for (RegionFile regionFile : file.regions) {
        for (TerritoryFile territoryFile : regionFile.getTerritories()) {
          Territory origin = territoryByName.get(territoryFile.name);
          for (String neighbor : territoryFile.adjacentTo) {
            Territory destination = territoryByName.get(neighbor);
            if (origin == null || destination == null) {
              System.out.println(OBJECT_MAPPER.writeValueAsString(territoryFile));
            }
            gameMap.addConnection(origin, destination);
          }
        }
      }
      return gameMap;
    } catch (Exception e) {
      LOG.error("Failed to load map", e);
      return null;
    }
  }

  //region Transient POJO
  @Data
  @NoArgsConstructor
  public static class JsonFile {
    private int columns;
    private int rows;
    private List<RegionFile> regions = new ArrayList<>();
  }

  @Data
  @NoArgsConstructor
  public static class RegionFile {
    private String name;
    private int bonusTroops;
    private Color color;
    private List<TerritoryFile> territories = new ArrayList<>();
  }

  @Data
  @NoArgsConstructor
  public static class TerritoryFile {
    private String name;
    private int q;
    private int r;
    private List<String> adjacentTo = new ArrayList<>();
  }
  //endregion
}
