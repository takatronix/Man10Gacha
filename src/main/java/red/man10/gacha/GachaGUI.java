package red.man10.gacha;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Created by takatronix on 2017/03/22.
 */
public class GachaGUI {
    private final GachaPlugin plugin;
    public GachaGUI(GachaPlugin plugin) {
        this.plugin = plugin;
    }

    public void spinMenu(Player p,String filename,int time,String title){
        Inventory inv = Bukkit.createInventory(null,27,title);
        ItemStack blackGlass = new ItemStack(Material.STAINED_GLASS_PANE,1, (byte) 15);
        ItemStack redGlass = new ItemStack(Material.STAINED_GLASS_PANE,1, (byte) 14);

        int[] black = {0,1,2,3,5,6,7,8,18,19,20,21,23,24,25,26};
        for(int i = 0;i < black.length; i++){
            inv.setItem(black[i],blackGlass);
        }
        inv.setItem(4,redGlass);
        inv.setItem(22,redGlass);
        p.openInventory(inv);
        plugin.gachaRunnable.roll(inv, time,p, filename);
    }
}
