package com.sulaxan.mirror;

import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.block.structure.Mirror;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;

@RequiredArgsConstructor
public class MirrorBoxListener implements Listener {

    private final MirrorBox box;

    @EventHandler
    public void onBoxEnter(PlayerMoveEvent e) {
        if (e.getTo() == null) {
            return;
        }
        if (!box.isInside(e.getFrom()) && box.isInside(e.getTo())) {
            box.createNpc(e.getPlayer());
            box.getPacketInterceptor().startIntercepting(e.getPlayer());
        }
    }

    @EventHandler
    public void onBoxExit(PlayerMoveEvent e) {
        if (e.getTo() == null) {
            return;
        }
        if (box.isInside(e.getFrom()) && !box.isInside(e.getTo())) {
            // only stop intercepting if we successfully destroy the npc (also an indicator that this player was in the
            // mirror to begin with)
            // this fixes a bug where if the user starts off within the mirror when it is created and leaves it,
            // there'll be an error since we try to stop intercepting for a player who wasn't being intercepted in the
            // first place
            if (box.destroyNpc(e.getPlayer())) {
                box.getPacketInterceptor().stopIntercepting(e.getPlayer());
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if (!box.isInside(e.getBlockPlaced().getLocation())) {
            return;
        }

        var placedBlock = e.getBlockPlaced();
        var mirrored = box.mirror(placedBlock.getLocation());

        var mirroredBlock = mirrored.getWorld().getBlockAt(mirrored);
        var mirroredData = placedBlock.getBlockData();
        mirroredData.mirror(Mirror.FRONT_BACK);
        mirroredBlock.setBlockData(mirroredData);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (!box.isInside(e.getBlock().getLocation())) {
            return;
        }

        var placedBlock = e.getBlock();
        var mirrored = box.mirror(placedBlock.getLocation());

        var mirroredBlock = mirrored.getWorld().getBlockAt(mirrored);
        mirroredBlock.setType(Material.AIR);
    }
}
