import java.util.Scanner;
import java.io.*;
import java.time.LocalDateTime;

public class pract15 {
    static int boardSize = 3;
    static char[][] board;
    static String playerX = "Player X";
    static String playerO = "Player O";

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        loadConfig();
        while (true) {
            System.out.println("\nМеню:");
            System.out.println("1. Грати");
            System.out.println("2. Налаштування");
            System.out.println("3. Переглянути статистику");
            System.out.println("4. Очистити статистику");
            System.out.println("5. Вийти");
            System.out.print("Виберіть опцію: ");
            if (!scanner.hasNextInt()) {
                scanner.nextLine();
                System.out.println("Невірний ввід.");
                continue;
            }
            int choice = scanner.nextInt();
            scanner.nextLine();
            switch (choice) {
                case 1 -> playGame(scanner);
                case 2 -> configureSettings(scanner);
                case 3 -> showStats();
                case 4 -> clearStats();
                case 5 -> { saveConfig(); System.out.println("Збережено. Вихід."); return; }
                default -> System.out.println("Невірний вибір.");
            }
        }
    }

    static void playGame(Scanner scanner) throws IOException {
        board = initBoard();
        char currentPlayer = 'X';
        while (true) {
            printBoard();
            int[] move = getMove(scanner);
            if (move[0] == -1) return;
            board[move[0]][move[1]] = currentPlayer;
            if (checkWin(currentPlayer)) {
                printBoard();
                System.out.println("Гравець " + currentPlayer + " переміг!");
                saveStats(currentPlayer);
                break;
            }
            if (isDraw()) {
                printBoard();
                System.out.println("Нічия!");
                saveStats('-');
                break;
            }
            currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
        }
    }

    static char[][] initBoard() {
        char[][] b = new char[boardSize][boardSize];
        for (int i = 0; i < boardSize; i++)
            for (int j = 0; j < boardSize; j++)
                b[i][j] = '-';
        return b;
    }

    static void printBoard() {
        for (char[] row : board) {
            for (char c : row) System.out.print(c + " ");
            System.out.println();
        }
    }

    static int[] getMove(Scanner scanner) {
        while (true) {
            System.out.print("Введіть рядок і стовпець (1-" + boardSize + ", або 0 0 для виходу): ");
            if (!scanner.hasNextInt()) {
                scanner.nextLine();
                System.out.println("Невірний ввід.");
                continue;
            }
            int r = scanner.nextInt();
            if (!scanner.hasNextInt()) {
                scanner.nextLine();
                System.out.println("Невірний ввід.");
                continue;
            }
            int c = scanner.nextInt();
            scanner.nextLine();
            if (r == 0 && c == 0) return new int[]{-1, -1};
            r--; c--;
            if (r >= 0 && r < boardSize && c >= 0 && c < boardSize && board[r][c] == '-')
                return new int[]{r, c};
            System.out.println("Невірний хід.");
        }
    }

    static boolean checkWin(char p) {
        for (int i = 0; i < boardSize; i++) {
            boolean row = true, col = true;
            for (int j = 0; j < boardSize; j++) {
                row &= board[i][j] == p;
                col &= board[j][i] == p;
            }
            if (row || col) return true;
        }
        boolean diag1 = true, diag2 = true;
        for (int i = 0; i < boardSize; i++) {
            diag1 &= board[i][i] == p;
            diag2 &= board[i][boardSize - i - 1] == p;
        }
        return diag1 || diag2;
    }

    static boolean isDraw() {
        for (char[] row : board)
            for (char c : row)
                if (c == '-') return false;
        return true;
    }

    static void configureSettings(Scanner scanner) {
        System.out.print("Введіть ім'я гравця X: ");
        playerX = scanner.nextLine();
        System.out.print("Введіть ім'я гравця O: ");
        playerO = scanner.nextLine();
        System.out.print("Введіть розмір поля (3-9): ");
        if (scanner.hasNextInt()) {
            int size = scanner.nextInt();
            scanner.nextLine();
            if (size >= 3 && size <= 9) boardSize = size;
        }
        System.out.println("Налаштування оновлено.");
    }

    static void showStats() throws IOException {
        File file = new File("game_history.txt");
        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) System.out.println(line);
            }
        } else {
            System.out.println("Статистика відсутня.");
        }
    }

    static void clearStats() throws IOException {
        File file = new File("game_history.txt");
        if (file.exists()) {
            try (PrintWriter pw = new PrintWriter(file)) {
                pw.print("");
            }
            System.out.println("Статистику очищено.");
        } else {
            System.out.println("Файл статистики не знайдено.");
        }
    }

    static void saveConfig() throws IOException {
        try (PrintWriter pw = new PrintWriter("config.txt")) {
            pw.println(boardSize);
            pw.println(playerX);
            pw.println(playerO);
        }
    }

    static void loadConfig() throws IOException {
        File file = new File("config.txt");
        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                boardSize = Integer.parseInt(br.readLine());
                playerX = br.readLine();
                playerO = br.readLine();
            }
        }
    }

    static void saveStats(char winner) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter("game_history.txt", true))) {
            pw.println(LocalDateTime.now() + " | " + playerX + " (X) vs " + playerO + " (O) | Розмір поля: " + boardSize + "x" + boardSize + " | Переможець: " +
                    (winner == 'X' ? playerX : winner == 'O' ? playerO : "Нічия"));
        }
    }
}
