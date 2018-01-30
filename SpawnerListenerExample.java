/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package <your_package>;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Paros
 */
public class SpawnerListenerExample implements Listener
{
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    private void onSpawnerPlacement(final BlockPlaceEvent e)
    {
        //Check if the block is a mob spawner
        if(e.getBlockPlaced().getType() == Material.MOB_SPAWNER)
        {  
            //Get the state of the block and cast it as a creature spawner
            CreatureSpawner cs = (CreatureSpawner)e.getBlockPlaced().getState();
            
            NMSSpawner spawner;
            //Check if the item is null or is not a mob spawner
            if(e.getItemInHand() == null || e.getItemInHand().getType() != Material.MOB_SPAWNER)
            {
                //Convert the spawner to the wrapper
                spawner = new NMSSpawner(cs);
            }
            else
            {
                try { //If the itemStack is a spawner with the right nbt, no exception will be thrown
                    //Convert the itemStack to the wrapper
                    spawner = new NMSSpawner(e.getItemInHand());
                } catch (NMSSpawner.SpawnerItemException ex) { //In case the itemStack has not the right nbt, i'll convert the spawner to the wrapper
                    spawner = new NMSSpawner(cs);
                }
            }


            //Get all the spawn potentials of this spawner, set the entity to spawn glowing, without ai and with 1000 ticks of fire.
            List<NMSSpawnData> spawnDataList = spawner.getSpawnPotentials().stream()
                    .map(spawnData -> spawnData.setGlowing(true).setAi(false).setFireTicks(1000))
                    .collect(Collectors.toList());
            //Set back the spawn potientials
            spawner.setSpawnPotentials(spawnDataList);
            //Update the spawner
            spawner.update(cs);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    private void onSpawnerBreak(final BlockBreakEvent e)
    {
        //Check if the block is a spawner
        if(e.getBlock().getType() == Material.MOB_SPAWNER)
        {   //Get the state of the block and cast it as a creature spawner
            CreatureSpawner cs = (CreatureSpawner)e.getBlock().getState();
            //Convert the spawner to the wrapper
            NMSSpawner spawner = new NMSSpawner(cs);
            //Create a new mob spawner ItemStack
            ItemStack spawnerItemStack = new ItemStack(Material.MOB_SPAWNER);
            //Apply the wrapper snapshot to the itemStack and then drop it.
            cs.getWorld().dropItem(cs.getLocation().add(0.5, 0.5, 0.5), spawner.addSnapshot(spawnerItemStack));
        }
    }
}
