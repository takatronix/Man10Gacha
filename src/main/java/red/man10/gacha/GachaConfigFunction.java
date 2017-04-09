package red.man10.gacha;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by sho-pc on 2017/03/23.
 */
public class GachaConfigFunction {

    private final GachaPlugin plugin;

    public GachaConfigFunction(GachaPlugin plugin) {
        this.plugin = plugin;
    }


    public int getSlotsFromFile(String file) {
        String fileName = file;
        File dataa = new File(Bukkit.getServer().getPluginManager().getPlugin("MChest").getDataFolder(), File.separator + "Chests");
        File f = new File(dataa, File.separator + fileName + ".yml");
        FileConfiguration data = YamlConfiguration.loadConfiguration(f);
        boolean isLargeChest = data.getBoolean("isLargeChest");
        if (isLargeChest == true) {
            return 54;
        }
        if (isLargeChest == false) {
            return 27;
        }
        return 27;
    }

    public List<ItemStack> fileItemsToList(String file) {
        List<ItemStack> items = new ArrayList<>();
        String fileName = file;
        File dataa = new File(Bukkit.getServer().getPluginManager().getPlugin("MChest").getDataFolder(), File.separator + "Chests");
        File f = new File(dataa, File.separator + fileName + ".yml");
        FileConfiguration data = YamlConfiguration.loadConfiguration(f);
        for (int i = 0; i < getSlotsFromFile(file); i++) {
            if (data.getItemStack("item." + i) != null) {
                items.add(data.getItemStack("item." + i));
            }
            if (data.getItemStack("item." + i) == null) {
            }
        }
        return items;
    }

    /*List<ItemStack> fileItemsToListRemoveNull(List<ItemStack> items) {
        List<ItemStack> removed = new ArrayList<ItemStack>();
        for (ItemStack item : items) {
            if (item != null) {
                removed.add(item);
            }
        }
        return removed;
    }*/

