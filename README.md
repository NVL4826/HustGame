# Hust Adventure (HustGame)

A 2D roguelite survival RPG inspired by *Vampire Survivors*, themed around student life at Hanoi University of Science and Technology (HUST).

The player takes control of a HUST student battling relentless waves of programming bugs and academic challenges across campus, acquiring study tools, solving academic puzzles, and surviving until the final thesis defense against **Professor T.H.T**.

---

## 🎮 Gameplay Overview

- **Survival & Progression**: Battle increasing waves of bugs across 5 canonical campus maps. Collect **Knowledge Gems** to gain Experience, level up, and upgrade your study equipment and passive gear.
- **Academic Challenges**: In Map 3 (Library), interact with academic terminals to solve intellectual mini-games (Simon Memory, Card Matching, Speed Math) to awaken your intellectual capacity.
- **Thesis Defense**: Overcome deadlines and advance to the defense hall to face the final academic boss: **Professor T.H.T**.

### Campus Map Progression (ADR-0001)

| Map Stage | Campus Area | Environment ID | Required Key Item |
| :--- | :--- | :--- | :--- |
| **Map 1** | Outdoor Grounds | `FINAL_OUTSIDE` | **Admission Note** (`note`) |
| **Map 2** | Floor 1 Lecture Hall | `TANG_1` | **Lecture Notes** (`lecture_notes`) |
| **Map 3** | University Library | `LIBRARY` | **Awakened Brain** (`brain`) |
| **Map 4** | Computer Laboratory | `LAB` | **Source Code USB** (`usb`) |
| **Final Map** | Thesis Defense Hall | `BOSS_ROOM` | Defeat Professor T.H.T → **Graduation** |

---

## 🕹️ Controls

| Action | Keybinding |
| :--- | :--- |
| **Movement** | `W`, `A`, `S`, `D` or Arrow Keys |
| **Dash / Sprint** | `Space` or `Shift` (consumes Stamina) |
| **Active Spells** | `Q`, `E`, `F` |
| **Interaction / Dialogue** | `Enter` / `E` / Mouse Click |
| **Pause / Resume** | `ESC` |
| **Debug Console / Tools** | `F4` – `F10` (Developer shortcuts) |

---

## 🛠️ Tech Stack & Architecture

- **Engine & Framework**: [libGDX](https://libgdx.com/) `1.14.0` with **LWJGL3** desktop backend.
- **Language & Runtime**: Java 21 (Temurin / OpenJDK 21).
- **Zero-Allocation Game Loop**: Custom object pooling (`GamePools`), reusable collider caches, non-allocating UI strings (`StringBuilder`).
- **Rendering & Shaders**: Custom GLSL shaders for ambient lighting and dynamic character silhouette behind walls.
- **Map System**: Tiled Map (`.tmx`, `.tsx`) with custom layer parsing for collisions, portals, and object spawning.
- **Build System**: Gradle with Gradle Wrapper (`gradlew`).

---

## 📂 Project Structure

```
.
├── assets/                  # Game static assets
│   ├── audio/              # Music and SFX (categorized by context)
│   ├── character/          # Player sprites, enemy textures, projectiles
│   ├── configs/            # JSON data-driven configurations (enemies, weapons, items, levels)
│   ├── items/              # Item icons and collectibles
│   ├── lab/                # Computer lab tileset components
│   ├── map/                # TMX maps & background images
│   ├── outside/            # Outdoor campus tileset components
│   ├── Phong_doc/          # Library entity textures (Candle, FloatingBook)
│   ├── puzzle/             # Card puzzle assets
│   ├── shaders/            # Custom GLSL vertex & fragment shaders
│   ├── tsx/                # TSX tilesets
│   └── ui/                 # UI skins, fonts (.ttf, .fnt), and HUD textures
├── core/                   # Shared game logic, entities, screens, mechanics (99% of code)
├── lwjgl3/                 # Desktop LWJGL3 launcher configuration
└── docs/                   # Architectural & technical documentation
    ├── CONVENTIONS.md      # Strict OOP, memory management & GC rules
    ├── PROJECT_MAP.md      # Complete package & class responsibility breakdown
    ├── TECH_STACK.md       # Technical specs & version manifest
    ├── tmx_map_rule.md     # Standards for Tiled maps and object layers
    ├── adr/                # Architecture Decision Records
    └── agents/             # Agent guidelines and domain context
```

---

## 🚀 Getting Started

### Prerequisites
- JDK 21 installed and configured on your `PATH`.

### Running the Game
```bash
# Run desktop client
./gradlew lwjgl3:run
```

### Running Tests & Building
```bash
# Run unit & integration test suites
./gradlew test

# Build executable JAR
./gradlew lwjgl3:jar

# Clean build artifacts
./gradlew clean
```
The compiled runnable JAR will be generated at `lwjgl3/build/libs/`.

---

## 📖 Domain Documentation

For developers and contributors:
- Consult [CONTEXT.md](CONTEXT.md) for the ubiquitous domain vocabulary.
- Review [docs/CONVENTIONS.md](docs/CONVENTIONS.md) before writing code to prevent memory leaks and OOP violations.
- Review [docs/PROJECT_MAP.md](docs/PROJECT_MAP.md) for module and class architectural responsibilities.

