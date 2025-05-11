package dev.onelili.bingoreloaded.stages;

import cn.jason31416.planetlib.PlanetLib;
import cn.jason31416.planetlib.wrapper.SimpleLocation;
import cn.jason31416.planetlib.wrapper.SimplePlayer;
import dev.onelili.bingoreloaded.BingoUHCReloaded;
import dev.onelili.bingoreloaded.games.Arena;
import dev.onelili.bingoreloaded.games.Team;
import dev.onelili.bingoreloaded.resources.Config;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RunningStage extends Stage {
    public RunningStage(Arena arena){
        super(arena);
    }

    @Override
    public void init(@NotNull Stage from) {
        // TODO:设置世界规则：旁观者无法加入、死亡不掉落
        PlanetLib.getScheduler().runNextTick(t->{
            arena.getWorld().getBukkitWorld().setGameRule(GameRule.SPECTATORS_GENERATE_CHUNKS, false);
            arena.getNether().getBukkitWorld().setGameRule(GameRule.SPECTATORS_GENERATE_CHUNKS, false);
            arena.getEnd().getBukkitWorld().setGameRule(GameRule.SPECTATORS_GENERATE_CHUNKS, false);
        });

        List<ItemStack> toolList = new ArrayList<>();
        Objects.requireNonNull(Config.getConfig().getConfigurationSection("arena-settings.default-tools")).getKeys(false).forEach(toolId -> {
            ConfigurationSection section = Objects.requireNonNull(Config.getConfig().getConfigurationSection("default-tools." + toolId));
            ItemStack tool = new ItemStack(Material.valueOf(Objects.requireNonNull(section.getString("material"))));
            ItemMeta meta = Objects.requireNonNull(tool.getItemMeta());
            //TODO:meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(section.getString("name"))));
            List<String> lore = new ArrayList<>();
            section.getStringList("lore").forEach(text -> lore.add(ChatColor.translateAlternateColorCodes('&', text)));
            meta.setLore(lore);
            // TODO:bingo物品不检测的nbt
            section.getStringList("enchantments").forEach(enchantmentId -> {
                String[] enchantmentData = new String[]{enchantmentId.split(" ")[0], enchantmentId.split(" ").length == 2 ? enchantmentId.split(" ")[1] : "I"};
                try {
                    Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(enchantmentData[0].toLowerCase()));
                    int level = 0;
                    int prevValue = 0;
                    for (int i = enchantmentData[1].length() - 1; i >= 0; i--) {
                        int currentValue = switch (enchantmentData[1].charAt(i)) {
                            case 'I' -> 1;
                            case 'V' -> 5;
                            case 'X' -> 10;
                            case 'L' -> 50;
                            case 'C' -> 100;
                            case 'D' -> 500;
                            case 'M' -> 1000;
                            default -> throw new IllegalArgumentException();
                        };
                        if (currentValue < prevValue) {
                            level -= currentValue;
                        } else {
                            level += currentValue;
                        }
                        prevValue = currentValue;
                    }
                    if (enchantment != null) meta.addEnchant(enchantment, level, true);
                } catch (Throwable ignored) {
                    BingoUHCReloaded.getInstance().getLogger().severe("Unknown enchantment data in configuration!");
                }
            });
            if (meta instanceof Damageable damageable && section.getInt("durability", 0) != 0) {
                int damage = tool.getType().getMaxDurability() - section.getInt("durability");
                damageable.setDamage(Math.max(damage, 0));
            }
            tool.setItemMeta(meta);
            toolList.add(tool);
        });
        ItemStack[] rawToolArray = (ItemStack[]) Array.newInstance(ItemStack.class, toolList.size());
        ItemStack[] toolArray = toolList.toArray(rawToolArray);
        for(SimplePlayer player : arena.getPlayers())
            PlanetLib.getScheduler().runAtEntity(player.getPlayer(), task -> {
                player.getPlayer().getInventory().addItem(toolArray);
                player.getPlayer().setExp(0);
                player.getPlayer().setLevel(0);
                player.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, PotionEffect.INFINITE_DURATION, 1));
            });
        // TODO:设置玩家相互显示
    }

    @Override
    public void end(@NotNull Stage to) {
    }

    @Override
    public void perTick() {
        for (Team team : arena.getTeams()) {
            if (team.isTeamWin()) {
                arena.setCurrentStage(new FinishingStage(arena));
                return;
            }
        }
    }

    @Override
    public void onMove(@NotNull PlayerMoveEvent event) {
        PlanetLib.getScheduler().runAtLocation(event.getFrom(), task -> {
            SimpleLocation location = SimpleLocation.of(event.getFrom()).getRelative(0, -1, 0);
            location.getLoadedBlock().thenAccept(block -> {
                if (block.getType() != Material.AIR)
                    event.getPlayer().removePotionEffect(PotionEffectType.SLOW_FALLING);
            });
        });
    }

    @Override
    @NotNull
    public JoinType onJoin(@NotNull SimplePlayer player) {
        if(arena.getPlayers().contains(player)) return JoinType.ACCEPT;
        player.getPlayer().setGameMode(GameMode.SPECTATOR);
        player.teleport(SimpleLocation.of(arena.getWorld().getBukkitWorld().getSpawnLocation()));
        return JoinType.SPECTATOR;
    }
}
