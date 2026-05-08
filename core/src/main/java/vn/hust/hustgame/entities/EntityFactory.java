package vn.hust.hustgame.entities;

import com.badlogic.gdx.maps.tiled.TiledMap;
import vn.hust.hustgame.input.IPlayerController;
import vn.hust.hustgame.inventory.Inventory;

public class EntityFactory {

    public static Player createPlayer(float x, float y, Inventory inventory, IPlayerController controller,
            TiledMap map) {
        return new Player(x, y, inventory, controller, map);
    }

    public static Entity createEnemy(String type, float x, float y, TiledMap map) {
        switch (type.toLowerCase()) {
            case "syntax_error":
                return new SyntaxErrorEnemy(x, y, map);
            case "null_pointer":
                return new NullPointerEnemy(x, y, map);
            case "infinite_loop":
                return new InfiniteLoopEnemy(x, y, map);
            case "stack_overflow":
                return new StackOverflowEnemy(x, y, map);
            default:
                throw new IllegalArgumentException("Unknown enemy type: " + type);
        }
    }
}
