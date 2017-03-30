package red.man10.gacha;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Created by takatronix on 2017/03/22.
 */
public class GachaCommand implements CommandExecutor {

    private final GachaPlugin plugin;

    public GachaCommand(GachaPlugin plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player p = (Player) sender;
        //  引数なし  -> Help
        if (args.length == 0) {
            showHelp(sender);
            return false;
        }

        if(args[0].equalsIgnoreCase("setenablewin")){
            if(args.length < 3) {
                p.sendMessage(plugin.prefix + "引数が足りません /mgacha help");
                return true;
            }
                boolean stat = Boolean.parseBoolean(args[2]);
                boolean result = plugin.configFunction.setGachaWin(args[1], stat);
                if(result == false){
                    p.sendMessage(plugin.prefix + args[1] + "は存在しません");
                    return true;
                }
                if(stat != true){
                    p.sendMessage(plugin.prefix + args[1] + "の勝者設定を無効にしました");
                    return true;
                }
            p.sendMessage(plugin.prefix + args[1] + "の勝者設定を有効にしました");
            return true;
        }

        if(args[0].equalsIgnoreCase("setenablewinmessage")){
            if(args.length < 3) {
                p.sendMessage(plugin.prefix + "引数が足りません /mgacha help");
                return true;
            }
            boolean stat = Boolean.parseBoolean(args[2]);
            boolean result = plugin.configFunction.enableWinBroadcast(args[1],stat);
            if(result == false){
                p.sendMessage(plugin.prefix + args[1] + "は存在しません");
                return true;
            }
            if(stat != true){
                p.sendMessage(plugin.prefix + args[1] + "の勝者放送設定を無効にしました");
                return true;
            }
            p.sendMessage(plugin.prefix + args[1] + "の勝者放送設定を有効にしました");
            return true;
        }

        if(args[0].equalsIgnoreCase("setwinmessage")){
            if(args.length < 3) {
                p.sendMessage(plugin.prefix + "引数が足りません /mgacha help");
                return true;
            }
            String message = args[2].replaceAll("&", "§");
            boolean result = plugin.configFunction.setWinBroadcast(args[1],message);
            if(result == false){
                p.sendMessage(plugin.prefix + args[1] + "は存在しません");
                return true;
            }
            p.sendMessage(plugin.prefix + args[1] + "の勝者放送設定を『" + message + "』に設定しました");
        }
        if(args[0].equalsIgnoreCase("list")){
            File dataa = new File(Bukkit.getServer().getPluginManager().getPlugin("Man10Gacha").getDataFolder(), File.separator);
            File f = new File(dataa, File.separator + "gachas.yml");
            FileConfiguration data = YamlConfiguration.loadConfiguration(f);
            Set<String> list = data.getConfigurationSection("gacha").getKeys(false);
            for(int i= 0; i < list.size(); i++){
                p.sendMessage(String.valueOf(list.toArray()[i]));
            }
        }
        if(args[0].equalsIgnoreCase("delete")){
            if(args.length < 2){
                p.sendMessage(plugin.prefix + "引数が足りません /mgacha help");
                return true;
            }
            boolean del = plugin.configFunction.deleteGacha(args[1]);
            if (del == false) {
                p.sendMessage(plugin.prefix + args[1] + "は存在しません");
                return true;
            }
            p.sendMessage(plugin.prefix + args[1] + "を消去しました");
            return true;
        }
        if(args[0].equalsIgnoreCase("setprice")){
            if(args.length < 2){
                p.sendMessage(plugin.prefix + "引数が足りません /mgacha help");
                return true;
            }
            try {
                double price = Double.parseDouble(args[2]);
                boolean s = plugin.configFunction.setPrice(args[1], price);
                if (s == false) {
                    p.sendMessage(plugin.prefix + args[1] + "は存在しません");
                    return true;
                }
                p.sendMessage(plugin.prefix + args[1] + "を" + price + "に設定しました");
            }catch (NumberFormatException e){
                p.sendMessage(plugin.prefix + "値段は数字のみです");
                return true;
            }
        }
        if(args[0].equalsIgnoreCase("create")){
            if(args.length < 5){
                p.sendMessage(plugin.prefix + "引数が足りません /mgacha create 名前 リンクチェスト タイトル 支払い方法 値段");
                return true;
            }
            try {
                String name = args[1];
                String linkedChest = args[2];
                String title = args[3].replaceAll("&", "§");
                String payType = args[4];
                double price = Double.parseDouble(args[5]);
                int result = plugin.configFunction.createGacha(name, linkedChest, title, payType, price, p.getInventory().getItemInMainHand(), p);
                if(result != 0){
                    return true;
                }
                p.sendMessage(plugin.prefix + "ガチャを作成しました : " + name);
            }catch (NumberFormatException e){
                p.sendMessage(plugin.prefix + "値段は数字だけです");
            }
            return true;
        }

        if(args[0].equalsIgnoreCase("help")){
            showHelp(sender);
            return true;
        }
        if(args[0].equalsIgnoreCase("get")){
            if(args.length != 2){
                return false;
            }
            //plugin.giveController((Player)sender,args[1]);
            return true;
        }
        return true;
    }


    //      ヘルプ表示
    void showHelp(CommandSender p){
        p.sendMessage("§e============== §d●§f●§a●§eMan10ガチャ§d●§f●§a● §e===============");
        p.sendMessage("§c赤色は管理者コマンド");
        p.sendMessage("§c/mgacha list - 作成されたガチャリストを表示する");
        p.sendMessage("§c/mgacha play [ガチャ名] - ガチャを開く");
        p.sendMessage("§c/mgacha get [ガチャ名] - ガチャ券を手に入れる");
        p.sendMessage("§c/mgacha give [ガチャ名] [ユーザー名] - ガチャ券をユーザーにわたす");
        p.sendMessage("§c/mgacha data [ガチャ名] - ガチャの統計データを表示する");
        p.sendMessage("§e============== §d●§f●§a●§eMan10ガチャ§d●§f●§a● §e===============");
        p.sendMessage("§ehttp://man10.red Minecraft Man10 Server");
        p.sendMessage("§ecreated by takatronix http://takatronix.com");
        p.sendMessage("§ecreated by takatronix http://twitter.com/takatronix");
    }

}


