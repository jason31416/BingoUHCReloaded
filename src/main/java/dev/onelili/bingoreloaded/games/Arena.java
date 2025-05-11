package dev.onelili.bingoreloaded.games;

import cn.jason31416.folialib.wrapper.task.WrappedTask;
import cn.jason31416.planetlib.PlanetLib;
import cn.jason31416.planetlib.wrapper.SimplePlayer;
import cn.jason31416.planetlib.wrapper.SimpleWorld;
import dev.onelili.bingoreloaded.resources.Config;
import dev.onelili.bingoreloaded.stages.Stage;
import dev.onelili.bingoreloaded.stages.WaitingStage;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static dev.onelili.bingoreloaded.utils.FileUtil.deletePath;

public class Arena implements Listener {
    //=========================================================================================================//
    @Getter
    private final UUID arenaID;
    //=========================================================================================================//
    @Getter
    private final SimpleWorld world, nether, end;
    //=========================================================================================================//
    @Getter
    private final Set<Team> teams = new HashSet<>();
    @Getter
    private final Set<SimplePlayer> players = new HashSet<>();
    //=========================================================================================================//
    @Getter
    private Stage currentStage;
    private final WrappedTask perTickTask;
    //=========================================================================================================//
    private Arena (
            @NotNull UUID arenaID,
            @NotNull SimpleWorld world,
            @NotNull SimpleWorld nether,
            @NotNull SimpleWorld end
    ) {
        this.arenaID = arenaID;
        this.world = world;
        this.nether = nether;
        this.end = end;
        perTickTask = PlanetLib.getScheduler().runTimerAsync(() -> currentStage.perTick(), 1L, 1L);
    }
    @Nullable
    public Team getPlayerTeam(SimplePlayer player){
        for(Team i: teams){
            if(i.getPlayers().contains(player)) return i;
        }
        return null;
    }
    //=========================================================================================================//
    @NotNull
    public static CompletableFuture<Arena> createArena() {
        UUID id = UUID.randomUUID();
        CompletableFuture<Arena> ret = new CompletableFuture<>();
        PlanetLib.getScheduler().runAsync(task -> {
            String generator = Config.getString("arena-settings.world-generator", "");
            World world = new WorldCreator("bingo_" + id + "_world")
                    .generator(!generator.isEmpty() ? generator : null)
                    .type(WorldType.NORMAL)
                    .environment(World.Environment.NORMAL)
                    .createWorld();
            World nether = new WorldCreator("bingo_" + id + "_nether")
                    .generator(!generator.isEmpty() ? generator : null)
                    .type(WorldType.NORMAL)
                    .environment(World.Environment.NETHER)
                    .createWorld();
            World the_end = new WorldCreator("bingo_" + id + "_the_end")
                    .generator(!generator.isEmpty() ? generator : null)
                    .type(WorldType.NORMAL)
                    .environment(World.Environment.THE_END)
                    .createWorld();
            Arena arena = new Arena(id, SimpleWorld.of(world), SimpleWorld.of(nether), SimpleWorld.of(the_end));
            arena.currentStage = new WaitingStage(arena);
            ret.complete(arena);
        });
        return ret;
    }
    //=========================================================================================================//
    public void join(@NotNull SimplePlayer player) {}
    public void leave(@NotNull SimplePlayer player) {}
    public void setCurrentStage (@NotNull Stage stage) {
        currentStage.end(stage);
        Stage tmp = currentStage;
        currentStage = stage;
        currentStage.init(tmp);
    }
    public void close() {
        perTickTask.cancel();
        File[] folders = new File[] {
                world.getBukkitWorld().getWorldFolder(),
                nether.getBukkitWorld().getWorldFolder(),
                end.getBukkitWorld().getWorldFolder()
        };
        Bukkit.unloadWorld(world.getBukkitWorld(), false);
        Bukkit.unloadWorld(nether.getBukkitWorld(), false);
        Bukkit.unloadWorld(end.getBukkitWorld(), false);
        for (File file : folders)
            deletePath(file);
    }
}
