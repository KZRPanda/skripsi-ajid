package com.example.skripsi4;

public class jaroWinklerDistance {

    public static double hitungJaroWinklerDistance(String word1, String word2) {
        double jaroDistance = hitungJaroDistance(word1, word2);
        int prefixLength = hitungPrefix(word1, word2);
        double jaroWinklerDistance = jaroDistance + (0.1 * prefixLength * (1 - jaroDistance));

        return jaroWinklerDistance;
    }

    private static double hitungJaroDistance(String word1, String word2) {
        if (word1.equals(word2)) {
            return 1.0;
        }
        int kataMirip = 0;
        int transpositions = 0;
        int maxDistance = Math.max(word1.length(), word2.length()) / 2 - 1;

        boolean[] word1Matched = new boolean[word1.length()];
        boolean[] word2Matched = new boolean[word2.length()];

        for (int i = 0; i < word1.length(); i++) {
            int start = Math.max(0, i - maxDistance);
            int end = Math.min(i + maxDistance + 1, word2.length());
            //System.out.println(word1Matched);
            for (int j = start; j < end; j++) {
                if (word1.charAt(i) == word2.charAt(j) && !word2Matched[j]) {
                    word1Matched[i] = true;
                    word2Matched[j] = true;
                    kataMirip++;
                    break;
                }
            }
        }

        if (kataMirip == 0) {
            return 0.0;
        }

        // Hitung jumlah transpositions
        int k = 0;
        for (int i = 0; i < word1.length(); i++) {
            if (word1Matched[i]) {
                int j;
                for (j = k; j < word2.length(); j++) {
                    if (word2Matched[j]) {
                        k = j + 1;
                        break;
                    }
                }
                if (word1.charAt(i) != word2.charAt(j)) {
                    transpositions++;
                }
            }
        }
        double hasil = (kataMirip / (double) word1.length() + kataMirip / (double) word2.length() + (kataMirip - transpositions / 2.0) / kataMirip) / 3.0;
        return hasil;
    }

    private static int hitungPrefix(String word1, String word2) {
        int prefix = 0;

        int maxLength = Math.min(4, Math.min(word1.length(), word2.length()));

        for (int i = 0; i < maxLength; i++) {
            if (word1.charAt(i) == word2.charAt(i)) {
                prefix++;
            } else {
                break;
            }
        }

        return prefix;
    }
}
