package com.example.triggerbot;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ModMenuIntegration::create;
    }

    private static Text t(String key) { return Text.translatable("config.triggerbot." + key); }

    public static Screen create(Screen parent) {
        TriggerBotConfig c = TriggerBotConfig.INSTANCE;
        ConfigBuilder b = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(t("title"))
                .setSavingRunnable(TriggerBotConfig::save);
        ConfigEntryBuilder e = b.entryBuilder();

        ConfigCategory gen = b.getOrCreateCategory(t("cat.general"));
        gen.addEntry(e.startBooleanToggle(t("showMessages"), c.showMessages).setDefaultValue(true)
                .setSaveConsumer(v -> c.showMessages = v).build());
        gen.addEntry(e.startBooleanToggle(t("pauseWhileUsingItem"), c.pauseWhileUsingItem).setDefaultValue(true)
                .setSaveConsumer(v -> c.pauseWhileUsingItem = v).build());
        gen.addEntry(e.startBooleanToggle(t("criticalsOnly"), c.criticalsOnly).setDefaultValue(false)
                .setSaveConsumer(v -> c.criticalsOnly = v).build());
        gen.addEntry(e.startIntSlider(t("minCooldown"), c.minCooldownPercent, 0, 100).setDefaultValue(95)
                .setTextGetter(v -> Text.literal(v + "%")).setSaveConsumer(v -> c.minCooldownPercent = v).build());
        gen.addEntry(e.startIntSlider(t("minDelay"), c.minDelayMs, 0, 500).setDefaultValue(30)
                .setTextGetter(v -> Text.literal(v + " ms")).setSaveConsumer(v -> c.minDelayMs = v).build());
        gen.addEntry(e.startIntSlider(t("maxDelay"), c.maxDelayMs, 0, 500).setDefaultValue(120)
                .setTextGetter(v -> Text.literal(v + " ms")).setSaveConsumer(v -> c.maxDelayMs = v).build());

        ConfigCategory w = b.getOrCreateCategory(t("cat.weapons"));
        w.addEntry(e.startBooleanToggle(t("weaponSword"), c.weaponSword).setDefaultValue(true)
                .setSaveConsumer(v -> c.weaponSword = v).build());
        w.addEntry(e.startBooleanToggle(t("weaponAxe"), c.weaponAxe).setDefaultValue(true)
                .setSaveConsumer(v -> c.weaponAxe = v).build());
        w.addEntry(e.startBooleanToggle(t("weaponMace"), c.weaponMace).setDefaultValue(true)
                .setSaveConsumer(v -> c.weaponMace = v).build());
        w.addEntry(e.startBooleanToggle(t("weaponTrident"), c.weaponTrident).setDefaultValue(true)
                .setSaveConsumer(v -> c.weaponTrident = v).build());
        w.addEntry(e.startBooleanToggle(t("allowEmptyHand"), c.allowEmptyHand).setDefaultValue(false)
                .setSaveConsumer(v -> c.allowEmptyHand = v).build());
        w.addEntry(e.startBooleanToggle(t("anyItem"), c.anyItem).setDefaultValue(false)
                .setSaveConsumer(v -> c.anyItem = v).build());
        w.addEntry(e.startStrList(t("customItems"), c.customItems)
                .setSaveConsumer(v -> c.customItems = new java.util.ArrayList<>(v)).build());
        w.addEntry(e.startBooleanToggle(t("autoSwitch"), c.autoSwitch).setDefaultValue(false)
                .setSaveConsumer(v -> c.autoSwitch = v).build());

        ConfigCategory tg = b.getOrCreateCategory(t("cat.targets"));
        tg.addEntry(e.startBooleanToggle(t("attackPlayers"), c.attackPlayers).setDefaultValue(true)
                .setSaveConsumer(v -> c.attackPlayers = v).build());
        tg.addEntry(e.startBooleanToggle(t("attackHostile"), c.attackHostile).setDefaultValue(true)
                .setSaveConsumer(v -> c.attackHostile = v).build());
        tg.addEntry(e.startBooleanToggle(t("attackNeutral"), c.attackNeutral).setDefaultValue(false)
                .setSaveConsumer(v -> c.attackNeutral = v).build());
        tg.addEntry(e.startBooleanToggle(t("attackPassive"), c.attackPassive).setDefaultValue(false)
                .setSaveConsumer(v -> c.attackPassive = v).build());
        tg.addEntry(e.startBooleanToggle(t("attackVillagers"), c.attackVillagers).setDefaultValue(false)
                .setSaveConsumer(v -> c.attackVillagers = v).build());
        tg.addEntry(e.startBooleanToggle(t("attackOther"), c.attackOther).setDefaultValue(false)
                .setSaveConsumer(v -> c.attackOther = v).build());
        tg.addEntry(e.startBooleanToggle(t("ignoreInvisible"), c.ignoreInvisible).setDefaultValue(false)
                .setSaveConsumer(v -> c.ignoreInvisible = v).build());
        tg.addEntry(e.startBooleanToggle(t("ignoreTeammates"), c.ignoreTeammates).setDefaultValue(true)
                .setSaveConsumer(v -> c.ignoreTeammates = v).build());
        tg.addEntry(e.startBooleanToggle(t("ignorePets"), c.ignorePets).setDefaultValue(true)
                .setSaveConsumer(v -> c.ignorePets = v).build());
        tg.addEntry(e.startStrList(t("ignoredEntities"), c.ignoredEntities)
                .setSaveConsumer(v -> c.ignoredEntities = new java.util.ArrayList<>(v)).build());
        tg.addEntry(e.startStrList(t("alwaysAttackEntities"), c.alwaysAttackEntities)
                .setSaveConsumer(v -> c.alwaysAttackEntities = new java.util.ArrayList<>(v)).build());

        return b.build();
    }
}
