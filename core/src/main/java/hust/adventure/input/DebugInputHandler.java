package hust.adventure.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;

import hust.adventure.core.assets.GameAssetManager;
import hust.adventure.core.context.ProgressContext;
import hust.adventure.entities.factory.EntityFactory;
import hust.adventure.entities.player.Player;
import hust.adventure.events.EventDispatcher;
import hust.adventure.events.EventType;
import hust.adventure.events.GameEvent;
import hust.adventure.events.MapTransitionData;
import hust.adventure.gamestate.PlayMode;
import hust.adventure.items.base.Item;
import hust.adventure.items.base.ItemManager;
import hust.adventure.ui.DebugOption;
import hust.adventure.ui.DebugOptionRegistry;
import hust.adventure.ui.SelectionMode;
import hust.adventure.ui.UIManager;

/**
 * Handles debug shortcut keys (F4-F8), manages selection states for debug options, and executes corresponding debug
 * actions such as map transition, item spawning, and monster spawning.
 */
public class DebugInputHandler {
    private final UIManager uiManager;
    private final EntityFactory entityFactory;
    private final GameAssetManager assetManager;

    // States for debug selection
    private SelectionMode activeMode = SelectionMode.NONE;
    private SelectionMode previousMode = SelectionMode.NONE;
    private int selectedIndex = 0;

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
        if (isActive()) {
            final DebugOption selected = handleSelectionInput();
            if (selected != null) {
                executeDebugAction(previousMode, selected, player);
                return PlayMode.RUNNING;
            } else if (!isActive()) {
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
            startSelection(SelectionMode.MAP);
            return PlayMode.IN_UI;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.F7)) {
            startSelection(SelectionMode.ITEM);
            return PlayMode.IN_UI;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.F8)) {
            startSelection(SelectionMode.MONSTER);
            return PlayMode.IN_UI;
        }

        return null;
    }

    /**
     * Starts the selection mode.
     *
     * @param mode the selection mode to start
     */
    public void startSelection(final SelectionMode mode) {
        this.activeMode = mode;
        this.previousMode = mode;
        this.selectedIndex = 0;
    }

    /**
     * Cancels the current selection mode.
     */
    public void cancelSelection() {
        this.activeMode = SelectionMode.NONE;
    }

    /**
     * Cancels the current debug selection mode.
     */
    public void cancelDebug() {
        cancelSelection();
    }

    /**
     * Updates selection menu input.
     *
     * @return selected DebugOption if confirmed, null otherwise.
     */
    private DebugOption handleSelectionInput() {
        final DebugOption[] currentOptions = getCurrentOptions();
        if (!isActive() || currentOptions == null) {
            return null;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            selectedIndex = (selectedIndex - 1 + currentOptions.length) % currentOptions.length;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            selectedIndex = (selectedIndex + 1) % currentOptions.length;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            cancelSelection();
            return null;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            final DebugOption selection = currentOptions[selectedIndex];
            cancelSelection();
            return selection;
        }

        return null;
    }

    /**
     * Checks if a debug selection mode is currently active.
     *
     * @return true if active, false otherwise
     */
    public boolean isActive() {
        return activeMode != SelectionMode.NONE;
    }

    /**
     * Gets the current active selection mode.
     *
     * @return the active SelectionMode
     */
    public SelectionMode getActiveMode() {
        return activeMode;
    }

    /**
     * Gets the previous selection mode.
     *
     * @return the previous SelectionMode
     */
    public SelectionMode getPreviousMode() {
        return previousMode;
    }

    /**
     * Gets the current selected index.
     *
     * @return the selected index
     */
    public int getSelectedIndex() {
        return selectedIndex;
    }

    /**
     * Gets the array of debug options for the current active selection mode.
     *
     * @return an array of DebugOption
     */
    public DebugOption[] getCurrentOptions() {
        return DebugOptionRegistry.getOptions(activeMode);
    }

    private void executeDebugAction(final SelectionMode mode, final DebugOption option, final Player player) {
        if (mode == SelectionMode.MAP) {
            final String targetMap = option.id;
            final MapTransitionData data = new MapTransitionData(targetMap, 400f, 400f);
            final GameEvent<MapTransitionData> event = new GameEvent<>(EventType.MAP_TRANSITION, data);
            EventDispatcher.getInstance().dispatch(event);
        } else if (mode == SelectionMode.ITEM) {
            final String itemId = option.id;
            final Item item = ItemManager.instance.getItem(itemId);
            if (item != null && player != null) {
                entityFactory.createItemDrop(player.getX() + 32f, player.getY(), item, Color.WHITE);
            }
        } else if (mode == SelectionMode.MONSTER) {
            final String enemyId = option.id;
            try {
                if (player != null) {
                    entityFactory.createEnemy(enemyId, player.getX() + 64f, player.getY());
                }
            } catch (final Exception e) {
                Gdx.app.log("DebugMode", "Failed to spawn enemy: " + enemyId, e);
            }
        }
    }
}
