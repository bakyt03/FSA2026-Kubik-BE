package sk.posam.fsa.statstracker.domain.service;

import sk.posam.fsa.statstracker.domain.Player;
import sk.posam.fsa.statstracker.domain.PlayerRepository;
import sk.posam.fsa.statstracker.domain.PlayerStatsRepository;
import sk.posam.fsa.statstracker.domain.PlayerStatsSnapshot;
import sk.posam.fsa.statstracker.domain.PlayerWithStats;
import sk.posam.fsa.statstracker.domain.StatsTrackerException;
import sk.posam.fsa.statstracker.domain.predicate.IsNotNullPredicate;
import sk.posam.fsa.statstracker.domain.predicate.IsUniqueNicknamePredicate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PlayerService implements PlayerFacade {

    private final PlayerRepository playerRepository;
    private final PlayerStatsRepository playerStatsRepository;

    public PlayerService(PlayerRepository playerRepository, PlayerStatsRepository playerStatsRepository) {
        this.playerRepository = playerRepository;
        this.playerStatsRepository = playerStatsRepository;
    }

    @Override
    public void createPlayer(Player player) throws StatsTrackerException {
        require(IsNotNullPredicate.<Player>getInstance().test(player),
                StatsTrackerException.Type.VALIDATION, "Player must not be null");
        player.validateForCreation();

        Player existingPlayer = playerRepository.get(player.getNickname()).orElse(null);
        require(IsUniqueNicknamePredicate.INSTANCE.test(player, existingPlayer),
                StatsTrackerException.Type.CONFLICT,
                "Player with nickname '" + player.getNickname() + "' already exists");

        playerRepository.create(player);
    }

    @Override
    public List<Player> findAll() {
        return playerRepository.getAll();
    }

    @Override
    public List<PlayerWithStats> findAllWithStats() {
        List<Player> players = playerRepository.getAll();
        if (players.isEmpty())
            return List.of();
        List<Long> ids = players.stream().map(Player::getId).collect(Collectors.toList());
        Map<Long, PlayerStatsSnapshot> snapMap = playerStatsRepository.getForPlayers(ids).stream()
                .collect(Collectors.toMap(PlayerStatsSnapshot::getPlayerId, s -> s));
        return players.stream()
                .map(p -> new PlayerWithStats(p, snapMap.get(p.getId())))
                .collect(Collectors.toList());
    }

    private void require(boolean valid, StatsTrackerException.Type type, String message) {
        if (!valid) {
            throw new StatsTrackerException(type, message);
        }
    }
}
