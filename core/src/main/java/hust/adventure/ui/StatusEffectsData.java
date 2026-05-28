package hust.adventure.ui;

/**
 * Data Transfer Object containing all necessary data to render active spells, status effects, and artifacts.
 */
public class StatusEffectsData {
    private final boolean hasNao;
    private final boolean hasUsb;
    private final float enemyTimeScale;
    private final float showEnemiesTimer;
    private final boolean isSpeedBoosted;
    private final boolean isHpRegen;
    private final boolean isConfused;

    public StatusEffectsData(boolean hasNao, boolean hasUsb, float enemyTimeScale,
                             float showEnemiesTimer, boolean isSpeedBoosted,
                             boolean isHpRegen, boolean isConfused) {
        this.hasNao = hasNao;
        this.hasUsb = hasUsb;
        this.enemyTimeScale = enemyTimeScale;
        this.showEnemiesTimer = showEnemiesTimer;
        this.isSpeedBoosted = isSpeedBoosted;
        this.isHpRegen = isHpRegen;
        this.isConfused = isConfused;
    }

    public boolean isHasNao() {
        return hasNao;
    }

    public boolean isHasUsb() {
        return hasUsb;
    }

    public float getEnemyTimeScale() {
        return enemyTimeScale;
    }

    public float getShowEnemiesTimer() {
        return showEnemiesTimer;
    }

    public boolean isSpeedBoosted() {
        return isSpeedBoosted;
    }

    public boolean isHpRegen() {
        return isHpRegen;
    }

    public boolean isConfused() {
        return isConfused;
    }
}
