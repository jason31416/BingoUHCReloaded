package dev.onelili.bingoreloaded.stages;

import cn.jason31416.planetlib.wrapper.SimpleLocation;
import cn.jason31416.planetlib.wrapper.SimplePlayer;
import dev.onelili.bingoreloaded.games.Arena;
import dev.onelili.bingoreloaded.resources.Config;
import dev.onelili.bingoreloaded.resources.Lang;
import org.bukkit.GameMode;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class WaitingStage extends Stage {
    private double timer = -1;
    private final Map<SimplePlayer, Boolean> readyStates = new HashMap<>();

    public WaitingStage(Arena arena) {
        super(arena);
    }

    @Override
    public void init(@NotNull Stage from) {
        arena.getWorld().getBukkitWorld().getWorldBorder().setCenter(arena.getWorld().getBukkitWorld().getSpawnLocation());
        arena.getWorld().getBukkitWorld().getWorldBorder().setSize(32);
    }

    @Override
    public void end(@NotNull Stage to) {
        arena.getWorld().getBukkitWorld().getWorldBorder().setSize(29999984);
    }

    @Override
    public void perTick() {
        boolean isAllReady = true;
        for (SimplePlayer i : arena.getPlayers()) {
            if (!readyStates.getOrDefault(i, false)) {
                isAllReady = false;
                break;
            }
        }
        if (arena.getPlayers().size() >= Config.getInt("arena-settings.min-players") && isAllReady && timer == -1) {
            arena.getPlayers().forEach(player -> player.sendMessage(Lang.getMessage("stage.waiting.start-countdown")));
            timer = Config.getInt("arena-settings.countdown-time", 10);
        } else if (timer > 0) {
            if (arena.getPlayers().size() < Config.getInt("arena-settings.min-players")) {
                arena.getPlayers().forEach(player -> player.sendMessage(Lang.getMessage("stage.waiting.cancel-countdown.not-enough-players")));
                timer = -1;
            } else if (!isAllReady) {
                arena.getPlayers().forEach(player -> player.sendMessage(Lang.getMessage("stage.waiting.cancel-countdown.not-ready")));
                timer = -1;
            }
        }

        if (timer > 0) {
            timer-=0.05;
            arena.getPlayers().forEach(p->{
                if(p.getPlayer().getGameMode()!=GameMode.SPECTATOR){
                    p.getPlayer().setExp((float) (timer / Config.getInt("arena-settings.countdown-time", 10)));
                    p.getPlayer().setLevel((int) timer);
                }
            });
            if(Math.abs(timer-(int)timer) < 0.0001){

            }
            if (timer == 0) {
                arena.setCurrentStage(new ObservingStage(arena));
            }
        }
    }

    @Override
    public void onMove(@NotNull PlayerMoveEvent event) {
        super.onMove(event);
    }

    @NotNull
    @Override
    public JoinType onJoin(@NotNull SimplePlayer player) {
        if (arena.getPlayers().size() >= Config.getInt("arena-settings.max-players", 16)) return JoinType.DECLINE;
        readyStates.put(player, false);
        player.getPlayer().setGameMode(GameMode.ADVENTURE);
        player.teleport(SimpleLocation.of(arena.getWorld().getBukkitWorld().getSpawnLocation()));
        return JoinType.ACCEPT;
    }

    @Override
    public void onQuit(@NotNull SimplePlayer player) {
        readyStates.remove(player);
    }
}
