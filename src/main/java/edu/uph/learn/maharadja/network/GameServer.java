package edu.uph.learn.maharadja.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>This class will manage any network related features needed for the game,
 * when the player choose to host the game.</p>
 *
 * <p>General Implementation:</p>
 *
 * <ol>
 *   <li>Broadcast server IP via UDP for discovery.</li>
 *   <li>Accept incoming TCP connections up to 3 clients.</li>
 *   <li>Maintain the list of connected clients.</li>
 *   <li>Accept actions from connected clients.</li>
 *   <li>Broadcast game state updates and synchronize actions across clients.</li>
 * </ol>
 */
public class GameServer {
  private static final Logger LOG = LoggerFactory.getLogger(GameServer.class);
  private static final int DEFAULT_PORT = 12000;

  //region Server State
  private boolean started;
  private boolean ready;
  private final List<Socket> clients = Collections.synchronizedList(new ArrayList<>());
  //endregion

  //region Singleton
  private GameServer() {
  }

  private static class GameServerHolder {
    private static final GameServer INSTANCE = new GameServer();
  }

  public static GameServer start() {
    // prevent multiple starts
    if (GameServerHolder.INSTANCE.started) {
      return GameServerHolder.INSTANCE;
    }

    GameServerHolder.INSTANCE.started = true;
    new Thread(GameServerHolder.INSTANCE::startUdpDiscovery).start();
    new Thread(GameServerHolder.INSTANCE::startTcpServer).start();

    return GameServerHolder.INSTANCE;
  }
  //endregion

  /**
   * The game server broadcasts its IP & Port using UDP.
   * The game client listens for this broadcast and automatically connect to avoid manual entry of game server IP.
   */
  private void startUdpDiscovery() {
    LOG.info("Broadcasting GameServer location via UDP...");
    // TODO: impl
  }

  /**
   * Create a persistent TCP server-client connection for multiplayer connection.
   * The server will listen for actions from the clients, validates them, and update the game state.
   */
  private void startTcpServer() {
    LOG.info("Starting GameServer on port: {}...", DEFAULT_PORT);
    // TODO: impl
  }
}
