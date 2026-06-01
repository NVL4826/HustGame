package hust.adventure.screens.levels;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import hust.adventure.HustGame;
import hust.adventure.entities.EntityManager;
import hust.adventure.entities.factory.EntityFactory;
import hust.adventure.entities.player.Player;
import hust.adventure.gamestate.PlayMode;
import hust.adventure.graphics.LightProvider;
import hust.adventure.input.PlayerController;
import hust.adventure.screens.LevelConfig;
import hust.adventure.ui.UIProvider;

/**
 * Interface representing the level context. Decouples concrete behaviors from the rendering screen class.
 */
public interface LevelContext {
    HustGame getGame();

    LevelConfig getConfig();

    EntityFactory getEntityFactory();

    EntityManager getEntityManager();

    UIProvider getUIManager();

    PlayerController getInputReader();

    Player getPlayer();

    void setPlayer(final Player player);

    Camera getCamera();

    SpriteBatch getBatch();

    ShapeRenderer getShapeRenderer();

    BitmapFont getFont();

    PlayMode getState();

    void setState(final PlayMode state);

    LightProvider getLightingManager();
}
