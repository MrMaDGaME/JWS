package fr.epita.assistant.jws.presentation.converter;

import javax.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@ApplicationScoped
public class RleCoder {
    public List<String> rleEncoded(String input){
        List<String> result = new ArrayList<String>();
        for (int i = 0; i < 15; i++){
            result.add(encodeString(input.substring(i * 17, (i + 1) * 17)));
        }
        return result;
    }

    public String encodeString(String str){
        // stores output string
        String encoding = "";
        // base case
        if (str == null) {
            return encoding;
        }
        int count;
        for (int i = 0; i < str.length(); i++)
        {
            // count occurrences of character at index `i`
            count = 1;
            while (count < 9 && i + 1 < str.length() && str.charAt(i) == str.charAt(i + 1))
            {
                count++;
                i++;
            }
            // append current character and its count to the result
            encoding += String.valueOf(count) + str.charAt(i);
        }
        return encoding;
    }

    public String rleDecoder(String path){
        List<String> list = new ArrayList<>();
        try (Stream<String> stream = Files.lines(Paths.get(path))) { stream.forEach(list::add); }
        catch (IOException e) { e.printStackTrace();
        }
        StringBuilder result = new StringBuilder();
        list.forEach(str -> result.append(decodeString(str)));
        return result.toString();
    }

    public String decodeString(String str){
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < str.length(); i+=2){
            for (int j = 0; j < Integer.parseInt(String.valueOf(str.charAt(i))); j++){
                result.append(str.charAt(i + 1));
            }
        }
        return result.toString();
    }
}
