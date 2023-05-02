package helpers;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileHelper
{
    public static void writeStringData(CopyOnWriteArrayList<String[]> requestData) throws IOException
    {
        File csvOutputFile = new File("outputCSV.csv");
        try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
            requestData.stream()
                    .map(x -> convertToCSV(x))
                    .forEach(pw::println);
        }
    }

    private static String convertToCSV(String[] data) {
        return Stream.of(data)
                .collect(Collectors.joining(","));
    }
}
