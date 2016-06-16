package me.MiniDigger.OnlineTime;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.TimeUnit;

/**
 * Created by Martin on 15.06.2016.
 */
public class OnlineTime extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();
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
        }

        return false;
    }

    private void printOnlineTime( Player player, CommandSender sender ) {
        int ticks = player.getStatistic( Statistic.PLAY_ONE_TICK );
        long[] time = formatDuration( (long) ( ticks * 0.05 * 1000 ) );
        String message = formatMessage( getConfig().getString( "message", "The player %p% has played on the server for %d% days %h% hours %m% mintues and %s% seconds." ), time, player );
        sender.sendMessage( message );
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
}
