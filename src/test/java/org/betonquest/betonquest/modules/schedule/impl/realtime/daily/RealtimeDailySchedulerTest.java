package org.betonquest.betonquest.modules.schedule.impl.realtime.daily;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.schedule.CatchupStrategy;
import org.betonquest.betonquest.modules.schedule.LastExecutionCache;
import org.betonquest.betonquest.modules.schedule.ScheduleID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests for the {@link RealtimeDailyScheduler}
 */
@SuppressWarnings("PMD.DoNotUseThreads")
@ExtendWith(MockitoExtension.class)
class RealtimeDailySchedulerTest {
    /**
     * Mocked schedule id.
     */
    private static final ScheduleID SCHEDULE_ID = mock(ScheduleID.class);

    static {
        when(SCHEDULE_ID.toString()).thenReturn("test.schedule");
    }

    @Mock
    private BetonQuestLogger logger;

    private static RealtimeDailySchedule getSchedule(final CatchupStrategy catchupStrategy) {
        final RealtimeDailySchedule schedule = mock(RealtimeDailySchedule.class);

        when(schedule.getId()).thenReturn(SCHEDULE_ID);
        when(schedule.getCatchup()).thenReturn(catchupStrategy);

        return schedule;
    }

    @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
    @Test
    void testStartWithoutSchedules() {
        final LastExecutionCache cache = mock(LastExecutionCache.class);
        final RealtimeDailyScheduler scheduler = spy(new RealtimeDailyScheduler(logger, cache));
        scheduler.start();

        verify(cache).cacheStartupTime(any());
        verify(logger, times(1)).debug("Starting simple scheduler.");
        verify(logger, times(1)).debug("Collecting missed schedules...");
        verify(logger, times(1)).debug("Found 0 missed schedule runs that will be caught up.");
        verify(logger, times(1)).debug("Simple scheduler start complete.");
        verifyNoMoreInteractions(logger);
        verify(scheduler, never()).schedule(any());
    }

    @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
    @Test
    void testStartWithMissedSchedulesStrategyOne() {
        final LastExecutionCache cache = mock(LastExecutionCache.class);
        final Instant lastExecution = Instant.now().minus(2, ChronoUnit.DAYS).plusSeconds(60);
        final Instant nextMissedExecution = lastExecution.plus(1, ChronoUnit.DAYS);
        when(cache.getLastExecutionTime(SCHEDULE_ID)).thenReturn(Optional.of(lastExecution));
        @SuppressWarnings("PMD.CloseResource") final ScheduledExecutorService executorService = mock(ScheduledExecutorService.class);
        final RealtimeDailyScheduler scheduler = new RealtimeDailyScheduler(logger, () -> executorService, cache);
        final RealtimeDailySchedule schedule = getSchedule(CatchupStrategy.ONE);
        when(schedule.getNextExecution(any())).thenReturn(nextMissedExecution);
        when(schedule.getNextExecution()).thenReturn(Instant.now());
        scheduler.addSchedule(schedule);
        scheduler.start();

        verify(logger, times(1)).debug("Starting simple scheduler.");
        verify(logger, times(1)).debug("Collecting missed schedules...");
        verify(logger, times(1)).debug(null, "Schedule 'test.schedule' run missed at " + nextMissedExecution);
        verify(logger, times(1)).debug("Found 1 missed schedule runs that will be caught up.");
        verify(logger, times(1)).debug("Running missed schedules to catch up...");
        verify(logger, times(1)).debug(null, "Schedule 'test.schedule' runs its events...");
        verify(logger, times(1)).debug("Simple scheduler start complete.");
        verifyNoMoreInteractions(logger);
        verify(schedule, times(1)).getEvents();
    }

    @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
    @Test
    void testStartWithMissedSchedulesStrategyAll() {
        final LastExecutionCache cache = mock(LastExecutionCache.class);
        final Instant lastExecution = Instant.now().minus(4, ChronoUnit.DAYS).plusSeconds(60);
        final Instant nextMissedExecution1 = lastExecution.plus(1, ChronoUnit.DAYS);
        final Instant nextMissedExecution2 = lastExecution.plus(2, ChronoUnit.DAYS);
        final Instant nextMissedExecution3 = lastExecution.plus(3, ChronoUnit.DAYS);
        when(cache.getLastExecutionTime(SCHEDULE_ID)).thenReturn(Optional.of(lastExecution));
        @SuppressWarnings("PMD.CloseResource") final ScheduledExecutorService executorService = mock(ScheduledExecutorService.class);
        final RealtimeDailyScheduler scheduler = new RealtimeDailyScheduler(logger, () -> executorService, cache);
        final RealtimeDailySchedule schedule = getSchedule(CatchupStrategy.ALL);
        when(schedule.getNextExecution(any())).thenReturn(nextMissedExecution1, nextMissedExecution2, nextMissedExecution3);
        when(schedule.getNextExecution()).thenReturn(Instant.now());
        scheduler.addSchedule(schedule);
        scheduler.start();

        verify(logger, times(1)).debug("Starting simple scheduler.");
        verify(logger, times(1)).debug("Collecting missed schedules...");
        verify(logger, times(1)).debug(null, "Schedule 'test.schedule' run missed at " + nextMissedExecution1);
        verify(logger, times(1)).debug(null, "Schedule 'test.schedule' run missed at " + nextMissedExecution2);
        verify(logger, times(1)).debug(null, "Schedule 'test.schedule' run missed at " + nextMissedExecution3);
        verify(logger, times(1)).debug("Found 3 missed schedule runs that will be caught up.");
        verify(logger, times(1)).debug("Running missed schedules to catch up...");
        verify(logger, times(3)).debug(null, "Schedule 'test.schedule' runs its events...");
        verify(logger, times(1)).debug("Simple scheduler start complete.");
        verifyNoMoreInteractions(logger);
        verify(schedule, times(3)).getEvents();
    }

