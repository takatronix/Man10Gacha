package red.man10.gacha;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
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
            if(!p.hasPermission("man10.gacha.setenablewin")){
                p.sendMessage(plugin.prefix + "あなたには勝利設定する権限はありません");
                return true;
            }
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
            if(!p.hasPermission("man10.gacha.setenablewinmessage")){
                p.sendMessage(plugin.prefix + "あなたには勝利メッセージを設定する権限はありません");
                return true;
            }
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
            if(!p.hasPermission("man10.gacha.setwinmessage")){
                p.sendMessage(plugin.prefix + "あなたには勝利メッセージを設定する権限はありません");
                return true;
            }
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
            if(!p.hasPermission("man10.gacha.list")){
                p.sendMessage(plugin.prefix + "あなたにはガチャリストを見る権限はありません");
                return true;
            }
            File dataa = new File(Bukkit.getServer().getPluginManager().getPlugin("Man10Gacha").getDataFolder(), File.separator);
            File f = new File(dataa, File.separator + "gachas.yml");
            FileConfiguration data = YamlConfiguration.loadConfiguration(f);
            Set<String> list = data.getConfigurationSection("gacha").getKeys(false);
            p.sendMessage("==========§7§l§nガチャリスト===========");
            for(int i= 0; i < list.size(); i++){
                p.sendMessage("§f§l" + String.valueOf(list.toArray()[i]));
            }
        }
        if(args[0].equalsIgnoreCase("delete")){
            if(!p.hasPermission("man10.gacha.delete")){
                p.sendMessage(plugin.prefix + "あなたにはガチャを消去する権限はありません");
                return true;
            }
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

        if(args[0].equalsIgnoreCase("lock")){
            if(!p.hasPermission("man10.gacha.lock")){
                p.sendMessage(plugin.prefix + "あなたにはガチャをロックする権限はありません");
                return true;
            }
            if(plugin.onLockDown == true){
                plugin.onLockDown = false;
                p.sendMessage(plugin.prefix + "ガチャのロックを解除しました");
                return true;
            }
            plugin.onLockDown = true;
            p.sendMessage(plugin.prefix + "ガチャをロックしました");
            return true;
        }

        if(args[0].equalsIgnoreCase("setprice")){
            if(!p.hasPermission("man10.gacha.setprice")){
                p.sendMessage(plugin.prefix + "あなたには値段を設定する権限を持っていません");
                return true;
            }
            if(args.length < 3){
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
        if(args[0].equalsIgnoreCase("reload")){
            if(!p.hasPermission("man10.gacha.reload")){
                p.sendMessage(plugin.prefix + "あなたにはコンフィグをリロードする権限はありません");
                return true;
            }
            p.sendMessage(plugin.prefix + "コンフィグをリロードしました");
            plugin.loadConfig();
            return true;
        }
        if(args[0].equalsIgnoreCase("create")){
            if(!p.hasPermission("man10.gacha.create")){
                p.sendMessage(plugin.prefix + "あなたにはガチャを作成する権限を持っていません");
                return true;
            }
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
                return true;
            }
            Bukkit.getServer().dispatchCommand(p,"mgacha reload");
            return true;
        }
        if(args[0].equalsIgnoreCase("placeholders")){
            p.sendMessage("§5===================================");
            p.sendMessage("§c%PLAYER%  当てたプレイヤーの名前を表示する");
            p.sendMessage("§c%ITEM% 当てたアイテムの名前を表示する");
            p.sendMessage("§c%AMMOUNT% 当てたアイテムの個数を表示する");
            p.sendMessage("§c%TITLE% 当てたガチャのタイトルを表示する");
            p.sendMessage("§5===================================");
        }
        if(args[0].equalsIgnoreCase("help")){
            showHelp(sender);
            return true;
        }
        if(args[0].equalsIgnoreCase("get")){
            if(args.length < 2){
                p.sendMessage(plugin.prefix + "引数が足りません /mgacha help");
                return true;
            }
            if(plugin.gachaConfig.getString("gacha." + args[1] + ".payType").equalsIgnoreCase("balance")){
                p.sendMessage(plugin.prefix + args[1] + "の支払い方法はチケットではありません");
                return true;
            }
            ItemStack item = plugin.gachaConfig.getItemStack("gacha." + args[1] + ".ticket");
            p.getInventory().addItem(item);
            p.sendMessage(plugin.prefix + "チケットを追加しました");
            return true;
        }
        return true;
    }



    //      ヘルプ表示
    void showHelp(CommandSender p){
        p.sendMessage("§e============== §d●§f●§a●§eMan10ガチャ§d●§f●§a● §e===============");
        p.sendMessage("§c/mgacha create - ガチャを作成する");
        p.sendMessage("§c/mgacha delete - ガチャを消去する");
        p.sendMessage("§c/mgacha setprice - ガチャの価格を設定する");
        p.sendMessage("§c/mgacha setenablewin - ガチャの勝利設定をする");
        p.sendMessage("§c/mgacha setenablewinmessage - ガチャの勝利時のメッセージを設定する");
        p.sendMessage("§c/mgacha setwinmessage - 勝利時のメッセージを設定");
        p.sendMessage("§c/mgacha setwinmessage - 勝利時のメッセージを設定");
        p.sendMessage("§c/mgacha placeholders - 勝利メッセージのプレイスホルダーを表示する");
        p.sendMessage("§c/mgacha lock - ガチャのロックダウンをトグルする");
        p.sendMessage("§c/mgacha list - 作成されたガチャリストを表示する");
        p.sendMessage("§c/mgacha reload - コンフィグのリロード");
        p.sendMessage("§e============== §d●§f●§a●§eMan10ガチャ§d●§f●§a● §e===============");
        p.sendMessage("§ehttp://man10.red Minecraft Man10 Server");
        p.sendMessage("§ecreated by takatronix http://takatronix.com");
        p.sendMessage("§ecreated by takatronix http://twitter.com/takatronix");
        p.sendMessage("§ealso created by Sho0");
    }

}


