package hust.adventure.ui;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import hust.adventure.core.data.GearDataLoader;
import hust.adventure.core.data.WeaponDataLoader;
import hust.adventure.entities.player.Player;
import hust.adventure.items.gear.GearManager;
import hust.adventure.items.weapons.WeaponManager;
import hust.adventure.ui.components.UpgradeAction;
import hust.adventure.core.context.GameProgressContext;
import hust.adventure.items.weapons.WeaponFactory;
import hust.adventure.items.gear.GearFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LevelUpChoiceBuilderTest {
    private GearDataLoader mockGearLoader;
    private WeaponDataLoader mockWeaponLoader;
    private LevelUpChoiceBuilder choiceBuilder;

    private Player mockPlayer;
    private WeaponManager mockWeaponManager;
    private GearManager mockGearManager;

    @BeforeEach
    public void setUp() {
        Gdx.app = mock(Application.class);
        mockGearLoader = mock(GearDataLoader.class);
        mockWeaponLoader = mock(WeaponDataLoader.class);
        GameProgressContext mockProgressContext = mock(GameProgressContext.class);
        WeaponFactory mockWeaponFactory = mock(WeaponFactory.class);
        GearFactory mockGearFactory = mock(GearFactory.class);
        choiceBuilder = new LevelUpChoiceBuilder(mockGearLoader, mockWeaponLoader, mockProgressContext, mockWeaponFactory, mockGearFactory);

        mockPlayer = mock(Player.class);
        mockWeaponManager = mock(WeaponManager.class);
        mockGearManager = mock(GearManager.class);

        when(mockPlayer.getWeaponManager()).thenReturn(mockWeaponManager);
        when(mockPlayer.getGearManager()).thenReturn(mockGearManager);
    }

    @Test
    public void testGetLevelUpChoicesWhenNoWeaponsOrGearsOwned() {
        // Arrange
        Array<String> weaponIds = new Array<>();
        weaponIds.add("whip");
        when(mockWeaponLoader.getAllWeaponIds()).thenReturn(weaponIds);
        when(mockWeaponLoader.getWeaponName("whip")).thenReturn("Whip");
        when(mockWeaponLoader.getWeaponLevelDescription("whip", 1)).thenReturn("Unlock Whip");

        Array<String> gearIds = new Array<>();
        gearIds.add("clover");
        when(mockGearLoader.getAllGearIds()).thenReturn(gearIds);
        when(mockGearLoader.getGearName("clover")).thenReturn("Clover");
        when(mockGearLoader.getGearLevelDescription("clover", 1)).thenReturn("Unlock Clover");

        // Player doesn't own them yet
        when(mockWeaponManager.getWeapons()).thenReturn(new Array<>());
        when(mockGearManager.getGear("clover")).thenReturn(null);

        // Act
        Array<UpgradeAction> choices = choiceBuilder.getLevelUpChoices(mockPlayer);

        // Assert
        assertNotNull(choices);
        assertTrue(choices.size <= 3);
        assertFalse(choices.isEmpty());
        // Should contain options to unlock clover/whip
        boolean hasWhip = false;
        boolean hasClover = false;
        for (UpgradeAction action : choices) {
            if (action.getName().contains("Whip"))
                hasWhip = true;
            if (action.getName().contains("Clover"))
                hasClover = true;
        }
        assertTrue(hasWhip || hasClover);
    }

    @Test
    public void testGetLevelUpChoicesWithFallbacksWhenEverythingIsMaxLevel() {
        // Arrange
        when(mockWeaponLoader.getAllWeaponIds()).thenReturn(new Array<>());
        when(mockGearLoader.getAllGearIds()).thenReturn(new Array<>());

        // Act
        Array<UpgradeAction> choices = choiceBuilder.getLevelUpChoices(mockPlayer);

        // Assert
        assertNotNull(choices);
        // Should fall back to HealAction or DamageIncreaseAction
        assertFalse(choices.isEmpty());
        for (UpgradeAction action : choices) {
            assertTrue(action.getName().contains("Heal") || action.getName().contains("Tăng sát thương"));
        }
    }
}
