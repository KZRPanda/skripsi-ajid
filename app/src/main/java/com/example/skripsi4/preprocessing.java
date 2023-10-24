package com.example.skripsi4;

import com.share424.sastrawi.Dictionary.ArrayDictionary;
import com.share424.sastrawi.Stemmer.Stemmer;

import java.util.ArrayList;
import java.util.List;


public class preprocessing {
    public static List<String> stemming(List<String> kata,List<String> words) {
        ArrayDictionary dictionary = new ArrayDictionary(words);

        Stemmer stemmer = new Stemmer(dictionary);
        List<String> hasil = new ArrayList<>();

        for (String item : kata){
            hasil.add(stemmer.stem(item));
        }

        return hasil;
    }

    public static List<String> tokenizing(String thisArray){
        int i = 0;
        String output = thisArray.replaceAll("[^a-zA-Z0-9\\s-]", "");
        output = output.replaceAll("[\r]","");
        output = output.replaceAll("[\n]"," ");
        String[] temp = output.split(" ");
        List<String> hasil = new ArrayList<>();
        for (String item : temp){
            hasil.add(item);
        }
        return hasil;
    }
}

