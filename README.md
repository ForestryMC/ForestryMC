[Forestry](https://minecraft.curseforge.com/projects/forestry/files)
====================================

### What is it?

Forestry is a modification for the game Minecraft and known primarily for its farms and bees. 

### Homepage

[Wiki](http://forestry.sengir.net/)

[Latest Builds for 1.11.2/1.12](http://jenkins.ic2.player.to/job/Forestry_1.11/)

[![Discord](https://img.shields.io/discord/417745379258400778.svg?colorB=7289DA&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAHYAAABWAgMAAABnZYq0AAAACVBMVEUAAB38%2FPz%2F%2F%2F%2Bm8P%2F9AAAAAXRSTlMAQObYZgAAAAFiS0dEAIgFHUgAAAAJcEhZcwAACxMAAAsTAQCanBgAAAAHdElNRQfhBxwQJhxy2iqrAAABoElEQVRIx7WWzdGEIAyGgcMeKMESrMJ6rILZCiiBg4eYKr%2Fd1ZAfgXFm98sJfAyGNwno3G9sLucgYGpQ4OGVRxQTREMDZjF7ILSWjoiHo1n%2BE03Aw8p7CNY5IhkYd%2F%2F6MtO3f8BNhR1QWnarCH4tr6myl0cWgUVNcfMcXACP1hKrGMt8wcAyxide7Ymcgqale7hN6846uJCkQxw6GG7h2MH4Czz3cLqD1zHu0VOXMfZjHLoYvsdd0Q7ZvsOkafJ1P4QXxrWFd14wMc60h8JKCbyQvImzlFjyGoZTKzohwWR2UzSONHhYXBQOaKKsySsahwGGDnb%2FiYPJw22sCqzirSULYy1qtHhXGbtgrM0oagBV4XiTJok3GoLoDNH8ooTmBm7ZMsbpFzi2bgPGoXWXME6XT%2BRJ4GLddxJ4PpQy7tmfoU2HPN6cKg%2BledKHBKlF8oNSt5w5g5o8eXhu1IOlpl5kGerDxIVT%2BztzKepulD8utXqpChamkzzuo7xYGk%2FkpSYuviLXun5bzdRf0Krejzqyz7Z3p0I1v2d6HmA07dofmS48njAiuMgAAAAASUVORK5CYII%3D)](https://discord.gg/49XNRJk)

For those interested in developement, you can also visit #forestry-dev.

### Building

The localization files reside in their own repository. It is pulled in automatically.

You may want to adjust the "./gradle.properties" file to set your mc account as needed.

To setup, open a command line and run "gradlew setupDecompWorkspace". Then run the following depending on your IDE:

Eclipse: "gradlew eclipse"
IntelliJ IDEA: "gradlew idea genIntellijRuns"

To package, open a command line and type "gradlew build".

For mod authors: the Forestry maven is located at http://maven.ic2.player.to/net/sengir/forestry/

### Notes

Beware of ugly code.

Bugfixes are generally welcome. If you want to contribute something which changes game mechanics, please talk to someone with commit privileges first. Nothing is more frustrating than putting a lot of work and effort into a new game mechanic and then having the PR rejected because it doesn’t fit gameplay-wise.

**If you submit a PR you must accept the [Contributor License Agreement](https://cla-assistant.io/ForestryMC/ForestryMC). There is no way around that, since otherwise changing the license later - even to something more permissive! - , becomes close to impossible.**

### License & Copyright

Forestry is (c) 2011 - 2014 SirSengir and licensed under LGPL v3. See the LICENSE.txt for all the gory details or go to http://www.gnu.org/licenses/lgpl-3.0.txt for more information. Forestry also contains code contributed by CovertJaguar, Player, MysteriousAges, Binnie, RichardG, cpw and others.
