package me.alexutzzu.teledon.model.dto.statistics;

public record MonthlyActiveDonorStatisticsDto(long current, long previous) implements WindowStatistics {
    @Override
    public long getCurrent() {
        return current;
    }

    @Override
    public long getPrevious() {
        return previous;
    }
}
