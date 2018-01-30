/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package <your_package>;
 
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.Material;

import net.minecraft.server.v1_12_R1.*;
import org.bukkit.craftbukkit.v1_12_R1.*;
import org.bukkit.craftbukkit.v1_12_R1.inventory.*;
/**
 *
 * @author Paros
 */
public final class NMSSpawner 
{
    public enum SpawnerDataEnum
    {
        PLACE_DELAY("Delay"),
        SPAWN_RANGE("SpawnRange"),
        REQUIRED_PLAYER_RANGE("RequiredPlayerRange"), 
        MAX_NEARBY_ENTITIES("MaxNearbyEntities"),
        MIN_SPAWN_DELAY("MinSpawnDelay"), 
        MAX_SPAWN_DELAY("MaxSpawnDelay"), 
        SPAWN_COUNT("SpawnCount");
        
        private final String nmsTag;
        private SpawnerDataEnum(final String nmsTag)
        {
            this.nmsTag = nmsTag;
        }
        
        public String getTag()
        {
            return nmsTag;
        }
    }
        
    private final NBTTagCompound snapshot;
    public NMSSpawner()
    {
        snapshot = new NBTTagCompound();
        snapshot.setString("id", "minecraft:mob_spawner");
    }
    
    public NMSSpawner(final CreatureSpawner cs)
    {
        this();
        
        TileEntityMobSpawner tileSpawner = (TileEntityMobSpawner)((CraftWorld)cs.getWorld()).getHandle().getTileEntity(new BlockPosition(cs.getX(), cs.getY(), cs.getZ()));
        tileSpawner.getSpawner().b(snapshot);
        snapshot.remove("Delay"); //Remove the delay of the spawning. Allows spawners to stack properly
    }
    
    public NMSSpawner(final org.bukkit.inventory.ItemStack item) throws SpawnerItemException
    {
        if(item == null || item.getType() != Material.MOB_SPAWNER)
        {
            throw new SpawnerItemException();
        }
        
        ItemStack nmsItemStack = CraftItemStack.asNMSCopy(item);
        if(!nmsItemStack.hasTag())
        {
            throw new SpawnerItemException();
        }
        
        NBTTagCompound tag = nmsItemStack.getTag();
        if(!tag.hasKey("BlockEntityTag"))
        {
            snapshot = new NBTTagCompound();
            tag.set("BlockEntityTag", snapshot);
        }
        else
        {
            snapshot = tag.getCompound("BlockEntityTag");
        }
        
        if(!snapshot.hasKey("id") || !snapshot.getString("id").contains("mob_spawner"))
        {
            snapshot.setString("id", "minecraft:mob_spawner");
        }
    }
    
    /**
     * Get a specific value of this spawner
     * @param data The spawner data to get
     * @return The spawner data short value
     */
    public short getData(final SpawnerDataEnum data)
    {
        return snapshot.getShort(data.getTag());
    }
    
    /**
     * Set a specific value of this spawner
     * @param data The spawner data to set
     * @param value The spawner data short value
     */
    public void setData(final SpawnerDataEnum data, final short value)
    {
        String tag = data.getTag();
        snapshot.setShort(tag, value);
    }
    
    /**
     * Get the SpawnData of this spawner
     * @return The wrapped SpawnData
     */
    public NMSSpawnData getSpawnData()
    {
        return new NMSSpawnData(snapshot.getCompound("SpawnData"));
    }
    
    /**
     * Set the SpawnData of this spawner. If the spawnPotentials are present, this will be overriden by them
     * @param data The wrapped SpawnData
     */
    public void setSpawnData(final NMSSpawnData data)
    {
        NBTTagCompound localSpawnData = data.getMobSpawnerData().b(); //Method "b" get only the Entity compound
        snapshot.set("SpawnData", localSpawnData);
    }
    
    /**
     * Set the Spawn Potentials of this spawner. This will override the SpawnData
     * @param list The list of wrapped SpawnData
     */
    public void setSpawnPotentials(final List<NMSSpawnData> list)
    {
        if(list == null)
        {
            return;
        }
        else if(list.isEmpty())
        {
            snapshot.remove("SpawnPotentials");
            return;
        }
        
        NBTTagList localSpawnDataList = new NBTTagList();
        list.stream().map(NMSSpawnData::getMobSpawnerData)
                .map(MobSpawnerData::a) //Method "a" get the compound with both Weight and Entity compound
                .forEach(localSpawnDataList::add);
        this.setSpawnData(list.get(0));
        snapshot.set("SpawnPotentials", localSpawnDataList);
    }
    
    /**
     * Add a SpawnData to the existing SpawnPotentials
     * @param newData The wrapper SpawnData to add
     */
    public void addSpawnPotential(final NMSSpawnData newData)
    {
        if(newData == null)
        {
            return;
        }
        
        NBTTagList localSpawnDataList = snapshot.getList("SpawnPotentials", 10); // Compound id = 10
        localSpawnDataList.add(newData.getMobSpawnerData().a()); //Method "a" get the compound with both Weight and Entity compound
        snapshot.set("SpawnPotentials", localSpawnDataList);
    }
    
    /**
     * Get all the wrapper SpawnData
     * @return A list of all SpawnData
     */
    public List<NMSSpawnData> getSpawnPotentials()
    {
        List<NMSSpawnData> spawnDataList = new ArrayList<>();
        
        NBTTagList list = snapshot.getList("SpawnPotentials", 10); //Compound id = 10
        IntStream.range(0, list.size()).forEach(x -> spawnDataList.add(new NMSSpawnData(list.get(x))));
        
        return spawnDataList;
    }
    
    /**
     * Remove all the SpawnPotentials
     */
    public void clearSpawnPotentials()
    {
        snapshot.remove("SpawnPotentials");
    }
    
    /**
     * Apply this snapshot to a placed spawner block
     * @param cs The creature spawner
     */
    public void update(final CreatureSpawner cs)
    {
        TileEntityMobSpawner tileSpawner = (TileEntityMobSpawner)((CraftWorld)cs.getWorld()).getHandle().getTileEntity(new BlockPosition(cs.getX(), cs.getY(), cs.getZ()));
        
        NBTTagCompound localCompound = (NBTTagCompound)snapshot.clone();
        tileSpawner.getSpawner().a(localCompound);
    }
    
    /**
     * Apply this snapshot to nbt tags of an ItemStack. You will be able to use the constructor of this class to reuse this data.
     * @param itemStack The ItemStack to apply the NBTTagCompound
     * @return The ItemStack after the NBTTagCompound is applied
     */
    public @Nullable org.bukkit.inventory.ItemStack addSnapshot(final org.bukkit.inventory.ItemStack itemStack)
    {
        if(itemStack == null)
        {
            return itemStack;
        }
        
        ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound tag = nmsItemStack.hasTag() ? nmsItemStack.getTag() : new NBTTagCompound();
        tag.set("BlockEntityTag", snapshot);
        nmsItemStack.setTag(tag);
        return CraftItemStack.asBukkitCopy(nmsItemStack);
    }
    
    @Override
    public String toString()
    {
        return snapshot.toString();
    }
    
    public class SpawnerItemException extends Exception
    {
        
    }
}
