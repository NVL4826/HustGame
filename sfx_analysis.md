# HustGame Sound Effects & Background Music (SFX/BGM) Analysis

This document provides a detailed breakdown of where and what audio assets should be added to **HustGame** to make it feel premium, responsive, and immersive. 

We can divide the audio systems into **Background Music (BGM)** and **Sound Effects (SFX)**.

---

## 1. Background Music (BGM) Recommendations

Each game state and screen level should have its own background music track to set the mood:

| Screen / Level ID | Map File | Recommended Audio Theme | Trigger Location |
| :--- | :--- | :--- | :--- |
| **Main Menu / Loading** | N/A | Ambient, calm electronic or low-fi synth track. | `LoadingScreen.java` (upon show) |
| **Map 1 (Outside)** | `Final Outside.tmx` | Upbeat, adventure-style pixel RPG music. | `PlayScreen.java` / `Map1Behavior.java` |
| **Library Level** | `library.tmx` | Academic, slightly mysterious, dark university library feel (violins, piano). | `LibraryBehavior.java` |
| **Lab Level** | `lab.tmx` | Retro sci-fi, tense synthesizer beats. | `LabBehavior.java` |
| **Final Boss Room** | `boss_room.tmx` | Dramatic, intense, fast-paced battle metal or orchestral theme. | `BossFightBehavior.java` |
| **Game Over Screen** | N/A | Sad, melancholic piano or dramatic slow melody. | `GameOverScreen.java` (upon show) |
| **Victory Screen** | N/A | Upbeat, triumphant graduation fanfare / anthem. | `BossFightBehavior.java` (Phase Victory) |

---

## 2. Sound Effects (SFX) Recommendations

Below are the key gameplay actions grouped by category, showing the recommended sound effect type, corresponding files, and exact methods where they should be triggered:

