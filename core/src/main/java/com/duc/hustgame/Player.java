package com.duc.hustgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.math.Polygon;
public class Player {
    private TiledMap map;
    private Rectangle playerBounds = new Rectangle();

    private Texture[] allTextures;
    private Animation<TextureRegion> walkLeft, walkRight, walkDown, walkUp;
    private TextureRegion idleDown, idleUp, idleLeft, idleRight;
    private Animation<TextureRegion> lastAnim = null;
    private float stateTime = 0f;

    private float x, y;
    private float speed = 80f;
    private boolean moving = false;
    private Direction direction = Direction.DOWN;

    private static final float DRAW_SIZE = 50f;
    private static final float MAP_WIDTH  = 128 * 16f;
    private static final float MAP_HEIGHT = 128 * 16f;

    public Player(float startX, float startY, TiledMap map) {
        this.x = startX;
        this.y = startY;
        this.map = map;
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

    public void update(float delta) {
        moving = false;
        Direction newDirection = direction;

        float newX = x;
        float newY = y;

        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            newX += speed * delta; newDirection = Direction.RIGHT; moving = true;
        } else if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            newX -= speed * delta; newDirection = Direction.LEFT;  moving = true;
        } else if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            newY += speed * delta; newDirection = Direction.UP;    moving = true;
        } else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            newY -= speed * delta; newDirection = Direction.DOWN;  moving = true;
        }

        if (!isColliding(newX, newY)) {
            x = newX;
            y = newY;
        }

        x = MathUtils.clamp(x, DRAW_SIZE / 2, MAP_WIDTH  - DRAW_SIZE / 2);
        y = MathUtils.clamp(y, DRAW_SIZE / 2, MAP_HEIGHT - DRAW_SIZE / 2);

        if (moving) {
            direction = newDirection;
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

    private static final float MAP_HEIGHT_PX = 128 * 16f; // 2048px

    private boolean isColliding(float newX, float newY) {
        if (map.getLayers().get("Object Layer 1") == null) return false;

        // Chỉ tính va chạm ở phần chân nhân vật (box nhỏ lại)
        float feetWidth = DRAW_SIZE * 0.4f; // ~20px
        float feetHeight = DRAW_SIZE * 0.2f; // ~10px

        // Căn giữa hộp va chạm theo chiều ngang, và nằm sát đáy của DRAW_SIZE
        float boxX = newX - feetWidth / 2f;
        float boxY = newY - DRAW_SIZE / 2f;
        playerBounds.set(boxX, boxY, feetWidth, feetHeight);

        MapObjects objects = map.getLayers().get("Object Layer 1").getObjects();

        for (RectangleMapObject rectObject : objects.getByType(RectangleMapObject.class)) {
            Rectangle rect = rectObject.getRectangle();

            if (playerBounds.overlaps(rect)) {
                return true;
            }
        }
        return false;
    }
    public void draw(SpriteBatch batch) {
        TextureRegion frame;
        if (moving && lastAnim != null) {
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
            x - DRAW_SIZE / 2f,
            y - DRAW_SIZE / 2f,
            DRAW_SIZE, DRAW_SIZE);
    }

    public void dispose() {
        for (Texture t : allTextures) t.dispose();
    }

    public float getX() { return x; }
    public float getY() { return y; }

    public void setMap(TiledMap newMap, float newX, float newY) {
        this.map = newMap;
        this.x = newX;
        this.y = newY;
    }
}
