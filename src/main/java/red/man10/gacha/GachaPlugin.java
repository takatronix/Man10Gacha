package red.man10.gacha;

import org.bukkit.*;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import red.man10.VaultManager;

import java.io.File;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

public final class GachaPlugin extends JavaPlugin implements Listener {

    GachaRunnable gachaRunnable = new GachaRunnable(this);
    GachaGUI gachaGUI = new GachaGUI(this);
    GachaConfigFunction configFunction = new GachaConfigFunction(this);
    VaultManager vault = null;

    HashMap<Player,String> playerState = new HashMap<>();

    String prefix = "§6[§aMg§fac§dha§6]§f§l§n";

    public boolean someOneInMenu = false;

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.getServer().getPluginManager().registerEvents(this,this);
        getCommand("mgacha").setExecutor(new GachaCommand(this));
        vault = new VaultManager(this);
        configFunction.createSignConfig();

    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e){
        if(someOneInMenu == true){
            if(!playerState.isEmpty()) {
                if (playerState.get(e.getPlayer()) != null && playerState.get(e.getPlayer()).equalsIgnoreCase("done")) {
                    playerState.remove(e.getPlayer());
                    if (playerState.isEmpty()) {
                        someOneInMenu = false;
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (e.getClickedBlock().getState() instanceof Sign) {
                if (((Sign) e.getClickedBlock().getState()).getLine(0).contains("§b===============")) {
                    String id = configFunction.locationToId(e.getClickedBlock().getLocation());
                    if (id == null) {
                        return;
                    }
                    if (!playerState.isEmpty()) {
                        if (playerState.get(e.getPlayer()) != null && playerState.get(e.getPlayer()).equalsIgnoreCase("rolling")) {
                            e.getPlayer().sendMessage(prefix + "ガチャは1度に１回しかできません");
                            return;
                        }
                    }
                    File dataaa = new File(Bukkit.getServer().getPluginManager().getPlugin("Man10Gacha").getDataFolder(), File.separator);
                    File ff = new File(dataaa, File.separator + "gachas.yml");
                    FileConfiguration data = YamlConfiguration.loadConfiguration(ff);
                    String linkedChest = data.getString("gacha." + id + ".linkedChest");
                    Player p = e.getPlayer();
                    UUID uuid = p.getUniqueId();
                    if(!p.hasPermission("man10.gacha.use." + id)){
                        p.sendMessage(prefix + "あなたは" + data.get("gacha." + id  + ".title") + "を回す権限はありません");
                        return;
                    }
                    double balance = vault.getBalance(uuid);
                    ItemStack item = data.getItemStack("gacha." + id + ".ticket");
                    if(data.getString("gacha." + id + ".payType").equalsIgnoreCase("ticket")) {
                        ItemStack itemInHand = p.getInventory().getItemInMainHand();
                        if(itemInHand.getType() == item.getType()){
                            if(item.getItemMeta() == null || itemInHand.getItemMeta().toString().equalsIgnoreCase(item.getItemMeta().toString())){
                                playerState.put(p, "rolling");
                                someOneInMenu = true;
                                gachaGUI.spinMenu(p, linkedChest, 1, data.getString("gacha." + id + ".title"));
                                return;
                            }
                        }
                    }
                    if (data.getString("gacha." + id + ".payType").equalsIgnoreCase("balance")) {
                        if (balance < data.getDouble("gacha." + id + ".price")) {
                            p.sendMessage(prefix + "残金が足りません");
                            return;
                        }
                        vault.withdraw(uuid, data.getDouble("gacha." + id + ".price"));
                        playerState.put(p, "rolling");
                        someOneInMenu = true;
                        gachaGUI.spinMenu(p, linkedChest, 1, data.getString("gacha." + id + ".title"));
                    }
                }
            }
        }
    }
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e){
        if(someOneInMenu == false){
            return;
        }
        if(playerState.get(e.getWhoClicked()) != null) {
            if (playerState.get(e.getWhoClicked()).equalsIgnoreCase("rolling") || playerState.get(e.getWhoClicked()).equalsIgnoreCase("done")) {
                e.setCancelled(true);
                return;
            }
        }
    }
    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
    @EventHandler
    public void onBreakSign(BlockBreakEvent e){
        if(e.getBlock().getType() == Material.SIGN_POST || e.getBlock().getType() == Material.WALL_SIGN){
            Sign s = (Sign) e.getBlock().getState();
            if(s.getLine(0).equalsIgnoreCase("§b===============")){
                e.getPlayer().sendMessage(prefix + "§c§l看板の登録を解除しました");
                configFunction.deleteSignLocation(s.getLocation());
            }
        }
    }

    @EventHandler
    public void onSign(SignChangeEvent e){
        if(e.getLine(0).equalsIgnoreCase("mgacha")){
            String line1 = e.getLine(1);
            if(e.getLine(1).equalsIgnoreCase("")){
                e.getPlayer().sendMessage(prefix + "§c§l看板の使い方が間違っています");
                e.setCancelled(true);
                e.getBlock().breakNaturally();
                return;
            }
            if(e.getPlayer().hasPermission("man10.gacha.sign.create")){
                e.getPlayer().sendMessage(prefix + "あなたには看板を作成する権限はありません");
                e.setCancelled(true);
                e.getBlock().breakNaturally();
                return;
            }
            File dataa = new File(Bukkit.getServer().getPluginManager().getPlugin("Man10Gacha").getDataFolder(), File.separator);
            File f = new File(dataa, File.separator + "gachas.yml");
            FileConfiguration data = YamlConfiguration.loadConfiguration(f);
            if(data.get("gacha." + e.getLine(1)) == null){
                e.getPlayer().sendMessage(prefix + "§c§lガチャが存在しません");
                e.setCancelled(true);
                e.getBlock().breakNaturally();
                return;
            }
            e.getPlayer().sendMessage(prefix + "§a§l看板を登録しました");
            //この下にアレイからコンフィグに書く処理
            configFunction.signLocationArrayToFile(e.getBlock().getLocation(), e.getLine(1)); //2017/025 ここ！！！！
            e.setLine(0,"§b===============");
            e.setLine(1, e.getLine(2).replaceAll("&","§"));
            e.setLine(3,"§b===============");


            if(data.getString("gacha." + line1 + ".payType").equalsIgnoreCase("balance")){
                e.setLine(2, String.valueOf(data.getDouble("gacha." + line1 + ".price")));
                return;
            }
            if(data.getString("gacha." + line1 + ".payType").equalsIgnoreCase("ticket")){
                e.setLine(2, "ticket");
                return;
            }

            //さらに下にコンフィグからアレイに戻す処理
        }
    }
    public int getSlotsFromFile(String file){
        String fileName = file;
        File dataa = new File(Bukkit.getServer().getPluginManager().getPlugin("MChest").getDataFolder(), File.separator + "Chests");
        File f = new File(dataa, File.separator + fileName + ".yml");
        FileConfiguration data = YamlConfiguration.loadConfiguration(f);
        boolean isLargeChest = data.getBoolean("isLargeChest");
        if(isLargeChest == true){
            return 54;
        }
        if(isLargeChest == false){
            return 27;
        }
        return 27;
    }

}
