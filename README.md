Forestry
====================================

### What is it?

Forestry is a modification for the game Minecraft and known primarily for its farms and bees. 

### Homepage

[Wiki](http://forestry.sengir.net/)

[Latest Builds for 1.7.10](http://jenkins.ic2.player.to/job/Forestry_Dev/)

[IRC #forestry on esper.net](http://webchat.esper.net/?nick=ForestryGithub...&channels=forestry&prompt=1)

For those interested in developement, you can also visit #forestry-dev.

### Building

The API and localization files reside in their own repository. It is pulled in automatically.

You may want to adjust the "./gradle.properties" file to set your mc account as needed.

To setup, open a command line and run "gradlew setupDecompWorkspace" and "gradlew eclipse".

To package, open a command line and type "gradlew release".

### Notes

Beware of ugly code.

Bugfixes are generally welcome. If you want to contribute something which changes game mechanics, please talk to someone with commit privileges first. Nothing is more frustrating than putting a lot of work and effort into a new game mechanic and then having the PR rejected because it doesn’t fit gameplay-wise.

**If you submit a PR you must accept the [Contributor License Agreement](https://cla-assistant.io/ForestryMC/ForestryMC). There is no way around that, since otherwise changing the license later - even to something more permissive! - , becomes close to impossible.**

### License & Copyright

Forestry is (c) 2011 - 2014 SirSengir and licensed under LGPL v3. See the LICENSE.txt for all the gory details or go to http://www.gnu.org/licenses/lgpl-3.0.txt for more information. Forestry also contains code contributed by CovertJaguar, Player, MysteriousAges, Binnie, RichardG, cpw and others.
