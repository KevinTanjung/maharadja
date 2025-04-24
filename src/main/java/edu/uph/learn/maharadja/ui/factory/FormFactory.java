package edu.uph.learn.maharadja.ui.factory;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.TextAlignment;

import static edu.uph.learn.maharadja.common.Constant.HEIGHT_NORMAL;
import static edu.uph.learn.maharadja.common.Constant.HEIGHT_SMALL;
import static edu.uph.learn.maharadja.common.UI.LARGE_FONT;
import static edu.uph.learn.maharadja.common.UI.SMALL_FONT;

public class FormFactory {
  public static Label label(String text, double width, Aligment aligment) {
    Label label = new Label(text);
    label.setFont(SMALL_FONT);
    label.setAlignment(aligment.pos);
    label.setContentDisplay(aligment.contentDisplay);
    label.setTextAlignment(aligment.textAlignment);
    label.setPadding(new Insets(8, 0, 8, 0));
    label.setMinWidth(width);
    label.setMinHeight(HEIGHT_SMALL);
    return label;
  }

  public static TextField textField(double width) {
    TextField field = new TextField();
    field.setStyle("-fx-focus-color: transparent; -fx-faint-focus-color: transparent;");
    field.setFont(LARGE_FONT);
    field.setAlignment(Pos.CENTER);
    field.setMinWidth(width);
    field.setMinHeight(HEIGHT_NORMAL);
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
