package edu.uph.learn.maharadja.network;

import edu.uph.learn.maharadja.Constant;
import edu.uph.learn.maharadja.controller.RegisterController;
import edu.uph.learn.maharadja.controller.common.Action;
import edu.uph.learn.maharadja.controller.common.ControllerFactory;
import edu.uph.learn.maharadja.controller.common.Message;
import edu.uph.learn.maharadja.controller.HeartbeatController;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

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
@Slf4j
public class GameServer {
  public static final int DEFAULT_PORT = 12000;
  public static final int BROADCAST_PORT = 12222;
  public static final long BROADCAST_INTERVAL = TimeUnit.SECONDS.toMillis(5);
  public static final String BROADCAST_MESSAGE = "MAHARADJA_SERVER";

  //region Server State
  private static boolean started;
  @Getter
  private static boolean ready;
  private static String username;
  private static final List<ClientHandler> clients = Collections.synchronizedList(new ArrayList<>());
  private static final ControllerFactory controllerFactory = new ControllerFactory(List.of(
      new HeartbeatController(),
      new RegisterController()
  ));
  //endregion

  //region Singleton
  private GameServer() {
  }

  private static class GameServerHolder {
    private static final GameServer INSTANCE = new GameServer();
  }

  public static GameServer start(String username) {
    // prevent multiple starts
    if (GameServer.started) {
      return GameServerHolder.INSTANCE;
    }

    GameServer.started = true;
    GameServer.username = username;
    new Thread(GameServerHolder.INSTANCE::startUdpBroadcast).start();
    new Thread(GameServerHolder.INSTANCE::startUdpMulticast).start();
    new Thread(GameServerHolder.INSTANCE::startTcpServer).start();
    return GameServerHolder.INSTANCE;
  }

  public static void broadcast(Message message) {
    for (ClientHandler clientHandler : clients) {
      clientHandler.sendMessage(message);
    }
  }
  //endregion

  /**
   * The game server broadcasts its IP & Port using UDP.
   * The game client listens for this broadcast and automatically connect to avoid manual entry of game server IP.
   */
  //region Server Auto-Discovery via UDP
  @SneakyThrows
  @SuppressWarnings("BusyWait")
  private void startUdpBroadcast() {
    String hostAddress = InetAddress.getLocalHost().getHostAddress();
    String broadcastAddress = getBroadcastAddress();
    log.info("[startUdpBroadcast] Broadcasting GameServer location on host: {} and port: {} via UDP at {}...",
        hostAddress, BROADCAST_PORT, broadcastAddress
    );
    try (DatagramSocket socket = new DatagramSocket()) {
      socket.setBroadcast(true);
      byte[] buffer = (BROADCAST_MESSAGE + ":" + hostAddress).getBytes();
      DatagramPacket packet = new DatagramPacket(
          buffer,
          buffer.length,
          InetAddress.getByName(broadcastAddress),
          BROADCAST_PORT
      );
      while (started) {
        socket.send(packet);
        try {
          Thread.sleep(BROADCAST_INTERVAL);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          break;
        }
      }
    } catch (SocketException e) {
      log.warn("[startUdpBroadcast] UDP Broadcast is not functional, skipping...");
    } catch (Exception e) {
      log.error("[startUdpBroadcast] Exception!", e);
    }
  }

  @SuppressWarnings("BusyWait")
  @SneakyThrows
  private void startUdpMulticast() {
    String hostAddress = InetAddress.getLocalHost().getHostAddress();
    log.info("[startUpdMulticast] Broadcasting GameServer location on host: {} and port: {} via UDP...",
        hostAddress, BROADCAST_PORT
    );
    try (DatagramSocket socket = new DatagramSocket()) {
      while (started) {
        byte[] buffer = (BROADCAST_MESSAGE + ":" + hostAddress).getBytes();
        DatagramPacket packet = new DatagramPacket(
            buffer,
            buffer.length,
            InetAddress.getByName("230.0.0.1"),
            BROADCAST_PORT
        );
        socket.send(packet);
        Thread.sleep(BROADCAST_INTERVAL);
      }
    } catch (SocketException e) {
      log.warn("[startUpdMulticast] UDP Multicast is not functional, skipping...");
    } catch (Exception e) {
      log.error("[startUpdMulticast] Exception!", e);
    }
  }

  @SneakyThrows
  private String getBroadcastAddress() {
    return NetworkInterface.getByInetAddress(InetAddress.getLocalHost())
        .getInterfaceAddresses()
        .stream()
        .map(InterfaceAddress::getBroadcast)
        .filter(Objects::nonNull)
        .map(InetAddress::getHostAddress)
        .findFirst()
        .orElseThrow();
  }
  //endregion

  /**
   * Create a persistent TCP server-client connection for multiplayer connection.
   * The server will listen for actions from the clients, validates them, and update the game state.
   */
  //region TCP Server-Client
  private void startTcpServer() {
    log.info("[startTcpServer] Starting GameServer on port: {}...", DEFAULT_PORT);
    try (ServerSocket serverSocket = new ServerSocket(DEFAULT_PORT)) {
      log.info("[startTcpServer] Server is waiting connections...");
      ready = true;
      while (true) {
        Socket clientSocket = serverSocket.accept();
        log.info("[startTcpServer] A new client #{} is connected: {}", clients.size()+1, clientSocket.getInetAddress());

        // If server is full, return LOBBY_FULL and let close socket connection.
        if (clients.size() >= (Constant.MAX_PLAYERS - 1)) {
          PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
          out.println(Constant.LOBBY_FULL);
          clientSocket.close();
          continue;
        }

        ClientHandler clientHandler = new ClientHandler(clientSocket, clients.size());
        clients.add(clientHandler);
        new Thread(clientHandler).start();
      }
    } catch (Exception e) {
      log.error("[startTcpServer] Exception!", e);
    }
  }

  /**
   * Handle the persistence request from a client.
   */
  public static class ClientHandler implements Runnable {
    private final Socket socket;
    private String sessionId;
    private String username;
    private PrintWriter out;

    public ClientHandler(
        Socket socket,
        int connectedClient
    ) {
      this.socket = socket;
      this.username = "Player " + (connectedClient + 2);
    }

    @Override
    public void run() {
      try {
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        out.println("Welcome to Maharadja! Lobby is hosted by: " + GameServer.username);

        String rawMessage;
        while ((rawMessage = in.readLine()) != null) {
          try {
            String[] chunks = rawMessage.split("\\|\\|");
            Message message = new Message(Action.valueOf(chunks[0]), chunks[1]);
            logMessage(message);
            Message response = controllerFactory.dispatch(message);
            if (response.getAction() == Action.REGISTER) {
              this.username = response.getPayload();
            }
            sendMessage(response);
          } catch (final Exception e) {
            log.error("[{}][{}] [clientHandler] Exception! Request > {}", socket.getInetAddress(), username, rawMessage, e);
          }
        }
      } catch (IOException e) {
        log.error("[{}][{}] [clientHandler] Exception!", socket.getInetAddress(), username, e);
      } finally {
        try {
          socket.close();
        } catch (IOException ignored) {}
        clients.remove(this);
      }
    }

    public void sendMessage(Message message) {
      if (out != null) {
        out.println(message.getAction() + "||" + message.getPayload());
      }
    }

    private void logMessage(Message message) {
      if (message.getAction() == Action.HEARTBEAT) {
        return;
      }

      log.info("[{}][{}] [clientHandler] Received Action: {}", socket.getInetAddress(), username, message.getAction());
    }
  }
  //endregion
}
