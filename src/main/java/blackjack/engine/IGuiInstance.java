package blackjack.engine;

import blackjack.engine.scene.Scene;

public interface IGuiInstance {

    void drawGui();

    boolean handleGuiInput(Scene scene, Window window);
}
