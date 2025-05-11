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
    @Getter @NotNull
    private final SimpleLocation teamSpawn;
    public Team (@NotNull Color color, @NotNull Material item, @NotNull Panel.RendererPosition position, SimpleLocation spawn) {
        this.teamColor = color;
        this.item = item;
        this.position = position;
        this.teamSpawn = spawn;
    }
}
