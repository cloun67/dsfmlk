package com.example.triggerbot;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class TriggerBotConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("triggerbot.json");
    public static TriggerBotConfig INSTANCE = new TriggerBotConfig();

    // General
    public boolean showMessages = true;
    public boolean pauseWhileUsingItem = true;

    // What to attack WITH
    public boolean weaponSword = true;
    public boolean weaponAxe = true;
    public boolean weaponMace = true;
    public boolean weaponTrident = true;
    public boolean allowEmptyHand = false;
    public boolean anyItem = false;
    public List<String> customItems = new ArrayList<>();
    public boolean autoSwitch = false;

    // WHOM to attack
    public boolean attackPlayers = true;
    public boolean attackHostile = true;
    public boolean attackNeutral = false;
    public boolean attackPassive = false;
    public boolean attackVillagers = false;
    public boolean attackOther = false;
    public boolean ignoreInvisible = false;
    public boolean ignoreTeammates = true;
    public boolean ignorePets = true;
    public List<String> ignoredEntities = new ArrayList<>();
    public List<String> alwaysAttackEntities = new ArrayList<>();

    // Timing
    public int minCooldownPercent = 95;
    public int minDelayMs = 30;
    public int maxDelayMs = 120;
    public boolean criticalsOnly = false;

    public static void load() {
        try {
            if (Files.exists(PATH)) {
                TriggerBotConfig c = GSON.fromJson(Files.readString(PATH), TriggerBotConfig.class);
                if (c != null) INSTANCE = c;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (INSTANCE.customItems == null) INSTANCE.customItems = new ArrayList<>();
        if (INSTANCE.ignoredEntities == null) INSTANCE.ignoredEntities = new ArrayList<>();
        if (INSTANCE.alwaysAttackEntities == null) INSTANCE.alwaysAttackEntities = new ArrayList<>();
        save();
    }

    public static void save() {
        try {
            Files.writeString(PATH, GSON.toJson(INSTANCE));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
