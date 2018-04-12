package nl.sogeti.reactivespring.util;

import org.springframework.util.ResourceUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Utils {

    /**
     * Convenience method to read data from a file into a {@link List<String>}
     *
     * @param resourceLocation
     * @return
     * @throws IOException
     */
    public static List<String> readDataFromResource(String resourceLocation)
            throws IOException {
        File bitcoinDataFile = ResourceUtils.getFile(resourceLocation);
        FileInputStream inputStream = new FileInputStream(bitcoinDataFile);
        List<String> lines =new ArrayList<>();
        try (BufferedReader br
                     = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        }
        return lines;
    }


}
