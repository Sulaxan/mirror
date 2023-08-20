# mirror
A mirror in Minecraft: "reflects" players and their actions.

**This will only work for Minecraft (Spigot) 1.20.1!**

This is heavily inspired by the Mirrorverse
[<sup>[1]</sup>](https://wiki.hypixel.net/Mirrorverse) 
[<sup>[2]</sup>](https://hypixel-skyblock.fandom.com/wiki/Mirrorverse) 
on Hypixel Skyblock. There really isn't much purpose in this, other than being something cool I saw that I wanted to
recreate. Feel free to use the code for whatever.

[![Mirror Showcase](./examples/mirror.gif)](./examples/mirror.gif)
You may notice a few inconsistencies here, namely held blocks not appearing on the right hand and no sneaking. Keep in
mind this doesn't strive to be a perfect mirror implementation. See [Some Disclaimers](#some-disclaimers).

You can view the original video [here](./examples/mirror.mp4).

## Usage

You can compile using
```shell
# unix
gradlew build

# windows
gradlew.bat build
```
Add the compiled plugin to your Spigot 1.20.1 server, and restart.

### Commands

#### /mirror
This will create a mirror box around you. Standing on either side of the glass within the box will reflect you onto the
other side.

## Some Disclaimers

- This implementation heavily uses NMS to create NPCs and to mimic some packets sent by the client.
- This isn't a perfect "mirror": there are plenty of player actions that should be implemented for that to be the case.
  The current implementation implements the most basic actions required for the mirror effect.
- I do not plan to continue updating this, neither in terms of features nor Minecraft versions.
- There is no way to change the direction of the reflection. Currently, the mirror runs along the x-axis; additional
  changes will need to be made to mirror across other axes and/or between axes. Regardless of what you want to do, the
  concepts are the same (may require tweaking here and there).
- There are some variable inconsistencies. The main focus was to get something working and less on creating something
  production ready.
- There are some Java language feature inconsistencies. I mostly come from a Java 8 background (with a little bit of 
  experience in Java 11), so there are remnants of me trying out newer language features throughout the code (like the 
  "var" keyword and being able to name variables in instanceof statements). I realize there are other features I could
  take advantage of (like records), but I mostly stuck with what I'm familiar with.

## Useful Resources

Some stuff I stumbled upon while trying to implement this that may be useful to others (/explains why some of the code
is the way it is):
- https://wiki.vg/Protocol
- https://en.wikipedia.org/wiki/Rotation_matrix
- https://netty.io/3.6/api/org/jboss/netty/channel/ChannelPipeline.html
- https://www.spigotmc.org/threads/1-16-3-entity-rotation-yaw-with-packets.486844/ (yaw head rotation; method to convert 
  float yaw -> byte yaw)
- https://www.spigotmc.org/threads/spigot-bungeecord-1-17-1-17-1.510208/ (older version, but NMS bit still relevant)
  - https://www.youtube.com/watch?v=K6-deuw4N_o (video form of above)
- https://nms.screamingsandals.org/ (NMS mapping viewer)
- https://github.com/patrick-choe/mojang-spigot-remapper (Mojang -> Spigot Remapper Gradle Plugin)