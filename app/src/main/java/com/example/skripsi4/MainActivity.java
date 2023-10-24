package com.example.skripsi4;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {
    private static final int PICK_FILE_REQUEST_CODE = 1;
    public int tekan = 0;
    private Spinner pilihHalaman;
    private Button pilihfile,konversi;
    private TextView alamatFile;
    public Uri uri;
    private TextView isiFile,alamat;
    public ArrayList<String> arrayList = new ArrayList<>();
    public ArrayList<String> options = new ArrayList<>();
    //public ArrayList<String> tokeniz = new ArrayList<>();
    public String hasilProses,tokeniz;
    public ProgressBar progressBar;
    public JSONObject myJsonObj;
    public TableLayout tableLayout;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pilihfile = findViewById(R.id.btn_pick_file);
        alamatFile = findViewById(R.id.txt_file_path);
        isiFile = findViewById(R.id.txt_file_content);
        konversi = findViewById(R.id.buttonConvert);
        progressBar = findViewById(R.id.progressBar);
        pilihHalaman = findViewById(R.id.pilih);
        tableLayout = findViewById(R.id.hasil);

        AssetManager assetManager = getAssets();
        getJSON();
        try {
            // Membuka file .txt sebagai InputStream
            InputStream inputStream = assetManager.open("rootwords.txt");

            // Membaca konten file menggunakan BufferedReader
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                // Menambahkan setiap baris teks ke ArrayList
                arrayList.add(line);
            }

            // Menutup BufferedReader
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        pilihfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFilePicker();
            }
        });

        konversi.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                jaroWinklerDistance jaro = new jaroWinklerDistance();
                Double nilai = jaro.hitungJaroWinklerDistance("nasi","mur");
                StringBuilder sb = new StringBuilder();
                EditText isiContent;
                isiContent = findViewById(R.id.txt_file_content);

                myAsyncTask ms = new myAsyncTask(tableLayout,isiContent,progressBar,getApplicationContext(),hasilProses);
                ms.setMainActivity(MainActivity.this);
                ms.setJson(myJsonObj);


                List<String> datahasil = new ArrayList<>();
                ms.execute();
                AsyncTask.Status status = ms.getStatus();
                if(status == AsyncTask.Status.RUNNING){

                }else if(status == AsyncTask.Status.FINISHED){
                    alamat.setText("Berhasil!");
                    Toast.makeText(MainActivity.this, "selesai proses", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, PICK_FILE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        TextView textView = findViewById(R.id.halaman);
        if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uri = data.getData();
            String filePath = uri.toString();
            alamatFile.setText(getFileName(uri));

            convert myconvert = new convert(MainActivity.this,textView);
            hasilProses = myconvert.convertPdfToText(uri,1);

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, myconvert.optionsHalaman());
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            pilihHalaman.setAdapter(adapter);
            tokeniz = tokenizing(hasilProses);
            isiFile.setText(tokeniz);

            pilihHalaman.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    tekan++;
                    convert myconvert = new convert(MainActivity.this,textView);
                    if(tekan > 1){
                        tokeniz = tokenizing(myconvert.convertPdfPage(uri,i+1));
                        isiFile.setText(tokeniz);
                        hasilProses = tokeniz;
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }

            });

            Toast.makeText(MainActivity.this,"Berhasil",Toast.LENGTH_LONG).show();
        }
    }

    private String getFileName(Uri uri) {
        String fileName = null;
        if (uri.getScheme().equals("file")) {
            File file = new File(uri.getPath());
            fileName = file.getName();
        } else {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (nameIndex != -1) {
                    fileName = cursor.getString(nameIndex);
                }
                cursor.close();
            }
        }
        return fileName;
    }
    private String loadJSONFromAsset() {
        String json = null;
        try {
            //AssetManager assetManager = getAssets();
            InputStream inputStream = getAssets().open("data.json");
            //Toast.makeText(MainActivity.this,"pler",Toast.LENGTH_LONG).show();
            Scanner scanner = new Scanner(inputStream);
            scanner.useDelimiter("\\A");
            json = scanner.next();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return json;
    }
    private void getJSON() {
        String jsonString = loadJSONFromAsset();
        try {
            JSONObject jsonObj = new JSONObject(jsonString);
            myJsonObj = jsonObj;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static String tokenizing(String thisArray){
        int i = 0;
        String output = thisArray.replaceAll("[^a-zA-Z0-9\\s-/]", "");
        output = output.replaceAll("[\r]"," ");
        output = output.replaceAll("[\n]"," ");
        output = output.replaceAll("/"," ");
        String[] temp = output.split(" ");
        List<String> hasil = new ArrayList<>();
        StringBuilder tempHasil = new StringBuilder();
        for (String item : temp){
            tempHasil.append(item).append(" ");
            //hasil.add(item);
        }
        return tempHasil.toString();
    }

}
