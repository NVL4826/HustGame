package hust.adventure.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

import hust.adventure.entities.base.BaseActor;
import hust.adventure.entities.base.Targetable;
import hust.adventure.entities.components.PlayerMovementBehavior;
import hust.adventure.entities.state.MovingState;
import hust.adventure.events.EventDispatcher;
import hust.adventure.events.EventListener;
import hust.adventure.events.EventType;
import hust.adventure.events.GameEvent;
import hust.adventure.input.PlayerController;
import hust.adventure.inventory.Inventory;
import hust.adventure.items.Item;
import hust.adventure.items.ItemManager;
import hust.adventure.items.Consumable;
import hust.adventure.events.ItemPickedUpEvent;
import hust.adventure.entities.interactables.ExpGem;
import hust.adventure.collision.CollisionLayer;
import hust.adventure.collision.CollisionManager;
import hust.adventure.core.context.ProgressContext;
import hust.adventure.entities.factory.EntityFactory;
import hust.adventure.entities.weapons.WeaponFactory;
import hust.adventure.entities.weapons.WeaponManager;
import hust.adventure.entities.base.GameEntity;

/**
 * Main player character class.
 */
public class Player extends BaseActor implements Targetable, EventListener {
    private final Inventory inventory;
    private final PlayerController controller;
    private final WeaponManager weaponManager;
    private CollisionManager collisionManager;
    private EntityFactory entityFactory;

    private Texture[] allTextures;
    private Animation<TextureRegion> walkLeft, walkRight, walkDown, walkUp;
    private TextureRegion idleDown, idleUp, idleLeft, idleRight;
    private Animation<TextureRegion> lastAnim = null;
    private float stateTime = 0f;

    private static final float DRAW_SIZE = 50f;
    private static final float MAX_HP = 100f;

    public Player(final float startX, final float startY, final Inventory inventory, final PlayerController controller,
            final CollisionManager collisionManager) {
        super(startX, startY, DRAW_SIZE, DRAW_SIZE, MAX_HP);
        this.inventory = inventory;
        this.controller = controller;
        this.collisionManager = collisionManager;
        this.weaponManager = new WeaponManager(this);

        // Starting weapon
        weaponManager.addWeapon(WeaponFactory.createWeapon("whip", this));

        // Composition: Movement behavior
        this.setMovementBehavior(new PlayerMovementBehavior(controller, collisionManager));
        loadTextures();

        // Register for events
        EventDispatcher.getInstance().addListener(EventType.PUZZLE_FAILED, this);
        EventDispatcher.getInstance().addListener(EventType.ITEM_USED, this);
        EventDispatcher.getInstance().addListener(EventType.ITEM_PICKED_UP, this);

        // Sync initial HP from global context
        this.setHp(ProgressContext.instance.hp);
    }

    private void loadTextures() {
        // Ideally these should come from an AssetManager, but keeping for now as
        // requested
        this.allTextures = new Texture[22];
        for (int i = 0; i < 22; i++) {
            allTextures[i] = new Texture((i + 4) + ".png");
        }

        final TextureRegion[] frames = new TextureRegion[22];
        for (int i = 0; i < 22; i++) {
            frames[i] = new TextureRegion(allTextures[i]);
        }

        idleDown = frames[0];
        idleUp = frames[1];
        idleRight = frames[2];
        idleLeft = frames[17];

        walkLeft = makeAnim(frames, new int[] { 9, 10, 11, 12, 13, 14, 15 }, 0.1f);
        walkRight = makeAnim(frames, new int[] { 4, 5, 6, 7 }, 0.1f);
        walkDown = makeAnim(frames, new int[] { 0, 3 }, 0.2f);
        walkUp = makeAnim(frames, new int[] { 18, 19, 20, 21 }, 0.1f);
    }

    private Animation<TextureRegion> makeAnim(final TextureRegion[] frames, final int[] indices, final float dur) {
        final Array<TextureRegion> arr = new Array<>();
        for (final int idx : indices)
            arr.add(frames[idx]);
        return new Animation<>(dur, arr);
    }

