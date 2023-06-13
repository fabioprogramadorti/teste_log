public class GameLogParser {
    public static List<Game> parseLogFile(String filePath) {

        String logFilePath = "path/to/games.log";
        List<Game> games = parseLogFile(logFilePath);

        for (int i = 0; i < games.size(); i++) {
            Game game = games.get(i);
            System.out.println("Game_" + (i + 1) + ":");
            System.out.println("\ttotal_kills: " + game.getTotalKills());
            System.out.println("\tplayers: " + game.getPlayers());
            System.out.println("\tkills: " + game.getKills());
        }

        return games;
    }

    private static List<Game> parseLogFile(String filePath) {
        List<Game> games = new ArrayList<>();
        Map<String, Integer> totalKillsMap = new HashMap<>();
        Map<String, Integer> killsMap = new HashMap<>();
        Set<String> players = new HashSet<>();
        int currentGameIndex = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("InitGame")) {
                    // Start a new game
                    totalKillsMap.put("world", 0);
                    killsMap.clear();
                    players.clear();
                } else if (line.contains("ClientUserinfoChanged")) {
                    // Extract player name
                    String playerName = extractPlayerName(line);
                    players.add(playerName);
                    killsMap.put(playerName, 0);
                } else if (line.contains("Kill")) {
                    // Parse kill information
                    KillInfo killInfo = parseKillInfo(line);
                    if (killInfo != null) {
                        String killer = killInfo.getKiller();
                        String victim = killInfo.getVictim();
                        int killCount = killsMap.getOrDefault(killer, 0);
                        killsMap.put(killer, killCount + 1);
                        if (!killer.equals("world")) {
                            int totalKillCount = totalKillsMap.getOrDefault(killer, 0);
                            totalKillsMap.put(killer, totalKillCount + 1);
                        }
                        if (!victim.equals("world")) {
                            int killCountOfVictim = killsMap.getOrDefault(victim, 0);
                            killsMap.put(victim, killCountOfVictim - 1);
                        }
                    }
                } else if (line.contains("ShutdownGame")) {
                    // End of game
                    int totalKills = totalKillsMap.values().stream().mapToInt(Integer::intValue).sum();
                    Set<String> gamePlayers = new HashSet<>(players);
                    gamePlayers.remove("world");

                    Game game = new Game("game_" + (currentGameIndex + 1), totalKills, gamePlayers, killsMap);
                    games.add(game);
                    currentGameIndex++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return games;
    }

    private static String extractPlayerName(String line) {
        int startIndex = line.indexOf("n\\") + 2;
        int endIndex = line.indexOf("\\t");
        return line.substring(startIndex, endIndex);
    }

    private static KillInfo parseKillInfo(String line) {
        try {
            int startIndex = line.indexOf("Kill: ") + 6;
            int endIndex = line.indexOf(": ");
            String[] tokens = line.substring(startIndex, endIndex).split(" ");
            String killer = tokens[0];
            String victim = tokens[1];

            if (killer.equals("<world>")) {
                killer = "world";
            }

            return new KillInfo(killer, victim);
        } catch (Exception e) {
            return null;
        }
    }

    static class KillInfo {
        private String killer;
        private String victim;

        public KillInfo(String killer, String victim) {
            this.killer = killer;
            this.victim = victim;
        }

        public String getKiller() {
            return killer;
        }

        public String getVictim() {
            return victim;
        }
    }

    static class Game {
        private String name;
        private int totalKills;
        private Set<String> players;
        private Map<String, Integer> kills;

        public Game(String name, int totalKills, Set<String> players, Map<String, Integer> kills) {
            this.name = name;
            this.totalKills = totalKills;
            this.players = players;
            this.kills = kills;
        }

        public String getName() {
            return name;
        }

        public int getTotalKills() {
            return totalKills;
        }

        public Set<String> getPlayers() {
            return players;
        }

        public Map<String, Integer> getKills() {
            return kills;
        }
    }
}
