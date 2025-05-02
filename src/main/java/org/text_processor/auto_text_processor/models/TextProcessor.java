package org.text_processor.auto_text_processor.models;

import org.text_processor.auto_text_processor.models.base.FileOperations;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;




public class TextProcessor implements FileOperations {


        private static final Logger LOGGER = Logger.getLogger(TextProcessor.class.getName());


        public BufferedReader readFile(String filepath) throws IOException {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(filepath));
                return reader;
            } catch (IOException e) {
                throw new FileNotFoundException("Failed to open file");
            }

        }

        public Stream<String> preProcessText (BufferedReader lines){
            String fullText = lines.lines().collect(Collectors.joining(" "));

            return Arrays.stream(fullText.split("(?<=[.!?])\\s+"));
        }

        public String retrieveTextContentFromFile(String filepath, int batchSize) throws IOException {
            BufferedReader reader = readFile(filepath);

            Stream<String> sentences = preProcessText(reader);

            StringBuilder content = new StringBuilder();

            BatchProcessor.batchStreamOf(sentences, batchSize)
                    .forEach(batch -> {
                        batch.forEach(sentence -> content.append(sentence).append("\n"));
                    });

            return content.toString();

        }



        public List<String> findMatchesUsingRegex(String pattern, String text){
            List<String> matches = new ArrayList<>();
            Pattern regexPattern = Pattern.compile(pattern);
            Matcher regexMatcher = regexPattern.matcher(text);

            while(regexMatcher.find()){
                String matchedString = regexMatcher.group();
                matches.add(matchedString);
            }

            return  matches;
        }

        public Integer countPatterOccurences(List<String> matches){
            int count = 0;
            for (String match:matches){
                System.out.println(match);
                count++;
            }

            return count;
        }

        public Long wordFrequency(String content, String text) {
            return Arrays.stream(content.split("\\s+"))
                    .map(word -> word.replaceAll("^[^a-zA-Z0-9]+|[^a-zA-Z0-9]+$", ""))
                    .map(String::toLowerCase)
                    .filter(word -> word.equals(text.toLowerCase()))
                    .count();
        }

        public String summarizeText(String text) {
            List<String> stopWords = List.of(
                    "a", "an", "and", "the", "is", "in", "on", "at", "for", "to", "with", "of", "by", "from",
                    "this", "that", "it", "as", "are", "was", "were", "be", "been", "has", "had", "have", "but", "or"
            );

            return Arrays.stream(text.split("\\s+"))
                    .map(String::toLowerCase)
                    .filter(word -> !stopWords.contains(word))
                    .collect(Collectors.joining(" "));
        }


        public void replacePatternsInFile(String pattern, String replacement, String filePath) throws FileNotFoundException {
            Stream<String> lines = null;
            try {
                lines = readFile(filePath).lines();
                List<String> replaced = lines
                        .map(line -> line.replaceAll(pattern, replacement))
                        .toList();
                Files.write(Path.of(filePath), replaced);
            } catch (IOException e) {
                throw new FileNotFoundException("Error writing to file");
            }


        }

        public String replacePatternsInText(String pattern, String replacement, String text) {
            return Arrays.stream(text.split("\n"))
                    .map(line -> line.replaceAll(pattern, replacement))
                    .collect(Collectors.joining("\n"));
        }



}
