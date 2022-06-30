package net.bbytes.bukkit.util;

import net.bbytes.bukkit.Main;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class BackupManager {

    String backupFolderPath = "";

    public void backup(){
        if(backupFolderPath.isEmpty())backupFolderPath = new File(Main.getInstance().getClass().getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .getPath()).getParentFile().getParentFile().getAbsolutePath() + File.separator + "backups";
        new File(backupFolderPath).mkdirs();

        long latestBackup = 0;
        for(File file : Objects.requireNonNull(new File(backupFolderPath).listFiles())){
            if(stringToMillis(file.getName()) > latestBackup){
                latestBackup = stringToMillis(file.getName());
            }
        }

        /*
          If the latest backup were taken less than 8 hours ago, cancel
         */
        if(System.currentTimeMillis() - latestBackup < TimeUnit.HOURS.toMillis(8)){
            return;
        }

        /*
            Do a backup
         */
        Bukkit.getLogger().info("[BackupManager] Backing up server...");
        File destination = new File(backupFolderPath, millisToString(System.currentTimeMillis()) + ".zip");
        try {
            FileOutputStream fos = new FileOutputStream(destination);
            ZipOutputStream zipOut = new ZipOutputStream(fos);
            for(File worldFolder : Objects.requireNonNull(Bukkit.getWorldContainer().listFiles())){
                zipFile(worldFolder, worldFolder.getName(), zipOut);
            }

            Bukkit.getLogger().info("[BackupManager] Server backed up");

            //Bukkit.broadcastMessage(Main.getInstance().PREFIX + "Server backed up!");


            zipOut.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            Bukkit.getLogger().info("[BackupManager] Server backup failed");
        }


        /*
            Delete older backups
         */

        for(File file : Objects.requireNonNull(new File(backupFolderPath).listFiles())){
            if(System.currentTimeMillis() - stringToMillis(file.getName()) > TimeUnit.DAYS.toMillis(15)){
                file.delete();
                Bukkit.getLogger().info("[BackupManager] Deleted >15-day old backup '" + file.getName() + "'");
            }
        }


        (new BukkitRunnable(){

            @Override
            public void run() {
                backup();
            }
        }).runTaskLaterAsynchronously(Main.getInstance(), (TimeUnit.HOURS.toSeconds(8) + 120) * 20);



    }

    private static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
        if (fileToZip.isHidden()) {
            return;
        }
        if (fileToZip.isDirectory()) {
            if (fileName.endsWith("/")) {
                zipOut.putNextEntry(new ZipEntry(fileName));
            } else {
                zipOut.putNextEntry(new ZipEntry(fileName + "/"));
            }
            zipOut.closeEntry();
            File[] children = fileToZip.listFiles();
            for (File childFile : children) {
                zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
            }
            return;
        }
        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileName);
        zipOut.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
        fis.close();
    }


    public String millisToString(long millis){
        Date date = new Date(millis);
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        StringBuilder builder = new StringBuilder("Backup-");

        builder.append(calendar.get(Calendar.DATE)).append("-");
        builder.append(calendar.get(Calendar.MONTH)).append("-");
        builder.append(calendar.get(Calendar.YEAR)).append("-");
        builder.append(calendar.get(Calendar.HOUR_OF_DAY)).append("-");
        builder.append(calendar.get(Calendar.MINUTE));
        return builder.toString();
    }

    public long stringToMillis(String str){
        Calendar calendar = new GregorianCalendar();
        String[] strings = str.split("\\.")[0].split("-");

        calendar.set(Calendar.DATE, Integer.parseInt(strings[1]));
        calendar.set(Calendar.MONTH, Integer.parseInt(strings[2]));
        calendar.set(Calendar.YEAR, Integer.parseInt(strings[3]));
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(strings[4]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(strings[5]));

        return calendar.getTimeInMillis();
    }



}
