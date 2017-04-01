package red.man10.gacha;

import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.List;
import java.util.Random;
import java.util.Timer;

import static org.bukkit.Bukkit.getServer;

/**
 * Created by sho-pc on 2017/03/23.
 */
public class GachaRunnable {

    private final GachaPlugin plugin;
    public GachaRunnable(GachaPlugin plugin) {
        this.plugin = plugin;
    }

    public void roll(Inventory inv,int time,Player p,String file){
        new BukkitRunnable(){
            int a = 0;
            //int slots = plugin.configFunction.getSlotsFromFile(file);
            List<ItemStack> items = plugin.configFunction.fileItemsToList(file);
            int[] itemnumber = new int[6];
            public void run() {
                    Random r = new Random();
                    int result = r.nextInt(items.size());
                    itemnumber[5] = result;
                    for (int i = 0; i < 8; i++) {
                        inv.setItem(i + 9, inv.getItem(i + 10));
                    }
                    for (int i = 0; i < 5; i++) {
                        itemnumber[i] = itemnumber[i + 1];
                    }
                    p.playSound(p.getLocation(), Sound.BLOCK_DISPENSER_DISPENSE, 1, 1);
                    inv.setItem(17, items.get(result));
                    a++;
                    if (a >= 25 && time == 4) {
                        String name = inv.getItem(13).getItemMeta().getDisplayName();
                        if (name == null) {
                            name = inv.getItem(13).getType().name();
                        }
                        if (plugin.configFunction.winIsTrue(file) == true) {
                            if(itemnumber[0] == 0) {
                                p.playSound(p.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
                                Location loc = new Location(p.getWorld(),p.getLocation().getX(),p.getLocation().getY()-1,p.getLocation().getZ());
                                Firework fw = p.getWorld().spawn(loc, Firework.class);
                                FireworkMeta fwm = fw.getFireworkMeta();
                                FireworkEffect effect = FireworkEffect.builder().withColor(Color.BLUE.mixColors(Color.YELLOW.mixColors(Color.WHITE))).with(FireworkEffect.Type.BALL).withFade(Color.BLACK).build();
                                fwm.addEffects(effect);
                                fw.setFireworkMeta(fwm);
                            }
                            if (plugin.configFunction.winBroadcastIsTrue(file) == true) {
                                if (itemnumber[0] == 0) {
                                    String message = plugin.configFunction.getWinBroadcast(file).replaceAll("%PLAYER%", p.getName()).replaceAll("%TITLE%", inv.getName()).replaceAll("%ITEM%", name).replaceAll("%AMMOUNT%", String.valueOf(inv.getItem(13).getAmount()));
                                    getServer().broadcastMessage(message);
                                    playAnvilSoundToAllPlayers();
                                }
                            }
                        }
                        p.getInventory().addItem(inv.getItem(13));
                        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                        p.sendMessage("§e§lおめでとうございます！『" + name + "§e§l』が当たりました");
                        plugin.playerState.put(p, "done");
                        if (plugin.playerState.isEmpty()) {
                            plugin.someOneInMenu = false;
                        }
                        cancel();
                        return;
                    }
                    if (a >= 15 && time == 3) {
                        roll(inv, 4, p, file);
                        cancel();
                    }
                    if (a >= 25) {
                        roll(inv, 3, p, file);
                        cancel();
                    }
                }
        }.runTaskTimer(plugin,0,time);
    }

    void playAnvilSoundToAllPlayers(){
        for(Player p : Bukkit.getOnlinePlayers()){
            p.playSound(p.getLocation(),Sound.BLOCK_ANVIL_USE,1,1);
        }
    }
/*
    public void roll(Player p,ItemFrame itemFrame,String file){
        String fileName = file;
        File dataa = new File(Bukkit.getServer().getPluginManager().getPlugin("MChest").getDataFolder(), File.separator + "Chests");
        File f = new File(dataa, File.separator + fileName + ".yml");
        FileConfiguration data = YamlConfiguration.loadConfiguration(f);
        if(!f.exists()){
            p.sendMessage("チェストが存在しません");
            return;
        }
        new BukkitRunnable(){
            int count = 0;
            @Override
            public void run() {
                itemFrame.setItem(getRandomItemFromFile(file));
                if(count == 25){
                    p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP,1,1);
                    p.getWorld().playEffect(p.getLocation(), Effect.DRAGON_BREATH,1,1);
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1,1);
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDERDRAGON_FIREBALL_EXPLODE, 1,1);
                    cancel();
                }
                count++;
            }
        }.runTaskTimer(this,0,3);
    }
*/
}
