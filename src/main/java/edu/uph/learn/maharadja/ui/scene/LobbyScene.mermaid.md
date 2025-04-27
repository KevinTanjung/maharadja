```mermaid
---
title: Lobby Scene
---

classDiagram
    namespace JavaFX {
        class Button
        class Label
        class TextField
        class BorderPane
        class HBox
        class VBox
    }
    
    class GameWindow
    class Color {
        <<enumeration>>
        IMPERIAL_GOLD,
        VOLCANIC_BLACK,
        IVORY_WHITE,
        SUNSET_RED
    }
    class LogoColor {
        <<enumeration>>
        IMPERIAL_GOLD,
        VOLCANIC_BLACK,
        IVORY_WHITE
    }
    class Scene {
        +setFill(Paint paint)
    }
    class SceneWithLogo {
        <<abstract>>
        +getBackgroundColor() Color*
        +getLogoColor() LogoColor*
        +renderLogo()
    }
    class LobbyScene {
        -GameWindow gameWindow
        -SimpleStringProperty usernameProperty
        -TextField usernameField
        +getBackgroundColor() Color
        +getLogoColor() LogoColor
        -renderPlayerRegistration() void
        -checkUsername() Optional~String~
    }
    
    SceneWithLogo <|-- Scene
    LobbyScene <|-- SceneWithLogo
    LobbyScene ..> GameWindow
    LobbyScene ..> Button
    LobbyScene ..> Label
    LobbyScene ..> TextField
    LobbyScene ..> BorderPane
    LobbyScene ..> HBox
    LobbyScene ..> VBox
    
    SceneWithLogo ..> Color
    SceneWithLogo ..> LogoColor
```
