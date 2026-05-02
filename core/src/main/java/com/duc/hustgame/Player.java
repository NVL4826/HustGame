package com.duc.hustgame;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.Array;

public class Player extends Actor implements ITargetable {
    private Inventory inventory;

    private Texture[] allTextures;
    private Animation<TextureRegion> walkLeft, walkRight, walkDown, walkUp;
    private TextureRegion idleDown, idleUp, idleLeft, idleRight;
    private Animation<TextureRegion> lastAnim = null;
    private float stateTime = 0f;

    private static final float DRAW_SIZE = 50f;

    public Player(float startX, float startY, Inventory inventory, IPlayerController controller, TiledMap map) {
        super(startX, startY, DRAW_SIZE, DRAW_SIZE);
        this.inventory = inventory;

        // Setup movement behavior
        PlayerMovementBehavior pmb = new PlayerMovementBehavior(controller, map);
        setMovementBehavior(pmb);

        loadTextures();
    }

    private void loadTextures() {
        allTextures = new Texture[22];
        for (int i = 0; i < 22; i++) {
            allTextures[i] = new Texture((i + 4) + ".png");
        }

        TextureRegion[] frames = new TextureRegion[22];
        for (int i = 0; i < 22; i++) {
            frames[i] = new TextureRegion(allTextures[i]);
        }

        idleDown  = frames[0];
        idleUp    = frames[1];
        idleRight = frames[2];
        idleLeft  = frames[17];

        walkLeft  = makeAnim(frames, new int[]{9, 10, 11, 12, 13, 14, 15}, 0.1f);
        walkRight = makeAnim(frames, new int[]{4, 5, 6, 7}, 0.1f);
        walkDown  = makeAnim(frames, new int[]{0, 3}, 0.2f);
        walkUp    = makeAnim(frames, new int[]{18, 19, 20, 21}, 0.1f);
    }

    private Animation<TextureRegion> makeAnim(TextureRegion[] frames, int[] indices, float dur) {
        Array<TextureRegion> arr = new Array<>();
        for (int idx : indices) arr.add(frames[idx]);
        return new Animation<>(dur, arr);
    }

    @Override
    public void update(float delta) {
        super.update(delta); // Runs IMovementBehavior logic which updates x, y, direction, and state

        GameState.instance.coffeeSystem.update(delta);

        if (state == EntityState.MOVING) {
            Animation<TextureRegion> anim;
            switch (direction) {
                case RIGHT: anim = walkRight; break;
                case LEFT:  anim = walkLeft;  break;
                case UP:    anim = walkUp;    break;
                default:    anim = walkDown;  break;
            }
            if (anim != lastAnim) {
                stateTime = 0f;
                lastAnim = anim;
            }
            stateTime += delta;
        }
    }

    @Override
    public void draw(SpriteBatch batch) {
        TextureRegion frame;
        if (state == EntityState.MOVING && lastAnim != null) {
            frame = lastAnim.getKeyFrame(stateTime, true);
        } else {
            switch (direction) {
                case RIGHT: frame = idleRight; break;
                case LEFT:  frame = idleLeft;  break;
                case UP:    frame = idleUp;    break;
                default:    frame = idleDown;  break;
            }
        }
        batch.draw(frame,
            x - width / 2f,
            y - height / 2f,
            width, height);
    }

    @Override
    public void dispose() {
        for (Texture t : allTextures) {
            if (t != null) t.dispose();
        }
    }

    public void setMap(TiledMap newMap, float newX, float newY) {
        if (this.movementBehavior instanceof PlayerMovementBehavior) {
            ((PlayerMovementBehavior) this.movementBehavior).setMap(newMap);
        }
        this.x = newX;
        this.y = newY;
    }
}
