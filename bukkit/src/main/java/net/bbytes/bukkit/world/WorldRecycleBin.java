package net.bbytes.bukkit.world;

import net.bbytes.bukkit.Main;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class WorldRecycleBin {

    private List<RecycledConfigurableWorld> recycledWorldsList = new ArrayList<>();

    public WorldRecycleBin() {
        loadRecycleBin();
    }

    public void saveRecycleBin(){
        Map<String, Object> data = new HashMap<String, Object>();
        for(RecycledConfigurableWorld hw : recycledWorldsList){
            Map<String, Object> map = hw.serialize();
            map.put("recycled", hw.getRecycled());
            map.put("recycledBy", hw.getRecycledBy());
            data.put(hw.getFileWorldName(), map);
        }

        DumperOptions options = new DumperOptions();
        options.setIndent(2);
        options.setPrettyFlow(true);
        options.setSplitLines(false);
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

        Yaml yaml = new Yaml(options);

        try {
            StringWriter stringWriter = new StringWriter();
            yaml.dump(data, stringWriter);
            OutputStreamWriter stream = new OutputStreamWriter(new FileOutputStream(Main.getInstance().getDataFolder() + "/worldRecycleBin.yml"), StandardCharsets.UTF_8);
            stream.write(stringWriter.toString());
            stream.flush();
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadRecycleBin(){
        Yaml yaml = new Yaml();
        File f = new File(Main.getInstance().getDataFolder() + "/worldRecycleBin.yml");
        if(!f.exists())return;

        try {

            InputStream inputStream = new FileInputStream(f);

            Map<String, Object> map = yaml.load(inputStream);
            List<String>  worlds = new ArrayList<>(map.keySet());
            for(String key : worlds){
                newWorld((Map<String, Object>) map.get(key), key);
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        List<RecycledConfigurableWorld> toRemove = new ArrayList<>();

        for(RecycledConfigurableWorld world : recycledWorldsList){
            if(System.currentTimeMillis() - world.getRecycled() > TimeUnit.DAYS.toMillis(7)){
                toRemove.add(world);
            }
        }

        for(RecycledConfigurableWorld world : toRemove){
            recycledWorldsList.remove(world);
            File file = new File(Bukkit.getWorldContainer(), world.getFileWorldName());;
            if(file.isDirectory()) {
                try {
                    FileUtils.deleteDirectory(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }


    public void newWorld(Map<String, Object> map, String name){
        RecycledConfigurableWorld hw = new RecycledConfigurableWorld(map, name);
        recycledWorldsList.add(hw);
    }

    public List<RecycledConfigurableWorld> getRecycledWorldsList() {
        return recycledWorldsList;
    }

    public RecycledConfigurableWorld getWorld(String worldID){
        for(RecycledConfigurableWorld world : recycledWorldsList)
            if(world.getFileWorldName().equals(worldID))
                return world;
            return null;
    }
}
