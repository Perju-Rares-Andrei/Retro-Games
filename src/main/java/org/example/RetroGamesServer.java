package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RetroGamesServer {
    private static final int PORT = 8080;
    private static int clientCount = 0;

    private static final String DB_URL = "jdbc:postgresql://localhost:5432/Hangman";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "rares";

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(10);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started. Listening on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress());

                clientCount++;

                // Servesc fiecare client concurent printr-un thread
                executor.submit(() -> handleClient(clientSocket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }
    }


    private static void handleClient(Socket clientSocket) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
             Connection connection = getConnection()
        ) {
            // Testarea conexiunii cu baza de date si afisarea primei linii din tabelul cu cuvinte (word)
            if (connection != null) {
                System.out.println("Database connection successful.");

                try {
                    String testQuery = "SELECT word, language FROM word LIMIT 1";
                    PreparedStatement statement = connection.prepareStatement(testQuery);
                    ResultSet resultSet = statement.executeQuery();

                    if (resultSet.next()) {
                        String testWord = resultSet.getString("word");
                        String testLanguage = resultSet.getString("language");
                        System.out.println("Test query result: Word=" + testWord + ", Language=" + testLanguage);
                    } else {
                        System.out.println("Test query returned no results.");
                    }
                } catch (SQLException e) {
                    System.out.println("Error executing the test query: " + e.getMessage());
                }
            } else {
                System.out.println("Failed to connect to the database.");
            }

            String inputLine;
            while ((inputLine = reader.readLine()) != null) {
                System.out.println("Received from client " + clientSocket + " : " + inputLine);

                if (inputLine.equalsIgnoreCase("Tic-Tac-Toe")) {
                    writer.println("Game mode: Tic Tac Toe");
                    handleTicTacToe(reader, writer);
                } else if (inputLine.equalsIgnoreCase("Rock Paper Scissors")) {
                    writer.println("Game mode: Rock Paper Scissors");
                    handleRockPaperScissors(reader, writer);
                } else if (inputLine.equalsIgnoreCase("Hangman")) {
                    writer.println("Game mode: Hangman");
                    handleHangman(reader, writer);
                }else if(inputLine.equalsIgnoreCase("Ramsey Graph")){
                    writer.println("Game mode: Ramsey Graph");
                    handleRamseyGraph(reader,writer);
                }else {
                    writer.println("Unsupported game mode : " + inputLine);
                }
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
                System.out.println("Client " + clientCount + " disconnected.");
                clientCount--;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private static void handleRamseyGraph(BufferedReader reader, PrintWriter writer) throws IOException{
        while(true){
            String command = reader.readLine();
            if(command.startsWith("backRG")){
                break;
            }
        }
    }

    private static Connection getConnection() throws SQLException {
        Properties props = new Properties();
        props.setProperty("user", DB_USER);
        props.setProperty("password", DB_PASSWORD);
        return DriverManager.getConnection(DB_URL, props);
    }

    private static void handleHangman(BufferedReader reader, PrintWriter writer) throws IOException {
        boolean playing = true;

        while (playing) {
            int id = generateRandomId();
            String word = getRandomWordFromDatabase(id);
            System.out.println(word);

            String maskedWord = getMaskedWord(word); // Initializez cuvantul cu "*"

            writer.println("WORD: " + maskedWord); // Trimit cuvantul catre client sub forma de "*"

            playing = playHangmanGame(reader, writer, word, maskedWord);

            String command = reader.readLine();
            System.out.println(command);
            if(command.equals("newGameHM")){
                // Daca primesc comanda de new game generez alt cuvant , il maschez , trimit un mesaj corespunzator catre client si pornez alt joc
                id = generateRandomId();
                word = getRandomWordFromDatabase(id);
                System.out.println(word);

                maskedWord = getMaskedWord(word);

                writer.println("WORD: " + maskedWord);
                playing=playHangmanGame(reader,writer,word,maskedWord);
            }
            else if (command.equals("backHM")) {
                // Daca primesc comanda de back trimit un mesaj corespunzator si oresc bucla
                System.out.println("big loop : Returning to the game mode menu...");
                return;
            }
        }
    }

    private static boolean playHangmanGame(BufferedReader reader, PrintWriter writer, String word, String maskedWord) throws IOException {
        int lives = 6;
        String guessedLetters = "";

        while (true) {
            String command = reader.readLine();
            System.out.println(command);

            if (command.startsWith("guess:")) {
                char guessedLetter = command.charAt(6);
                if (word.contains(String.valueOf(guessedLetter))) {
                    guessedLetters += guessedLetter;
                    // Daca cuvantul contine litera ghicita de catre client inlocuesc in cuvantul mascat litera in toate pozitiile in care apare
                    maskedWord = updateMaskedWord(word, maskedWord, guessedLetter);
                    // Trimit un mesaj catre client daca litera ghicita este buna , cu numarul de vieti ramase si cu cuvantul mascat updatat
                    writer.println("GOOD:" + lives + " : " + maskedWord);

                    if (maskedWord.equals(word)) {
                        //Daca cuvantul mascat este egal cu cuvantul initial inseamna ca , clientul a ghicit to cuvantul si a terminat jocul
                        writer.println("Congratulations! You won!:" + word);
                        return false;
                    }
                } else {
                    //Daca litera ghicita nu este corecta scad numarul de vieti si trimit un mesaj catre client
                    lives--;
                    writer.println("BAD:" + lives + " : " + maskedWord);

                    if (lives == 0) {
                        //Daca numarul de vieti a ajuns la 0 se termina jocul si clientul a pierdut
                        writer.println("GAME OVER:"+word);
                        return false;
                    }
                }
                writer.flush();
            }
            if (command.startsWith("newGameHM")) {
                // Daca primesc mesajul corespunzator de new game pentru jocul Hangman , iau alt cuvant random din baza de date
                // il maschez , reinitializez numarul de vieti , reinitializez litera ghicita , Trimit cuvantul mascat la client si pornesc alt joc
                int id = generateRandomId();
                word = getRandomWordFromDatabase(id);
                maskedWord = getMaskedWord(word);
                lives = 6;
                guessedLetters = "";
                writer.println("WORD: " + maskedWord);
                writer.flush();
                playHangmanGame(reader,writer,word,maskedWord);

            }
            if (command.startsWith("backHM")) {
                //Daca primesc comanda de back trimit un mesaj corespunzator catre client si opresc bucla
                System.out.println("small loop : Returning to the game mode menu...");
                writer.println("Returning to the game mode menu...");
                return false;
            }
        }
    }

    private static String getRandomWordFromDatabase(int id) {
        //Extrag cuvantul din baza de date pe baza id-ului random generat
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement("SELECT word FROM word WHERE id = ?")) {
            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("word").toUpperCase();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        throw new IllegalStateException("Failed to retrieve a word from the database.");
    }

    private static int generateRandomId() {
        //Generez un id random intre 1 si 169 , acestea sunt id-urile pentru cuvintele in limba romana di baza de date
        Random random = new Random();
        return random.nextInt(169) + 1;
    }

    private static String getMaskedWord(String word) {
        // returnez un sir de "*" de lungimea cuvantului primit
        return "*".repeat(word.length());
    }

    private static String updateMaskedWord(String word, String maskedWord, char guessedLetter) {
        // Schimb in sirul  de "*" ,pe pozitiile, literele ghicite the client care sunt corecte
        StringBuilder updatedWord = new StringBuilder(maskedWord);
        for (int i = 0; i < word.length(); i++) {
            if (word.charAt(i) == guessedLetter) {
                updatedWord.setCharAt(i, guessedLetter);
            }
        }
        return updatedWord.toString();
    }


    private static void handleRockPaperScissors(BufferedReader reader, PrintWriter writer) throws IOException {


        while (true) {
            // Generez un numar random intre 1 si 3
            Random random = new Random();
            int serverMove = random.nextInt(3) + 1;

            // Citesc de la client alegerea acestuia

            String playerMoveString = reader.readLine();

            int playerMove;
            // Salvez alegerea clientului in functie de mesajul primit , iar daca mesajul este backRPS opresc bucla
            if (playerMoveString.equalsIgnoreCase("rock")) {
                playerMove = 1;
            } else if (playerMoveString.equalsIgnoreCase("paper")) {
                playerMove = 2;
            } else if (playerMoveString.equalsIgnoreCase("scissors")) {
                playerMove = 3;
            } else if (playerMoveString.equalsIgnoreCase("backRPS")) {
                writer.println("Returning to game mode selection...");
                break;
            } else {
                writer.println("Invalid move!");
                continue;
            }

            // Compar alegerea clientului cu numarul generat random  si stabilesc srezultatul jocului
            String result;
            if (playerMove == serverMove) {
                result = "It's a tie!";
            } else if ((playerMove == 1 && serverMove == 2) || (playerMove == 2 && serverMove == 3) || (playerMove == 3 && serverMove == 1)) {
                result = "Server wins!";
            } else {
                result = "Player wins!";
            }

            // Trimit un mesaj catre client cu miscarea serverului , miscarea aleasa de acesta si rezultatul jocului
            String message = "Server move: " + serverMove + ", Player move: " + playerMove + ", Result: " + result;
            writer.println(message);
        }
    }

    private static void handleTicTacToe(BufferedReader reader, PrintWriter writer) throws IOException {
        //Trimit un mesaj catre client pentru alegerea modului de joc si initializez matricea pentru joc
        writer.println("Please select game mode: ");
        String gameMode;
        int[][] grid = new int[3][3];

        while ((gameMode = reader.readLine()) != null) {
            // Daca mesajul primit de la client este de back trimit un mesaj corespunzator si opresc bucla
            if (gameMode.equalsIgnoreCase("backTicTacToe")) {
                writer.println("Back to game menu.");
                break;
            }
            // In functie de modul de joc ales apelez metoda corespunzatoare
            if (gameMode.equalsIgnoreCase("PVP")) {
                playPvPGame(reader, writer, grid);
            } else if (gameMode.equalsIgnoreCase("AIHard")) {
                playAIGame(reader, writer, grid, false);
            } else if (gameMode.equalsIgnoreCase("AIEasy")) {
                playAIGame(reader, writer, grid, true);
            }
        }
    }

    private static void playPvPGame(BufferedReader reader, PrintWriter writer, int[][] grid) throws IOException {
        // Trimit un mesaj catre client co modul de joc ales
        writer.println("Starting Player vs Player game.");

        // initializez variabilele necesare intru joc
        boolean isXPlayerTurn = true;
        boolean newGameRequested = false;
        boolean gameOn = true;

        while (gameOn) {
            if (newGameRequested) {
                // verific daca este cerere pentru un joc nou
                isXPlayerTurn = true;
                resetGrid(grid);
                newGameRequested = false;
                writer.println("New game started.");
            } else {
                // verific daca jocul s-a terminat
                String gameResult = checkGameEnd(grid);
                boolean gameEnded = !gameResult.equals("Game not over yet");

                if (gameEnded) {
                    writer.println("Game ended. Grid state:");
                    printGridState(grid, writer);
                    writer.println(gameResult);
                    writer.flush();

                    // Daca jocul s-a terminat astept un alt mesaj de joc nou sau de back
                    String newGameCommand = null;
                    while (newGameCommand == null) {
                        newGameCommand = reader.readLine();
                        if (newGameCommand != null) {
                            if (newGameCommand.equalsIgnoreCase("newGamePVP")) {
                                newGameRequested = true;
                            } else if (newGameCommand.equalsIgnoreCase("backPVP")) {
                                writer.println("Back button pressed. Game ended.");
                                newGameRequested = true;
                                gameOn = false;
                            } else {
                                newGameCommand = null;
                            }
                        }
                    }
                    continue;
                }

                writer.println("Existing game state:");
                printGridState(grid, writer);
            }

            // Citesc te la client mutarile jucatorilor
            String move = reader.readLine();

            // daca jucatori au trims comada de new game sau back din modul de joc sau bac din joc le tratez ca atare
            if (move.equalsIgnoreCase("backPVP")) {
                writer.println("Back button pressed. Game ended.");
                resetGrid(grid);
                gameOn = false;
                break;
            } else if (move.equalsIgnoreCase("newGamePVP")) {
                writer.println("New game requested.");
                newGameRequested = true;
                continue;
            } else if (move.equalsIgnoreCase("backTicTacToe")) {
                writer.println("Back button pressed. Game ended.");
                break;
            }
            // updatez matricea cu mutarea jucatorului , si jucatorul curent
            makeMove(move, grid, isXPlayerTurn, writer);

            // Varific daca jocul s-a terminat
            String gameResult = checkGameEnd(grid);
            boolean gameEnded = !gameResult.equals("Game not over yet");

            // Schimb jucatorii
            isXPlayerTurn = !isXPlayerTurn;

        }
        //Daca jocul s-a terminat sau am iesit din joc resetez matricea
        resetGrid(grid);
    }

    private static void playAIGame(BufferedReader reader, PrintWriter writer, int[][] grid, boolean easyMode) throws IOException {
        // Trimit un mesaj clientului cu modul de joc
        writer.println("Starting Player vs AI game");

        // Initializez variabilele pentru joc
        boolean isXPlayerTurn = true;
        boolean newGameRequested = false;
        boolean gameOn = true;
        Random random = new Random();
        while (gameOn) {
            if (newGameRequested) {
                isXPlayerTurn = true;
                resetGrid(grid);
                newGameRequested = false;
                writer.println("New game started.");
            } else {
                String gameResult = checkGameEnd(grid);
                boolean gameEnded = !gameResult.equals("Game not over yet");

                if (gameEnded) {
                    writer.println("Game ended. Grid state:");
                    printGridState(grid, writer);
                    writer.println(gameResult);

                    String newGameCommand = null;
                    while (newGameCommand == null) {
                        newGameCommand = reader.readLine();
                        if (newGameCommand != null) {
                            if (newGameCommand.equalsIgnoreCase("newGameAI")) {
                                newGameRequested = true;
                            } else if (newGameCommand.equalsIgnoreCase("backAI")) {
                                writer.println("Back button pressed. Game ended.");
                                newGameRequested = true;
                                gameOn = false;

                            } else {
                                newGameCommand = null;
                            }
                        }
                    }

                    continue;
                }

                writer.println("Existing game state:");
                printGridState(grid, writer);
            }


            if (isXPlayerTurn) {
                // Citesc miscarea clientului
                String move = reader.readLine();

                // Varifica daca lientul a trimis o comanda de back sau new game
                if (move.equalsIgnoreCase("backAI")) {
                    writer.println("Back button pressed. Game ended.");
                    resetGrid(grid);
                    gameOn = false;
                    break;
                } else if (move.equalsIgnoreCase("newGameAI")) {
                    writer.println("New game requested.");
                    newGameRequested = true;
                    continue;
                }

                makeMove(move, grid, isXPlayerTurn, writer);
            } else {
                // Trimit un mesaj ca client ca este randul AI-ul sa faca o miscare
                writer.println("AI is making a move...");

                int moveRow;
                int moveCol;

                if (easyMode) {
                    // Daca modul de joc este easy generez o mutare random pana cand gasesc un spatiu liber
                    do {
                        moveRow = random.nextInt(3);
                        moveCol = random.nextInt(3);
                    } while (grid[moveRow][moveCol] != 0);
                } else {
                    // verific pe fiecare linie coloana sau diagonala daca exista deja doua simbolui de 0 pentru a completa linia coloana sau diagonala
                    // Daca nu exitsa verific daca exista doua simbolui de x pe fiecare line coloana sau diagonala si blochez x-ul in spatiu liber
                    // Daca nu exista mut random
                    if (grid[0][0] == 0 && ((grid[0][1] == 2 && grid[0][2] == 2) || (grid[1][0] == 2 && grid[2][0] == 2) || (grid[1][1] == 2 && grid[2][2] == 2))) {
                        moveRow = 0;
                        moveCol = 0;
                    } else if (grid[0][1] == 0 && ((grid[0][0] == 2 && grid[0][2] == 2) || (grid[1][1] == 2 && grid[2][1] == 2))) {
                        moveRow = 0;
                        moveCol = 1;
                    } else if (grid[0][2] == 0 && ((grid[0][0] == 2 && grid[0][1] == 2) || (grid[1][2] == 2 && grid[2][2] == 2) || (grid[1][1] == 2 && grid[2][0] == 2))) {
                        moveRow = 0;
                        moveCol = 2;
                    } else if (grid[1][0] == 0 && ((grid[0][0] == 2 && grid[2][0] == 2) || (grid[1][1] == 2 && grid[1][2] == 2))) {
                        moveRow = 1;
                        moveCol = 0;
                    } else if (grid[1][1] == 0 && ((grid[0][0] == 2 && grid[2][2] == 2) || (grid[0][2] == 2 && grid[2][0] == 2) || (grid[0][1] == -1 && grid[2][1] == -1) || (grid[1][0] == -1 && grid[1][2] == -1))) {
                        moveRow = 1;
                        moveCol = 1;
                    } else if (grid[1][2] == 0 && ((grid[0][2] == 2 && grid[2][2] == 2) || (grid[1][0] == 2 && grid[1][1] == 2))) {
                        moveRow = 1;
                        moveCol = 2;
                    } else if (grid[2][0] == 0 && ((grid[0][0] == 2 && grid[1][0] == 2) || (grid[2][1] == 2 && grid[2][2] == 2) || (grid[1][1] == 2 && grid[0][2] == 2))) {
                        moveRow = 2;
                        moveCol = 0;
                    } else if (grid[2][1] == 0 && ((grid[0][1] == 2 && grid[1][1] == 2) || (grid[2][0] == 2 && grid[2][2] == 2))) {
                        moveRow = 2;
                        moveCol = 1;
                    } else if (grid[2][2] == 0 && ((grid[0][2] == 2 && grid[1][2] == 2) || (grid[2][0] == 2 && grid[2][1] == 2) || (grid[0][0] == 2 && grid[1][1] == 2))) {
                        moveRow = 2;
                        moveCol = 2;
                    }else if (grid[0][0] == 0 && ((grid[0][1] == 1 && grid[0][2] == 1) || (grid[1][0] == 1 && grid[2][0] == 1) || (grid[1][1] == 1 && grid[2][2] == 1))) {
                        moveRow = 0;
                        moveCol = 0;
                    } else if (grid[0][1] == 0 && ((grid[0][0] == 1 && grid[0][2] == 1) || (grid[1][1] == 1 && grid[2][1] == 1))) {
                        moveRow = 0;
                        moveCol = 1;
                    } else if (grid[0][2] == 0 && ((grid[0][0] == 1 && grid[0][1] == 1) || (grid[1][2] == 1 && grid[2][2] == 1) || (grid[1][1] == 1 && grid[2][0] == 1))) {
                        moveRow = 0;
                        moveCol = 2;
                    } else if (grid[1][0] == 0 && ((grid[0][0] == 1 && grid[2][0] == 1) || (grid[1][1] == 1 && grid[1][2] == 1))) {
                        moveRow = 1;
                        moveCol = 0;
                    } else if (grid[1][1] == 0 && ((grid[0][0] == 1 && grid[2][2] == 1) || (grid[0][2] == 1 && grid[2][0] == 1) || (grid[0][1] == 1 && grid[2][1] == 1) || (grid[1][0] == 1 && grid[1][2] == 1))) {
                        moveRow = 1;
                        moveCol = 1;
                    } else if (grid[1][2] == 0 && ((grid[0][2] == 1 && grid[2][2] == 1) || (grid[1][0] == 1 && grid[1][1] == 1))) {
                        moveRow = 1;
                        moveCol = 2;
                    } else if (grid[2][0] == 0 && ((grid[0][0] == 1 && grid[1][0] == 1) || (grid[2][1] == 1 && grid[2][2] == 1) || (grid[1][1] == 1 && grid[0][2] == 1))) {
                        moveRow = 2;
                        moveCol = 0;
                    } else if (grid[2][1] == 0 && ((grid[0][1] == 1 && grid[1][1] == 1) || (grid[2][0] == 1 && grid[2][2] == 1))) {
                        moveRow = 2;
                        moveCol = 1;
                    } else if (grid[2][2] == 0 && ((grid[0][2] == 1 && grid[1][2] == 1) || (grid[2][0] == 1 && grid[2][1] == 1) || (grid[0][0] == 1 && grid[1][1] == 1))) {
                        moveRow = 2;
                        moveCol = 2;
                    }else {
                        do {
                            moveRow = random.nextInt(3);
                            moveCol = random.nextInt(3);
                        } while (grid[moveRow][moveCol] != 0);
                    }
                }


                grid[moveRow][moveCol] = isXPlayerTurn ? 1 : 2;

                writer.println("AI: " + moveRow + " " + moveCol);

                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                writer.flush();
            }

            // Vrific daca jocul s-a terminat
            String gameResult = checkGameEnd(grid);
            boolean gameEnded = !gameResult.equals("Game not over yet");

            // Schimb jucatorii
            isXPlayerTurn = !isXPlayerTurn;
        }
        //resetez matricea daca jocul s-a terminat
        resetGrid(grid);
    }

    private static void makeMove(String move, int[][] grid, boolean isXPlayerTurn, PrintWriter writer) {

        String[] moveCoords = move.split(" ");
        if (moveCoords.length != 2) {
            writer.println("Invalid move. Please try again.");
            return;
        }

        int row, col;
        try {
            row = Integer.parseInt(moveCoords[0]);
            col = Integer.parseInt(moveCoords[1]);
        } catch (NumberFormatException e) {
            writer.println("Invalid move. Please try again.");
            return;
        }

        if (row < 0 || row >= 3 || col < 0 || col >= 3 || grid[row][col] != 0) {
            writer.println("Invalid move. Please try again.");
            return;
        }

        grid[row][col] = isXPlayerTurn ? 1 : 2;
    }

    private static void resetGrid(int[][] grid) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                grid[i][j] = 0;
            }
        }
    }

    private static void printGridState(int[][] grid, PrintWriter writer) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                writer.print(grid[i][j] + " ");
            }
            writer.println();
        }
    }

    private static String checkGameEnd(int[][] grid) {
        // Verific daca sunt 3 simboluri identice pe fiecare linie
        for (int i = 0; i < 3; i++) {
            if (grid[i][0] != 0 && grid[i][0] == grid[i][1] && grid[i][0] == grid[i][2]) {
                return "Player " + (grid[i][0] == 1 ? "X" : "O") + " wins! Congratulations!";
            }
        }

        // Verific daca sunt 3 simboluri identice pe fiecare coloana
        for (int j = 0; j < 3; j++) {
            if (grid[0][j] != 0 && grid[0][j] == grid[1][j] && grid[0][j] == grid[2][j]) {
                return "Player " + (grid[0][j] == 1 ? "X" : "O") + " wins! Congratulations!";
            }
        }

        // Verific daca sunt 3 simboluri identice pe fiecare diagonala
        if (grid[0][0] != 0 && grid[0][0] == grid[1][1] && grid[0][0] == grid[2][2]) {
            return "Player " + (grid[0][0] == 1 ? "X" : "O") + " wins! Congratulations!";
        }
        if (grid[0][2] != 0 && grid[0][2] == grid[1][1] && grid[0][2] == grid[2][0]) {
            return "Player " + (grid[0][2] == 1 ? "X" : "O") + " wins! Congratulations!";
        }

        //Verific daca este egalitate
        boolean isDraw = true;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (grid[i][j] == 0) {
                    isDraw = false;
                    break;
                }
            }
        }
        if (isDraw) {
            return "It's a draw! Good game!";
        }

        return "Game not over yet";
    }


}
