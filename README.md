# OnlineTime [![Build Status](http://bender.minidigger.me:9090/job/OnlineTime/badge/icon)](http://bender.minidigger.me:9090/job/OnlineTime/)

This is a simple spigot plugin to display the time a player spend on your server.
It uses the inbuild stats system so it will work right away and will not start at 0!

Download the latest dev build [here](http://bender.minidigger.me:9090/job/OnlineTime/lastSuccessfulBuild/artifact/target/OnlineTime.jar). 

USE AT YOUR OWN RISK!

# Command

/onlinetime: Displays your online time<br>
/onlinetime < player>: Displays the online time of < player>. < player> needs to be online!

/onlinetimerewards: Displays the online time rewards you have gained<br>
/onlinetimerewards < player>: Displays the online time rewards < player> has gained. < player> needs to be online!

# Permissions

/onlinetime: onlinetime.self<br>
/onlinetime < player>: onlinetime.other

/onlinetimerewards: onlinetimerewards.self<br>
/onlinetimerewards < player>: onlinetimerewards.other

# Aliases
You can use the following alises:
/playtime, /onlinetime, /timeplayed

You can easily add your own alises by editing the plguin.yml in the plugins jar file. You can just open it in WinRAR or something similar.

#Rewards
You can define rewards players should get for playing x amount of time on your server.
Just add a new section to the rewards list. It should look like that:
```
rewards:
  test-reward:    // the name
     message: "Wow, you have managed to play 1 hour on this server. Take this as a gift!"   // the message the player should get when getting that reward
     time: "1H" // the time he needs to have played, for info below
     commands: // the commands that get executed %p% gets replaced with the players name.
       - "say reward given"
       - "give %p% DIAMOND_AXE"
```

Time:<br>
1D = 1 Day<br>
1H = 1 Hour<br>
1M = 1 Minute<br>
1S = 1 Second<br>
1D2H3M4S = 1 Day + 2 Hours + 3 Minutes + 4 Seconds. 

# Usefull Links:
CI: http://bender.minidigger.me:9090/job/OnlineTime/<br>
GitHub: https://github.com/MiniDigger/OnlineTime<br>
Spigot: https://www.spigotmc.org/resources/onlinetime.24998/
