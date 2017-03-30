package red.man10.gacha;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.List;
import java.util.Random;

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
                inv.setItem(9, inv.getItem(10));
                inv.setItem(10, inv.getItem(11));
                inv.setItem(11, inv.getItem(12));
                inv.setItem(12, inv.getItem(13));
                inv.setItem(13, inv.getItem(14));
                inv.setItem(14, inv.getItem(15));
                inv.setItem(15, inv.getItem(16));
                inv.setItem(16, inv.getItem(17));
                inv.setItem(17, new ItemStack(Material.AIR));
                itemnumber[0] = itemnumber[1];
                itemnumber[1] = itemnumber[2];
                itemnumber[2] = itemnumber[3];
                itemnumber[3] = itemnumber[4];
                itemnumber[4] = itemnumber[5];
                p.playSound(p.getLocation(), Sound.BLOCK_DISPENSER_DISPENSE,1,1);
                inv.setItem(17,items.get(result));
                a++;
                if(a >= 25 && time == 3){
                    String name = inv.getItem(13).getItemMeta().getDisplayName();
                    if(name == null){
                        name = inv.getItem(13).getType().name();
                    }
                    if(plugin.configFunction.winIsTrue(file) == true){
                        if(plugin.configFunction.winBroadcastIsTrue(file) == true) {
                            if(itemnumber[0] == 0) {
                                String message = plugin.configFunction.getWinBroadcast(file).replaceAll("%PLAYER%", p.getName()).replaceAll("%TITLE%", inv.getName()).replaceAll("%ITEM%", name).replaceAll("%AMMOUNT%", String.valueOf(inv.getItem(13).getAmount()));
                                Bukkit.getServer().broadcastMessage(message);
                            }
                        }
                    }
                    p.getInventory().addItem(inv.getItem(13));
                    p.playSound(p.getLocation(),Sound.ENTITY_PLAYER_LEVELUP,1,1);
                    p.sendMessage("§e§lおめでとうございます！『" + name + "§e§l』が当たりました");
                    plugin.playerState.put(p,"done");
                    if(plugin.playerState.isEmpty()){
                        plugin.someOneInMenu = false;
                    }
                    cancel();
                }
                if(a >= 30 && time == 2){
                    roll(inv,3,p,file);
                    cancel();
                }
                if(a >= 50){
                    roll(inv,2,p,file);
                    cancel();
                }

            }
        }.runTaskTimer(plugin,0,time);
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