### A. Combat & Player Actions
| Action | SFX Suggestion | Target File & Method | Notes |
| :--- | :--- | :--- | :--- |
| **Player Firing Bun Dau** | Small projectile shoot / thud sound | [BunDauWeapon.java](file:///Users/hungthkk123/Desktop/HustGame/core/src/main/java/hust/adventure/items/weapons/BunDauWeapon.java#L25) | Plays inside `executeAttackAction()` |
| **Player Firing Magic Wand** | High-pitch magic zap / spark | [MagicWandWeapon.java](file:///Users/hungthkk123/Desktop/HustGame/core/src/main/java/hust/adventure/items/weapons/MagicWandWeapon.java#L30) | Plays inside `fireAt()` |
| **Player Firing Whip** | Crisp whip crack | [WhipWeapon.java](file:///Users/hungthkk123/Desktop/HustGame/core/src/main/java/hust/adventure/items/weapons/WhipWeapon.java#L29) | Plays inside `executeAttackAction()` |
| **Garlic Aura Pulse** | Gentle electric/magic humming pulse | [GarlicAuraWeapon.java](file:///Users/hungthkk123/Desktop/HustGame/core/src/main/java/hust/adventure/items/weapons/GarlicAuraWeapon.java#L20) | Plays inside `executeAttackAction()` |
| **Player Damaged** | Hurt grunt or retro crash sound | [Player.java](file:///Users/hungthkk123/Desktop/HustGame/core/src/main/java/hust/adventure/entities/Player.java#L305) | Plays inside `takeDamage()` when HP is reduced |
| **Use Consumable (Coffee)**| Gulping / drinking sound | [Player.java](file:///Users/hungthkk123/Desktop/HustGame/core/src/main/java/hust/adventure/entities/Player.java#L333) | Plays when `ITEM_USED` event occurs |
| **Skill Q (Slow Motion)** | Time warping or bass-boosted whoosh | [Player.java](file:///Users/hungthkk123/Desktop/HustGame/core/src/main/java/hust/adventure/entities/Player.java#L140) | Plays when Q is pressed |
| **Skill E (Stun)** | Electrical zap or static distortion | [Player.java](file:///Users/hungthkk123/Desktop/HustGame/core/src/main/java/hust/adventure/entities/Player.java#L144) | Plays when E is pressed |
| **Skill F (Radar)** | Sonar beep / radar ping | [Player.java](file:///Users/hungthkk123/Desktop/HustGame/core/src/main/java/hust/adventure/entities/Player.java#L149) | Plays when F is pressed |

### B. Enemies
| Action | SFX Suggestion | Target File & Method | Notes |
| :--- | :--- | :--- | :--- |
| **Enemy Spawn** | Swoop or digital glitch sound | [WaveManager.java](file:///Users/hungthkk123/Desktop/HustGame/core/src/main/java/hust/adventure/wave/WaveManager.java#L134) | Plays inside `spawn()` |
| **Enemy Damaged** | Squish / punch impact | [BaseActor.java](file:///Users/hungthkk123/Desktop/HustGame/core/src/main/java/hust/adventure/entities/base/BaseActor.java#L65) | Plays inside `takeDamage()` (Check if `this` is not player) |
| **Enemy Defeated** | Small explosion / pop sound | [BaseActor.java](file:///Users/hungthkk123/Desktop/HustGame/core/src/main/java/hust/adventure/entities/base/BaseActor.java#L77) | Plays inside `takeDamage()` when `isDead()` becomes true |

### C. World Interactions
| Action | SFX Suggestion | Target File & Method | Notes |
| :--- | :--- | :--- | :--- |
| **Chest Opened** | Wooden squeak + glitter/chime | [TreasureChest.java](file:///Users/hungthkk123/Desktop/HustGame/core/src/main/java/hust/adventure/entities/interactables/TreasureChest.java#L43) | Plays inside `interact()` |
| **Pick up EXP Gem** | High-pitched chime / bubble pop | [ExpGem.java](file:///Users/hungthkk123/Desktop/HustGame/core/src/main/java/hust/adventure/entities/interactables/ExpGem.java#L46) | Plays inside `setCollider()` callback |
| **Pick up Item Drop** | Retro inventory pick up sound | [ItemDrop.java](file:///Users/hungthkk123/Desktop/HustGame/core/src/main/java/hust/adventure/entities/interactables/ItemDrop.java#L92) | Plays inside `setCollider()` callback |
| **Level Portal Entry** | Portal warp / whoosh | [PlayScreen.java](file:///Users/hungthkk123/Desktop/HustGame/core/src/main/java/hust/adventure/screens/PlayScreen.java#L394) | Plays inside `checkTriggers()` on teleport |
| **Time Limit Reached** | Alarm bell / sirens | [WaveManager.java](file:///Users/hungthkk123/Desktop/HustGame/core/src/main/java/hust/adventure/wave/WaveManager.java#L57) | Plays when `limitReached` becomes true |

### D. User Interface (UI)
| Action | SFX Suggestion | Target File & Method | Notes |
| :--- | :--- | :--- | :--- |
| **Inventory Open** | Backpack zipper / rustle | [PlayScreen.java](file:///Users/hungthkk123/Desktop/HustGame/core/src/main/java/hust/adventure/screens/PlayScreen.java#L309) | Plays when toggling backpack |
| **Menu Button Hover** | Light click / tick | [GameOverScreen.java](file:///Users/hungthkk123/Desktop/HustGame/core/src/main/java/hust/adventure/screens/GameOverScreen.java#L89) | Plays when hover state changes |
| **Menu Button Click** | Confirm beep / snap | [GameOverScreen.java](file:///Users/hungthkk123/Desktop/HustGame/core/src/main/java/hust/adventure/screens/GameOverScreen.java#L97) | Plays upon click confirmation |
| **Level Up UI Open** | Triumphant level-up trumpet/fanfare | [PlayScreen.java](file:///Users/hungthkk123/Desktop/HustGame/core/src/main/java/hust/adventure/screens/PlayScreen.java#L416) | Plays inside event listener for `LEVEL_UP` |
| **Roulette Spinning** | Rapid tick-tick-tick wheel spin sound | [RouletteUI.java](file:///Users/hungthkk123/Desktop/HustGame/core/src/main/java/hust/adventure/ui/RouletteUI.java#L23) | Plays during `active = true` spin phase |
| **Roulette Result Selected**| Item reward jingle | [RouletteUI.java](file:///Users/hungthkk123/Desktop/HustGame/core/src/main/java/hust/adventure/ui/RouletteUI.java#L33) | Plays inside `update()` when timer ends |

### E. Library Puzzle
| Action | SFX Suggestion | Target File & Method | Notes |
| :--- | :--- | :--- | :--- |
| **Drag Puzzle Book** | Wood click / paper rustling | [BookPuzzle.java](file:///Users/hungthkk123/Desktop/HustGame/core/src/main/java/hust/adventure/ui/BookPuzzle.java#L78) | Plays when player clicks to drag a book |
| **Correct Book Snap** | Heavy click / bookshelf slide | [BookPuzzle.java](file:///Users/hungthkk123/Desktop/HustGame/core/src/main/java/hust/adventure/ui/BookPuzzle.java#L105) | Plays when placed correctly |
| **Wrong Book Placement** | Buzz sound / error alarm | [BookPuzzle.java](file:///Users/hungthkk123/Desktop/HustGame/core/src/main/java/hust/adventure/ui/BookPuzzle.java#L108) | Plays inside `handleDrop()` on fail |
| **Puzzle Solved** | Magic unlock jingle | [BookPuzzle.java](file:///Users/hungthkk123/Desktop/HustGame/core/src/main/java/hust/adventure/ui/BookPuzzle.java#L131) | Plays inside `checkWinCondition()` |

### F. Final Boss Fight (Tạ Hải Tùng)
| Action | SFX Suggestion | Target File & Method | Notes |
| :--- | :--- | :--- | :--- |
| **Cutscene Dialogue Enter**| Advance beep / typing sound | [BossFightBehavior.java](file:///Users/hungthkk123/Desktop/HustGame/core/src/main/java/hust/adventure/screens/levels/BossFightBehavior.java#L182) | Plays when advancing dialogue |
| **Correct Q&A Answer** | Success ding / boss grunt | [BossFightBehavior.java](file:///Users/hungthkk123/Desktop/HustGame/core/src/main/java/hust/adventure/screens/levels/BossFightBehavior.java#L234) | Plays inside `handleAnswerInput()` |
| **Wrong Q&A Answer** | Shock / hit impact sound | [BossFightBehavior.java](file:///Users/hungthkk123/Desktop/HustGame/core/src/main/java/hust/adventure/screens/levels/BossFightBehavior.java#L237) | Plays inside `handleAnswerInput()` |
| **Q&A Time Out** | Time alarm buzzer | [BossFightBehavior.java](file:///Users/hungthkk123/Desktop/HustGame/core/src/main/java/hust/adventure/screens/levels/BossFightBehavior.java#L198) | Plays when `answerTimer <= 0` |
| **Paper Dodging Spawn** | Paper rustle / wind swoosh | [BossFightBehavior.java](file:///Users/hungthkk123/Desktop/HustGame/core/src/main/java/hust/adventure/screens/levels/BossFightBehavior.java#L209) | Plays when paper is spawned |
| **Hit by Paper** | Paper slap / hit sound | [BossFightBehavior.java](file:///Users/hungthkk123/Desktop/HustGame/core/src/main/java/hust/adventure/screens/levels/BossFightBehavior.java#L222) | Plays inside overlaps check |

---

## 3. Recommended Implementation Strategy

To implement this audio system cleanly and maintainably in LibGDX:

1. **Create an `AudioManager` Class**: 
   Develop a singleton `AudioManager` that holds loaded `Sound` and `Music` instances, controls volume levels, and exposes helper methods like `playSFX(String key)` and `playBGM(String key, boolean loop)`.
2. **Use LibGDX AssetManager**: 
   Integrate sound loading directly into `GameAssetManager.java` using `manager.load("sfx/shoot.wav", Sound.class)` and `manager.load("music/bg_outside.mp3", Music.class)`. This ensures sounds are loaded asynchronously during `LoadingScreen.java`.
3. **Register Event Listeners**:
   Leverage the game's existing event-driven architecture (`EventDispatcher.java`). The `AudioManager` can register as a listener for common event types like `LEVEL_UP`, `ITEM_PICKED_UP`, `TREASURE_OPENED`, `PUZZLE_FAILED`, and `PUZZLE_SOLVED`, playing the corresponding sounds automatically whenever these events occur. This avoids polluting core game logic files with audio playback code.
