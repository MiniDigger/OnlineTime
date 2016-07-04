package me.MiniDigger.OnlineTime.OnlineTime;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by Martin on 15.06.2016.
 */
public class OnlineTime extends JavaPlugin implements Listener {

    private static OnlineTime INSTANCE;

    private List<Reward> rewards = new ArrayList<>();
    private Map<UUID, List<String>> rewardMap = new HashMap<>();

    @Override
    public void onEnable() {
        INSTANCE = this;
        saveDefaultConfig();
        loadRewards();

        getServer().getPluginManager().registerEvents( this, this );

        long p = Reward.parseTime( getConfig().getString( "check-period", "5M" ) );

        Bukkit.getScheduler().runTaskTimer( this, () -> Bukkit.getOnlinePlayers().forEach( OnlineTime.this::checkForReward ), p, p );

        if ( Bukkit.getOnlinePlayers().size() != 0 ) {
            getLogger().warning( "Reload detected. Reloading the data for all online players!" );
            Bukkit.getScheduler().runTaskAsynchronously( this, () -> Bukkit.getOnlinePlayers().forEach( player -> loadFile( player.getUniqueId() ) ) );
        }
    }

    @Override
    public void onDisable() {
        INSTANCE = null;
        rewards = null;
        rewardMap = null;
    }

    @EventHandler
    public void onJoin( PlayerJoinEvent event ) {
        Bukkit.getScheduler().runTaskAsynchronously( this, () -> {
            loadFile( event.getPlayer().getUniqueId() );
            checkForReward( event.getPlayer() );
        } );
    }

    @EventHandler
    public void onLeave( PlayerQuitEvent event ) {
        rewardMap.remove( event.getPlayer().getUniqueId() );
    }

    @Override
    public boolean onCommand( CommandSender sender, Command command, String label, String[] args ) {
        if ( command.getName().equalsIgnoreCase( "onlinetime" ) ) {
            if ( args.length == 0 ) {
                if ( !sender.hasPermission( "onlinetime.self" ) ) {
                    sender.sendMessage( ChatColor.RED + "You don't have permission to check your online time!" );
                    return true;
                }

                if ( !( sender instanceof Player ) ) {
                    sender.sendMessage( ChatColor.RED + "You can't get the playtime of the console..." );
                    sender.sendMessage( "Try adding a name after the command ;)" );
                    return true;
                }

                printOnlineTime( (Player) sender, sender );
                return true;
            } else {
                if ( !sender.hasPermission( "onlinetime.other" ) ) {
                    sender.sendMessage( ChatColor.RED + "You don't have permission to check your the online time of " + args[0] + "!" );
                    return true;
                }

                Player player = Bukkit.getPlayer( args[0] );
                if ( player == null ) {
                    sender.sendMessage( ChatColor.RED + "That player is offline. " );
                    return true;
                }

                printOnlineTime( player, sender );
                return true;
            }
        } else if ( command.getName().equalsIgnoreCase( "onlinetimerewards" ) ) {
            if ( args.length == 0 ) {
                if ( !sender.hasPermission( "onlinetimerewards.self" ) ) {
                    sender.sendMessage( ChatColor.RED + "You don't have permission to check your online time rewards!" );
                    return true;
                }

                if ( !( sender instanceof Player ) ) {
                    sender.sendMessage( ChatColor.RED + "You can't get the playtime of the console..." );
                    sender.sendMessage( "Try adding a name after the command ;)" );
                    return true;
                }

                printOnlineTimeRewards( (Player) sender, sender );
                return true;
            } else {
                if ( !sender.hasPermission( "onlinetimerewards.other" ) ) {
                    sender.sendMessage( ChatColor.RED + "You don't have permission to check your the online time rewards of " + args[0] + "!" );
                    return true;
                }

                Player player = Bukkit.getPlayer( args[0] );
                if ( player == null ) {
                    sender.sendMessage( ChatColor.RED + "That player is offline. " );
                    return true;
                }

                printOnlineTimeRewards( player, sender );
                return true;
            }
        }

        return false;
    }

