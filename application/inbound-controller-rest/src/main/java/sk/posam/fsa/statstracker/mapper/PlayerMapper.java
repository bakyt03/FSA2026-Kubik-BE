package sk.posam.fsa.statstracker.mapper;

import org.springframework.stereotype.Component;
import sk.posam.fsa.statstracker.rest.dto.CreatePlayerRequestDto;
import sk.posam.fsa.statstracker.rest.dto.PlayerSummaryDto;
import sk.posam.fsa.statstracker.domain.Player;
import sk.posam.fsa.statstracker.domain.PlayerStatsSnapshot;
import sk.posam.fsa.statstracker.domain.PlayerWithStats;

@Component
public class PlayerMapper {

    public Player toEntity(CreatePlayerRequestDto dto) {
        if (dto == null) {
            return null;
        }
        Player player = new Player();
        player.setName(dto.getName());
        player.setNickname(dto.getNickname());

        return player;
    }

    public PlayerSummaryDto toDto(Player player) {
        if (player == null) {
            return null;
        }
        PlayerSummaryDto dto = new PlayerSummaryDto();
        dto.setId(player.getId());
        dto.setName(player.getName());
        dto.setNickname(player.getNickname());

        return dto;
    }

    public PlayerSummaryDto toDto(PlayerWithStats pws) {
        if (pws == null)
            return null;
        PlayerSummaryDto dto = toDto(pws.getPlayer());
        PlayerStatsSnapshot s = pws.getStats();
        if (s != null) {
            dto.setMatchesPlayed(s.getMatchesPlayed());
            dto.setAvgKills(s.getAvgKills());
            dto.setAvgDeaths(s.getAvgDeaths());
            dto.setAvgAdr(s.getAvgAdr());
        }
        return dto;
    }
}
