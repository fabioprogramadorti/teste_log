import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/games")
public class GameController {
    @GetMapping("/parse")
    public ResponseEntity<List<Game>> parseLog() {
        String logFilePath = "../../resources/logfiles/games.log";
        List<Game> games = GameLogParser.parseLogFile(logFilePath);
        return new ResponseEntity<>(games, HttpStatus.OK);
    }
}