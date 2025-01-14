/*
 *                              ____                 _
 *                             |  _ \               | |
 *                   __ _  ___ | |_) |_ __ _   _ ___| |__
 *                  / _` |/ _ \|  _ <| '__| | | / __| '_ \
 *                 | (_| | (_) | |_) | |  | |_| \__ \ | | |
 *                  \__, |\___/|____/|_|   \__,_|___/_| |_|
 *                   __/ |
 *                  |___/
 *
 *    goBrush is designed to streamline and simplify your mountain building experience.
 *                            Copyright (C) 2022 Arcaniax
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.arcaniax.gobrush;

import com.arcaniax.gobrush.command.CommandHandler;
import com.arcaniax.gobrush.listener.InventoryClickListener;
import com.arcaniax.gobrush.listener.PlayerInteractListener;
import com.arcaniax.gobrush.listener.PlayerJoinListener;
import com.arcaniax.gobrush.listener.PlayerQuitListener;
import com.arcaniax.gobrush.util.BlockUtils;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import io.papermc.lib.PaperLib;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.serverlib.ServerLib;


public class GoBrushPlugin extends JavaPlugin {

    private static final int BSTATS_ID = 10558;
    public static GoBrushPlugin plugin;
    public static int amountOfValidBrushes;

    /**
     * This method returns the main JavaPlugin instance, used for several things.
     *
     * @return The main JavaPlugin instance.
     */
    public static JavaPlugin getPlugin() {
        return plugin;
    }

    @Override
    public void onEnable() {
        plugin = this;
        try {
            Class.forName("java.awt.Graphics2D");
        } catch (ClassNotFoundException ignored) {
            getLogger().severe("Cannot locate Java AWT classes. It appears your server uses a headless Java build, where a " +
                    "normal one is recommended. Get it here: https://adoptium.net/. goBrush will now disable itself until you " +
                    "installed the correct Java version.");
            Bukkit.getPluginManager().disablePlugin(this);
        }
        saveDefaultConfig();
        Session.initializeConfig(this.getConfig());
        Session.initializeBrushPlayers();
        Session.setWorldEdit((WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit"));
        registerListeners();
        registerCommands();
        // Check if we are in a safe environment
        ServerLib.checkUnsafeForks();
        ServerLib.isJavaSixteen();
        PaperLib.suggestPaper(this);
        amountOfValidBrushes = Session.initializeValidBrushes();

        try {
            Class.forName("org.bukkit.generator.WorldInfo");
            BlockUtils.setNewerWorldVersion(true);
        } catch (ClassNotFoundException ignored) {
        }
    }

    private void registerListeners() {
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerQuitListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new InventoryClickListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerInteractListener(), this);
    }

    /**
     * This method registers the commands of the plugin.
     */
    private void registerCommands() {
        getCommand("gobrush").setExecutor(new CommandHandler());
    }

}
