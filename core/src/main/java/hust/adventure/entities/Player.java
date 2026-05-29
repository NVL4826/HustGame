package hust.adventure.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

import hust.adventure.core.GameAssetManager;
import hust.adventure.entities.base.BaseActor;
import hust.adventure.entities.base.Targetable;
import hust.adventure.entities.components.PlayerMovementBehavior;
import hust.adventure.entities.components.SpellController;
import hust.adventure.entities.state.MovingState;
import hust.adventure.input.PlayerController;
import hust.adventure.inventory.Inventory;
import hust.adventure.collision.CollisionLayer;
import hust.adventure.collision.CollisionManager;
import hust.adventure.core.context.ProgressContext;
import hust.adventure.entities.factory.EntityFactory;
import hust.adventure.entities.base.GameEntity;
import hust.adventure.entities.interactables.ExpGem;
import hust.adventure.items.Gear;
import hust.adventure.items.GearManager;
import hust.adventure.weapons.WeaponManager;
import hust.adventure.weapons.Weaponable;

/**
 * Main player character class.
 */
public class Player extends BaseActor implements Targetable {
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

    // Delegated controllers and handlers
    private final SpellController spellController;
    private final PlayerEventHandler eventHandler;

    public Player(final float startX, final float startY, final Inventory inventory, final PlayerController controller,
            final CollisionManager collisionManager, final GameAssetManager assetManager) {
        super(startX, startY, DRAW_SIZE, DRAW_SIZE, MAX_HP);
        this.inventory = inventory;
        this.controller = controller;
        this.collisionManager = collisionManager;
        this.weaponManager = new WeaponManager(this);
        this.gearManager = new GearManager();
        this.iframeTimer = 0f;

        // Restore weapons and gears from global context via persistence service
        PlayerPersistenceService.restoreWeaponsAndGears(this);

        // Composition: Movement behavior
        this.setMovementBehavior(new PlayerMovementBehavior(controller, collisionManager));
        loadTextures(assetManager);

        // Delegate event handling
        this.eventHandler = new PlayerEventHandler(this);

        // Initialize spell controller
        this.spellController = new SpellController();
        ProgressContext.instance.setPlayer(this);

        // Sync initial stats from global context via persistence service
        PlayerPersistenceService.restoreStats(this);
    }

    private void loadTextures(final GameAssetManager assetManager) {
        this.allTextures = new Texture[22];
        for (int i = 0; i < 22; i++) {
            allTextures[i] = assetManager.getTexture((i + 4) + ".png");
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

        // Update spells through the delegated controller
        spellController.update(this, controller, delta);

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

        // Sync core stats to global context via persistence service
        PlayerPersistenceService.saveStats(this);

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
        // Sync stats to context
        PlayerPersistenceService.saveStats(this);
    }

    public void setPlayerHp(final float hp) {
        super.setHp(hp);
    }

    public void setPlayerMaxHp(final float maxHp) {
        super.setMaxHp(maxHp);
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
        // Sync stats to context
        PlayerPersistenceService.saveStats(this);
    }

    public float getIframeTimer() {
        return iframeTimer;
    }

    public void setIframeTimer(final float iframeTimer) {
        this.iframeTimer = iframeTimer;
    }

    public SpellController getSpellController() {
        return spellController;
    }

    @Override
    public void dispose() {
        ProgressContext.instance.setPlayer(null);
        if (eventHandler != null) {
            eventHandler.dispose();
        }
        allTextures = null;
    }
}
