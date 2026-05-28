package hust.adventure.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import hust.adventure.core.GameAssetManager;
import hust.adventure.core.context.ProgressContext;
import hust.adventure.entities.Player;
import hust.adventure.entities.factory.EntityFactory;
import hust.adventure.events.EventDispatcher;
import hust.adventure.events.EventType;
import hust.adventure.events.GameEvent;
import hust.adventure.events.MapTransitionData;
import hust.adventure.gamestate.PlayMode;
import hust.adventure.items.Item;
import hust.adventure.items.ItemManager;
import hust.adventure.ui.DebugUI;
import hust.adventure.ui.UIManager;

/**
 * Handles debug shortcut keys (F4-F8) and executes debug actions
 * such as map transition, item spawning, and monster spawning.
 */
public class DebugInputHandler {
    private final UIManager uiManager;
    private final EntityFactory entityFactory;
    private final GameAssetManager assetManager;

    /**
     * Constructs a new DebugInputHandler.
     *
     * @param uiManager     the UI manager
     * @param entityFactory the entity factory
     * @param assetManager  the game asset manager
     */
    public DebugInputHandler(final UIManager uiManager, final EntityFactory entityFactory,
                             final GameAssetManager assetManager) {
        this.uiManager = uiManager;
        this.entityFactory = entityFactory;
        this.assetManager = assetManager;
    }

    /**
     * Handles debug input. Returns a new PlayMode if the state changes, null otherwise.
     *
     * @param player       the current player (to get spawn coordinates)
     * @param currentState the current PlayMode state
     * @return the new PlayMode if it changes, null if unchanged
     */
    public PlayMode handleDebugInput(final Player player, final PlayMode currentState) {
        final DebugUI debugUI = uiManager.getDebugUI();
        if (debugUI == null) {
            return null;
        }

        if (debugUI.isActive()) {
            final DebugUI.DebugOption selected = debugUI.handleSelectionInput();
            if (selected != null) {
                executeDebugAction(debugUI.getPreviousMode(), selected, player);
                return PlayMode.RUNNING;
            } else if (!debugUI.isActive()) {
                // Cancelled selection via ESC
                return PlayMode.RUNNING;
            }
            return null; // Suppress other actions while selection is active
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.F4)) {
            ProgressContext.instance.setGodMode(!ProgressContext.instance.isGodMode());
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.F5)) {
            ProgressContext.instance.setFastRun(!ProgressContext.instance.isFastRun());
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.F6)) {
            debugUI.startSelection(DebugUI.SelectionMode.MAP);
            return PlayMode.IN_UI;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.F7)) {
            debugUI.startSelection(DebugUI.SelectionMode.ITEM);
            return PlayMode.IN_UI;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.F8)) {
            debugUI.startSelection(DebugUI.SelectionMode.MONSTER);
            return PlayMode.IN_UI;
        }

        return null;
    }

    /**
     * Cancels the current debug selection mode.
     */
    public void cancelDebug() {
        if (uiManager.getDebugUI() != null) {
            uiManager.getDebugUI().cancelSelection();
        }
    }

    private void executeDebugAction(final DebugUI.SelectionMode mode, final DebugUI.DebugOption option, final Player player) {
        if (mode == DebugUI.SelectionMode.MAP) {
            final String targetMap = option.id;
            final MapTransitionData data = new MapTransitionData(targetMap, 400f, 400f);
            final GameEvent<MapTransitionData> event = new GameEvent<>(EventType.MAP_TRANSITION, data);
            EventDispatcher.getInstance().dispatch(event);
        } else if (mode == DebugUI.SelectionMode.ITEM) {
            final String itemId = option.id;
            final Item item = ItemManager.instance.getItem(itemId);
            if (item != null && player != null) {
                entityFactory.createItemDrop(player.getX() + 32f, player.getY(), item, Color.WHITE);
            }
        } else if (mode == DebugUI.SelectionMode.MONSTER) {
            final String enemyId = option.id;
            try {
                if (player != null) {
                    if (enemyId.equals("libboss")) {
                        entityFactory.createLibraryBoss(player.getX() + 64f, player.getY());
                    } else if (enemyId.equals("finalboss")) {
                        entityFactory.createFinalBoss(player.getX() + 64f, player.getY(),
                                assetManager.getTexture("Boss THT.png"));
                    } else {
                        entityFactory.createEnemy(enemyId, player.getX() + 64f, player.getY());
                    }
                }
            } catch (final Exception e) {
                Gdx.app.log("DebugMode", "Failed to spawn enemy: " + enemyId, e);
            }
        }
    }
}
