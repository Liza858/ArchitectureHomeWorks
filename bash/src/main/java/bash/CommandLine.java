package bash;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.NoSuchElementException;
import java.util.Scanner;

// Класс командной строки. Считывает новые команды и выводит их результат.
public class CommandLine {

    private boolean execute;
    private Environment environment;
    private LinesExecutor linesExec;

    // конструктор
    public CommandLine() {
        execute = false;
        environment = new Environment();
        linesExec = new LinesExecutor(environment, this);
    }

    // Запускает бесконечный цикл, читает в этом цикле строки,
    // которые затем идут на обработку в LinesExecutor,
    // результат обработки команды -- строка, которая выводится
    // в интерпретатор. Цикл останавлиается вызовом метода exit().
    public void run() throws IOException {
        execute = true;
        System.out.printf("> ");
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        while (execute) {
                 String newLine = in.readLine();
                 if (!newLine.isEmpty()) {
                     String result = linesExec.execute(newLine);
                     if (!result.equals("")) {
                         System.out.println(result);
                     }
                     if (result.equals("exit")) {
                         exit();
                     }
                 }
            System.out.printf("> ");
        }
    }

    // Устанавливает переменной execute значение false
    public void exit() {
        execute = false;
    }
}