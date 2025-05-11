package dev.onelili.bingoreloaded.stages;

import cn.jason31416.planetlib.wrapper.SimpleLocation;
import cn.jason31416.planetlib.wrapper.SimplePlayer;
import dev.onelili.bingoreloaded.games.Arena;
import org.bukkit.GameMode;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;

public class RunningStage extends Stage {
    public RunningStage(Arena arena){
        super(arena);
    }

    @Override
    public void init(@NotNull Stage from) {
    }

    @Override
    public void end(@NotNull Stage to) {
    }

    @Override
    public void perTick() {
        for(var i: arena.getTeams()){
            if(i.isTeamWin()){
                arena.setCurrentStage(new FinishingStage());
                return;
            }
        }
    }

    @Override
    public @NotNull JoinType onJoin(@NotNull SimplePlayer player) {
        if(arena.getPlayers().contains(player)) return JoinType.ACCEPT;
        player.getPlayer().setGameMode(GameMode.SPECTATOR);
        player.teleport(SimpleLocation.of(arena.getWorld().getBukkitWorld().getSpawnLocation()));
        return JoinType.SPECTATOR;
    }
}
