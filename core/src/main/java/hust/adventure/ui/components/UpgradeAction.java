package hust.adventure.ui.components;

import hust.adventure.entities.Player;

public interface UpgradeAction {
    String getName();
    String getDescription();
    void execute(Player player);
}
