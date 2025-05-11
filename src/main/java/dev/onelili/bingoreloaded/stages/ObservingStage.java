package dev.onelili.bingoreloaded.stages;

import cn.jason31416.planetlib.wrapper.SimpleLocation;
import cn.jason31416.planetlib.wrapper.SimplePlayer;
import dev.onelili.bingoreloaded.games.Arena;
import dev.onelili.bingoreloaded.games.Team;
import dev.onelili.bingoreloaded.resources.Config;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;

public class ObservingStage extends Stage {
    private double timer = 0;

    public ObservingStage(Arena arena) {
        super(arena);
    }

    @Override
    public void init(@NotNull Stage from) {
        arena.getPlayers().forEach(p -> {
            if (arena.getPlayerTeam(p) == null) {
                p.getPlayer().setGameMode(GameMode.SPECTATOR);
                p.teleport(SimpleLocation.of(arena.getWorld().getBukkitWorld().getSpawnLocation()));
            } else {
                p.getPlayer().setGameMode(GameMode.SURVIVAL);
                p.teleport(arena.getPlayerTeam(p).getTeamSpawn());
            }
        });
        timer = Config.getDouble("arena-settings.observing-time");
    }

    @Override
    public void end(@NotNull Stage to) {
    }

    @Override
    public void perTick() {
        if (--timer < 0) {
            arena.setCurrentStage(new RunningStage(arena));
        }
    }

    @Override
    public void onMove(@NotNull PlayerMoveEvent event) {
        if (event.getTo() == null)
            return;
        Location loc = event.getTo();
        Team team = arena.getPlayerTeam(SimplePlayer.of(event.getPlayer()));
        if (team == null)
            return;
        loc.setX(team.getTeamSpawn().x());
        loc.setY(team.getTeamSpawn().y());
        loc.setZ(team.getTeamSpawn().z());
        event.setTo(loc);
    }

    @Override
    public @NotNull JoinType onJoin(@NotNull SimplePlayer player) {
        player.getPlayer().setGameMode(GameMode.SPECTATOR);
        player.teleport(SimpleLocation.of(arena.getWorld().getBukkitWorld().getSpawnLocation()));
        return JoinType.SPECTATOR;
    }
}
