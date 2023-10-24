package com.example.skripsi4;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class convert {
    private Context context;
    private TextView fileContentTextView,jumHal;
    private PdfReader reader;
    private StringBuilder text = new StringBuilder();
    private int jumlahHalaman = 0;

    public convert(Context context, TextView textView) {
        this.context = context;
        this.jumHal = textView;
    }

    public String convertPdfToText(Uri uri,int n) {
        try {
            ContentResolver contentResolver = context.getContentResolver();
            InputStream inputStream = contentResolver.openInputStream(uri);

            if (inputStream != null) {
                reader = new PdfReader(inputStream);
                int totalPage = reader.getNumberOfPages();
                jumlahHalaman = totalPage;

                for (int page = n; page <= n; page++) {
                    text.append(PdfTextExtractor.getTextFromPage(reader, page));
                }
                reader.close();
                inputStream.close();
                jumHal.setText(Integer.toString(jumlahHalaman));
                //Toast.makeText(context, jumlahHalaman, Toast.LENGTH_SHORT).show();
                return text.toString();
                //fileContentTextView.setText(text.toString());
            }
        } catch (IOException e) {
            Toast.makeText(context, "Failed to convert PDF to text.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        return null;
    }

    public String convertPdfPage(Uri uri,int n){
        try {
            ContentResolver contentResolver = context.getContentResolver();
            InputStream inputStream = contentResolver.openInputStream(uri);

            if (inputStream != null) {
                reader = new PdfReader(inputStream);
                int totalPage = reader.getNumberOfPages();
                jumlahHalaman = totalPage;

                for (int page = n; page <= n/*totalPage*/; page++) {
                    text.append(PdfTextExtractor.getTextFromPage(reader, page));
                }
                reader.close();
                inputStream.close();
                return text.toString();
                //fileContentTextView.setText(text.toString());
            }
        } catch (IOException e) {
            Toast.makeText(context, "Failed to convert PDF to text.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<String> optionsHalaman(){
        ArrayList<String> temp = new ArrayList<>();
        for (int i = 0; i < jumlahHalaman; i++) {
            temp.add(Integer.toString(i+1));
        }

        return temp;
    }
}

