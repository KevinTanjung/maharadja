package edu.uph.learn.maharadja.ui.factory;

import edu.uph.learn.maharadja.common.UI;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.TextAlignment;


public class FormFactory {
  private FormFactory() {
  }

  public static Label label(String text, double width, Aligment aligment) {
    Label label = new Label(text);
    label.setFont(UI.SMALL_FONT);
    label.setAlignment(aligment.pos);
    label.setContentDisplay(aligment.contentDisplay);
    label.setTextAlignment(aligment.textAlignment);
    label.setPadding(new Insets(8, 0, 8, 0));
    label.setMinWidth(width);
    label.setMinHeight(UI.LARGE);
    return label;
  }

  public static TextField textField(double width) {
    TextField field = new TextField();
    field.setStyle("-fx-focus-color: transparent; -fx-faint-focus-color: transparent;");
    field.setFont(UI.LARGE_FONT);
    field.setAlignment(Pos.CENTER);
    field.setMinWidth(width);
    field.setMinHeight(UI.EXTRA_LARGE);
    field.setPadding(new Insets(8, 0, 8, 0));
    field.setBorder(BorderFactory.primary());
    return field;
  }

  public enum Aligment {
    CENTER(Pos.CENTER, ContentDisplay.CENTER, TextAlignment.CENTER);

    public final Pos pos;
    public final ContentDisplay contentDisplay;
    public final TextAlignment textAlignment;

    Aligment(Pos pos, ContentDisplay contentDisplay, TextAlignment textAlignment) {
      this.pos = pos;
      this.contentDisplay = contentDisplay;
      this.textAlignment = textAlignment;
    }
  }
}
