package hust.adventure.entities;

import com.badlogic.gdx.graphics.Color;
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
import hust.adventure.items.weapons.WeaponFactory;
import hust.adventure.items.weapons.WeaponManager;
import hust.adventure.items.weapons.Weaponable;
import hust.adventure.entities.base.GameEntity;
import hust.adventure.items.Gear;
import hust.adventure.items.GearManager;

/**
 * Main player character class.
 */
public class Player extends BaseActor implements Targetable, EventListener {
    private final Inventory inventory;
    private final PlayerController controller;
    private final WeaponManager weaponManager;
    private final GearManager gearManager;
    private CollisionManager collisionManager;
    private EntityFactory entityFactory;

    private Texture[] allTextures;
    private Animation<TextureRegion> walkLeft, walkRight, walkDown, walkUp;
    private TextureRegion idleDown, idleUp, idleLeft, idleRight;
    private Animation<TextureRegion> lastAnim = null;
    private float stateTime = 0f;

    private float iframeTimer = 0f;
    private static final float IFRAME_DURATION = 0.5f;

    private static final float DRAW_SIZE = 50f;
    private static final float MAX_HP = 100f;

    // Spells Constants
    private static final float SLOW_MOTION_DURATION = 3f;
    private static final float SLOW_MOTION_TIME_SCALE = 0.3f;
    private static final float STUN_DURATION = 2f;
    private static final float STUN_STAMINA_COST = 20f;
    private static final float RADAR_DURATION = 5f;
    private static final float RADAR_STAMINA_COST = 10f;

    // Spells Timers
    private float slowMotionTimer;
    private float stunTimer;
    private float showEnemiesTimer;

