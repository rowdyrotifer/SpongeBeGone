package com.marklalor.spongebegone;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class SpongeBeGone extends JavaPlugin implements Listener
{
    @Override
    public void onEnable()
    {
        getServer().getPluginManager().registerEvents(this, this);
    }
    
    @EventHandler
    public void onBlockPlace(final BlockPlaceEvent event)
    {
        if(event.getBlock().getType() != Material.SPONGE)
            return;
        
        final List<WaterDataHolder> waterBlocksAroundSponge = getWaterBlocks(event.getBlock());
        
        System.out.println("1");
        Bukkit.getScheduler().runTask(this, new Runnable()
        {
            @SuppressWarnings("deprecation")
            public void run()
            {
                for(WaterDataHolder holder : waterBlocksAroundSponge)
                {
                    // System.out.println("2");
                    holder.getBlock().setType(holder.getType());
                    System.out.println(holder.getBlock().getData());
                    // if(holder.getType() == Material.STATIONARY_WATER)
                    // System.out.println("STAT");
                    // else if(holder.getType() == Material.WATER)
                    // System.out.println("FLOW");
                    holder.getBlock().setData(holder.getData());
                }
                event.getBlock().setType(Material.SPONGE);
            }
        });
    }
    
    private final static int radius = 6;
    
    @SuppressWarnings("deprecation")
    private static List<WaterDataHolder> getWaterBlocks(Block source)
    {
        final int x1 = source.getX() - radius, x2 = source.getX() + radius;
        
        final List<WaterDataHolder> blocks = new ArrayList<WaterDataHolder>();
        
        for(int i = x1; i <= x2; i++)
        {
            final int distance = Math.abs(source.getX() - i);
            final int squareRadius = radius - distance;
            
            final int y1 = source.getY() - squareRadius, y2 = source.getY() + squareRadius;
            final int z1 = source.getZ() - squareRadius, z2 = source.getZ() + squareRadius;
            final int halfPoint = (y2 + y1) / 2;
            
            int jc = 0;
            for(int j = y1; j <= y2; j++)
            {
                for(int k = z1; k <= z2; k++)
                {
                    if(Math.abs(source.getZ() - k) > jc)
                        continue;
                    
                    Block target = source.getWorld().getBlockAt(i, j, k);
                    if(isBlockWater(target))
                        blocks.add(new WaterDataHolder(target.getType(), target.getData(), target));
                    
                }
                if(j < halfPoint)
                    jc++;
                else
                    jc--;
            }
        }
        
        return blocks;
    }
    
    private static class WaterDataHolder
    {
        private Material type;
        private byte data;
        private Block block;
        
        public WaterDataHolder(Material type, byte data, Block block)
        {
            this.type = type;
            this.data = data;
            this.block = block;
        }
        
        public Material getType()
        {
            return type;
        }
        
        public byte getData()
        {
            return data;
        }
        
        public Block getBlock()
        {
            return block;
        }
    }
    
    private static boolean isBlockWater(Block target)
    {
        return target.getType() == Material.WATER || target.getType() == Material.STATIONARY_WATER;
    }
    //
    // @EventHandler
    // public void blockFromTo(BlockFromToEvent event)
    // {
    // System.out.println(event.getToBlock().getType().toString());
    // event.setCancelled(true);
    // }
}
