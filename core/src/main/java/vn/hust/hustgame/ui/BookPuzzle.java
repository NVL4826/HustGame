package vn.hust.hustgame.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import java.util.ArrayList;
import java.util.List;

import vn.hust.hustgame.GameState;
import vn.hust.hustgame.screens.LibraryScreen;

public class BookPuzzle {
    public static class Book {
        public String name;
        public Rectangle rect;
        public int targetSemester;
        public boolean placedCorrectly = false;
        public boolean isDragging = false;
        
        public Book(String name, float x, float y, int targetSemester) {
            this.name = name;
            this.rect = new Rectangle(x, y, 100, 30);
            this.targetSemester = targetSemester;
        }
    }
    
    public static class Slot {
        public int semester;
        public Rectangle rect;
        
        public Slot(int semester, float x, float y) {
            this.semester = semester;
            this.rect = new Rectangle(x, y, 120, 40);
        }
    }
    
    public List<Book> books;
    public List<Slot> slots;
    private BitmapFont font;
    private boolean isSolved = false;
    private LibraryScreen screen;
    
    public BookPuzzle(LibraryScreen screen) {
        this.screen = screen;
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        
        books = new ArrayList<>();
        books.add(new Book("Toan cao cap", 50, 400, 1));
        books.add(new Book("CTDL & GT", 50, 350, 3));
        books.add(new Book("Mang may tinh", 50, 300, 5));
        books.add(new Book("CSDL", 50, 250, 4));
        books.add(new Book("Lap trinh Java", 50, 200, 4));
        books.add(new Book("Ky nghe PM", 50, 150, 5));
        books.add(new Book("AI", 50, 100, 7));
        books.add(new Book("Do an", 50, 50, 8));
        
        slots = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            slots.add(new Slot(i + 1, 300, 400 - i * 50));
        }
    }
    
    public void update(float delta) {
        if (isSolved) return;
        
        com.badlogic.gdx.math.Vector3 touchPos = new com.badlogic.gdx.math.Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        screen.getCamera().unproject(touchPos);
        float mx = touchPos.x;
        float my = touchPos.y;
        
        if (Gdx.input.justTouched()) {
            for (Book b : books) {
                if (!b.placedCorrectly && b.rect.contains(mx, my)) {
                    b.isDragging = true;
                    break; // Only drag one at a time
                }
            }
        }
        
        if (Gdx.input.isTouched()) {
            for (Book b : books) {
                if (b.isDragging) {
                    b.rect.x = mx - b.rect.width / 2;
                    b.rect.y = my - b.rect.height / 2;
                }
            }
        } else {
            // Check drops
            for (Book b : books) {
                if (b.isDragging) {
                    b.isDragging = false;
                    boolean placed = false;
                    for (Slot s : slots) {
                        if (s.rect.overlaps(b.rect)) {
                            if (b.targetSemester == s.semester) {
                                b.placedCorrectly = true;
                                b.rect.setPosition(s.rect.x + 10, s.rect.y + 5);
                                placed = true;
                            } else {
                                // Wrong placement penalty
                                GameState.instance.hp -= 10;
                                screen.flashRed();
                            }
                            break;
                        }
                    }
                    if (!placed && !b.placedCorrectly) {
                        // Return to some initial layout (simplified)
                        b.rect.x = 50;
                    }
                }
            }
            
            // Check win condition
            boolean allPlaced = true;
            for (Book b : books) {
                if (!b.placedCorrectly) {
                    allPlaced = false;
                    break;
                }
            }
            if (allPlaced) {
                isSolved = true;
                screen.onPuzzleSolved();
            }
        }
    }
    
    public void render(ShapeRenderer shapeRenderer, SpriteBatch batch) {
        if (isSolved) return;
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        for (Slot s : slots) {
            shapeRenderer.setColor(Color.YELLOW);
            shapeRenderer.rect(s.rect.x, s.rect.y, s.rect.width, s.rect.height);
        }
        shapeRenderer.end();
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (Book b : books) {
            if (b.placedCorrectly) {
                shapeRenderer.setColor(Color.GREEN);
            } else if (GameState.instance.hasNao && b.isDragging) {
                // Hint logic: highlight red if over wrong slot (simplified)
                shapeRenderer.setColor(Color.BLUE);
            } else {
                shapeRenderer.setColor(Color.BROWN);
            }
            shapeRenderer.rect(b.rect.x, b.rect.y, b.rect.width, b.rect.height);
        }
        shapeRenderer.end();
        
        batch.begin();
        for (Slot s : slots) {
            font.draw(batch, "HK " + s.semester, s.rect.x + 10, s.rect.y + 25);
        }
        for (Book b : books) {
            font.draw(batch, b.name, b.rect.x + 5, b.rect.y + 20);
        }
        batch.end();
    }
    
    public void dispose() {
        font.dispose();
    }
}
