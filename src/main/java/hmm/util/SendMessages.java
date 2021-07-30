package hmm.util;

import java.util.List;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.world.World;

public class SendMessages {
    
    /**
     * Sends a message to all players of a world
     * @param world The world
     * @param message The message
     * @param actionBar If the message should be to the chat or action bar (above inventory)
     */
    public static void sendMessageToAll(World world, Text message, boolean actionBar)
    {
        List<? extends PlayerEntity> players = world.getPlayers();
        
        for (PlayerEntity player : players) {
            player.sendMessage(message, actionBar);
        }
    }

    /**
     * Sends a message to the chat of all players of a world
     * @param world The world
     * @param message The message
     */
    public static void sendMessageToAll(World world, Text message)
    {
        sendMessageToAll(world, message, false);
    }

	/**
     * Sends a message to all players of a world
     * @param world The world
     * @param message The message
     * @param actionBar If the message should be to the chat or action bar (above inventory)
     */
    public static void sendMessageToAll(World world, String message, boolean actionBar)
    {
        List<? extends PlayerEntity> players = world.getPlayers();
        
        for (PlayerEntity player : players) {
            player.sendMessage(
                new LiteralText(
                    message
                )
                , actionBar
            );
        }
    }

    public static void sendMessageToAll(World world, String message)
    {
        sendMessageToAll(world, message, false);
    }
}
