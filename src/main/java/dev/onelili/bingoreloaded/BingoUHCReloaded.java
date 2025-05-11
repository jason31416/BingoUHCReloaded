package dev.onelili.bingoreloaded;

import cn.jason31416.planetlib.PlanetLib;
import cn.jason31416.planetlib.Required;
import cn.jason31416.planetlib.message.InternalPlaceholder;
import cn.jason31416.planetlib.message.MessageLoader;
import dev.onelili.bingoreloaded.commands.BingoCommand;
import dev.onelili.bingoreloaded.games.Arena;
import dev.onelili.bingoreloaded.resources.Config;
import dev.onelili.bingoreloaded.resources.Lang;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public final class BingoUHCReloaded extends JavaPlugin {
    @Getter
    private static BingoUHCReloaded instance;
    @Getter
    private static final Set<Arena> arenas = new HashSet<>();

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        PlanetLib.initialize(this, Required.NBT);
        Config.start(this);
        Lang.loader = new MessageLoader(new File(getDataFolder(), "lang/" + Config.getString("language") + ".yml"));

        InternalPlaceholder.registerPlaceholderHandler(st -> st.replace("[-]", Lang.loader.getRawMessage("prefix")));

        new BingoCommand("bingo").register();

    }

    @Override
    public void onDisable() {
        PlanetLib.disable();
    }
}
