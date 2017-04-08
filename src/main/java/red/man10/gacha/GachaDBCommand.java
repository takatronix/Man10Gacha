package red.man10.gacha;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;

/**
 * Created by sho-pc on 2017/04/08.
 */
public class GachaDBCommand implements CommandExecutor {
    private final GachaPlugin plugin;

    public GachaDBCommand(GachaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player p = (Player) sender;
        if(args[0].equalsIgnoreCase("stats")){
            if(args.length != 2){
                p.sendMessage(plugin.prefix + "コマンドの使い方が間違っています /mgachadb stats <name>");
                return true;
            }
            ResultSet result = plugin.mysql.query("SELECT * FROM `man10_gacha` WHERE gacha = '" + args[1] + "'");
            double price = 0;
            int count = 0;
            try {
                while (result.next()){
                    price = price + result.getDouble("price");
                    count++;
                }
                if(count == 0){
                    p.sendMessage(plugin.prefix + "ガチャのデーター存在しません");
                    return true;
                }
                p.sendMessage("§c§l" + args[1] + "のステータス");
                p.sendMessage("§b§l " + price + " 円分回転");
                p.sendMessage("§b§l " + count + " 回転");
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
        return false;
    }
}
