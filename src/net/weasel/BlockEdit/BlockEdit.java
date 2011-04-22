package net.weasel.BlockEdit;

import java.io.File;
import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.java.JavaPlugin;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import org.bukkit.plugin.Plugin;

public class BlockEdit extends JavaPlugin
{
	public static Block targetBlock = null;
	public static Integer currentBlockType = 0;
	public static Integer valueToSet = 0;
	public static ArrayList<Player> editPlayers = new ArrayList<Player>();
	public static ArrayList<Integer> editBlockTypes = new ArrayList<Integer>();
	public static ArrayList<Integer> editDataValues = new ArrayList<Integer>();
	public static PermissionHandler Permissions;
	public static String pluginName = "";
	public static String pluginVersion = "";
	
	@Override
	public void onDisable() 
	{
		logOutput( pluginName + " v" + pluginVersion + " disabled." );
	}

	@Override
	public void onEnable() 
	{
		pluginName = this.getDescription().getName();
		pluginVersion = this.getDescription().getVersion();
		
		setupPermissions();
		
		Listener listener = new Listener(this);
		getServer().getPluginManager().registerEvent(Event.Type.PLAYER_INTERACT, listener, Event.Priority.Monitor, this);
		getServer().getPluginManager().registerEvent(Event.Type.PLAYER_ANIMATION, listener, Event.Priority.Monitor, this);
		
		if( new File("plugins/BlockEdit/").exists() == false )
		{
			logOutput( "Settings folder does not exist - creating it.. ");
			
			boolean chk = new File("plugins/BlockEdit/").mkdir();
			
			if( chk )
				logOutput( "Successfully created folder." );
			else
				logOutput( "Unable to create folder!" );
		}	
		
		logOutput( pluginName + " v" + pluginVersion + " enabled." );
	}
	
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) 
    {
    	Player player = (Player) sender;
    	String pCommand = command.getName().toLowerCase();
    	String contents = arrayToString(args, " ").trim();
    	boolean retVal = false;
    	
    	if( pCommand.equals( "bset" ) || pCommand.equals( "blockset" ) )
    	{
    		if( isAllowedSet(player) == true )
    		{
				if( contents == "" )
	    		{
	    			retVal = false;
	    		}
	    		
	    		else
	    		{
	    			if( args.length < 1 )
	    			{
	    				retVal = false;
	    			}
	    			else if( args.length < 2 )
	    			{
	  					setBlockType( player, Integer.parseInt(args[0]) );
	  					setBlockData( player, -1 );
	    				player.sendMessage( ChatColor.BLUE + "Default block type set to " 
								  + ChatColor.YELLOW + getBlockName( Integer.parseInt(args[0]) ) 
								  + ChatColor.WHITE );
	    				retVal = true;
	    			}
	    			else
	    			{
	  					setBlockType( player, Integer.parseInt(args[0]) );
	  					setBlockData( player, Integer.parseInt(args[1]) );
	    				player.sendMessage( ChatColor.BLUE + "Default block type set to " 
	    								  + ChatColor.YELLOW + getBlockName( Integer.parseInt(args[0]) ) 
	    								  + ChatColor.WHITE );
	    				player.sendMessage( ChatColor.BLUE + "Default block data set to " 
	    							      + ChatColor.YELLOW + Integer.parseInt(args[1]) 
	    							      + ChatColor.WHITE );
	    				retVal = true;
	    			}
	    		}
    		}
    		else
    		{
    			player.sendMessage( "Unknown console command. Type \"help\" for help." );
    			retVal = true;
    		}
    	}
    	
    	if( pCommand.equals( "btype" ) || pCommand.equals( "blocktype" ) )
    	{
    		if( isAllowedSet(player) == true )
    		{
				if( contents == "" )
	    		{
	    			retVal = false;
	    		}
	    		
	    		else
	    		{
	    			if( args.length == 1 )
	    			{
						setBlockType( player, Integer.parseInt(args[0]) );
						setBlockData( player, -1 );

						player.sendMessage( ChatColor.BLUE + "Default block type set to " 
								  + ChatColor.YELLOW + getBlockName( Integer.parseInt(args[0]) ) 
								  + ChatColor.WHITE );
	    			}
	    			else if( args.length == 2 )
	    			{
	    				if( isNumeric(args[0]) && isNumeric(args[1]) )
	    				{
	    					setBlockType( player, Integer.parseInt(args[0]) );
	    					setBlockData( player, Integer.parseInt(args[1]) );

	    					player.sendMessage( ChatColor.BLUE + "Default block type set to " 
	  							  + ChatColor.YELLOW + getBlockName( Integer.parseInt(args[0]) ) 
	  							  + ChatColor.WHITE );
	    					player.sendMessage( ChatColor.BLUE + "Default block data set to " 
	  							  + ChatColor.YELLOW + getBlockName( Integer.parseInt(args[1]) ) 
	  							  + ChatColor.WHITE );
	    				}
	    			}

	    			retVal = true;
	    		}
    		}
    		else
    		{
    			player.sendMessage( "Unknown console command. Type \"help\" for help." );
    			retVal = true;
    		}
    	}
    	
    	if( pCommand.equals( "bdata" ) || pCommand.equals( "blockdata" ) )
    	{
    		if( isAllowedSet(player) == true )
    		{
				if( contents == "" )
	    		{
	    			retVal = false;
	    		}
	    		
	    		else
	    		{
					setBlockData( player, Integer.parseInt(args[0]) );
					setBlockType( player, -1 );
					player.sendMessage( ChatColor.BLUE + "Default block data set to " 
							  + ChatColor.YELLOW + Integer.parseInt(args[0] ) 
							  + ChatColor.WHITE );
	   				retVal = true;
	    		}
    		}
    		else
    		{
    			player.sendMessage( "Unknown console command. Type \"help\" for help." );
    			retVal = true;
    		}
    	}

    	if( pCommand.equals( "bget" ) || pCommand.equals( "blockget" ) )
    	{
    		if( isAllowedSet(player) == true )
    		{
	    		int X = getPlayerIndex(player);
	    		
	    		if( X == -1 )
	    		{
	    			player.sendMessage( "You have no block type or data set." );
	    			retVal = true;
	    		}
	    		else
	    		{
	    			player.sendMessage( ChatColor.BLUE + "Current block type: " 
	    					+ ChatColor.YELLOW + getBlockName(editBlockTypes.get(X)) 
	    					+ ChatColor.WHITE );
	
	    			player.sendMessage( ChatColor.BLUE + "Current block data: " 
	    					+ ChatColor.YELLOW + editDataValues.get(X) + ChatColor.WHITE );
	    		}
    		}
    		else
    		{
    			player.sendMessage( "Unknown console command. Type \"help\" for help." );
    			retVal = true;
    		}
    	}
    	
    	return retVal;
    }	
	
	public static boolean isAllowedUse( Player player )
	{
		if( Permissions.has(player, "blockedit.use")) 
		    return true;
		else
			return false;
	}
	
	public static boolean isAllowedSet( Player player )
	{
		if( Permissions.has(player, "blockedit.set")) 
		    return true;
		else
			return false;
	}
	
	private void setupPermissions() 
	{
		Plugin test = this.getServer().getPluginManager().getPlugin("Permissions");

	    if (BlockEdit.Permissions == null) 
	    {
	    	if (test != null) 
	    	{
	    		BlockEdit.Permissions = ((Permissions)test).getHandler();
	    	}
	    	else 
	    	{
	    		logOutput("Permission system not detected, defaulting to OP");
	        }
	    }
	}

	public static String arrayToString(String[] a, String separator) 
    {
        String result = "";
        
        if (a.length > 0) 
        {
            result = a[0];    // start with the first element
            for (int i=1; i<a.length; i++) {
                result = result + separator + a[i];
            }
        }
        
        return result;
    }
    
    public static Integer getPlayerIndex(Player who)
    {
    	Integer retVal = -1;
    	
    	if( editPlayers.size() > 0 )
    	{
    		for( int X = 0; X < editPlayers.size(); X++ )
    		{
    			if( editPlayers.get(X) == who ) retVal = X;
    		}
    	}
    	
    	return retVal;
    }
    
    public static void setBlockType( Player who, Integer blockID )
    {
    	int X = getPlayerIndex(who);
    	
    	if( X == -1 ) 
    	{
    		editPlayers.add( who );
    		editBlockTypes.add( blockID );
    		editDataValues.add( -1 );
    	}
    	else

    	{
    		editBlockTypes.set( X, blockID );
    	}
    }

    public static void setBlockData( Player who, Integer value )
    {
    	int X = getPlayerIndex(who);
    	
    	if( X == -1 ) 
    	{
    		editPlayers.add( who );
        	editBlockTypes.add( -1 );
        	editDataValues.add( value );
    	}
    	else
    	{
    		editDataValues.set( X, value );
    	}
    }
    
    public static String getBlockName( Integer blockID )
    {
    	String retVal = "NOT FOUND";
    	
    	String bSplit = "Air;Stone;Grass;Dirt;Cobblestone;" // 0-4
    				  + "Wooden Planks;Sapling;Adminium;Water;Stationary Water;" // 5-9 
    				  + "Lava;Stationary Lava;Sand;Gravel;Gold Ore;" // 10-14
    				  + "Iron Ore;Coal Ore;Wood;Leaves;Sponge;" // 15-19
    				  + "Glass;Lapis Ore;Lapis Block;Dispenser;Sandstone;" // 20-24
    				  + "Note Block;Bed;0;0;0;" // 25-29
    				  + "0;0;0;0;0;" // 30-34
    				  + "Wool;0;Yellow Flower;Red Rose;Brown Mushroom;" // 35-39
    				  + "Red Mushroom;Gold Block;Iron Block;Double Slab;Slab;" // 40-44
    				  + "Brick Block;TNT;Bookshelf;Moss Stone;Obsidian;" // 45-49
    				  + "Torch;Fire;Mob Spawner;Wooden Stairs;Chest;" // 50-54
    				  + "Redstone Wire Block;Diamond Ore;Diamond Block;Crafting Table;Crops;" // 55-59
    				  + "Farmland;Furnace;Burning Furnace;Sign Post;Wooden Door;" // 60-64
    				  + "Ladder;Rails;Stone Stairs;Wall Sign;Lever;" // 65-69
    				  + "Stone Pressure Plate;Iron Door;Wooden Pressure Plate;"
    				     + "Redstone Ore;Redstone Ore (lit);" // 70-74
    				  + "Redstone Torch (off);Redstone Torch (on);Stone Button;Snow;Ice;" // 75-79
    				  + "Snow Block;Cactus Block;Clay Block;Sugar Cane;Jukebox;" // 80-84
    				  + "Fence;Pumpkin;Netherrack;Soul Sand;Glowstone Block;" // 85-89
    				  + "Portal;Jack-o-Lantern;Cake Block;Diode (off);Diode (on)"; // 90-94
    	
    	String[] blockNames = bSplit.split(";");
    	
    	if( blockID < blockNames.length ) retVal = blockNames[blockID];
    	
    	return retVal;
    }

	public static void logOutput(String output)
	{
		System.out.println( "[BlockEdit] " + output );
	}
	
	public static boolean isNumeric( String text )
	{
		return text.matches( "[-+]?\\d+(\\.\\d+)?" );
	}
}