    @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
    @Test
    void testStartWithoutMissedSchedulesStrategyAll() {
        final LastExecutionCache cache = mock(LastExecutionCache.class);
        final Instant lastExecution = Instant.now().minus(1, ChronoUnit.DAYS).plus(1, ChronoUnit.HOURS);
        final Instant nextExecution = lastExecution.plus(1, ChronoUnit.DAYS);
        when(cache.getLastExecutionTime(SCHEDULE_ID)).thenReturn(Optional.of(lastExecution));
        @SuppressWarnings("PMD.CloseResource") final ScheduledExecutorService executorService = mock(ScheduledExecutorService.class);
        final RealtimeDailyScheduler scheduler = new RealtimeDailyScheduler(logger, () -> executorService, cache);
        final RealtimeDailySchedule schedule = getSchedule(CatchupStrategy.ALL);
        when(schedule.getNextExecution(any())).thenReturn(nextExecution);
        when(schedule.getNextExecution()).thenReturn(nextExecution);
        scheduler.addSchedule(schedule);
        scheduler.start();

        verify(logger, times(1)).debug("Starting simple scheduler.");
        verify(logger, times(1)).debug("Collecting missed schedules...");
        verify(logger, times(1)).debug("Found 0 missed schedule runs that will be caught up.");
        verify(logger, times(1)).debug("Simple scheduler start complete.");
        verifyNoMoreInteractions(logger);
        verify(schedule, never()).getEvents();
    }

    @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
    @Test
    void testStartWithIrregularLastExecution() {
        final LastExecutionCache cache = mock(LastExecutionCache.class);
        final Instant firstStartup = Instant.now().minus(1, ChronoUnit.DAYS).minus(3, ChronoUnit.HOURS);
        final Instant missedExecution = Instant.now().minus(6, ChronoUnit.HOURS);
        when(cache.getLastExecutionTime(SCHEDULE_ID)).thenReturn(Optional.of(firstStartup));
        @SuppressWarnings("PMD.CloseResource") final ScheduledExecutorService executorService = mock(ScheduledExecutorService.class);
        final RealtimeDailyScheduler scheduler = new RealtimeDailyScheduler(logger, () -> executorService, cache);
        final RealtimeDailySchedule schedule = getSchedule(CatchupStrategy.ALL);
        when(schedule.getNextExecution(any())).thenReturn(missedExecution);
        when(schedule.getNextExecution()).thenReturn(Instant.now());
        scheduler.addSchedule(schedule);
        scheduler.start();

        verify(logger, times(1)).debug("Starting simple scheduler.");
        verify(logger, times(1)).debug("Collecting missed schedules...");
        verify(logger, times(1)).debug(null, "Schedule 'test.schedule' run missed at " + missedExecution);
        verify(logger, times(1)).debug("Found 1 missed schedule runs that will be caught up.");
        verify(logger, times(1)).debug("Running missed schedules to catch up...");
        verify(logger, times(1)).debug(null, "Schedule 'test.schedule' runs its events...");
        verify(logger, times(1)).debug("Simple scheduler start complete.");
        verifyNoMoreInteractions(logger);
        verify(schedule, times(1)).getEvents();
    }

    @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
    @Test
    void testStartSchedule() {
        final LastExecutionCache cache = mock(LastExecutionCache.class);
        final Instant nextExecution1 = Instant.now();
        final Instant nextExecution2 = nextExecution1.plus(1, ChronoUnit.DAYS);
        final Instant nextExecution3 = nextExecution1.plus(2, ChronoUnit.DAYS);
        final Instant nextExecution4 = nextExecution1.plus(3, ChronoUnit.DAYS);
        @SuppressWarnings("PMD.CloseResource") final ScheduledExecutorService executorService = mock(ScheduledExecutorService.class);
        when(executorService.schedule(any(Runnable.class), anyLong(), eq(TimeUnit.MILLISECONDS))).then(invocation -> {
            final Runnable runnable = invocation.getArgument(0);
            final long delay = invocation.getArgument(1);
            if (!Instant.now().plusMillis(delay + 1000).isAfter(nextExecution4)) {
                runnable.run();
            }
            return null;
        });
        final RealtimeDailyScheduler scheduler = new RealtimeDailyScheduler(logger, () -> executorService, cache);
        final RealtimeDailySchedule schedule = getSchedule(CatchupStrategy.NONE);
        when(schedule.getNextExecution()).thenReturn(nextExecution1, nextExecution2, nextExecution3, nextExecution4);
        scheduler.addSchedule(schedule);
        scheduler.start();

        verify(cache).cacheStartupTime(any());
        verify(logger, times(1)).debug("Starting simple scheduler.");
        verify(logger, times(1)).debug("Collecting missed schedules...");
        verify(logger, times(1)).debug("Found 0 missed schedule runs that will be caught up.");
        verify(logger, times(3)).debug(null, "Schedule 'test.schedule' runs its events...");
        verify(logger, times(1)).debug("Simple scheduler start complete.");
        verifyNoMoreInteractions(logger);
        verify(schedule, times(3)).getEvents();
    }
}
