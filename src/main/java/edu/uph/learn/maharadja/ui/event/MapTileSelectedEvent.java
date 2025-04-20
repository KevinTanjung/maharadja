package edu.uph.learn.maharadja.ui.event;

import edu.uph.learn.maharadja.event.Event;
import edu.uph.learn.maharadja.ui.components.MapTile;

public record MapTileSelectedEvent(MapTile mapTile) implements Event {
}
