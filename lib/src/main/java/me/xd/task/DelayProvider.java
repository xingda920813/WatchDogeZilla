package me.xd.task;

@FunctionalInterface
public interface DelayProvider {

    long EVERY_15_MINUTES = -1;
    long DAILY = -2;

    long getDelay(boolean fromAlarmManager);
}
