package net.weasel.BlockEdit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.plugin.Plugin;

public class Listener extends PlayerListener
{
	public Listener(BlockEdit instance)
	{
		@SuppressWarnings("unused")
		Plugin plugin = instance;
	}
	
	public void onPlayerAnimation( PlayerAnimationEvent e )
	{
		Player player = e.getPlayer();

		if( BlockEdit.isAllowedUse(player) == false ) return;
		
		Block targetBlock = player.getTargetBlock(null, 100);
		int heldItem = player.getItemInHand().getTypeId();
		Integer P = BlockEdit.getPlayerIndex(player);
		
		if( P == -1 ) return;
			
		if( heldItem == 262 )
		{
			Integer T = BlockEdit.editBlockTypes.get(P);
			
			if( T != -1 ) targetBlock.setTypeId(T);
			int  D = BlockEdit.editDataValues.get(P);
			if( D != -1 ) targetBlock.setData( (byte)D );
		}
	}
	
	public void onPlayerInteract( PlayerInteractEvent e )
	{
		Player player = e.getPlayer();

		if( BlockEdit.isAllowedUse(player) == false ) return;

		int heldItem = player.getItemInHand().getTypeId();
		
		if( heldItem == 262 )
		{
			Block targetBlock = player.getTargetBlock(null, 100);
			Integer X = targetBlock.getX();
			Integer Y = targetBlock.getY();
			Integer Z = targetBlock.getZ();
			
			player.sendMessage( ChatColor.GREEN + "Block @ " 
					+ ChatColor.YELLOW + X + ChatColor.WHITE + "," 
					+ ChatColor.YELLOW + Y + ChatColor.WHITE + "," 
					+ ChatColor.YELLOW + Z + ChatColor.WHITE + ": ID=" 
					+ ChatColor.YELLOW + targetBlock.getTypeId() + " " 
					+ ChatColor.AQUA + "(" + BlockEdit.getBlockName(targetBlock.getTypeId()) + ")"
					+ ChatColor.GREEN + ":" );
			
			player.sendMessage( ChatColor.BLUE + " * Chunk: " + ChatColor.YELLOW + targetBlock.getChunk().getX() + ChatColor.WHITE + "," + ChatColor.YELLOW + targetBlock.getChunk().getZ() + ChatColor.WHITE + "." );
			player.sendMessage( ChatColor.BLUE + " * Biome: " + ChatColor.YELLOW + targetBlock.getBiome().name());
			player.sendMessage( ChatColor.BLUE + " * Data : " + ChatColor.YELLOW + targetBlock.getData() + ChatColor.WHITE );
		}
	}
}