    @Override
    public void update(final float delta) {
        super.update(delta);
        // Sync stats to global context for UI/saving
        ProgressContext.instance.hp = getHp();
        ProgressContext.instance.stamina = getStamina();

        // Magnetic radius for ExpGem
        if (collisionManager != null) {
            Array<GameEntity> items = collisionManager.getEntitiesInRadius(getX(), getY(), 150f,
                    CollisionLayer.ITEM);
            for (GameEntity item : items) {
                if (item instanceof ExpGem) {
                    ((ExpGem) item).setTarget(this);
                }
            }
        }

        if (getState() instanceof MovingState) {
            Animation<TextureRegion> anim;
            switch (getDirection()) {
            case RIGHT:
                anim = walkRight;
                break;
            case LEFT:
                anim = walkLeft;
                break;
            case UP:
                anim = walkUp;
                break;
            default:
                anim = walkDown;
                break;
            }
            if (anim != lastAnim) {
                stateTime = 0f;
                lastAnim = anim;
            }
            stateTime += delta;
        }

        weaponManager.update(delta);
    }

    @Override
    public void draw(final SpriteBatch batch) {
        TextureRegion frame;
        if (getState() instanceof MovingState && lastAnim != null) {
            frame = lastAnim.getKeyFrame(stateTime, true);
        } else {
            switch (getDirection()) {
            case RIGHT:
                frame = idleRight;
                break;
            case LEFT:
                frame = idleLeft;
                break;
            case UP:
                frame = idleUp;
                break;
            default:
                frame = idleDown;
                break;
            }
        }
        batch.draw(frame, getX() - getWidth() / 2f, getY() - getHeight() / 2f, getWidth(), getHeight());
        weaponManager.draw(batch);
    }

    public void setCollisionContext(final CollisionManager newManager, final float newX, final float newY) {
        if (getMovementBehavior() instanceof PlayerMovementBehavior) {
            ((PlayerMovementBehavior) getMovementBehavior()).setCollisionManager(newManager);
        }
        this.collisionManager = newManager;
        setX(newX);
        setY(newY);
    }

    public final Inventory getInventory() {
        return inventory;
    }

    public final PlayerController getController() {
        return controller;
    }

    public final CollisionManager getCollisionManager() {
        return collisionManager;
    }

    public final WeaponManager getWeaponManager() {
        return weaponManager;
    }

    public void setFactory(final EntityFactory factory) {
        this.entityFactory = factory;
    }

    public EntityFactory getFactory() {
        return entityFactory;
    }

    @Override
    public void takeDamage(final float amount) {
        super.takeDamage(amount);
        // Sync to global context
        ProgressContext.instance.hp = getHp();
    }

    @Override
    public void onEvent(final GameEvent<?> event) {
        if (event.getType() == EventType.PUZZLE_FAILED) {
            final Float damage = (Float) event.getData();
            takeDamage(damage);
        } else if (event.getType() == EventType.ITEM_USED) {
            final String itemId = (String) event.getData();
            final Item item = ItemManager.instance.getItem(itemId);
            if (item instanceof Consumable) {
                if (inventory.removeItem(item, 1)) {
                    ((Consumable) item).consume(this);
                }
            }
        } else if (event.getType() == EventType.ITEM_PICKED_UP) {
            final ItemPickedUpEvent data = (ItemPickedUpEvent) event.getData();
            if (data.getPicker() == this) {
                inventory.addItem(data.getItem(), 1);
            }
        }
    }

    @Override
    public void dispose() {
        EventDispatcher.getInstance().removeListener(EventType.PUZZLE_FAILED, this);
        EventDispatcher.getInstance().removeListener(EventType.ITEM_USED, this);
        EventDispatcher.getInstance().removeListener(EventType.ITEM_PICKED_UP, this);
        if (allTextures != null) {
            for (final Texture t : allTextures) {
                if (t != null)
                    t.dispose();
            }
        }
    }
}
