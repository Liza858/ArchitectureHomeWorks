package bash;

import java.io.*;
import java.util.Arrays;
import java.util.Scanner;

// CommandExecutor исполняет команды
public class CommandExecutor {

    private Parser parser;
    private CommandLine commandL;

    // конструктор
    public CommandExecutor(CommandLine cl) {
        parser = new Parser(new Environment());
        commandL = cl;
    }

    // В зависимости от того, какая команда пришла на вход -- выбирает метод, который её исполнит
    public String executeCommand(String command, String[] args, String resultOfLastCommand, int pipePosition) throws IOException {
        switch (command) {
            case ("cat"):
                return executeCat(args, resultOfLastCommand, pipePosition);
            case ("echo"):
                return executeEcho(args);
            case ("wc"):
                return executeWc(args, resultOfLastCommand, pipePosition);
            case ("pwd"):
                return executePwd(args);
            case ("exit"):
                return executeExit(args);
            default:
                String newCommand = command + " ";
                if (args.length == 0 & pipePosition > 1) {
                    command += resultOfLastCommand;
                } else {
                    for (String arg : args) {
                        newCommand = newCommand + arg + " ";
                    }
                }
                return executeOtherCommand(newCommand);
        }
    }

    // Выполняет команду cat. Если команда не первая в очереди pipe и у нее нет аргументов -- то
    // в качестве входа ей подается результат предыдущей команды.
    public String executeCat(String[] args, String resultOfLastCommand, int pipePosition) {
        if (args.length == 0 & pipePosition > 1) {
            return resultOfLastCommand;
        }
        if (args.length < 1) {
            return "error arguments for cat";
        }
        File file = new File(args[0]);
        String result = "";
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String text = null;
            while ((text = reader.readLine()) != null) {
                result += text;
                result += "\n";
            }
        } catch (FileNotFoundException e) {
            return "cat: file does not exist";
        } catch (IOException e) {
            return "catch IO exception";
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
            }
        }
        return result;
    }

    // Выполняет команду echo.
    public String executeEcho(String[] args) {
        String result = "";
        for (int i = 0; i < args.length; i++) {
            result += args[i];
            if (i != args.length - 1) {
                result += " ";
            }
        }
        return result;
    }

    // Выполняет команду wc. Если команда не первая в очереди pipe и у нее нет аргументов -- то
    // в качестве входа ей подается результат предыдущей команды.
    public String executeWc(String[] args, String resultOfLastCommand, int pipePosition) {
        if (args.length == 0 & pipePosition > 1) {
            return computeWcFromString(resultOfLastCommand);
        }
        if (args.length == 0) {
            return "error count of args";
        } else {
            return computeWcResultFromFile(args[0]);
        }
    }


    private String computeWcResultFromFile(String fileName) {
        String result = "";
        long linesCount = 0;
        long wordsCount = 0;
        long bytesCount = 0;
        File file = new File(fileName);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            linesCount = computeCountOfLines(reader);
        } catch (FileNotFoundException e) {
            return "cat: file does not exist";
        } catch (IOException e) {
            return "catch IO exception";
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            wordsCount = computeCountOfWords(reader);
        } catch (FileNotFoundException e) {
            return "cat: file does not exist";
        } catch (IOException e) {
            return "catch IO exception";
        }
        bytesCount = file.length();
        result = result + String.valueOf(linesCount) + " " + String.valueOf(wordsCount) + " " + String.valueOf(bytesCount);
        return result;
    }

    private int computeCountOfLines(BufferedReader reader) throws IOException {
        int c = 1;
        int linesCount = 0;
        while ((c = reader.read()) >= 0) {
            if ((char) c == '\n') {
                linesCount += 1;
            }
        }
        return linesCount;
    }

    private int computeCountOfWords(BufferedReader reader) throws IOException {
        String text = null;
        int wordsCount = 0;
        while ((text = reader.readLine()) != null) {
            String[] arr = text.trim().split("\\s+");
            if (arr.length != 0) {
                if (!arr[0].equals("")) {
                    wordsCount += arr.length;
                }
            }
        }
        return wordsCount;
    }

    private String computeWcFromString(String text) {
        long linesCount = 0;
        long wordsCount = 0;
        long bytesCount = 0;
        String result = "";
        int counter = 0;
        for (char c : text.toCharArray()) {
            if (c == '\n') {
                counter += 1;
            }
        }
        bytesCount += counter;
        String[] splitArgs = text.split("\n");
        linesCount = splitArgs.length;
        for (String str : splitArgs) {
            bytesCount += str.length();
            String[] splits = parser.splitByWhitespace(str);
            wordsCount += splits.length;
        }
        result = result + String.valueOf(linesCount) + " " + String.valueOf(wordsCount) + " " + String.valueOf(bytesCount);
        return result;
    }

    // Выполняет команду pwd
    public String executePwd(String[] args) {
        return new File("").getAbsolutePath();
    }

    // Выполняет команду exit.
    // Если у exit есть какие-то аргументы -- то ничего не делает.
    // Иначе вызывает метод exit() у командной строки.
    public String executeExit(String[] args) {
        if (args.length != 0) {
            return "error args of command exit";
        } else {
            commandL.exit();
            return "exit";
        }
    }

    // Выполняет команду bash
    // Возвращает её output в виде строки.
    public String executeOtherCommand(String command) throws IOException {
        StringBuilder result = new StringBuilder();
        Process process = new ProcessBuilder("bash", "-c", command).start();
        BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        String line;
        while ((line = errorReader.readLine()) != null) {
            result.append(line);
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        while ((line = reader.readLine()) != null) {
            result.append(line);
        }
        return result.toString();
    }
}