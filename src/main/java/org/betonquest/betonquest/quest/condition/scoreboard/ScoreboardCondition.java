package org.betonquest.betonquest.quest.condition.scoreboard;

import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

/**
 * Checks the amount of points on scoreboards.
 */
public class ScoreboardCondition implements PlayerCondition {

    /**
     * The objective to check.
     */
    private final String objective;

    /**
     * The count to check.
     */
    private final VariableNumber count;

    /**
     * Checks the amount of points on scoreboards.
     *
     * @param objective the objective to check
     * @param count     the count to check
     */
    public ScoreboardCondition(final String objective, final VariableNumber count) {
        this.objective = objective;
        this.count = count;
    }

    @Override
    public boolean check(final Profile profile) throws QuestRuntimeException {
        final Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
        final Objective obj = board.getObjective(objective);
        if (obj == null) {
            throw new QuestRuntimeException("Scoreboard objective " + objective + " dose not exist!");
        }
        final int score = obj.getScore(profile.getPlayer()).getScore();
        return score >= count.getValue(profile).intValue();
    }
}
