package hust.adventure.items.equipment;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import hust.adventure.items.gear.GearConfig;
import hust.adventure.core.data.GearDataLoader;
import hust.adventure.items.weapons.WeaponConfig;
import hust.adventure.core.data.WeaponDataLoader;
import hust.adventure.entities.player.Player;
import hust.adventure.items.gear.Gear;
import hust.adventure.items.gear.GearFactory;
import hust.adventure.items.weapons.BaseWeapon;
import hust.adventure.items.weapons.WeaponFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class StudentEquipmentTest {
    private Player mockPlayer;
    private WeaponFactory mockWeaponFactory;
    private GearFactory mockGearFactory;
    private WeaponDataLoader mockWeaponDataLoader;
    private GearDataLoader mockGearDataLoader;
    private StudentEquipment equipment;

    @BeforeEach
    public void setUp() {
        Gdx.app = mock(Application.class);
        mockPlayer = mock(Player.class);
        mockWeaponFactory = mock(WeaponFactory.class);
        mockGearFactory = mock(GearFactory.class);
        mockWeaponDataLoader = mock(WeaponDataLoader.class);
        mockGearDataLoader = mock(GearDataLoader.class);

        equipment = new DefaultStudentEquipment(
                mockPlayer,
                mockWeaponFactory,
                mockGearFactory,
                mockWeaponDataLoader,
                mockGearDataLoader
        );
    }

    @Test
    @DisplayName("Adds and retrieves weapons cleanly through StudentEquipment interface")
    public void testWeaponLifecycle() {
        BaseWeapon weapon = mock(BaseWeapon.class);
        when(weapon.getId()).thenReturn("bun_dau");
        when(weapon.getLevel()).thenReturn(1);

        assertFalse(equipment.hasWeapon("bun_dau"));
        equipment.addWeapon(weapon);

        assertTrue(equipment.hasWeapon("bun_dau"));
        assertEquals(weapon, equipment.getWeapon("bun_dau"));
        assertEquals(1, equipment.getWeapons().size);

        equipment.removeWeapon("bun_dau");
        assertFalse(equipment.hasWeapon("bun_dau"));
        assertEquals(0, equipment.getWeapons().size);
    }

    @Test
    @DisplayName("Computes and updates multipliers dynamically when gears are equipped and upgraded")
    public void testGearMultipliers() {
        GearConfig spinachConfig = new GearConfig();
        spinachConfig.setId("spinach");
        spinachConfig.setOnEquipEffect("add_power");
        spinachConfig.setOnEquipValue(0.10f);
        when(mockGearDataLoader.getConfig("spinach")).thenReturn(spinachConfig);

        GearConfig wingsConfig = new GearConfig();
        wingsConfig.setId("wings");
        wingsConfig.setOnEquipEffect("add_speed");
        wingsConfig.setOnEquipValue(0.15f);
        when(mockGearDataLoader.getConfig("wings")).thenReturn(wingsConfig);

        GearConfig tomeConfig = new GearConfig();
        tomeConfig.setId("empty_tome");
        tomeConfig.setOnEquipEffect("reduce_cooldown");
        tomeConfig.setOnEquipValue(0.08f);
        when(mockGearDataLoader.getConfig("empty_tome")).thenReturn(tomeConfig);

        // Base multipliers
        assertEquals(1.0f, equipment.getPowerMultiplier(), 0.001f);
        assertEquals(1.0f, equipment.getCooldownMultiplier(), 0.001f);
        assertEquals(1.0f, equipment.getSpeedMultiplier(), 0.001f);

        Gear spinach = new Gear("spinach", "Banh Mi", "Power", mockGearFactory);
        equipment.addGear(spinach);
        assertEquals(1.10f, equipment.getPowerMultiplier(), 0.001f);

        Gear wings = new Gear("wings", "Giay", "Speed", mockGearFactory);
        equipment.addGear(wings);
        assertEquals(1.15f, equipment.getSpeedMultiplier(), 0.001f);

        Gear tome = new Gear("empty_tome", "De Cuong", "Cooldown", mockGearFactory);
        equipment.addGear(tome);
        assertEquals(0.92f, equipment.getCooldownMultiplier(), 0.001f);

        // Upgrading spinach to level 3 should yield 1.0 + (0.10 * 3) = 1.30
        equipment.applyUpgrade("spinach");
        equipment.applyUpgrade("spinach");
        assertEquals(3, spinach.getLevel());
        assertEquals(1.30f, equipment.getPowerMultiplier(), 0.001f);
    }

    @Test
    @DisplayName("Generates upgrade choices distinguishing unlocks from upgrades")
    public void testUpgradeEvaluation() {
        Array<String> weaponIds = new Array<>();
        weaponIds.add("bun_dau");
        weaponIds.add("magic_wand");
        when(mockWeaponDataLoader.getAllWeaponIds()).thenReturn(weaponIds);
        when(mockWeaponDataLoader.getWeaponName("bun_dau")).thenReturn("Bun Dau");
        when(mockWeaponDataLoader.getWeaponLevelDescription("bun_dau", 2)).thenReturn("Level 2");
        when(mockWeaponDataLoader.getWeaponName("magic_wand")).thenReturn("Magic Wand");
        when(mockWeaponDataLoader.getWeaponLevelDescription("magic_wand", 1)).thenReturn("Unlock Wand");

        BaseWeapon existingWeapon = mock(BaseWeapon.class);
        when(existingWeapon.getId()).thenReturn("bun_dau");
        when(existingWeapon.getLevel()).thenReturn(1);
        equipment.addWeapon(existingWeapon);

        when(mockGearDataLoader.getAllGearIds()).thenReturn(new Array<>());

        List<UpgradeOption> options = equipment.getAvailableUpgrades(5);
        assertEquals(2, options.size());

        UpgradeOption bunDauOption = options.stream().filter(o -> o.id().equals("bun_dau")).findFirst().orElseThrow();
        assertFalse(bunDauOption.isUnlock());
        assertEquals(2, bunDauOption.targetLevel());

        UpgradeOption wandOption = options.stream().filter(o -> o.id().equals("magic_wand")).findFirst().orElseThrow();
        assertTrue(wandOption.isUnlock());
        assertEquals(1, wandOption.targetLevel());
    }

    @Test
    @DisplayName("Reconstitutes weapons and gears state in a single restore operation")
    public void testPersistenceRestore() {
        Map<String, Integer> savedWeapons = new HashMap<>();
        savedWeapons.put("bun_dau", 3);

        Map<String, Integer> savedGears = new HashMap<>();
        savedGears.put("spinach", 2);

        BaseWeapon bunDau = mock(BaseWeapon.class);
        when(bunDau.getId()).thenReturn("bun_dau");
        when(bunDau.getLevel()).thenReturn(3);
        when(mockWeaponFactory.createWeapon("bun_dau", mockPlayer)).thenReturn(bunDau);

        GearConfig spinachConfig = new GearConfig();
        spinachConfig.setId("spinach");
        spinachConfig.setOnEquipEffect("add_power");
        spinachConfig.setOnEquipValue(0.10f);
        when(mockGearDataLoader.getConfig("spinach")).thenReturn(spinachConfig);

        Gear spinachGear = new Gear("spinach", "Banh Mi", "Power", mockGearFactory);
        when(mockGearFactory.createGear("spinach")).thenReturn(spinachGear);

        equipment.restore(savedWeapons, savedGears);

        verify(bunDau).setLevel(3);
        assertEquals(1, equipment.getWeapons().size);
        assertEquals(1, equipment.getGears().size);
        assertEquals(2, equipment.getGear("spinach").getLevel());
        assertEquals(1.20f, equipment.getPowerMultiplier(), 0.001f);

        Map<String, Integer> exportedWeapons = equipment.getWeaponLevels();
        assertEquals(3, exportedWeapons.get("bun_dau"));

        Map<String, Integer> exportedGears = equipment.getGearLevels();
        assertEquals(2, exportedGears.get("spinach"));
    }

    @Test
    @DisplayName("Restore with empty map equips default weapon without mutating caller map")
    public void testPersistenceRestoreDoesNotMutateInputMap() {
        Map<String, Integer> emptyWeapons = Collections.emptyMap();
        Map<String, Integer> emptyGears = Collections.emptyMap();

        BaseWeapon defaultWeapon = mock(BaseWeapon.class);
        when(defaultWeapon.getId()).thenReturn("bun_dau");
        when(mockWeaponFactory.createWeapon("bun_dau", mockPlayer)).thenReturn(defaultWeapon);

        assertDoesNotThrow(() -> equipment.restore(emptyWeapons, emptyGears));
        assertEquals(1, equipment.getWeapons().size);
        assertEquals(defaultWeapon, equipment.getWeapon("bun_dau"));
        assertTrue(emptyWeapons.isEmpty());
    }
}