    private void loadFile( UUID id ) {
        File folder = new File( getDataFolder(), "data" );
        if ( !folder.exists() ) {
            if ( !folder.mkdir() ) {
                getLogger().warning( "Can't create data folder! Rewards can't be saved!" );
                return;
            }
        }
        File file = new File( folder, id.toString() );
        if ( !file.exists() ) {
            try {
                if ( !file.createNewFile() ) {
                    getLogger().warning( "Can't create data file for uuid " + id + "! Can't save/load rewards!" );
                    return;
                }
            } catch ( IOException e ) {
                getLogger().warning( "Can't create data file for uuid " + id + "! Can't save/load rewards!" );
                e.printStackTrace();
            }
        }

        try ( Scanner sc = new Scanner( new FileInputStream( file ) ) ) {
            List<String> rewards = new ArrayList<>();
            while ( sc.hasNext() ) {
                rewards.add( sc.next() );
            }
            rewardMap.put( id, rewards );
        } catch ( IOException e ) {
            getLogger().warning( "Can't read data file for uuid " + id + "! Can't load rewards!" );
            e.printStackTrace();
        }
    }

    private void saveFile( UUID id ) {
        File folder = new File( getDataFolder(), "data" );
        File file = new File( folder, id.toString() );
        if ( !folder.exists() || !file.exists() ) {
            // we printed about the error before, no need to be spammy
            return;
        }

        try ( PrintWriter writer = new PrintWriter( new FileOutputStream( file ) ) ) {
            rewardMap.get( id ).forEach( writer::println );
        } catch ( IOException e ) {
            getLogger().warning( "Can't write data file for uuid " + id + "! Can't save rewards!" );
            e.printStackTrace();
        }
    }

    private void loadRewards() {
        if ( !getConfig().isConfigurationSection( "rewards" ) ) {
            getLogger().warning( "Could not find rewards in config!" );
            return;
        }

        ConfigurationSection section = getConfig().getConfigurationSection( "rewards" );
        for ( String key : section.getKeys( false ) ) {
            String message = section.getString( key + ".message" );
            String time = section.getString( key + ".time" );
            List<String> commands = section.getStringList( key + ".commands" );

            if ( time == null ) {
                getLogger().warning( "Could not get time for reward " + key + ". Skipping!" );
                continue;
            }

            rewards.add( new Reward( key, message, time, commands ) );
        }

        getLogger().info( "Loaded " + rewards.size() + " rewards!" );
    }


    private void checkForReward( Player player ) {
        long t = (long) ( player.getStatistic( Statistic.PLAY_ONE_TICK ) * 0.05 * 1000 );

        List<String> playerRewards = rewardMap.get( player.getUniqueId() );
        int oldSize = playerRewards.size();//can't have a flag because it needs to be final
        rewards.stream().filter( reward -> reward.getTime() <= t ).filter( reward -> !playerRewards.contains( reward.getTag() ) ).forEach( reward -> {
            reward.apply( player );
            playerRewards.add( reward.getTag() );
        } );

        if ( oldSize != playerRewards.size() ) {
            rewardMap.put( player.getUniqueId(), playerRewards );
            saveFile( player.getUniqueId() );
        }
    }

    private void printOnlineTime( Player player, CommandSender sender ) {
        long t = (long) ( player.getStatistic( Statistic.PLAY_ONE_TICK ) * 0.05 * 1000 );
        long[] time = formatDuration( t );
        String message = formatMessage( getConfig().getString( "message", "The player %p% has played on the server for %d% days %h% hours %m% mintues and %s% seconds." ), time, player );
        sender.sendMessage( message );
    }

    private void printOnlineTimeRewards( Player player, CommandSender sender ) {
        List<String> rewards = rewardMap.get( player.getUniqueId() );
        StringBuilder sb = new StringBuilder();
        rewards.forEach( reward -> {
            sb.append( reward );
            sb.append( " " );
        } );
        sender.sendMessage( "The player " + player.getDisplayName() + " has these been given these rewards: " + sb.toString() );
    }

    private String formatMessage( String message, long[] time, Player player ) {
        return message.replace( "%d%", time[0] + "" ).replace( "%h%", time[1] + "" ).replace( "%m%", time[2] + "" ).replace( "%s%", time[3] + "" ).replace( "%p%", player.getDisplayName() );
    }

    private long[] formatDuration( long millis ) {
        long days = TimeUnit.MILLISECONDS.toDays( millis );
        millis -= TimeUnit.DAYS.toMillis( days );
        long hours = TimeUnit.MILLISECONDS.toHours( millis );
        millis -= TimeUnit.HOURS.toMillis( hours );
        long minutes = TimeUnit.MILLISECONDS.toMinutes( millis );
        millis -= TimeUnit.MINUTES.toMillis( minutes );
        long seconds = TimeUnit.MILLISECONDS.toSeconds( millis );

        return new long[]{ days, hours, minutes, seconds };
    }

    public static OnlineTime getInstance() {
        return INSTANCE;
    }
}
