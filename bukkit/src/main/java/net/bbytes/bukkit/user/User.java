package net.bbytes.bukkit.user;

import net.bbytes.bukkit.Main;
import net.bbytes.bukkit.project.Project;
import net.bbytes.bukkit.warp.Warp;
import net.bbytes.bukkit.world.ConfigurableWorld;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class User {

    private final Player player;
    private Language language = Language.ENGLISH;
    private List<ConfigurableWorld> recentWorlds = new ArrayList<>();
    private List<Warp> recentWarps = new ArrayList<>();

    private Project projectToUpload = null;

    private Project workingProject = null;

    public User(Player player) {
        this.player = player;
        Main.getInstance().getMySQLManager().mysql.query("SELECT Language,RecentWorlds" + Main.getInstance().getSubversion() + ",RecentWarps" + Main.getInstance().getSubversion() + " FROM Users WHERE UUID='" + player.getUniqueId().toString() + "';", (rs) -> {
            try {
                if(rs.next()){
                    setLanguage(Language.getLanguage(rs.getString("Language")));
                    if(rs.getString("RecentWorlds" + Main.getInstance().getSubversion()) != null)
                        if(!rs.getString("RecentWorlds" + Main.getInstance().getSubversion()).equals(" "))
                            for(String id : rs.getString("RecentWorlds" + Main.getInstance().getSubversion()).split(";")){
                                ConfigurableWorld hw = ConfigurableWorld.getWorld(id);
                                if(hw != null) {
                                    recentWorlds.add(hw);
                                }
                            }
                    if(rs.getString("RecentWarps" + Main.getInstance().getSubversion()) != null)
                        if(!rs.getString("RecentWarps" + Main.getInstance().getSubversion()).equals(" "))
                            for(String id : rs.getString("RecentWarps" + Main.getInstance().getSubversion()).split(";")){
                                Warp warp = Warp.getWarp(id);
                                if(warp != null) {
                                    recentWarps.add(warp);
                                }
                            }

                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
    }

    public void saveUser_Sync() throws SQLException {
        ResultSet rs = Main.getInstance().getMySQLManager().mysql.getMySQL().query("SELECT Language FROM Users WHERE UUID='" + player.getUniqueId().toString()+ "';");

        StringBuilder worldBuilder = new StringBuilder();
        for(int i = 0; i < recentWorlds.size(); i++){
            if(i > 8)break;
            worldBuilder.append(recentWorlds.get(i).getFileWorldName()).append(";");
        }


        StringBuilder warpBuilder = new StringBuilder();
        for(int i = 0; i < recentWarps.size(); i++) {
            if (i > 8) break;
            warpBuilder.append(recentWarps.get(i).getName()).append(";");
        }

        if(rs.next()){
            Main.getInstance().getMySQLManager().mysql.getMySQL().queryUpdate("UPDATE Users SET " +
                    "Language='" + language.getID() + "', " +
                    "RecentWorlds" + Main.getInstance().getSubversion() + "='" + worldBuilder.toString() + "', " +
                    "RecentWarps" + Main.getInstance().getSubversion() + "='" + warpBuilder.toString() + "' " +
                    "WHERE UUID='" + player.getUniqueId().toString() + "';");
        }else{
            Main.getInstance().getMySQLManager().mysql.getMySQL().queryUpdate("INSERT INTO Users (Language, RecentWorlds" + Main.getInstance().getSubversion() + ", RecentWarps" + Main.getInstance().getSubversion() + ", UUID) VALUES (" +
                    "'" + language.getID() + "', " +
                    "'" + worldBuilder.toString() + "', " +
                    "'" + warpBuilder.toString() + "', " +
                    "'" + player.getUniqueId().toString() + "');");
        }
    }

    public Player getPlayer() {
        return player;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public List<ConfigurableWorld> getRecentWorlds() {
        return recentWorlds;
    }

    public void setRecentWorlds(List<ConfigurableWorld> recentWorlds) {
        this.recentWorlds = recentWorlds;
    }

    public static User getUser(Player player){
        return Main.getInstance().getUserManager().getUser(player);
    }

    public void logWorld(ConfigurableWorld configurableWorld) {
        recentWorlds.removeIf(world -> world.getFileWorldName().equals(configurableWorld.getFileWorldName()));
        recentWorlds.add(0, configurableWorld);
    }

    public void logWarp(Warp warp) {
        recentWarps.removeIf(w -> w.getName().equals(warp.getName()));
        recentWarps.add(0, warp);
    }

    public List<Warp> getRecentWarps() {
        return recentWarps;
    }

    public Project getProjectToUpload() {
        return projectToUpload;
    }

    public void setProjectToUpload(Project projectToUpload) {
        this.projectToUpload = projectToUpload;
    }

//    public boolean setSkin(UUID uuid) {
//        try {
//            HttpsURLConnection connection = (HttpsURLConnection) new URL(String.format("https://sessionserver.mojang.com/session/minecraft/profile/%s?unsigned=false", uuid.toString())).openConnection();
//            if (connection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
//                StringBuilder reply = new StringBuilder();
//                try (InputStreamReader instream = new InputStreamReader(connection.getInputStream());
//                     BufferedReader buffer = new BufferedReader(instream)) {
//                    String line;
//                    while ((line = buffer.readLine()) != null) {
//                        reply.append(line);
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//
////                System.out.println(reply);
//                JSONObject obj = (JSONObject) new JSONParser().parse(reply.toString());
//                JSONArray array = (JSONArray) obj.get("properties");
//
////                String skin = reply.split("\"value\":\"")[1].split("\"")[0];
////                String signature = reply.split("\"signature\":\"")[1].split("\"")[0];
//                String skin = (String) ((JSONObject)array.get(0)).get("value");
//                String signature  = (String) ((JSONObject)array.get(0)).get("signature");
//                Main.getInstance().getWrapper().getProfile(player).getProperties().put("textures", new Property("textures", skin, signature));
//                System.out.println("Skin set");
//
//                return true;
//            } else {
//                System.out.println("Connection could not be opened (Response code " + connection.getResponseCode() + ", " + connection.getResponseMessage() + ")");
//                return false;
//            }
//        } catch (IOException | ParseException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
}
