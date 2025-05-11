package dev.onelili.bingoreloaded.games;

import cn.jason31416.planetlib.wrapper.SimpleLocation;
import cn.jason31416.planetlib.wrapper.SimplePlayer;
import lombok.Getter;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Team {
    @Getter
    private final Color teamColor;
    @Getter
    private final Material item;
    @Getter
    private final Panel.RendererPosition position;
    @Getter
    private final List<Material> collected = new ArrayList<>();
    @Getter
    private final Set<SimplePlayer> players = new HashSet<>();
    @Getter
    private final SimpleLocation teamSpawn;

    private final Arena arena;

    public Team(@NotNull Arena arena, @NotNull Color color, @NotNull Material item, @NotNull Panel.RendererPosition position, @NotNull SimpleLocation spawn) {
        this.arena = arena;
        this.teamColor = color;
        this.item = item;
        this.position = position;
        this.teamSpawn = spawn;
    }

    public boolean isTeamWin() {
        Material[][] materials = arena.getPanel().getMaterials();
        for (int i = 0; i < 5; i++) {
            outer1:
            {
                for (int j = 0; j < 5; j++)
                    if (!collected.contains(materials[i][j])) break outer1;
                return true;
            }
            outer2:
            {
                for (int j = 0; j < 5; j++)
                    if (!collected.contains(materials[j][i])) break outer2;
                return true;
            }
        }
        outerDiag1:
        {
            for (int i = 0; i < 5; i++)
                if (!collected.contains(materials[i][i])) break outerDiag1;
            return true;
        }
        outerDiag2:
        {
            for (int i = 0; i < 5; i++)
                if (!collected.contains(materials[i][4 - i])) break outerDiag2;
            return true;
        }
        return false;
    }
}