    public Player(final float startX, final float startY, final Inventory inventory, final PlayerController controller,
            final CollisionManager collisionManager) {
        super(startX, startY, DRAW_SIZE, DRAW_SIZE, MAX_HP);
        this.inventory = inventory;
        this.controller = controller;
        this.collisionManager = collisionManager;
        this.weaponManager = new WeaponManager(this);
        this.gearManager = new GearManager();
        this.iframeTimer = 0f;

        // Starting weapon
        weaponManager.addWeapon(WeaponFactory.createWeapon("bun_dau", this));

        // Composition: Movement behavior
        this.setMovementBehavior(new PlayerMovementBehavior(controller, collisionManager));
        loadTextures();

        // Register for events
        EventDispatcher.getInstance().addListener(EventType.PUZZLE_FAILED, this);
        EventDispatcher.getInstance().addListener(EventType.ITEM_USED, this);
        EventDispatcher.getInstance().addListener(EventType.ITEM_PICKED_UP, this);

        this.slowMotionTimer = 0f;
        this.stunTimer = 0f;
        this.showEnemiesTimer = 0f;
        ProgressContext.instance.setPlayer(this);

        // Sync initial HP from global context
        this.setHp(ProgressContext.instance.getHp());
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
        if (ProgressContext.instance.isGodMode()) {
            setHp(getMaxHp());
        }
        if (iframeTimer > 0) {
            iframeTimer = Math.max(0f, iframeTimer - delta);
        }

        // Skill key checks
        if (ProgressContext.instance.isHasNao() && controller.isSkillQJustPressed()) {
            slowMotionTimer = SLOW_MOTION_DURATION;
        }

        if (controller.isSkillEJustPressed() && getStamina() >= STUN_STAMINA_COST) {
            setStamina(getStamina() - STUN_STAMINA_COST);
            stunTimer = STUN_DURATION;
        }

        if (controller.isSkillFJustPressed() && getStamina() >= RADAR_STAMINA_COST) {
            setStamina(getStamina() - RADAR_STAMINA_COST);
            showEnemiesTimer = RADAR_DURATION;
        }

        // Timer updates
        if (slowMotionTimer > 0) {
            slowMotionTimer = Math.max(0f, slowMotionTimer - delta);
        }
        if (stunTimer > 0) {
            stunTimer = Math.max(0f, stunTimer - delta);
        }
        if (showEnemiesTimer > 0) {
            showEnemiesTimer = Math.max(0f, showEnemiesTimer - delta);
        }

        // Propagate active timer states to global ProgressContext
        float enemyTimeScale = 1.0f;
        if (stunTimer > 0) {
            enemyTimeScale = 0f;
        } else if (slowMotionTimer > 0) {
            enemyTimeScale = SLOW_MOTION_TIME_SCALE;
        }
        ProgressContext.instance.setEnemyTimeScale(enemyTimeScale);
        ProgressContext.instance.setShowEnemiesTimer(showEnemiesTimer);

        if (ProgressContext.instance.getPlayer() != this) {
            ProgressContext.instance.setPlayer(this);
        }

        float wingsSpeedMultiplier = 1.0f;
        if (gearManager != null) {
            final Gear wings = gearManager.getGear("wings");
            if (wings != null) {
                wingsSpeedMultiplier += wings.getLevel() * 0.10f;
            }
        }
        setSpeedMultiplier(wingsSpeedMultiplier);

        super.update(delta);
        // Sync stats to global context for UI/saving
        ProgressContext.instance.setHp(getHp());
        ProgressContext.instance.setStamina(getStamina());

        // Magnetic radius for ExpGem
        if (collisionManager != null) {
            final float magnetRadius = 150f * getMagnetMultiplier();
            Array<GameEntity> items = collisionManager.getEntitiesInRadius(getX(), getY(), magnetRadius,
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

        if (controller.isSpaceJustPressed()) {
            for (final Weaponable weapon : weaponManager.getWeapons()) {
                if (!weapon.isAutoFiring()) {
                    weapon.fire();
                }
            }
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
        float oldAlpha = batch.getColor().a;
        if (iframeTimer > 0) {
            float alpha = 0.5f + 0.3f * (float) Math.sin(iframeTimer * 30f);
            Color color = batch.getColor();
            batch.setColor(color.r, color.g, color.b, alpha);
        }
        batch.draw(frame, getX() - getWidth() / 2f, getY() - getHeight() / 2f, getWidth(), getHeight());
        if (iframeTimer > 0) {
            Color color = batch.getColor();
            batch.setColor(color.r, color.g, color.b, oldAlpha);
        }
        weaponManager.draw(batch);
    }

    public void setCollisionManager(final CollisionManager newManager) {
        this.collisionManager = newManager;
        if (getMovementBehavior() instanceof PlayerMovementBehavior) {
            ((PlayerMovementBehavior) getMovementBehavior()).setCollisionManager(newManager);
        }
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

    public final GearManager getGearManager() {
        return gearManager;
    }



    public float getPowerMultiplier() {
        float mult = 1.0f;
        if (gearManager != null) {
            final Gear spinach = gearManager.getGear("spinach");
            if (spinach != null) {
                mult += spinach.getLevel() * 0.10f;
            }
        }
        return mult;
    }

    public float getCooldownMultiplier() {
        float mult = 1.0f;
        if (gearManager != null) {
            final Gear emptyTome = gearManager.getGear("empty_tome");
            if (emptyTome != null) {
                mult -= emptyTome.getLevel() * 0.08f;
            }
        }
        return Math.max(0.2f, mult);
    }

    public float getAreaMultiplier() {
        float mult = 1.0f;
        if (gearManager != null) {
            final Gear candelabrador = gearManager.getGear("candelabrador");
            if (candelabrador != null) {
                mult += candelabrador.getLevel() * 0.20f;
            }
        }
        return mult;
    }

    public float getMagnetMultiplier() {
        float mult = 1.0f;
        if (gearManager != null) {
            final Gear attractorb = gearManager.getGear("attractorb");
            if (attractorb != null) {
                mult += attractorb.getLevel() * 0.20f;
            }
        }
        return mult;
    }

    public void increaseMaxHp(final float amount) {
        final float oldMaxHp = getMaxHp();
        final float newMaxHp = oldMaxHp + amount;
        setMaxHp(newMaxHp);
        heal(amount);
        ProgressContext.instance.setMaxHp(newMaxHp);
        ProgressContext.instance.setHp(getHp());
    }

    public void setFactory(final EntityFactory factory) {
        this.entityFactory = factory;
    }

    public EntityFactory getFactory() {
        return entityFactory;
    }

    @Override
    public void takeDamage(final float amount) {
        takeDamage(amount, false, false);
    }

    @Override
    public void takeDamage(final float amount, final boolean isCrit) {
        takeDamage(amount, isCrit, false);
    }

    public void takeDamage(final float amount, final boolean isCrit, final boolean ignoreIFrames) {
        if (ProgressContext.instance.isGodMode()) {
            return;
        }
        if (!ignoreIFrames && iframeTimer > 0) {
            return;
        }
        super.takeDamage(amount, isCrit);
        if (!ignoreIFrames) {
            iframeTimer = IFRAME_DURATION;
        }
        // Sync to global context
        ProgressContext.instance.setHp(getHp());
    }

    public float getIframeTimer() {
        return iframeTimer;
    }

    public void setIframeTimer(final float iframeTimer) {
        this.iframeTimer = iframeTimer;
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
        ProgressContext.instance.setPlayer(null);
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
