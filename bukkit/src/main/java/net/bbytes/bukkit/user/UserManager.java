package net.bbytes.bukkit.user;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class UserManager{

    private List<User> loadedUsers = new ArrayList<>();

    public List<User> getLoadedUsers() {
        return loadedUsers;
    }

    public User getUser(Player player){
        for(User u : loadedUsers)
            if(u.getPlayer() == player)
                return u;
            return null;
    }




    public void addUser(Player p){
        User u = new User(p);
        loadedUsers.add(u);
    }

    public void removeUser(Player p){
        loadedUsers.removeIf(u -> u.getPlayer() == p);
    }
}
