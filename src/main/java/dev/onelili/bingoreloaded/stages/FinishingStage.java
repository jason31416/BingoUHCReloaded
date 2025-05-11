package dev.onelili.bingoreloaded.stages;

import cn.jason31416.planetlib.wrapper.SimplePlayer;
import dev.onelili.bingoreloaded.games.Arena;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;

public class FinishingStage extends Stage {
    public FinishingStage(Arena arena){
        super(arena);
    }

    @Override
    public @NotNull JoinType onJoin(@NotNull SimplePlayer player) {
        return null;
    }

    @Override
    public void perTick() {

    }

    @Override
    public void init(@NotNull Stage from) {

    }
    @Override
    public void end(@NotNull Stage to) {

    }
}
