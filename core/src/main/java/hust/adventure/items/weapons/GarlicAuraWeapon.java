package hust.adventure.items.weapons;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import hust.adventure.collision.CollisionLayer;
import hust.adventure.entities.Player;
import hust.adventure.entities.base.GameEntity;
import hust.adventure.entities.base.Damageable;

/**
 * A garlic weapon that creates an aura damaging all nearby enemies periodically.
 * Renders three rotating concentric dashed rings around the player.
 */
public class GarlicAuraWeapon extends BaseWeapon {
    private float rotationAngle = 0f;
    private static Texture whitePixel;

    public GarlicAuraWeapon(final Player owner, final float baseDamage, final float cooldown, final float area) {
        super(owner, "garlic", "Tỏi bảo hộ", "Tạo vòng hào quang gây sát thương xung quanh.", baseDamage, cooldown,
                area);
    }

    @Override
    protected void executeAttackAction() {
        final float radius = getArea(); // Using 'area' stat as radius
        final Array<GameEntity> targets = getOwner().getCollisionManager().getEntitiesInRadius(getOwner().getX(), getOwner().getY(),
                radius, CollisionLayer.ENEMY);

        for (final GameEntity target : targets) {
            if (target instanceof Damageable) {
                ((Damageable) target).takeDamage(getBaseDamage());
            }
        }
    }

    @Override
    public void updateTimer(final float delta) {
        super.updateTimer(delta);
        rotationAngle += delta * 45f; // rotate 45 degrees per second
    }

    @Override
    public void draw(final SpriteBatch batch) {
        final float px = getOwner().getX();
        final float py = getOwner().getY();
        final float baseRadius = getArea();

        // Draw 3 rotating concentric circles
        drawDashedCircle(batch, px, py, baseRadius * 0.6f, rotationAngle, new Color(0.85f, 0.95f, 0.75f, 0.3f));
        drawDashedCircle(batch, px, py, baseRadius * 0.8f, -rotationAngle * 0.7f, new Color(0.85f, 0.95f, 0.75f, 0.25f));
        drawDashedCircle(batch, px, py, baseRadius * 1.0f, rotationAngle * 0.4f, new Color(0.85f, 0.95f, 0.75f, 0.15f));
    }

    private static Texture getWhitePixel() {
        if (whitePixel == null) {
            final Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
            pixmap.setColor(Color.WHITE);
            pixmap.fill();
            whitePixel = new Texture(pixmap);
            pixmap.dispose();
        }
        return whitePixel;
    }

    public static void disposeStatic() {
        if (whitePixel != null) {
            whitePixel.dispose();
            whitePixel = null;
        }
    }

    private void drawDashedCircle(final SpriteBatch batch, final float cx, final float cy, final float r, final float angleOffset, final Color color) {
        final int numDashes = 12;
        final float dashDegrees = 15f;
        for (int i = 0; i < numDashes; i++) {
            final float midAngle = i * (360f / numDashes) + angleOffset;
            drawArcSegment(batch, cx, cy, r, midAngle - dashDegrees / 2f, midAngle + dashDegrees / 2f, 2f, color);
        }
    }

    private void drawArcSegment(final SpriteBatch batch, final float cx, final float cy, final float r, final float startAngle, final float endAngle, final float thickness, final Color color) {
        final int steps = 3;
        final float stepSize = (endAngle - startAngle) / steps;
        for (int i = 0; i < steps; i++) {
            final float a1 = startAngle + i * stepSize;
            final float a2 = a1 + stepSize;

            final float rad1 = (float) Math.toRadians(a1);
            final float rad2 = (float) Math.toRadians(a2);

            final float x1 = cx + r * (float) Math.cos(rad1);
            final float y1 = cy + r * (float) Math.sin(rad1);
            final float x2 = cx + r * (float) Math.cos(rad2);
            final float y2 = cy + r * (float) Math.sin(rad2);

            drawLine(batch, x1, y1, x2, y2, thickness, color);
        }
    }

    private void drawLine(final SpriteBatch batch, final float x1, final float y1, final float x2, final float y2, final float thickness, final Color color) {
        final float dx = x2 - x1;
        final float dy = y2 - y1;
        final float length = (float) Math.sqrt(dx * dx + dy * dy);
        final float angle = (float) Math.toDegrees(Math.atan2(dy, dx));

        batch.setColor(color);
        batch.draw(getWhitePixel(),
            x1, y1 - thickness / 2f, // x, y
            0f, thickness / 2f,      // originX, originY (pivot at start of line)
            length, thickness,       // width, height
            1f, 1f,                  // scaleX, scaleY
            angle,                   // rotation
            0, 0, 1, 1,              // srcX, srcY, srcWidth, srcHeight
            false, false             // flipX, flipY
        );
        batch.setColor(Color.WHITE);
    }

    @Override
    public void upgrade(final float damageBonus, final float cooldownReduction) {
        if (getLevel() >= 5) {
            return;
        }
        super.upgrade(damageBonus, cooldownReduction);
        applyLevelStats();
    }

    private void applyLevelStats() {
        switch (getLevel()) {
            case 2:
                setArea(60f * 1.4f);
                setBaseDamage(7f);
                break;
            case 3:
                setCooldown(1.2f);
                setBaseDamage(8f);
                break;
            case 4:
                setArea(60f * 1.6f);
                setBaseDamage(9f);
                break;
            case 5:
                setCooldown(1.1f);
                setBaseDamage(11f);
                break;
        }
    }
}
