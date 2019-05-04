# MusicCore

#### About MusicCore
MusicCore is written in Java 8 and using the [JDA](https://github.com/DV8FromTheWorld/JDA) (Java Discord API). It plays requested songs 
from YouTube on your Discord server. This bot also features a permission system, which you can edit in the created config.properties file when starting the bot for the first time.

## Getting started

#### Configuration
First of all, get the [latest release](https://github.com/Winterfuchs/MusicCore/releases) version and extract it in your choosen directory.
You should also have Java 8 or later installed. When finished, just start the bot. When starting for the first time, a config.properties
file will appear and the bot will shutdown. You need to edit the file first before you restart the bot again.

But don't worry, there is not much to do!

Create an application at the [Discord Developers](https://discordapp.com/developers/applications/) site.
You should now have access to your token.

Go to your config.properties file which should be in the root directory of your application/bot.
Insert the token in the configue.properties file at ```token=your_token```.
Basically you're ready to go but you should also set ```roles=your_roles``` to allow certain roles to command the bot.
If you wish to have more than one role, just seperate the roles with a comma.

Well, you're finsihed!

## Commands

#### Explaining the commands

Use ```"-"``` as prefix for all commands.

* ```"-m p [YouTube Link] or [Genre/Playlist]"``` plays a song, genre or playlist
* ```"-m stop"``` stops the current track/playlist
* ```"-m queue"``` shows the current playlist with all songs
* ```"-m now"``` shows the current song with title and length
* ```"-m skip"```skips to the next song in the queue
* ```"-m shuffle``` shuffle all the songs in the queue
* ```"-m vol"``` sets the music volume
* ```"-help"``` shows a list of all commands
* ```"-ping"``` pong!

Make sure you're in a voice channel when requesting a song.

## Next features

* Better role-permission system (edit commands for individual roles)
* Voice channel permissions
* ...

If you have any request which features should come next, just let me know!
