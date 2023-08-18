# mirror
A mirror in Minecraft: "reflects" players and their actions.

This is heavily inspired by the Mirrorverse
[[1]](https://wiki.hypixel.net/Mirrorverse) 
[[2]](https://hypixel-skyblock.fandom.com/wiki/Mirrorverse)
on Hypixel Skyblock. There really isn't much purpose in this, other than being something cool I saw that I wanted to
recreate. Feel free to use the code for whatever.

**This will only work for Minecraft (Spigot) 1.20.1**

## Some Disclaimers

- This implementation heavily uses NMS to create NPCs and to mimic some packets sent by the client.

## Useful Resources

Some stuff I stumbled upon while trying to implement this that may be useful to others (/explains why some of the code
is the way it is):
- https://wiki.vg/Protocol
- https://en.wikipedia.org/wiki/Rotation_matrix
- https://www.spigotmc.org/threads/1-16-3-entity-rotation-yaw-with-packets.486844/ (yaw head rotation; method to convert 
  float yaw -> byte yaw)
- https://www.spigotmc.org/threads/spigot-bungeecord-1-17-1-17-1.510208/ (older version, but NMS bit still relevant)
  - https://www.youtube.com/watch?v=K6-deuw4N_o (video form of above)
- https://nms.screamingsandals.org/ (NMS mapping viewer)
- https://github.com/patrick-choe/mojang-spigot-remapper (Mojang -> Spigot Remapper Gradle Plugin)