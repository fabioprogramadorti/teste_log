import java.util.List;

public interface GameService {
    List<Game> getAllGames();
    Game getGameById(String gameId);
    Game saveGame(Game game);
    void deleteGame(String gameId);
}
