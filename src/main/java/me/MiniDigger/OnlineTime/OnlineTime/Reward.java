package me.MiniDigger.OnlineTime.OnlineTime;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Martin on 04.07.2016.
 */
public class Reward {

    private String tag;
    private String message;
    private long time;
    private List<String> commands;

    public Reward( String tag, String message, String time, List<String> commands ) {
        this.tag = tag;
        this.message = message;
        this.commands = commands;

        if ( commands == null ) {
            this.commands = new ArrayList<>( 0 );
        }

        this.time = parseTime( time );
    }

    public long getTime() {
        return time;
    }

    public void apply( Player player ) {
        //lets sync back, just in case we are async...
        Bukkit.getScheduler().runTask( OnlineTime.getInstance(), () -> {
            if ( OnlineTime.getInstance().getConfig().isBoolean( "show-reward-notification" ) ) {
                OnlineTime.getInstance().getLogger().info( "Applying reward " + tag + " " );
            }

            for ( String command : commands ) {
                Bukkit.dispatchCommand( Bukkit.getConsoleSender(), formatCommand( command, player ) );
            }

            if ( message != null ) {
                player.sendMessage( ChatColor.translateAlternateColorCodes( '&', message ) );
            }
        } );
    }

    private String formatCommand( String command, Player player ) {
        command = command.replace( "%p%", player.getName() );

        return command;
    }

    public static long parseTime( String time ) {
        // fix duration to be iso conform
        if ( time.contains( "H" ) || time.contains( "M" ) || time.contains( "S" ) ) {
            if ( time.contains( "D" ) ) {
                time = "P" + time.replace( "D", "DT" );
            } else {
                time = "PT" + time;
            }
        } else {
            time = "P" + time;
        }
        try {
            return Duration.parse( time ).toMillis() / 1000 * 20;
        } catch ( Exception ex ) {
            OnlineTime.getInstance().getLogger().warning( "Could not parse " + time );
        }

        return -1;
    }

    public String getTag() {
        return tag;
    }
}
