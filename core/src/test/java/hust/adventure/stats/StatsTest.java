package hust.adventure.stats;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.EnumMap;
import java.util.Map;

public class StatsTest {
    private PlayerStats stats;

    @BeforeEach
    void setUp() {
        Map<StatType, Float> baseStats = new EnumMap<>(StatType.class);
        baseStats.put(StatType.MAX_HP, 100f);
        baseStats.put(StatType.MOVE_SPEED, 1.0f);
        baseStats.put(StatType.COOLDOWN, 1.0f);
        
        CharacterData data = new CharacterData(
            "Antonio", "tex", "sprite", "desc", "WHIP", baseStats
        );
        
        stats = new PlayerStats(data);
    }

    @Test
    void testInitialValue() {
        assertEquals(100f, stats.getStatValue(StatType.MAX_HP));
    }

    @Test
    void testFlatModifier() {
        StatModifier flatHp = new StatModifier().init(20f, ModifierType.FLAT, "item1");
        stats.addModifier(StatType.MAX_HP, flatHp, false);
        assertEquals(120f, stats.getStatValue(StatType.MAX_HP));
    }

    @Test
    void testPercentageModifier() {
        StatModifier percentHp = new StatModifier().init(0.5f, ModifierType.PERCENTAGE, "item2");
        stats.addModifier(StatType.MAX_HP, percentHp, false);
        // Formula: (baseValue + flatSum) * (1 + percentSum) -> (100 + 0) * 1.5 = 150
        assertEquals(150f, stats.getStatValue(StatType.MAX_HP));
    }

    @Test
    void testMixedModifiers() {
        StatModifier flatHp = new StatModifier().init(20f, ModifierType.FLAT, "item1");
        StatModifier percentHp = new StatModifier().init(0.5f, ModifierType.PERCENTAGE, "item2");
        
        stats.addModifier(StatType.MAX_HP, flatHp, false);
        stats.addModifier(StatType.MAX_HP, percentHp, false);
        
        // Formula: (100 + 20) * 1.5 = 180
        assertEquals(180f, stats.getStatValue(StatType.MAX_HP));
    }

    @Test
    void testClamping() {
        // Cooldown max 90% reduction means min 0.1 multiplier
        StatModifier reduction = new StatModifier().init(-0.95f, ModifierType.PERCENTAGE, "god_buff");
        stats.addModifier(StatType.COOLDOWN, reduction, false);
        
        assertEquals(0.1f, stats.getStatValue(StatType.COOLDOWN), 0.001f);
    }

    @Test
    void testSourceRemoval() {
        Object source1 = "item1";
        Object source2 = "item2";
        
        StatModifier mod1 = new StatModifier().init(10f, ModifierType.FLAT, source1);
        StatModifier mod2 = new StatModifier().init(20f, ModifierType.FLAT, source2);
        
        stats.addModifier(StatType.MAX_HP, mod1, false);
        stats.addModifier(StatType.MAX_HP, mod2, false);
        
        assertEquals(130f, stats.getStatValue(StatType.MAX_HP));
        
        stats.removeModifiersFromSource(StatType.MAX_HP, source1, false);
        assertEquals(120f, stats.getStatValue(StatType.MAX_HP));
    }
}
