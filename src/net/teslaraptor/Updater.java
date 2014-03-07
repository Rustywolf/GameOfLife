package net.teslaraptor;

import java.util.Arrays;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

public class Updater extends BukkitRunnable {
    public GameOfLife plugin;
    
    public boolean[][] twoStepMap;
    public boolean[][] lifeMap;
    public boolean[][] tempMap;
    
    World plotWorld;
    
    int cycleCount = 0;
    int initialCells = 0;
    int tempCells = 0;
    int pulseCycles = 20;
    int pulseCycleCount = 0;
    
    public Updater(GameOfLife plugin) {
        this.plugin = plugin;
        plotWorld = Bukkit.getWorld("plotworld");
    }
    
    public void setupArrays() {
        lifeMap = new boolean[32][32];    //Live map of current live cells
        tempMap = new boolean[32][32];    //Map of next generation of cells
        twoStepMap = new boolean[32][32]; //Used to check for 2-step pulsers
        
        initialCells = 0; //Count's the amount of initial live cells
        
        int y = plugin.y;
        for(int x = plugin.xStart; x <= plugin.xEnd; x++) {
            for(int z = plugin.zStart; z <= plugin.zEnd; z++) {
                Block b = plotWorld.getBlockAt(x, y, z);
                if(b.getType() == Material.GLOWSTONE) { //Glowstone represents live cell
                    lifeMap[x-plugin.xStart][z-plugin.zStart] = true; //Set true in array
                    initialCells++; //Increment cell count
                }
            }
        }
    }
    
    @Override
    public void run() {
        cycleCount++; //Increment cycle
        tempCells = 0; //Recent count of livecells incase last iteration
        for(int x = 0; x < lifeMap.length; x++) {
            for(int z = 0; z < lifeMap[0].length; z++) {
                
                int neighbours = 0;
                boolean alive = lifeMap[x][z];
                
                /*
                The next part is a little bit annoying
                Get every cell around the block, checking that it wont cause a nullpointer
                I.E.
                X = Neighbour
                O = Origin
                
                   X X X
                   X O X
                   X X X
                
                */
                //-1, -1
                if(x != 0 && z != 0)
                    if(lifeMap[x-1][z-1])
                        neighbours++;
                //-1, 0
                if(x != 0)
                    if(lifeMap[x-1][z])
                        neighbours++;
                //-1, +1
                if(x != 0 && z != lifeMap[0].length-1)
                    if(lifeMap[x-1][z+1])
                        neighbours++;
                //0, -1
                if(z != 0)
                    if(lifeMap[x][z-1])
                        neighbours++;
                //0, +1
                if(z != lifeMap[0].length-1)
                    if(lifeMap[x][z+1])
                        neighbours++;
                //+1, -1
                if(z != 0 && x != lifeMap.length-1)
                    if(lifeMap[x+1][z-1])
                        neighbours++;
                //+1, 0
                if(x != lifeMap.length-1)
                    if(lifeMap[x+1][z])
                        neighbours++;
                //+1, +1
                if(x != lifeMap.length-1 && z != lifeMap[0].length-1)
                    if(lifeMap[x+1][z+1])
                        neighbours++;

                
                if(alive) {
                    
                    if(neighbours < 2) { //Underpopulation
                        tempMap[x][z] = false;
                    }
                    
                    else if(neighbours == 2 || neighbours == 3) { //Prosperity
                        tempMap[x][z] = true;
                        tempCells ++;
                    }
                    
                    else if(neighbours > 3) { //Overpopulation
                        tempMap[x][z] = false;
                    }
                    
                } else {
                    
                    if(neighbours == 3) { //Reproduction
                        tempMap[x][z] = true;
                        tempCells++;
                    }
                    
                }
                
            }
        }
        
        if(compareArrays(lifeMap, tempMap)) { //Detect sandstill by comparing arrays
           
            Bukkit.broadcastMessage(ChatColor.GREEN + "[GameOfLife] " + ChatColor.RESET + "StandStill detected.");
            Bukkit.broadcastMessage(ChatColor.GREEN + "[GameOfLife] " + ChatColor.RESET + "Round lasted " + ChatColor.GOLD + this.cycleCount + ChatColor.RESET + " cycles.");
            Bukkit.broadcastMessage(ChatColor.GREEN + "[GameOfLife] " + ChatColor.RESET + "Initial Cells: " + ChatColor.DARK_AQUA + initialCells + ChatColor.RESET + "; Final Cells: " + ChatColor.DARK_AQUA + tempCells + ChatColor.RESET + ";");
            
            plugin.stopUpdater(); 
            
        } else {
    
            if(compareArrays(tempMap, twoStepMap)) {
        
                //Allow prettyful display to pulse for (theoretical) 5 seconds
                if(pulseCycleCount < pulseCycles) {
                    pulseCycleCount++; 
                } else {
                    Bukkit.broadcastMessage(ChatColor.GREEN + "[GameOfLife] " + ChatColor.RESET + "2-Step Pulse Loop detected.");
                    Bukkit.broadcastMessage(ChatColor.GREEN + "[GameOfLife] " + ChatColor.RESET + "Round lasted " + ChatColor.GOLD + this.cycleCount + ChatColor.RESET + " cycles.");
                    Bukkit.broadcastMessage(ChatColor.GREEN + "[GameOfLife] " + ChatColor.RESET + "Initial Cells: " + ChatColor.DARK_AQUA + initialCells + ChatColor.RESET + "; Final Cells: " + ChatColor.DARK_AQUA + tempCells + ChatColor.RESET + ";");

                    plugin.stopUpdater();
                    return;
                }
            }
            
            twoStepMap = lifeMap;
            lifeMap = tempMap;
            tempMap = new boolean[32][32]; //reset tempMap;

            int y = plugin.y;
            for(int x = 0; x < lifeMap.length; x++) {
                for(int z = 0; z < lifeMap[0].length; z++) {
                    Block b = plotWorld.getBlockAt(x + plugin.xStart, y, z + plugin.zStart);
                    if(lifeMap[x][z]) { //If live cell
                        b.setType(Material.GLOWSTONE);
                    }
                    else {
                        b.setType(Material.COAL_BLOCK);
                    }
                }
            }
        }
    }
    
    public void clearBoard() { //Reset arrays
        lifeMap = new boolean[32][32];
        tempMap = new boolean[32][32];
        twoStepMap = new boolean[32][32];
        
        int y = plugin.y;
        for(int x = 0; x < lifeMap.length; x++) {
            for(int z = 0; z < lifeMap[0].length; z++) {
                Block b = plotWorld.getBlockAt(x + plugin.xStart, y, z + plugin.zStart);
                b.setType(Material.COAL_BLOCK);
            }
        }
    }
    
    public void resetCounter() {
        this.cycleCount = 0;
    }
    
    public boolean compareArrays(boolean[][] arr1, boolean[][] arr2) {
        boolean difference = false;
        
        //How can our arrays be real if the lengths arent real -Jayden Smith
        if(arr1.length != arr2.length || arr1[0].length != arr2[0].length) return false;
        
        for(int x = 0; x < arr1.length; x++) {
            for(int z = 0; z < arr1[0].length; z++) {
                if(arr1[x][z] != arr2[x][z]) difference = true;
            }
        }
        
        return !difference;
    }
}
