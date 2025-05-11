package dev.onelili.bingoreloaded.games;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class Panel extends MapRenderer {
    public enum RendererPosition {
        LEFT_UP,
        LEFT_DOWN,
        RIGHT_UP,
        RIGHT_DOWN
    }

    @Getter
    private final Material[][] materials;
    @Getter
    private final BufferedImage[][] textures;
    @Getter
    private BufferedImage image;

    private final Arena arena;

    public Panel(@NotNull Arena arena, @NotNull Material[][] materials) throws RuntimeException {
        this.arena = arena;
        this.materials = materials;
        this.textures = new BufferedImage[5][5];
        for (int i = 0; i < 5; i++)
            for (int j = 0; j < 5; j++) {
                Material material = materials[i][j];
                try(InputStream stream = getClass().getClassLoader().getResourceAsStream("textures/" + material.name().toLowerCase() + ".png")) {
                    textures[i][j] = ImageIO.read(Objects.requireNonNull(stream));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
    }

    @Override
    public void render(@NotNull MapView view, @NotNull MapCanvas canvas, @NotNull Player player) {
        canvas.drawImage(0, 0, image);
    }

    public void flush() {
        BufferedImage backgroundImage = null;
        try (InputStream stream = getClass().getClassLoader().getResourceAsStream("textures/background.png")) {
            if (stream != null) {
                backgroundImage = ImageIO.read(stream);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        image = backgroundImage != null ? backgroundImage : new BufferedImage(1, 1, 1);
        for (int i = 0; i < 5; i++)
            for (int j = 0; j < 5; j++) {
                Material material = materials[i][j];
                for (Team team : arena.getTeams()) {
                    if(team.getCollected().contains(material)) {
                        switch (team.getPosition()) {
                            case RIGHT_DOWN -> _fill(24 * i + 17, 24 * j + 17, 24 * i + 26, 24 * j + 26, image, team.getTeamColor());
                            case LEFT_DOWN -> _fill(24 * i + 6, 24 * j + 17, 24 * i + 15, 24 * j + 26, image, team.getTeamColor());
                            case RIGHT_UP -> _fill(24 * i + 17, 24 * j + 6, 24 * i + 26, 24 * j + 15, image, team.getTeamColor());
                            case LEFT_UP -> _fill(24 * i + 6, 24 * j + 6, 24 * i + 15, 24 * j + 15, image, team.getTeamColor());
                        }
                    }
                }
            }
        Graphics2D graphics = image.createGraphics();
        graphics.setComposite(AlphaComposite.SrcOver);
        for (int i = 0; i < 5; i++)
            for (int j = 0; j < 5; j++) {
                graphics.drawImage(textures[i][j], i * 24 + 8, j * 24 + 8, null);
            }
        graphics.dispose();
    }
    public static void _fill(int sx, int sy, int ex, int ey, BufferedImage image, Color color) {
        for (int i = sy; i <= ey; i++)
            for (int j = sx; j <= ex; j++) {
                image.setRGB(j, i, color.getRGB());
            }
    }
}