    public void createSignConfig() {
        File dataa = new File(Bukkit.getServer().getPluginManager().getPlugin("Man10Gacha").getDataFolder(), File.separator);
        File f = new File(dataa, File.separator + "signs.yml");
        FileConfiguration data = YamlConfiguration.loadConfiguration(f);
        if (!f.exists()) {
            try {
                f.createNewFile();
                data.set("signs", "");
                data.save(f);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void signLocationArrayToFile(Location l, String id) {
        File dataa = new File(Bukkit.getServer().getPluginManager().getPlugin("Man10Gacha").getDataFolder(), File.separator);
        File f = new File(dataa, File.separator + "signs.yml");
        FileConfiguration data = YamlConfiguration.loadConfiguration(f);
        if (!f.exists()) {
            createSignConfig();
        }
        java.lang.Object obj = data.get("signs." + id);
        List list = data.getList("signs." + id);
        if (obj == null) {
            data.set("signs." + id, l);
            try {
                data.save(f);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        if (obj != null && list == null) {
            data.set("signs." + id, null);
            List<Location> a = new ArrayList<>();
            a.add((Location) obj);
            a.add(l);
            data.set("signs." + id, a.toArray());
            try {
                data.save(f);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        list.add(l);
        data.set("signs." + id, null);
        data.set("signs." + id, list);
        //locc.add(l);
        //data.set("signs." + id, list);
        try {
            data.save(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List idToLocation(String id) {
        List<Location> loc = new ArrayList();
        File dataa = new File(Bukkit.getServer().getPluginManager().getPlugin("Man10Gacha").getDataFolder(), File.separator);
        File f = new File(dataa, File.separator + "signs.yml");
        FileConfiguration data = YamlConfiguration.loadConfiguration(f);
        List list = data.getList("signs." + id);
        if (!f.exists()) {
            createSignConfig();
            return null;
        }
        java.lang.Object obj = data.get("signs." + id);
        if (obj == null) {
            loc.add((Location) obj);
            return null;
        }
        if (obj != null && list == null) {
            loc.add((Location) obj);
            return loc;
        }
        return list;
    }

    public String locationToId(Location l) {
        File dataa = new File(Bukkit.getServer().getPluginManager().getPlugin("Man10Gacha").getDataFolder(), File.separator);
        File f = new File(dataa, File.separator + "signs.yml");
        FileConfiguration data = YamlConfiguration.loadConfiguration(f);
        Set<String> list = data.getConfigurationSection("signs").getKeys(false);
        if (!f.exists()) {
            createSignConfig();
            return "none";//ポイント１
        }
        for (int i = 0; i < list.size(); i++) {
            java.lang.Object obj = data.get("signs." + list.toArray()[i]);
            List objlist = data.getList("signs." + list.toArray()[i]);
            if (obj == null) {
            }
            if (obj != null && objlist == null) {
                Location loc = (Location) obj;
                if (loc.getX() == l.getX() && loc.getY() == l.getY() && loc.getZ() == l.getZ() && loc.getWorld() == l.getWorld()) {
                    return (String) list.toArray()[i];
                }
            }
            if (obj != null && objlist != null) {
                for (int ii = 0; ii < objlist.size(); ii++) {
                    Location loc = (Location) objlist.get(ii);
                    if (loc.getX() == l.getX() && loc.getY() == l.getY() && loc.getZ() == l.getZ() && loc.getWorld() == l.getWorld()) {
                        return (String) list.toArray()[i];
                    }
                }
            }
        }
        return null;
    }

    public void createGachaConfig() {
        File dataa = new File(Bukkit.getServer().getPluginManager().getPlugin("Man10Gacha").getDataFolder(), File.separator);
        File f = new File(dataa, File.separator + "gachas.yml");
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public int createGacha(String name, String linkedChest, String title, String payType, double price, ItemStack ticket, Player p) {
        File dataa = new File(Bukkit.getServer().getPluginManager().getPlugin("MChest").getDataFolder(), File.separator + "Chests");
        File f = new File(dataa, File.separator + linkedChest + ".yml");

        if (payType.equalsIgnoreCase("ticket") && ticket.getType() == Material.AIR) {
            p.sendMessage("チケットを手に持ってください");
            return -1;
        }
        if (!f.exists()) {
            p.sendMessage("チェストが存在しません");
            return -3;
        }
        if (payType == null) {
            p.sendMessage("支払い方法が間違ってます <balance/ticket>");
            return -4;
        }
        if (!payType.equalsIgnoreCase("balance") && !payType.equalsIgnoreCase("ticket")) {
            p.sendMessage("支払い方法が間違ってます <balance/ticket>");
            return -5;
        }
        File dataaa = new File(Bukkit.getServer().getPluginManager().getPlugin("Man10Gacha").getDataFolder(), File.separator);
        File ff = new File(dataaa, File.separator + "gachas.yml");
        FileConfiguration data = YamlConfiguration.loadConfiguration(ff);
        if (!ff.exists()) {
            createGachaConfig();
        }
        data.set("gacha." + name + ".title", title);
        data.set("gacha." + name + ".linkedChest", linkedChest);
        data.set("gacha." + name + ".payType", payType);
        data.set("gacha." + name + ".price", price);
        data.set("gacha." + name + ".enableWin", false);
        data.set("gacha." + name + ".enableWinBroadcast", false);
        data.set("gacha." + name + ".winMessage", "none");
        if (payType.equalsIgnoreCase("balance")) {
            data.set("gacha." + name + ".ticket", "none");
        }
        if (payType.equalsIgnoreCase("ticket")) {
            data.set("gacha." + name + ".ticket", ticket);
        }
        try {
            data.save(ff);
            p.sendMessage("ガチャが保存されました : " + name);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }


    public void deleteSignLocation(Location location) {
        File dataa = new File(Bukkit.getServer().getPluginManager().getPlugin("Man10Gacha").getDataFolder(), File.separator);
        File f = new File(dataa, File.separator + "signs.yml");
        FileConfiguration data = YamlConfiguration.loadConfiguration(f);
        if (!f.exists()) {
            createSignConfig();
            return;
        }
        String signId = locationToId(location);
        List list = data.getList("signs." + signId);
        java.lang.Object obj = data.get("signs." + signId);
        if (obj == null) {
            return;
        }
        if (obj != null && list == null) {
            Location testLoc = (Location) obj;
            if (testLoc.getX() == location.getX() && testLoc.getY() == location.getY() && testLoc.getZ() == location.getZ() && testLoc.getWorld() == location.getWorld()) {
                data.set("signs." + signId, null);
            }
        }
        if (obj != null && list != null) {
            List testlist = data.getList("signs." + signId);
            for (int i = 0; i < testlist.size(); i++) {
                Location testLoc = (Location) testlist.get(i);
                if (testLoc.getX() == location.getX() && testLoc.getY() == location.getY() && testLoc.getZ() == location.getZ() && testLoc.getWorld() == location.getWorld()) {
                    testlist.remove(testLoc);
                }
            }
            data.set("signs." + signId, testlist);
        }
        try {
            data.save(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
        plugin.loadConfig();
    }

    public boolean setPrice(String id, double price) {
        File dataaa = new File(Bukkit.getServer().getPluginManager().getPlugin("Man10Gacha").getDataFolder(), File.separator);
        File ff = new File(dataaa, File.separator + "gachas.yml");
        FileConfiguration data = YamlConfiguration.loadConfiguration(ff);
        java.lang.Object obj = data.get("gacha." + id);
        if (obj == null) {
            return false;
        }
        data.set("gacha." + id + ".price", price);
        try {
            data.save(ff);
        } catch (IOException e) {
            e.printStackTrace();
        }
        updatePrice(id, price);
        plugin.loadConfig();
        return true;
    }

    void updatePrice(String id, double price) {
        List locations = idToLocation(id);
        for (int i = 0; i < locations.size(); i++) {
            Location loc = (Location) locations.get(i);
            if (loc.getBlock().getState() instanceof Sign) {
                Sign sign = (Sign) loc.getBlock().getState();
                sign.setLine(2, String.valueOf(price));
                sign.update();
            } else {
                deleteSignLocation(loc);
            }
        }
    }

    public boolean deleteGacha(String id) {// 2017/03/27
        File dataaa = new File(Bukkit.getServer().getPluginManager().getPlugin("Man10Gacha").getDataFolder(), File.separator);
        File ff = new File(dataaa, File.separator + "gachas.yml");
        FileConfiguration data = YamlConfiguration.loadConfiguration(ff);
        java.lang.Object obj = data.get("gacha." + id);
        List list = idToLocation(id);
        if (!ff.exists()) {
            createGachaConfig();
            return false;
        }
        removeSignsFromLocations(list);
        if (obj == null) {
            return false;
        }
        data.set("gacha." + id, null);
        try {
            data.save(ff);
        } catch (IOException e) {
            e.printStackTrace();
        }
        plugin.loadConfig();
        return true;
    }

    public void searchForMissingSigns() {
        ResultSet resultSet = plugin.mysql.query("SELECT * FROM man10_gacha_sign");
        try {
            while(resultSet.next()){
                World w = Bukkit.getWorld(resultSet.getString("world"));
                double x = resultSet.getDouble("x");
                double y = resultSet.getDouble("y");
                double z = resultSet.getDouble("z");

                Location l = new Location(w,x,y,z);
                if(l!=null){
                    if(l.getBlock().getState() instanceof Sign == false){
                        deleteSignFromLocation(w.getName(),x,y,z);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean setGachaWin(String id, boolean state) {
        File dataaa = new File(Bukkit.getServer().getPluginManager().getPlugin("Man10Gacha").getDataFolder(), File.separator);
        File ff = new File(dataaa, File.separator + "gachas.yml");
        FileConfiguration data = YamlConfiguration.loadConfiguration(ff);
        java.lang.Object obj = data.get("gacha." + id);
        if (!ff.exists()) {
            createGachaConfig();
            return false;
        }
        if (obj == null) {
            return false;
        }
        data.set("gacha." + id + ".enableWin", state);
        try {
            data.save(ff);
        } catch (IOException e) {
            e.printStackTrace();
        }
        plugin.loadConfig();
        return true;
    }

    public boolean enableWinBroadcast(String id, boolean state) {
        File dataaa = new File(Bukkit.getServer().getPluginManager().getPlugin("Man10Gacha").getDataFolder(), File.separator);
        File ff = new File(dataaa, File.separator + "gachas.yml");
        FileConfiguration data = YamlConfiguration.loadConfiguration(ff);
        if (!ff.exists()) {
            createGachaConfig();
            return false;
        }
        java.lang.Object obj = data.get("gacha." + id);
        if (obj == null) {
            return false;
        }
        data.set("gacha." + id + ".enableWinBroadcast", state);
        try {
            data.save(ff);
        } catch (IOException e) {
            e.printStackTrace();
        }
        plugin.loadConfig();
        return true;
    }

    public boolean setWinBroadcast(String id, String message) {
        File dataaa = new File(Bukkit.getServer().getPluginManager().getPlugin("Man10Gacha").getDataFolder(), File.separator);
        File ff = new File(dataaa, File.separator + "gachas.yml");
        FileConfiguration data = YamlConfiguration.loadConfiguration(ff);
        if (!ff.exists()) {
            createGachaConfig();
            return false;
        }
        java.lang.Object obj = data.get("gacha." + id);
        if (obj == null) {
            return false;
        }
        data.set("gacha." + id + ".winMessage", message);
        try {
            data.save(ff);
        } catch (IOException e) {
            e.printStackTrace();
        }
        plugin.loadConfig();
        return true;
    }

    public void removeSignsFromLocations(List list) {
        if (list == null || list.size() == 0) {
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            Location l = (Location) list.get(i);
            Block b = l.getBlock();
            if (b.getState() instanceof Sign) {
                b.setType(Material.AIR);
            }
        }
    }

    public boolean winBroadcastIsTrue(String id) {
        File dataaa = new File(Bukkit.getServer().getPluginManager().getPlugin("Man10Gacha").getDataFolder(), File.separator);
        File ff = new File(dataaa, File.separator + "gachas.yml");
        FileConfiguration data = YamlConfiguration.loadConfiguration(ff);
        if (!ff.exists()) {
            createGachaConfig();
            return false;
        }
        java.lang.Object obj = data.get("gacha." + id + ".enableWinBroadcast");
        if (obj == null) {
            return false;
        }
        return data.getBoolean("gacha." + id + ".enableWinBroadcast");
    }

    public boolean winIsTrue(String id) {
        File dataaa = new File(Bukkit.getServer().getPluginManager().getPlugin("Man10Gacha").getDataFolder(), File.separator);
        File ff = new File(dataaa, File.separator + "gachas.yml");
        FileConfiguration data = YamlConfiguration.loadConfiguration(ff);
        if (!ff.exists()) {
            createGachaConfig();
            return false;
        }
        java.lang.Object obj = data.get("gacha." + id + ".enableWin");
        if (obj == null) {
            return false;
        }

        return data.getBoolean("gacha." + id + ".enableWin");
    }

    public String getWinBroadcast(String id) {
        File dataaa = new File(Bukkit.getServer().getPluginManager().getPlugin("Man10Gacha").getDataFolder(), File.separator);
        File ff = new File(dataaa, File.separator + "gachas.yml");
        FileConfiguration data = YamlConfiguration.loadConfiguration(ff);
        if (!ff.exists()) {
            createGachaConfig();
            return null;
        }
        java.lang.Object obj = data.get("gacha." + id + ".winMessage");
        if (obj == null) {
            return null;
        }
        return data.getString("gacha." + id + ".winMessage");
    }

    public String locationToId(String wolrd, double x, double y, double z) {
        ResultSet result = plugin.mysql.query("SELECT * FROM man10_gacha_sign WHERE world = '" + wolrd + "' and x = '" + x + "' and y = '" + y + "' and z ='" + z + "'");
        try {
            while (result.next()) {
                return result.getString("gacha");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deleteSignFromLocation(String wolrd, double x, double y, double z) {
        plugin.mysql.execute("DELETE FROM man10_gacha_sign WHERE world = '" + wolrd + "' and x = '" + x + "' and y = '" + y + "' and z ='" + z + "'");
    }
}
