package dev.onelili.bingoreloaded.stages;

import cn.jason31416.planetlib.wrapper.SimplePlayer;
import dev.onelili.bingoreloaded.games.Arena;
import dev.onelili.bingoreloaded.games.Stage;
import dev.onelili.bingoreloaded.games.Team;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;

public class ObservingStage extends Stage {
    public ObservingStage(Arena arena){
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

    }

    @Override
    public void onMove(@NotNull PlayerMoveEvent event) {
        if(event.getTo() == null)
            return;
        Location loc = event.getTo();
        Team team = arena.getPlayerTeam(SimplePlayer.of(event.getPlayer()));
        if(team == null) return;
        loc.setX(team.getTeamSpawn().x());
        loc.setY(team.getTeamSpawn().y());
        loc.setZ(team.getTeamSpawn().z());
        event.setTo(loc);
    }

    @Override
    public @NotNull JoinType onJoin(@NotNull SimplePlayer player) {
        return JoinType.SPECTATOR;
    }
}
