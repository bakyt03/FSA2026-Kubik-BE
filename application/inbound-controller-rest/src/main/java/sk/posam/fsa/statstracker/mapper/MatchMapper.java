package sk.posam.fsa.statstracker.mapper;

import org.springframework.stereotype.Component;
import sk.posam.fsa.statstracker.domain.Match;
import sk.posam.fsa.statstracker.domain.PlayerMatchStats;
import sk.posam.fsa.statstracker.rest.dto.CreateMatchRequestDto;
import sk.posam.fsa.statstracker.rest.dto.MatchSummaryDto;
import sk.posam.fsa.statstracker.rest.dto.PlayerStatsRequestDto;

import java.util.List;

@Component
public class MatchMapper {

    public Match toEntity(CreateMatchRequestDto dto) {
        if (dto == null)
            return null;
        Match match = new Match();
        match.setMap(sk.posam.fsa.statstracker.domain.Map.valueOf(dto.getMap().getValue()));
        match.setPlayedAt(dto.getPlayedAt());
        match.setTeam1Score(dto.getTeam1Score());
        match.setTeam2Score(dto.getTeam2Score());
        return match;
    }

    public List<PlayerMatchStats> toTeam1Stats(CreateMatchRequestDto dto) {
        return toStatsList(dto.getTeam1Players());
    }

    public List<PlayerMatchStats> toTeam2Stats(CreateMatchRequestDto dto) {
        return toStatsList(dto.getTeam2Players());
    }

    private List<PlayerMatchStats> toStatsList(List<PlayerStatsRequestDto> dtos) {
        if (dtos == null)
            return List.of();
        return dtos.stream().map(this::toPlayerStats).toList();
    }

    private PlayerMatchStats toPlayerStats(PlayerStatsRequestDto dto) {
        PlayerMatchStats stats = new PlayerMatchStats();
        stats.setPlayerId(dto.getPlayerId());
        stats.setKills(dto.getKills());
        stats.setDeaths(dto.getDeaths());
        stats.setDamage(dto.getDamage());
        return stats;
    }

    public MatchSummaryDto toDto(Match match) {
        if (match == null)
            return null;
        MatchSummaryDto dto = new MatchSummaryDto();
        dto.setId(match.getId());
        dto.setMap(match.getMap() != null ? match.getMap().name() : null);
        dto.setPlayedAt(match.getPlayedAt());
        dto.setTeam1Score(match.getTeam1Score());
        dto.setTeam2Score(match.getTeam2Score());
        return dto;
    }
}
