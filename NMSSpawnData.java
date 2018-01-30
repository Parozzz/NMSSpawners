/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package <your_package>;

import net.minecraft.server.v1_12_R1.*;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;

import java.util.stream.IntStream;
import javax.annotation.Nullable;
import org.bukkit.entity.EntityType;

/**
 *
 * @author Paros
 */
public final class NMSSpawnData 
{
    public enum SpawnDataEnum
    {
        ENTITY("Entity"), WEIGHT("Weight");
        
        private final String nmsTag;
        private SpawnDataEnum(final String nmsTag)
        {
            this.nmsTag = nmsTag;
        }
        
        public String getTag()
        {
            return nmsTag;
        }
    }
    
    private final NBTTagCompound compound;
    public NMSSpawnData(final NBTTagCompound compound)
    {
        this.compound = compound;
    }
    
    public NMSSpawnData()
    {
        this(new NBTTagCompound());
        this.setEntityType(EntityType.PIG);
        this.setWeight(1);
    }
    
    public NMSSpawnData(final EntityType et, final int weight)
    {
        this.compound = new NBTTagCompound();
        this.setEntityType(et);
        this.setWeight(weight);
    }
    
    public NMSSpawnData(final EntityType et)
    {
        this(et, 1);
    }
    
    public NMSSpawnData(final String name, final int weight)
    {
        this.compound = new NBTTagCompound();
        this.setEntityName(name);
        this.setWeight(weight);
    }
    
    public NMSSpawnData(final String name)
    {
        this(name, 1);
    }
    
    /**
     * Set if the spawned entity will be silent
     * @param silent If the entity will spawn silent or not
     * @return This NMSSpawnData instance.
     */
    public NMSSpawnData setSilent(final boolean silent)
    {
        String tag = SpawnDataEnum.ENTITY.getTag();
        NBTTagCompound localCompound = compound.getCompound(tag);
        localCompound.setBoolean("Silent", silent);
        compound.set(tag, localCompound);
        
        return this;
    }
    
    /**
     * Set if the spawned entity will glow
     * @param glow If the entity will glow or not.
     * @return This NMSSpawnData instance.
     */
    public NMSSpawnData setGlowing(final boolean glow)
    {
        String tag = SpawnDataEnum.ENTITY.getTag();
        NBTTagCompound localCompound = compound.getCompound(tag);
        localCompound.setBoolean("Glowing", glow);
        compound.set(tag, localCompound);
        
        return this;
    }
    
    /**
     * Set the fire ticks the spawned entity will have
     * @param ticks The fire ticks
     * @return This NMSSpawnData instance
     */
    public NMSSpawnData setFireTicks(final int ticks)
    {
        String tag = SpawnDataEnum.ENTITY.getTag();
        NBTTagCompound localCompound = compound.getCompound(tag);
        localCompound.setShort("Fire", (short)ticks);
        compound.set(tag, localCompound);
        
        return this;
    }
    
    /**
     * Set if the spawned entity will have AI
     * @param ai If the mob will have id or not.
     * @return This NMSSpawnData instance.
     */
    public NMSSpawnData setAi(final boolean ai)
    {
        String tag = SpawnDataEnum.ENTITY.getTag();
        NBTTagCompound localCompound = compound.getCompound(tag);
        localCompound.setBoolean("NoAI", !ai);
        compound.set(tag, localCompound);
        
        return this;
    }
    
    /**
     * Get the entity internal id this NMSSpawnData spawns.
     * @return The entity internal id
     */
    public String getEntityName()
    {
        String tag = SpawnDataEnum.ENTITY.getTag();
        return compound.getCompound(tag).getString("id").replace("minecraft:", "");
    }
    
    /**
     * Get the EntityType this NMSSpawnData spawns. Remember that custom entities in spawners won't show up client side.
     * @return The entity type spawned. 
     */
    public EntityType getEntityType()
    {
        return EntityType.fromName(getEntityName());
    }
    
