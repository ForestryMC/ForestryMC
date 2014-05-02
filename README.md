Forestry
====================================

### What is it?

Forestry is a modification for the game Minecraft and known primarily for its farms and bees. 

### Homepage

[Wiki](http://forestry.sengir.net/)

[Forums](http://forestry.sengir.net/forum/)

[IRC #forestry on esper.net](http://webchat.esper.net/?nick=ForestryWiki...&channels=forestry&prompt=1)

### Building

The API and localization files reside in their own repository. It is pulled in automatically.

You need to put the Ic2 and CraftGuide APIs in "../IC2/src/" and "../CraftGuide/src" respectively. Hopefully that can be automated in the future.

You will need to define a "./gradle.properties" file.

Sample properties file:
```
mcversion=1.7.2
forgeversion=10.12.1.1061
version_major=2
version_minor=4
version_patch=0
version_revision=0
version_build=0
mcUsername=SirSengir
mcPassword=password
```

To setup, open a command line and run "gradlew setupDecompWorkspace" and "gradlew eclipse".

To package, open a command line and type "gradlew release".

### Notes

Beware of ugly code.

Bugfixes are generally welcome. If you want to contribute something which changes game mechanics, please talk to someone with commit privileges first. Nothing is more frustrating than putting a lot of work and effort into a new game mechanic and then having the PR rejected because it doesn’t fit gameplay-wise.

If you submit a PR you must accept the Contributor License Agreement. There is no way around that, since otherwise changing the license later - even to something more permissive! - , becomes close to impossible.

### License & Copyright

Forestry is (c) 2011 - 2014 SirSengir and licensed under CC BY-NC-ND 3.0. See the LICENSE.txt for all the gory details or go to http://creativecommons.org/licenses/by-nc-nd/3.0/ for more information. Forestry also contains code contributed by CovertJaguar, Player, MysteriousAges, Binnie, RichardG, cpw and others.
