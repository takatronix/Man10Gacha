package red.man10.gacha;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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

        //  引数なし  -> Help
        if (args.length == 0) {
            showHelp(sender);
            return false;
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


