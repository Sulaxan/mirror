package com.sulaxan.mirror;

import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

@Data
public class MirrorManager {

    private final Plugin plugin;
    private List<MirrorBox> mirrors = new ArrayList<>();

    public void constructNewBox(Location loc) {
        var bboxA = new Location(loc.getWorld(), loc.getBlockX() - 15, loc.getY(), loc.getBlockZ() - 15);
        var bboxB = new Location(loc.getWorld(), loc.getBlockX() + 15, loc.getY() + 20, loc.getBlockZ() + 15);

        var box = new MirrorBox(bboxA, bboxB);
        box.init(); //temp
        box.constructBox();
        mirrors.add(box);

        Bukkit.getPluginManager().registerEvents(new MirrorBoxListener(box), plugin);

        Bukkit.getScheduler().runTaskTimer(plugin, box::update, 0, 1);
    }
}
