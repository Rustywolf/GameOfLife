package net.teslaraptor;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class GameOfLife extends JavaPlugin {
    public int xStart = -191;
    public int zStart = 4;
    public int y = 64;
    public int xEnd = -160;
    public int zEnd = 35;
    
    public Updater updater;
    public int updaterID;
    
    private GameListener gameListener;
    
    @Override
    public void onEnable() {
        gameListener = new GameListener();
        Bukkit.getPluginManager().registerEvents(gameListener, this);
        updater = new Updater(this);
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        
        if(cmd.getName().equalsIgnoreCase("lifestart")) {
            sender.sendMessage(ChatColor.GREEN + "[GameOfLife] " + ChatColor.RESET + "Started!");
            startUpdater();
            return true;
        }
        
        else if(cmd.getName().equalsIgnoreCase("lifestop")) {
            sender.sendMessage(ChatColor.GREEN + "[GameOfLife] " + ChatColor.RESET + "Stopped!");
            stopUpdater();
            return true;
        }
        
        else if (cmd.getName().equalsIgnoreCase("lifeload")) {
            sender.sendMessage(ChatColor.GREEN + "[GameOfLife] " + ChatColor.RESET + "Block Data Reloaded!");
            updater.resetCounter();
            updater.setupArrays();
            return true;
        }
        
        else if (cmd.getName().equalsIgnoreCase("liferun")) {
            sender.sendMessage(ChatColor.GREEN + "[GameOfLife] " + ChatColor.RESET + "Cycle Incremented!");
            updater.run();
            return true;
        }
        
        else if (cmd.getName().equalsIgnoreCase("lifeclear")) {
            sender.sendMessage(ChatColor.GREEN + "[GameOfLife] " + ChatColor.RESET + "Cleared!");
            updater.clearBoard();
            return true;
        }
        return false;
    }
    
    public void startUpdater() {
        
        updater.resetCounter(); //Set initial count
        updater.setupArrays(); // Load placed configuration
        updaterID = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, updater, 1, 5); //set task to run 4 times a second
        
    }
    
    public void stopUpdater() {
        
       Bukkit.getScheduler().cancelTask(updaterID);
       
    }
}
