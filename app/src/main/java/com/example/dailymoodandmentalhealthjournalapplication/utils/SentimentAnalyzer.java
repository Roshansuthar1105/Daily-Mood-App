package com.example.dailymoodandmentalhealthjournalapplication.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Simple sentiment analysis utility for analyzing journal entries.
 */
public class SentimentAnalyzer {
    // Positive words dictionary
    private static final List<String> POSITIVE_WORDS = Arrays.asList(
            "happy", "joy", "excited", "amazing", "wonderful", "great", "good", "excellent",
            "fantastic", "delighted", "pleased", "glad", "cheerful", "content", "satisfied",
            "grateful", "thankful", "blessed", "love", "loving", "hope", "hopeful", "positive",
            "optimistic", "motivated", "inspired", "proud", "confident", "peaceful", "calm",
            "relaxed", "energetic", "enthusiastic", "thrilled", "accomplished", "successful"
    );

    // Negative words dictionary
    private static final List<String> NEGATIVE_WORDS = Arrays.asList(
            "sad", "unhappy", "depressed", "miserable", "gloomy", "disappointed", "upset",
            "frustrated", "angry", "mad", "annoyed", "irritated", "furious", "enraged",
            "anxious", "worried", "nervous", "stressed", "tense", "afraid", "scared", "fearful",
            "terrified", "lonely", "alone", "isolated", "abandoned", "rejected", "hurt",
            "pain", "suffering", "grief", "regret", "guilty", "ashamed", "embarrassed",
            "hopeless", "helpless", "worthless", "tired", "exhausted", "sick", "ill"
    );

    /**
     * Analyze the sentiment of a text.
     *
     * @param text The text to analyze
     * @return A sentiment score between -1.0 (very negative) and 1.0 (very positive)
     */
    public static float analyzeSentiment(String text) {
        if (text == null || text.isEmpty()) {
            return 0.0f;
        }

        // Convert to lowercase and split into words
        String[] words = text.toLowerCase().replaceAll("[^a-zA-Z\\s]", "").split("\\s+");
        
        int positiveCount = 0;
        int negativeCount = 0;
        
        for (String word : words) {
            if (POSITIVE_WORDS.contains(word)) {
                positiveCount++;
            } else if (NEGATIVE_WORDS.contains(word)) {
                negativeCount++;
            }
        }
        
        // Calculate sentiment score
        int totalSentimentWords = positiveCount + negativeCount;
        if (totalSentimentWords == 0) {
            return 0.0f;
        }
        
        return (float) (positiveCount - negativeCount) / totalSentimentWords;
    }

    /**
     * Get the most frequent emotions in a text.
     *
     * @param text The text to analyze
     * @param limit The maximum number of emotions to return
     * @return A map of emotions and their frequencies
     */
    public static Map<String, Integer> getEmotions(String text, int limit) {
        if (text == null || text.isEmpty()) {
            return new HashMap<>();
        }

        // Emotion words dictionary
        Map<String, String> emotionWords = new HashMap<>();
        emotionWords.put("happy", "happiness");
        emotionWords.put("joy", "happiness");
        emotionWords.put("excited", "excitement");
        emotionWords.put("sad", "sadness");
        emotionWords.put("unhappy", "sadness");
        emotionWords.put("depressed", "depression");
        emotionWords.put("angry", "anger");
        emotionWords.put("mad", "anger");
        emotionWords.put("furious", "anger");
        emotionWords.put("anxious", "anxiety");
        emotionWords.put("worried", "anxiety");
        emotionWords.put("nervous", "anxiety");
        emotionWords.put("afraid", "fear");
        emotionWords.put("scared", "fear");
        emotionWords.put("terrified", "fear");
        emotionWords.put("lonely", "loneliness");
        emotionWords.put("alone", "loneliness");
        emotionWords.put("isolated", "loneliness");
        emotionWords.put("grateful", "gratitude");
        emotionWords.put("thankful", "gratitude");
        emotionWords.put("love", "love");
        emotionWords.put("loving", "love");
        emotionWords.put("hope", "hope");
        emotionWords.put("hopeful", "hope");
        emotionWords.put("proud", "pride");
        emotionWords.put("confident", "confidence");
        emotionWords.put("peaceful", "peace");
        emotionWords.put("calm", "calmness");
        emotionWords.put("relaxed", "relaxation");

        // Convert to lowercase and split into words
        String[] words = text.toLowerCase().replaceAll("[^a-zA-Z\\s]", "").split("\\s+");
        
        // Count emotions
        Map<String, Integer> emotionCounts = new HashMap<>();
        for (String word : words) {
            if (emotionWords.containsKey(word)) {
                String emotion = emotionWords.get(word);
                emotionCounts.put(emotion, emotionCounts.getOrDefault(emotion, 0) + 1);
            }
        }
        
        // Sort emotions by frequency and limit the result
        return sortByValueAndLimit(emotionCounts, limit);
    }

    /**
     * Sort a map by value in descending order and limit the result.
     *
     * @param map The map to sort
     * @param limit The maximum number of entries to return
     * @return A sorted and limited map
     */
    private static <K, V extends Comparable<? super V>> Map<K, V> sortByValueAndLimit(Map<K, V> map, int limit) {
        Map<K, V> result = new HashMap<>();
        map.entrySet().stream()
                .sorted(Map.Entry.<K, V>comparingByValue().reversed())
                .limit(limit)
                .forEach(entry -> result.put(entry.getKey(), entry.getValue()));
        return result;
    }
}
