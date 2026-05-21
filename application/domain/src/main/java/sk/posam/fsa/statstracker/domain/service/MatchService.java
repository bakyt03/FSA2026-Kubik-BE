package sk.posam.fsa.statstracker.domain.service;

import sk.posam.fsa.statstracker.domain.Match;
import sk.posam.fsa.statstracker.domain.MatchRepository;
import sk.posam.fsa.statstracker.domain.PlayerMatchStats;
import sk.posam.fsa.statstracker.domain.PlayerStatsRepository;
import sk.posam.fsa.statstracker.domain.StatsTrackerException;

import java.util.List;

public class MatchService implements MatchFacade {

    private final MatchRepository matchRepository;
    private final PlayerStatsRepository playerStatsRepository;

    public MatchService(MatchRepository matchRepository, PlayerStatsRepository playerStatsRepository) {
        this.matchRepository = matchRepository;
        this.playerStatsRepository = playerStatsRepository;
    }

    @Override
    public List<Match> getAll() {
        return matchRepository.getAll();
    }

    @Override
    public void create(Match match, List<PlayerMatchStats> team1Stats, List<PlayerMatchStats> team2Stats)
            throws StatsTrackerException {
        validateScore(match.getTeam1Score(), match.getTeam2Score());
        int totalRounds = match.getTeam1Score() + match.getTeam2Score();
        Match created = matchRepository.create(match);
        team1Stats.forEach(s -> {
            s.setMatchId(created.getId());
            s.setTeam("TEAM1");
            computeAdr(s, totalRounds);
            playerStatsRepository.create(s);
        });
        team2Stats.forEach(s -> {
            s.setMatchId(created.getId());
            s.setTeam("TEAM2");
            computeAdr(s, totalRounds);
            playerStatsRepository.create(s);
        });
    }

    private void computeAdr(PlayerMatchStats s, int totalRounds) {
        if (totalRounds > 0) {
            s.setAdr((double) s.getDamage() / totalRounds);
        }
    }

    private void validateScore(int s1, int s2) throws StatsTrackerException {
        if (!isValidScore(s1, s2)) {
            throw new StatsTrackerException(
                    StatsTrackerException.Type.VALIDATION,
                    "Invalid CS2 score: " + s1 + "-" + s2 +
                            ". Regulation: first to 13 with 2+ round lead. OT: first to 16/19/22/... with 2+ round lead.");
        }
    }

    private boolean isValidScore(int s1, int s2) {
        int max = Math.max(s1, s2);
        int min = Math.min(s1, s2);
        // Regulation: first team to 13, must win by at least 2
        if (max == 13 && min >= 0 && min <= 11)
            return true;
        // Overtime rounds: winner = 12 + 4n, loser in [12 + 3*(n-1), 12 + 4n - 2]
        // Minimum difference in OT is always 2 (4-2 in OT rounds)
        for (int n = 1; n <= 20; n++) {
            int win = 12 + 4 * n;
            int loMin = 12 + 3 * (n - 1);
            int loMax = win - 2;
            if (max == win && min >= loMin && min <= loMax)
                return true;
        }
        return false;
    }
}
