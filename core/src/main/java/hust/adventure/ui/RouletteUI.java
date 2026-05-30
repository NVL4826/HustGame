package hust.adventure.ui;
 
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import hust.adventure.events.EventDispatcher;
import hust.adventure.events.EventListener;
import hust.adventure.events.EventType;
import hust.adventure.events.GameEvent;
import hust.adventure.events.RewardSelectedEvent;
import hust.adventure.entities.Player;
import hust.adventure.weapons.Weaponable;
import com.badlogic.gdx.utils.Array;
 
public class RouletteUI implements EventListener {
    private boolean active = false;
    private float timer = 0;
    
    public RouletteUI() {
        EventDispatcher.getInstance().addListener(EventType.TREASURE_OPENED, this);
    }
    
    @Override
    public void onEvent(GameEvent<?> event) {
        if (event.getType() == EventType.TREASURE_OPENED) {
            active = true;
            timer = 2.0f; // 2 seconds spinning animation
        }
    }
    
    public void update(float delta, Player player) {
        if (active) {
            timer -= delta; // Even when game is paused, we need delta. BaseLevelScreen uses real delta for UI.
            if (timer <= 0) {
                active = false;
                
                String selectedWeaponId = null;
                if (player != null && player.getWeaponManager() != null) {
                    Array<String> upgradableWeaponIds = new Array<>();
                    for (Weaponable w : player.getWeaponManager().getWeapons()) {
                        if (w.getLevel() < 5) {
                            upgradableWeaponIds.add(w.getId());
                        }
                    }
                    if (upgradableWeaponIds.size > 0) {
                        selectedWeaponId = upgradableWeaponIds.random();
                    }
                }
                
                if (selectedWeaponId == null) {
                    selectedWeaponId = "whip"; // Fallback to whip if no weapon is upgradable
                }
                
                RewardSelectedEvent data = new RewardSelectedEvent(selectedWeaponId);
                GameEvent<RewardSelectedEvent> rewardEvent = new GameEvent<>(EventType.REWARD_SELECTED, data);
                EventDispatcher.getInstance().dispatch(rewardEvent);
            }
        }
    }
    
    public void render(SpriteBatch batch, ShapeRenderer shapeRenderer, BitmapFont font) {
        if (!active) return;
        
        batch.begin();
        font.setColor(Color.YELLOW);
        font.draw(batch, "SPINNING ROULETTE... " + String.format("%.1f", timer), 300, 300);
        font.setColor(Color.WHITE);
        batch.end();
    }
    
    public boolean isActive() {
        return active;
    }
 
    public void dispose() {
        EventDispatcher.getInstance().removeListener(EventType.TREASURE_OPENED, this);
    }
}