    /**
     * Set the nms entity that this NMSSpawnData will spawn. Useful in case of custom entities with custom ids.
     * @param name The entity id from EntityTypes registry
     * @return This NMSSpawnData instance
     */
    public NMSSpawnData setEntityName(final String name)
    {
        String tag = SpawnDataEnum.ENTITY.getTag();
        NBTTagCompound localCompound = compound.getCompound(tag);
        localCompound.setString("id", "minecraft:" + name);
        compound.set(tag, localCompound);
        
        return this;
    }
    
    /**
     * Set the entity type this NMSSpawnData will spawn
     * @param et The entity type to spawn
     * @return This NMSSpawnData instance
     */
    public NMSSpawnData setEntityType(final EntityType et)
    {
        String tag = SpawnDataEnum.ENTITY.getTag();
        NBTTagCompound localCompound = compound.getCompound(tag);
        localCompound.setString("id", "minecraft:" + et.getName());
        compound.set(tag, localCompound);
        
        return this;
    }
    
    /**
     * Set the "LeftHanded" tag to this NMSSpawnData
     * @param left If the mob will be left handed or not
     * @return This NMSSpawnData instance
     */
    public NMSSpawnData setLeftHanded(final boolean left)
    {
        String tag = SpawnDataEnum.ENTITY.getTag();
        NBTTagCompound localCompound = compound.getCompound(tag);
        localCompound.setBoolean("LeftHanded", left);
        compound.set(tag, localCompound);
        
        return this;
    }
    
    /**
     * Set the spawn potential weight for this spawn data
     * @param weight Set weight to set
     * @return This NMSSpawnData instance
     */
    public NMSSpawnData setWeight(final int weight)
    {
        String tag = SpawnDataEnum.WEIGHT.getTag();
        compound.setInt(tag, weight);
        
        return this;
    }
    
    /**
     * Get the spawn potential weight for this spawn data.
     * @return The weight data
     */
    public int getWeight()
    {
        String tag = SpawnDataEnum.WEIGHT.getTag();
        return compound.getInt(tag);
    }
    
    /**
     * Set the armor items of the mob
     * @param items 0 = FEET. 1 = LEGS. 2 = CHEST. 3 = HEAD. Input an empty vararg for remove all the equipment or add a null value in the array
     * @return 
     */
    public NMSSpawnData setArmorItems(final org.bukkit.inventory.ItemStack... items)
    {
        String tag = SpawnDataEnum.ENTITY.getTag();
        
        NBTTagList localArmorList = new NBTTagList();
        IntStream.range(0, 4).forEach(x -> 
        {
            NBTTagCompound localItemCompound = items.length >= x + 1 && items[x] != null 
                    ? CraftItemStack.asNMSCopy(items[x]).save(new NBTTagCompound()) 
                    : new NBTTagCompound();
            localArmorList.add(localItemCompound);
        });
        
        NBTTagCompound localCompound = compound.getCompound(tag);
        localCompound.set("ArmorItems", localArmorList);
        compound.set(tag, localCompound);
        
        return this;
    }
    
    /**
     * Set the hand items of the mob
     * @param mainHand The item in the main hand
     * @param offHand The item in the off hand
     * @return 
     */
    public NMSSpawnData setHandItems(final @Nullable org.bukkit.inventory.ItemStack mainHand, final @Nullable org.bukkit.inventory.ItemStack offHand)
    {
        String tag = SpawnDataEnum.ENTITY.getTag();
        
        NBTTagList localHandList = new NBTTagList();
        localHandList.add(mainHand == null ? new NBTTagCompound() : CraftItemStack.asNMSCopy(mainHand).save(new NBTTagCompound()));
        localHandList.add(offHand == null ? new NBTTagCompound() : CraftItemStack.asNMSCopy(offHand).save(new NBTTagCompound()));
        
        NBTTagCompound localCompound = compound.getCompound(tag);
        localCompound.set("HandItems", localHandList);
        compound.set(tag, localCompound);
        
        return this;
    }
    
    protected MobSpawnerData getMobSpawnerData()
    {
        return new MobSpawnerData(compound);
    }
    
    @Override
    public String toString()
    {
        return compound.toString();
    }
}
