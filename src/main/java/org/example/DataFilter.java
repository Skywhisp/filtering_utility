package org.example;

import org.apache.commons.cli.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.math.BigInteger;

public class DataFilter {
    private static final String INTEGER_FILE = "integers.txt";
    private static final String FLOAT_FILE = "floats.txt";
    private static final String STRING_FILE = "strings.txt";

    private static final Options OPTIONS = new Options();
    private static boolean shortStat = false;
    private static boolean fullStat = false;
    private static boolean append = false;
    private static String outputPath = ".";
    private static String prefix = "";

    static {
        OPTIONS.addOption("s", "short", false, "short statistics");
        OPTIONS.addOption("f", "full", false, "full statistics");
        OPTIONS.addOption("o", "output", true, "output path");
        OPTIONS.addOption("p", "prefix", true, "output file prefix");
        OPTIONS.addOption("a", "append", false, "append to existing files");
    }

    public void processData(String[] args) {
        if (args == null || args.length == 0 || Arrays.stream(args).noneMatch(arg -> !arg.trim().isEmpty())) {
            System.out.println("Не указаны пути к входным файлам. Ничего не будет обработано.");
            return;
        }

        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(OPTIONS, args);
            shortStat = cmd.hasOption("s");
            fullStat = cmd.hasOption("f");
            if (cmd.hasOption("o")) {
                outputPath = cmd.getOptionValue("o");
            }
            if (cmd.hasOption("p")) {
                prefix = cmd.getOptionValue("p");
            }
            append = cmd.hasOption("a");

            List<Path> filePaths = new ArrayList<>();
            for (String arg : cmd.getArgs()) {
                filePaths.add(Paths.get(arg));
            }

            processFiles(filePaths);
        } catch (ParseException error) {
            System.err.println("Ошибка при попытке парсинга аргументов cmd: " + error.getMessage());
        }
    }

    private static void processFiles(List<Path> filePaths) {
        if (filePaths.isEmpty()) {
            System.out.println("Не указаны входные файлы. Завершение работы без обработки.");
            return;
        }

        List<String> integers = new ArrayList<>();
        List<String> floats = new ArrayList<>();
        List<String> strings = new ArrayList<>();

        for (Path filePath : filePaths) {
            if (Files.exists(filePath) && Files.isReadable(filePath)) {
                try (BufferedReader reader = Files.newBufferedReader(filePath)) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (isInteger(line)) {
                            integers.add(line);
                        } else if (isFloat(line)) {
                            floats.add(line);
                        } else {
                            strings.add(line);
                        }
                    }
                } catch (IOException error) {
                    System.err.println("Ошибка при попытке прочтения файла: " + filePath);
                }
            } else {
                System.err.println("Файл не существует или недоступен для чтения: " + filePath);
            }
        }

        Path outputDirectory = Paths.get(outputPath);
        try {
            if (!Files.exists(outputDirectory)) {
                Files.createDirectories(outputDirectory);
            }
        } catch (IOException error) {
            System.err.println("Ошибка при создании директории для выходных файлов: " + outputDirectory);
            return;
        }

        if (!integers.isEmpty()) {
            writeToFile(integers, getOutputPath(INTEGER_FILE));
        }

        if (!floats.isEmpty()) {
            writeToFile(floats, getOutputPath(FLOAT_FILE));
        }

        if (!strings.isEmpty()) {
            writeToFile(strings, getOutputPath(STRING_FILE));
        }

        if (shortStat) {
            printShortStatistics(integers, floats, strings);
        }
        if (fullStat) {
            printFullStatistics(integers, floats, strings);
        }
    }

    private static boolean isInteger(String s) {
        try {
            new BigInteger(s);
            return true;
        } catch (NumberFormatException error) {
            return false;
        }
    }

    private static boolean isFloat(String str) {
        try {
            Float.parseFloat(str);
            return true;
        } catch (NumberFormatException error) {
            return false;
        }
    }

    private static void writeToFile(List<String> lines, Path filePath) {
        try (BufferedWriter writer = Files.newBufferedWriter(filePath, getOpenOptions())) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException error) {
            System.err.println("Ошибка при попытке записи файла: " + filePath);
        }
    }

    private static StandardOpenOption[] getOpenOptions() {
        return new StandardOpenOption[]{
                StandardOpenOption.CREATE,
                append ? StandardOpenOption.APPEND : StandardOpenOption.TRUNCATE_EXISTING
        };
    }

    private static Path getOutputPath(String fileName) {
        return Paths.get(outputPath, prefix + fileName);
    }

    private static void printShortStatistics(List<String> integers, List<String> floats, List<String> strings) {
        System.out.println("Краткая статистика:");
        System.out.println("Integers: " + integers.size());
        System.out.println("Floats: " + floats.size());
        System.out.println("Strings: " + strings.size());
    }

    private static void printFullStatistics(List<String> integers, List<String> floats, List<String> strings) {
        System.out.println("Полная статистика:");
        printIntStatistics(integers);
        printFloatStatistics(floats);
        printStringStatistics(strings);
    }

    private static void printIntStatistics(List<String> integers) {
        if (integers.isEmpty()) {
            System.out.println("Integers отсутствуют");
            return;
        }

        BigInteger min = new BigInteger(integers.getFirst());
        BigInteger max = new BigInteger(integers.getFirst());
        BigInteger sum = BigInteger.ZERO;

        for (String integer : integers) {
            BigInteger value = new BigInteger(integer);
            min = min.min(value);
            max = max.max(value);
            sum = sum.add(value);
        }

        BigInteger average = sum.divide(BigInteger.valueOf(integers.size()));

        System.out.println("Integers: " + "количество чисел = " + integers.size() +
                ", мин. число = " + min + ", макс. число = " + max +
                ", сумма чисел = " + sum + ", среднее значение = " + average);
    }

    private static void printFloatStatistics(List<String> floats) {
        if (floats.isEmpty()) {
            System.out.println("Целые числа отсутствуют");
            return;
        }

        float min = Float.MAX_VALUE;
        float max = Float.MIN_VALUE;
        double sum = 0;
        for (String floatStr : floats) {
            float value = Float.parseFloat(floatStr);
            min = Math.min(min, value);
            max = Math.max(max, value);
            sum += value;
        }
        double avg = sum / floats.size();

        System.out.println("Floats: " + "количество чисел = " + floats.size() + ", мин. число =" + min
                + ", макс. число = " + max + "," + " сумма чисел = " + sum + ", среднее значение = " + avg);
    }

    private static void printStringStatistics(List<String> strings) {
        if (strings.isEmpty()) {
            System.out.println("Строки отсутсвуют");
            return;
        }

        int minLength = Integer.MAX_VALUE;
        int maxLength = 0;
        for (String string : strings) {
            minLength = Math.min(minLength, string.length());
            maxLength = Math.max(maxLength, string.length());
        }

        System.out.println("Strings: " + "количество строк = " + strings.size() + ", мин. длина строки = "
                + minLength + "," + " макс. длина строки = " + maxLength);
    }
}