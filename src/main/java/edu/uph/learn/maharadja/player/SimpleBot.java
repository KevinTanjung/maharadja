package edu.uph.learn.maharadja.player;

public class SimpleBot extends Player {
  public SimpleBot(int i) {
    super("Computer " + i);
    computer = true;
    setColor(PLAYER_COLORS.get(i-1));
  }
}
