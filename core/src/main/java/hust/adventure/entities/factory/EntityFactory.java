package hust.adventure.entities.factory;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import hust.adventure.entities.interactables.ExpGem;

import hust.adventure.entities.Player;
import hust.adventure.entities.base.MapObject;
import hust.adventure.entities.combat.Projectile;
import hust.adventure.input.PlayerController;
import hust.adventure.inventory.Inventory;
import hust.adventure.entities.enemies.FinalBoss;
import hust.adventure.entities.interactables.ItemDrop;
import hust.adventure.entities.interactables.TreasureChest;
import hust.adventure.graphics.LightProvider;
import hust.adventure.items.Item;

/**
 * Abstract Factory interface for entity creation.
 */
public interface EntityFactory {
    Player createPlayer(float x, float y, Inventory inventory, PlayerController controller);

    MapObject createEnemy(String type, float x, float y);

    Projectile createProjectile(float x, float y, float vx, float vy, float damage, Color color, boolean isPlayer);

    ItemDrop createItemDrop(float x, float y, Item item, Color color);

    ExpGem createExpGem(float x, float y, float amount);

    TreasureChest createTreasureChest(float x, float y, Texture texture);

    MapObject createLibraryBoss(float x, float y);

    MapObject createLibraryArtifact(float x, float y);

    FinalBoss createFinalBoss(float x, float y, Texture texture);

    MapObject createFloatingBook(float x, float y, LightProvider lightProvider);

    MapObject createCandle(float x, float y, LightProvider lightProvider);

    void freeEntity(MapObject entity);
}
