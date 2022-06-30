package net.bbytes.bukkit.world;

import net.bbytes.bukkit.Main;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class WorldUploader {


    private String id;
    private String world;

    public WorldUploader(String id) {
        this.id = id;
    }


    public Response downloadToTemp(){
        File DESTINATION = null;
        File DESTINATION_FOLDER = null;

        try {
            URL FILE_URL = new URL("https://upload.bbytes.net/uploads/" + id + ".zip");
            DESTINATION = new File(Main.getInstance().getDataFolder() + "/uploads/" + id + ".zip");

            DESTINATION.getParentFile().mkdirs();

            FileUtils.copyURLToFile(FILE_URL, DESTINATION);

            DESTINATION_FOLDER = new File(Main.getInstance().getDataFolder() + "/uploads/" + id);
            DESTINATION_FOLDER.mkdirs();

            unzipFile(DESTINATION, DESTINATION_FOLDER);



            File worldFolder = searchForWorldFolder(DESTINATION_FOLDER);

            if(worldFolder == null){
                FileUtils.deleteDirectory(DESTINATION_FOLDER);
                return Response.INVALID_WORLD;
            }

            String worldID = ConfigurableWorld.generateRandomName();
            this.world = worldID;

            // moving the world folder
            File world = new File(Main.getInstance().getDataFolder() + "/readyToUpload/" + worldID);
            world.mkdirs();
            FileUtils.moveDirectory(new File(worldFolder.getAbsolutePath() + "/region"), new File(world.getAbsolutePath() + "/region"));
            FileUtils.moveFile(new File(worldFolder.getAbsolutePath() + "/level.dat"), new File(world.getAbsolutePath() + "/level.dat"));


        } catch (IOException | ZipException e) {
            if(DESTINATION != null) if(DESTINATION.exists()) DESTINATION.delete();
            if(DESTINATION_FOLDER != null) if(DESTINATION_FOLDER.isDirectory()) {
                try {
                    FileUtils.deleteDirectory(DESTINATION_FOLDER);
                } catch (IOException ignored) { }
            }

            return Response.INVALID_ID;
        }

        if(DESTINATION.exists()) DESTINATION.delete();
        if(DESTINATION_FOLDER.isDirectory()) {
            try {
                FileUtils.deleteDirectory(DESTINATION_FOLDER);
            } catch (IOException ignored) { }
        }

        return Response.SUCCESS;
    }

    private File searchForWorldFolder(File root){
        if(!root.isDirectory())return null;
        boolean regionFolder = false;
        boolean levelDat = false;

        for(File file : root.listFiles()){
            if(file.getName().equals("region") && file.isDirectory()) regionFolder = true;
            if(file.getName().equals("level.dat") && file.isFile()) levelDat = true;
            if(regionFolder && levelDat)break;
        }
        if(regionFolder && levelDat)return root;
        for(File file : root.listFiles()){
            File file1 = searchForWorldFolder(file);
            if(file1 != null) return file1;
        }
        return null;
    }

    private void unzipFile(File file, File destination) throws IOException, ZipException {
        ZipFile zipFile = new ZipFile(file);
        zipFile.extractAll(destination.getAbsolutePath());
    }

    public String getWorld() {
        return world;
    }


    public enum Response{
        SUCCESS,INVALID_ID,INVALID_WORLD
    }


}
