package hmm.util;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.ListIterator;

import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.util.math.ChunkPos;

public class ChunkHelper
{
    /**
     * "Renamed" UNKNOWN chunk ticket
     */
    public static final ChunkTicketType<ChunkPos> PEARL_CHUNK_LOADER_TICKET = ChunkTicketType.create("hmm_pearl_chunk_loader", Comparator.comparingLong(ChunkPos::toLong), 30);
    
    protected static LinkedList<ChunkForceLoadingEntry> ForceLoadedChunks = new LinkedList<ChunkForceLoadingEntry>();
    
    /**
     * Force loads a chunk for a specified duration in ticks (converts position into a chunk coord)
     * @param blockX X axis location of a block
     * @param blockZ Z axis location of a block
     * @param tickDuration How long to keep the chunk loaded for
     * @param chunkManager The server-side chunk manager to modify
     */
    public static void ForceLoadChunk(int blockX, int blockZ, int tickDuration, ServerChunkManager chunkManager)
    {
        ChunkHelper.ForceLoadChunk(new ChunkPos(blockX / 16, blockZ / 16), tickDuration, chunkManager);
    }

    /**
     * Force loads a chunk for a specified duration in ticks
     * @param chunkPos The chunk
     * @param tickDuration The time to load it for
     * @param chunkManager The server-side chunk manager to modify
     */
    public static void ForceLoadChunk(ChunkPos chunkPos, int tickDuration, ServerChunkManager chunkManager)
    {
        // Force-load the chunk
        // chunkManager.getChunk(chunkPos.x, chunkPos.z, ChunkStatus.FULL, true);
        // System.out.println("Set chunk force loaded: x:" + chunkPos.x + " z: " + chunkPos.z);
        
        // Java ugly mutex
        synchronized (ForceLoadedChunks)
        {
            // Add to the list

            // Check if it already exists
            ListIterator<ChunkForceLoadingEntry> i = ForceLoadedChunks.listIterator();

            int id = ChunkForceLoadingEntry.GenerateGlobalID(chunkManager, chunkPos);
            
            while (i.hasNext())
            {
                // Get the current entry
                ChunkForceLoadingEntry entry = i.next();
                
                // If the chunk is already tracked, modify it instead
                if (entry.globalID == id)
                {
                    // System.out.println("Updating existing: x:" + chunkPos.x + " z: " + chunkPos.z);
                    // If the time left is less than our duration, set it to what we wanted
                    if (entry.timeLeft < tickDuration)
                    {
                        entry.timeLeft = tickDuration;
                    }
                    
                    // Exit, we're done here
                    return;
                }
            }

            // System.out.println("Adding chunk ticket");
            chunkManager.addTicket(ChunkTicketType.FORCED, chunkPos, 4, chunkPos);
            // System.out.println("Adding new entry x:" + chunkPos.x + " z: " + chunkPos.z);
            ForceLoadedChunks.add(new ChunkForceLoadingEntry(chunkManager, chunkPos, tickDuration, id));
        }
    }

    public static void TickForceLoadedChunks()
    {
        synchronized(ForceLoadedChunks)
        {
            if (ForceLoadedChunks.size() == 0) return;
            
            for (int i = 0; i < ForceLoadedChunks.size(); i++)
            {
                ChunkForceLoadingEntry entry = ForceLoadedChunks.get(i);

                // If there's no time left, delete it
                // First, decrement the time left
                if (--entry.timeLeft <= 0)
                {
                    // System.out.println("Released chunk x:" + entry.pos.x + " z: " + entry.pos.z);

                    // Release the chunk
                    // System.out.println("Removing chunk ticket");
                    entry.manager.removeTicket(ChunkTicketType.FORCED, entry.pos, 4, entry.pos);

                    // Delete the entry
                    ForceLoadedChunks.remove(i--);
                }
            }
        }
    }
    
    protected static class ChunkForceLoadingEntry
    {
        public ChunkForceLoadingEntry(ServerChunkManager manager, ChunkPos pos, int timeLeft)
        {
            this(manager, pos, timeLeft, GenerateGlobalID(manager, pos));
        }
        
        public ChunkForceLoadingEntry(ServerChunkManager manager, ChunkPos pos, int timeLeft, int globalID)
        {
            this.manager = manager;
            this.pos = pos;
            this.timeLeft = timeLeft;
            this.globalID = globalID;
        }

        public static int GenerateGlobalID(ServerChunkManager manager, ChunkPos pos)
        {
            return (manager.getWorld().getRegistryKey().getValue().toString() + pos.x + ":" + pos.z).hashCode();
        }
        
        public ServerChunkManager manager;
        public ChunkPos pos;
        public int timeLeft;
        public int globalID;
    }
}
