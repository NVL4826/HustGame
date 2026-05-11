package hust.adventure.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

import hust.adventure.events.EntityDamagedEvent;
import hust.adventure.events.EventDispatcher;
import hust.adventure.events.EventListener;
import hust.adventure.events.EventType;
import hust.adventure.events.GameEvent;
import hust.adventure.utils.GamePools;

public class DamageTextManager implements EventListener {
    private final Array<DamageText> activeTexts;

    public DamageTextManager() {
        activeTexts = new Array<>();
        EventDispatcher.getInstance().addListener(EventType.ENTITY_DAMAGED, this);
    }

    public void update(float delta) {
        for (int i = activeTexts.size - 1; i >= 0; i--) {
            DamageText dt = activeTexts.get(i);
            dt.update(delta);
            if (dt.isExpired()) {
                activeTexts.removeIndex(i);
                GamePools.free(dt);
            }
        }
    }

    public void render(SpriteBatch batch, BitmapFont font) {
        for (DamageText dt : activeTexts) {
            font.setColor(dt.getColor());
            font.draw(batch, dt.getText(), dt.getPosition().x, dt.getPosition().y);
        }
        font.setColor(Color.WHITE); // Reset color
    }

    @Override
    public void onEvent(GameEvent<?> event) {
        if (event.getType() == EventType.ENTITY_DAMAGED) {
            EntityDamagedEvent data = (EntityDamagedEvent) event.getData();
            
            float vx = MathUtils.random(-30f, 30f);
            float vy = MathUtils.random(50f, 100f);
            Color color = data.isCrit() ? Color.YELLOW : Color.WHITE;
            String text = String.valueOf((int) data.getAmount());

            DamageText dt = GamePools.obtain(DamageText.class);
            // Spawn slightly above the entity
            dt.init(data.getEntity().getX(), data.getEntity().getY() + data.getEntity().getHeight() / 2, 
                    vx, vy, text, color, 1.0f);
            activeTexts.add(dt);
        }
    }

    public void dispose() {
        EventDispatcher.getInstance().removeListener(EventType.ENTITY_DAMAGED, this);
        for (DamageText dt : activeTexts) {
            GamePools.free(dt);
        }
        activeTexts.clear();
    }
}
