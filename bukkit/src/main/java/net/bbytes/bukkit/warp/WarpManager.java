package net.bbytes.bukkit.warp;

import net.bbytes.bukkit.Main;
import net.bbytes.bukkit.message.Message;
import org.bukkit.entity.Player;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class WarpManager {

    private List<Warp> warpList = new ArrayList<>();

    public void loadWarps(){
        Yaml yaml = new Yaml();
        File f = new File(Main.getInstance().getDataFolder() + "/warps.yml");
        if(!f.exists())return;

        try {
            InputStream inputStream = new FileInputStream(f);

            Map<String, Object> map = yaml.load(inputStream);
            List<String> warps = new ArrayList<>(map.keySet());
            for(String key : warps){
                warpList.add(new Warp((Map<String, Object>) map.get(key), key));
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void saveWarps(){
        if(warpList.size() == 0)return;

        Map<String, Object> data = new LinkedHashMap<String, Object>();
        for(Warp warp : warpList)
            data.put(warp.getName(), warp.serialize());

        DumperOptions options = new DumperOptions();
        options.setIndent(2);
        options.setPrettyFlow(true);
        options.setSplitLines(false);
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

        Yaml yaml = new Yaml(options);

        try {
            StringWriter stringWriter = new StringWriter();
            yaml.dump(data, stringWriter);
            OutputStreamWriter stream = new OutputStreamWriter(new FileOutputStream(Main.getInstance().getDataFolder() + "/warps.yml"), StandardCharsets.UTF_8);
            stream.write(stringWriter.toString());
            stream.flush();
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public Warp getWarp(String name){
        for(Warp w : warpList)
            if(w.getName().replaceAll(" ", "_").equalsIgnoreCase(name.replaceAll(" ", "_")))
                return w;
            return null;
    }

    public void newWarp(Player player, String name){
        if(getWarp(name) != null){
            player.sendMessage(Message.ERROR_WARP_NAME_EXISTS.get(player));
            return;
        }

        Warp w = new Warp(player.getLocation(), name);
        warpList.add(w);
        player.sendMessage("§6" + Message.WARP_SET.get(player).replace("{warp}", "§c" + name + "§6"));

    }

    public void deleteWarp(Player player, String name){
        if(getWarp(name) == null){
            player.sendMessage(Message.ERROR_WARP_NO_EXIST.get(player));
            return;
        }

        Warp w = getWarp(name);

        if(!w.canAccess(player)){
            player.sendMessage(Message.ERROR_NO_ACCESS_DELETE_WARP.get(player));
            return;
        }

        warpList.removeIf(warp -> warp.getName().replaceAll(" ", "_").equals(w.getName().replaceAll(" ", "_")));
        player.sendMessage("§6" + Message.WARP_REMOVED.get(player).replace("{warp}", "§c" + name + "§6"));

    }

    public List<Warp> getUncategorizedWarps(){
        List<Warp> warpList = new ArrayList<>();
        for(Warp warp : this.warpList) {
            if (warp.getConfigurableWorld() != null)
                if (warp.getConfigurableWorld().getProject() != null) {
                    continue;
                }
            warpList.add(warp);
        }

        return warpList;
    };

    public List<Warp> getWarpList() {
        List<Warp> newWarpList = new ArrayList<>();
        for(Warp w : this.warpList){
        }


        return warpList;
    }
}
