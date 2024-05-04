package org.betonquest.betonquest.compatibility.effectlib;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.compatibility.Compatibility;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.utils.location.CompoundLocation;
import org.betonquest.betonquest.variables.GlobalVariableResolver;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Displays a particle above NPCs with conversations.
 */
public class EffectLibParticleManager {
    /**
     * The config section for the location and npc settings
     */
    private static final String EFFECTLIB_CONFIG_SECTION = "effectlib";

    /**
     * The config section for the npcs
     */
    private static final String NPCS_CONFIG_SECTION = "npcs";

    /**
     * The {@link BetonQuestLoggerFactory} to use for creating {@link BetonQuestLogger} instances.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * All active {@link EffectLibRunnable}s managed by this class.
     */
    private final List<EffectLibRunnable> activeParticles = new ArrayList<>();

    /**
     * Loads the particle configuration and starts the effects.
     */
    public EffectLibParticleManager(final BetonQuestLoggerFactory loggerFactory, final BetonQuestLogger log) {
        this.loggerFactory = loggerFactory;
        this.log = log;
        loadParticleConfiguration();
    }

    @SuppressWarnings({"PMD.AvoidDuplicateLiterals", "PMD.CognitiveComplexity"})
    private void loadParticleConfiguration() {
        for (final QuestPackage pack : Config.getPackages().values()) {
            final ConfigurationSection section = pack.getConfig().getConfigurationSection(EFFECTLIB_CONFIG_SECTION);
            if (section == null) {
                continue;
            }

            for (final String key : section.getKeys(false)) {
                final ConfigurationSection settings = section.getConfigurationSection(key);
                if (settings == null) {
                    continue;
                }

                final String effectClass = settings.getString("class");
                if (effectClass == null) {
                    log.warn(pack, "Could not load npc effect '" + key + "' in package " + pack.getQuestPath() + ": "
                            + "No effect class given.");
                    continue;
                }

                final int interval = settings.getInt("interval", 100);
                if (interval <= 0) {
                    log.warn(pack, "Could not load npc effect '" + key + "' in package " + pack.getQuestPath() + ": "
                            + "Effect interval must be bigger than 0.");
                    continue;
                }

                final int conditionsCheckInterval = settings.getInt("checkinterval", 100);
                if (conditionsCheckInterval <= 0) {
                    log.warn(pack, "Could not load npc effect '" + key + "' in package " + pack.getQuestPath() + ": "
                            + "Check interval must be bigger than 0.");
                    continue;
                }

                final Set<Integer> npcs = new HashSet<>();
                if (Compatibility.getHooked().contains("Citizens")) {
                    npcs.addAll(loadNpcs(settings));
                }
                final List<CompoundLocation> locations = loadLocations(pack, settings, key);
                final List<ConditionID> conditions = loadConditions(pack, key, settings);

                final EffectConfiguration effect = new EffectConfiguration(effectClass, locations, npcs, conditions, settings, conditionsCheckInterval);
                final EffectLibRunnable particleRunnable = new EffectLibRunnable(loggerFactory.create(EffectLibRunnable.class), effect);

                activeParticles.add(particleRunnable);
                particleRunnable.runTaskTimer(BetonQuest.getInstance(), 1, interval);
            }
        }
    }

    /**
     * Reloads the particle effect.
     */
    public void reload() {
        for (final EffectLibRunnable activeParticle : activeParticles) {
            activeParticle.cancel();
        }
        activeParticles.clear();
        loadParticleConfiguration();
    }

    private List<CompoundLocation> loadLocations(final QuestPackage pack, final ConfigurationSection settings, final String key) {
        final List<CompoundLocation> locations = new ArrayList<>();
        if (settings.isList("locations")) {
            for (final String rawLocation : settings.getStringList("locations")) {
                if (rawLocation == null) {
                    continue;
                }
                try {
                    locations.add(new CompoundLocation(pack, GlobalVariableResolver.resolve(pack, rawLocation)));
                } catch (final InstructionParseException exception) {
                    log.warn(pack, "Could not load npc effect '" + key + "' in package " + pack.getQuestPath() + ": "
                            + "Location is invalid:" + exception.getMessage());
                }
            }
        }
        return locations;
    }

    private List<ConditionID> loadConditions(final QuestPackage pack, final String key, final ConfigurationSection settings) {
        final List<ConditionID> conditions = new ArrayList<>();
        for (final String conditionID : settings.getStringList("conditions")) {
            try {
                conditions.add(new ConditionID(pack, conditionID));
            } catch (final ObjectNotFoundException e) {
                log.warn(pack, "Error while loading npc_effects '" + key + "': " + e.getMessage(), e);
            }
        }
        return conditions;
    }

    private Set<Integer> loadNpcs(final ConfigurationSection settings) {
        final Set<Integer> npcs = new HashSet<>();
        if (settings.isList(NPCS_CONFIG_SECTION)) {
            npcs.addAll(settings.getIntegerList(NPCS_CONFIG_SECTION));
        }
        return npcs;
    }
}
