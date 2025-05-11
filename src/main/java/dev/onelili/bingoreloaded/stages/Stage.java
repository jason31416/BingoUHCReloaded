package dev.onelili.bingoreloaded.stages;

import cn.jason31416.planetlib.wrapper.SimplePlayer;
import dev.onelili.bingoreloaded.games.Arena;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;

public abstract class Stage {
    public enum JoinType {
        ACCEPT,
        SPECTATOR,
        DECLINE
    }

    public final Arena arena;

    public Stage(Arena arena) {
        this.arena = arena;
    }

    public abstract void init(@NotNull Stage from);

    public abstract void end(@NotNull Stage to);

    public abstract void perTick();

    public void onMove(@NotNull PlayerMoveEvent event){}

    @NotNull
    public abstract JoinType onJoin(@NotNull SimplePlayer player);

    public void onQuit(@NotNull SimplePlayer player) {
    }
}