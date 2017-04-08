package red.man10.gacha;

import org.bukkit.*;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import red.man10.MySQLFunc;
import red.man10.MySQLManager;
import red.man10.VaultManager;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public final class GachaPlugin extends JavaPlugin implements Listener {

    GachaRunnable gachaRunnable = new GachaRunnable(this);
    GachaGUI gachaGUI = new GachaGUI(this);
    GachaConfigFunction configFunction = new GachaConfigFunction(this);
    VaultManager vault = null;

    HashMap<Player,String> playerState = new HashMap<>();

    String prefix = "§6[§aMg§fac§dha§6]§f§l";

    MySQLManager mysql = null;

    public boolean someOneInMenu = false;
    public boolean onLockDown = false;

    public FileConfiguration signsConfig = null;
    public FileConfiguration gachaConfig = null;

    public void loadConfig(){
        File dataa = new File(Bukkit.getServer().getPluginManager().getPlugin("Man10Gacha").getDataFolder(), File.separator);
        File f = new File(dataa, File.separator + "signs.yml");
        if(!f.exists()){
            configFunction.createSignConfig();
        }
        signsConfig = YamlConfiguration.loadConfiguration(f);

        File dataaa = new File(Bukkit.getServer().getPluginManager().getPlugin("Man10Gacha").getDataFolder(), File.separator);
        File ff = new File(dataaa, File.separator + "gachas.yml");
        gachaConfig = YamlConfiguration.loadConfiguration(ff);
        if(!f.exists()){
            configFunction.createGachaConfig();
        }
    }

        @Override
        public void onEnable() {
            // Plugin startup logic
            this.getServer().getPluginManager().registerEvents(this,this);
            this.saveDefaultConfig();
            getCommand("mgacha").setExecutor(new GachaCommand(this));
            getCommand("mgachadb").setExecutor(new GachaDBCommand(this));
            vault = new VaultManager(this);
            configFunction.createSignConfig();
            configFunction.searchForMissingSigns();
            loadConfig();
            mysql = new MySQLManager(this,"mgacha");
            createTable();
        }

    public String currentTime(){

        //long timestamp = 1371271256;
        //Date date = new Date(timestamp * 1000);

        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy'-'MM'-'dd' 'HH':'mm':'ss");
        Bukkit.getLogger().info("datetime ");
        String currentTime = "'"+sdf.format(date)+"'";
        Bukkit.getLogger().info(currentTime);
        return currentTime;
    }


    void createTable(){
        mysql.execute("CREATE TABLE `man10_gacha` (\n" +
                "\t`id` INT NOT NULL AUTO_INCREMENT,\n" +
                "\t`uuid` VARCHAR(64) NOT NULL DEFAULT '0',\n" +
                "\t`username` VARCHAR(32) NOT NULL DEFAULT '0',\n" +
                "\t`gacha` VARCHAR(32) NOT NULL DEFAULT '0',\n" +
                "\t`price` DOUBLE NOT NULL DEFAULT '0',\n" +
                "\t`item` VARCHAR(64) NULL DEFAULT '0',\n" +
                "\t`world` VARCHAR(64) NULL DEFAULT '0',\n" +
                "\t`x` DOUBLE NULL DEFAULT '0',\n" +
                "\t`y` DOUBLE NULL DEFAULT '0',\n" +
                "\t`z` DOUBLE NULL DEFAULT '0',\n" +
                "\t`time` DATETIME NULL DEFAULT NULL,\n" +
                "\tPRIMARY KEY (`id`)\n" +
                ")ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8" +
                ";\n");
        }

        @EventHandler
        public void onCloseInventory(InventoryCloseEvent e){
            if(someOneInMenu == false){
                return;
            }
            if(playerState.get(e.getPlayer()) != null) {
                if (playerState.get(e.getPlayer()).equalsIgnoreCase("done")) {
                    playerState.remove(e.getPlayer());
                    return;
                }
            }
        }

        @EventHandler
        public void onInteract(PlayerInteractEvent e) {
            if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (e.getClickedBlock().getState() instanceof Sign) {
                    if (((Sign) e.getClickedBlock().getState()).getLine(0).contains("§b===============")) {
                        if(onLockDown == true){
                            e.getPlayer().sendMessage(prefix + "現在このガチャは使用できません");
                            return;
                        }
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
                        String linkedChest = gachaConfig.getString("gacha." + id + ".linkedChest");
                        Player p = e.getPlayer();
                        UUID uuid = p.getUniqueId();
                        if(!p.hasPermission("man10.gacha.use." + id)){
                            p.sendMessage(prefix + "あなたは" + gachaConfig.get("gacha." + id  + ".title") + "を回す権限はありません");
                            return;
                        }
                        double balance = vault.getBalance(uuid);
                        if(gachaConfig.getString("gacha." + id + ".payType").equalsIgnoreCase("ticket")) {
                            ItemStack item = gachaConfig.getItemStack("gacha." + id + ".ticket");
                            int ammount = item.getAmount();
                            ItemStack itemInHand = p.getInventory().getItemInMainHand();
                            if(itemInHand.getType() == item.getType()){
                                if(item.getItemMeta() == null || itemInHand.getItemMeta().toString().equalsIgnoreCase(item.getItemMeta().toString())){
                                    if(p.getInventory().getItemInMainHand().getAmount() < ammount){
                                        if(item.getItemMeta().getDisplayName() == null){
                                            p.sendMessage(prefix + item.getType().name() + "§fが" + ammount + "枚必要です");
                                            return;
                                        }
                                        p.sendMessage(prefix + item.getItemMeta().getDisplayName() + "§fが" + ammount + "枚必要です");
                                        return;
                                    }
                                    p.getInventory().getItemInMainHand().setAmount(p.getInventory().getItemInMainHand().getAmount() - ammount);
                                    playerState.put(p, "rolling");
                                    someOneInMenu = true;
                                    gachaGUI.spinMenu(p, linkedChest, 3, gachaConfig.getString("gacha." + id + ".title"),0,e.getClickedBlock().getLocation());
                                    return;
                                }
                                p.sendMessage(prefix + item.getItemMeta().getDisplayName() + "§fが" + ammount + "枚必要です");
                                return;
                            }else{
                                if(p.getInventory().getItemInMainHand().getAmount() < ammount){
                                    if(item.getItemMeta().getDisplayName() == null){
                                        p.sendMessage(prefix + item.getType().name() + "§fが" + ammount + "枚必要です");
                                        return;
                                    }
                                    p.sendMessage(prefix + item.getItemMeta().getDisplayName() + "§fが" + ammount + "枚必要です");
                                    return;
                                }
                            }
                        }
                        if (gachaConfig.getString("gacha." + id + ".payType").equalsIgnoreCase("balance")) {
                            if (balance < gachaConfig.getDouble("gacha." + id + ".price")) {
                                p.sendMessage(prefix + "残金が足りません");
                                return;
                            }
                            vault.withdraw(uuid, gachaConfig.getDouble("gacha." + id + ".price"));
                            playerState.put(p, "rolling");
                            someOneInMenu = true;
                            gachaGUI.spinMenu(p, linkedChest, 3, gachaConfig.getString("gacha." + id + ".title"),gachaConfig.getDouble("gacha." + id + ".price"),e.getClickedBlock().getLocation());
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
                if(playerState.get(e.getWhoClicked()).equalsIgnoreCase("done")){
                    e.setCancelled(true);
                    e.getWhoClicked().closeInventory();
                    playerState.remove(e.getWhoClicked());
                    return;
                }
                if (playerState.get(e.getWhoClicked()).equalsIgnoreCase("rolling")) {
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
                if(!e.getPlayer().hasPermission("man10.gacha.sign.create")){
                    e.getPlayer().sendMessage(prefix + "あなたには看板を作成する権限はありません");
                    e.setCancelled(true);
                    e.getBlock().breakNaturally();
                    return;
                }
                if(gachaConfig.get("gacha." + e.getLine(1)) == null){
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


                if(gachaConfig.getString("gacha." + line1 + ".payType").equalsIgnoreCase("balance")){
                    e.setLine(2, String.valueOf(gachaConfig.getDouble("gacha." + line1 + ".price")));
                    return;
                }
                if(gachaConfig.getString("gacha." + line1 + ".payType").equalsIgnoreCase("ticket")){
                    e.setLine(2, "チケット");
                    return;
                }
            }
        }

    }
