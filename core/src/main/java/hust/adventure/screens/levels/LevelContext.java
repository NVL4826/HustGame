package hust.adventure.screens.levels;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import hust.adventure.HustGame;
import hust.adventure.core.data.LevelConfig;
import hust.adventure.entities.EntityManager;
import hust.adventure.entities.factory.EntityFactory;
import hust.adventure.entities.player.Player;
import hust.adventure.entities.player.input.PlayerController;
import hust.adventure.gamestate.PlayMode;
import hust.adventure.entities.base.LightProvider;
import hust.adventure.ui.UIProvider;

import hust.adventure.core.context.GameProgressContext;

/**
 * Interface representing the level context. Decouples concrete behaviors from the rendering screen class.
 */
public interface LevelContext {
    GameProgressContext getProgressContext();

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
