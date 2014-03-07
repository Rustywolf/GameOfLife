package net.teslaraptor;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class GameListener implements Listener {
    
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        ItemStack item = e.getItem();
        
        //Toggle Glowstone using Glowstone dust
        
        if(item.getType() == Material.GLOWSTONE_DUST && e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            
            Block b = e.getClickedBlock();
            
            if(b.getType() == Material.COAL_BLOCK) {
                b.setType(Material.GLOWSTONE);
            } else if (b.getType() == Material.GLOWSTONE) {
                b.setType(Material.COAL_BLOCK);
            }
        }
    }
}
