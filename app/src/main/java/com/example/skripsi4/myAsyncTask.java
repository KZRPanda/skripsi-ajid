package com.example.skripsi4;

import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.share424.sastrawi.Stemmer.Stemmer;
import com.share424.sastrawi.Stemmer.StemmerFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class myAsyncTask extends AsyncTask<Void, Integer, ArrayList<String>> {
    private EditText isiFile;
    private Context myContext;
    private MainActivity mainActivity;
    private ArrayList<String> dataHasil,dataInput,dataSaran = new ArrayList<>();
    private ArrayList<Double> dataMirip;
    private StringBuilder perbaikan = new StringBuilder();
    private String inputStr,stemmedWord;;
    private StringBuilder stringBuilder = new StringBuilder();
    private ProgressBar myProgres;
    private JSONObject myJson;
    private TableLayout mytableLayout;
    private ArrayList<Integer> indexes = new ArrayList<>();

    public myAsyncTask(TableLayout tableLayout,EditText ed, ProgressBar progressBar, Context context, String string) {
        this.isiFile = ed;
        this.myContext = context;
        this.inputStr = string;
        this.stemmedWord = "";
        this.myProgres = progressBar;
        this.dataMirip = new ArrayList<Double>();
        this.mytableLayout = tableLayout;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.myProgres.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPostExecute(ArrayList<String> arrayList) {
        super.onPostExecute(arrayList);
        setWarna sw = new setWarna();
        int j = 0;

        for (int i : this.indexes){
            if(this.dataSaran.get(j).equals("-")){
                j++;
                continue;
            }
            this.dataInput.set(i,this.dataSaran.get(j));
            j++;
        }
        for (String item:this.dataInput){
            perbaikan.append(item).append(" ");
        }
        buatTabel();

        isiFile.setText(sw.setWarnaX(perbaikan.toString(),dataSaran,dataHasil));
        this.dataHasil = new ArrayList<>();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        int tempProgres = values[0];
        this.myProgres.setProgress(tempProgres);
    }

    @Override
    protected ArrayList<String> doInBackground(Void... voids) {
        int taskProgres = 0,max,progress = 1;
        ArrayList<String> list1 = (ArrayList<String>) tokenizing(this.inputStr);
        max = list1.size();
        this.myProgres.setMax(max);
        dataHasil = new ArrayList<>();
        dataInput = new ArrayList<>();

        StemmerFactory stemmerFactory = new StemmerFactory(this.myContext);
        Stemmer stemmer = stemmerFactory.create();
        for (String item : list1){
            String word = stemmer.stem(item);
            dataInput.add(item);
            this.stringBuilder.append(word);
            if(!cekSalah(word)) {
                dataHasil.add(word);
                try {
                    dataMirip.add(cariJaro(word));
                    indexes.add(progress-1);

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            if (progress < max){
                publishProgress(progress);
                progress += 1;
            }
        }

        return dataHasil;
    }


    public List<String> tokenizing(String thisArray){
        int i = 0;
        thisArray = thisArray.toLowerCase();
        String output = thisArray.replaceAll("[^a-zA-Z0-9\\s-/]", "");
        output = output.replaceAll("[\r]"," ");
        output = output.replaceAll("[\n]"," ");
        output = output.replaceAll("-"," ");
        output = output.replaceAll("/"," ");
        String[] temp = output.split(" ");
        List<String> hasil = new ArrayList<>();
        for (String item : temp){
            hasil.add(item);
        }
        return hasil;
    }

    private boolean cekSalah(String kata) {
        int panjang = kata.length();
        JSONArray tempObj = new JSONArray();

        kata = kata.toLowerCase();
        Pattern pattern = Pattern.compile(".*\\d+.*");
        Matcher matcher = pattern.matcher(kata);
        jaroWinklerDistance jwd = new jaroWinklerDistance();

        if(matcher.matches() || panjang == 1){
            return true;
        }
        if(panjang > 20){
            return false;
        }
        try {
            tempObj = this.myJson.getJSONArray(Integer.toString(panjang));
            //dataMirip.add(cariJaro(kata));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        for (int i = 0;i < tempObj.length();i++){
            try {
                if(tempObj.getString(i).equals(kata)){
                    return true;
                }else{
                    continue;
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
        return false;
    }

    public void setJson(JSONObject json){
        this.myJson = json;
    }

    public void buatTabelHeader(){
        TableRow tableRow = new TableRow(myContext);

        TextView textView1 = new TextView(myContext);
        TextView textView2 = new TextView(myContext);
        TextView textView3 = new TextView(myContext);

        textView1.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        textView2.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        textView3.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        TableRow.LayoutParams tl = new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT);
        tl.setMargins(5,5,5,5);
        tl.weight = 1;

        textView1.setLayoutParams(tl);
        textView2.setLayoutParams(tl);
        textView3.setLayoutParams(tl);

        Typeface typeface = textView1.getTypeface();
        textView1.setTypeface(typeface, Typeface.BOLD);

        typeface = textView2.getTypeface();
        textView2.setTypeface(typeface, Typeface.BOLD);

        typeface = textView3.getTypeface();
        textView3.setTypeface(typeface, Typeface.BOLD);

        textView1.setText("Kata");
        textView2.setText("Saran");
        textView3.setText("Kemiripan");

        tableRow.addView(textView1);
        tableRow.addView(textView2);
        tableRow.addView(textView3);

        mytableLayout.addView(tableRow);
    }
    public void buatTabel(){
        int i = 0, temp = mytableLayout.getChildCount();
        if(temp >= 2){
            mytableLayout.removeAllViews();
            buatTabelHeader();
        }
        for (String item : dataHasil){
            TableRow tableRow = new TableRow(myContext);
            //TextView[] mytext = new TextView[3];

            TextView textView1 = new TextView(myContext);
            TextView textView2 = new TextView(myContext);
            TextView textView3 = new TextView(myContext);

            textView1.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            textView2.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            textView3.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            TableRow.LayoutParams tl = new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT);
            tl.setMargins(5,5,5,5);
            tl.weight = 1;

            textView1.setLayoutParams(tl);
            textView2.setLayoutParams(tl);
            textView3.setLayoutParams(tl);

            textView1.setText(item);
            textView2.setText(dataSaran.get(i));

            DecimalFormat decimalFormat = new DecimalFormat("#.###");
            String result = decimalFormat.format(dataMirip.get(i));
            textView3.setText(result);

            i++;

            tableRow.addView(textView1);
            tableRow.addView(textView2);
            tableRow.addView(textView3);

            mytableLayout.addView(tableRow);
        }
    }

    public Double cariJaro(String kata) throws JSONException {
        ArrayList<Double> hasil = new ArrayList<>();
        jaroWinklerDistance jwd = new jaroWinklerDistance();
        double kemiripan = 0;
        int tempLong = kata.length();
        tempLong = (tempLong < 0)?1:tempLong;
        tempLong = (tempLong > 31)?31:tempLong;
        tempLong = (tempLong == 30)?31:tempLong;
        String kataSaran = "-";
        JSONArray jsonArray = new JSONArray();
        int kataMirip = 0;
        ArrayList<Integer> panjang = new ArrayList<>();

        if(tempLong - 2 > 0){
            panjang.add(tempLong-2);
            panjang.add(tempLong-1);
            panjang.add(tempLong);
            panjang.add(tempLong+1);
            panjang.add(tempLong+2);
        }else{
            panjang.add(tempLong-1);
            panjang.add(tempLong);
            panjang.add(tempLong+1);
            panjang.add(tempLong+2);
        }

        if(tempLong == 26 || tempLong == 27 || tempLong == 30 || tempLong > 31){
            hasil.add(kemiripan);
            this.dataSaran.add(kataSaran);
            return kemiripan;
        }
        for (int x : panjang){
            if(x == 26 || x == 27 || x == 30 || x > 31){
                continue;
            }
            try {
                jsonArray = this.myJson.getJSONArray(Integer.toString(x));

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            for (int i = 0; i < jsonArray.length(); i++) {
                String dicocokan = jsonArray.getString(i);
                int minimalHuruf = (int) (Math.min(kata.length(), dicocokan.length()));
                if(hitungHurufMirip(dicocokan,kata) < minimalHuruf){
                    continue;
                }
                if(dicocokan.equals("aritmetika")){
                    Log.d("panjang", Integer.toString(x) + " : "+ dicocokan + " - " + kata + " : " + Double.toString(jwd.hitungJaroWinklerDistance(kata,dicocokan)));
                }
//                if(kata.equals("pekejaan")){
//                    Log.d("kemiripan", kata+" : "+dicocokan+" = "+jwd.hitungJaroWinklerDistance(kata,dicocokan) + " : "+hitungHurufMirip(dicocokan,kata) + " : " +minimalHuruf);
//                }

                if(jwd.hitungJaroWinklerDistance(kata,dicocokan) > kemiripan){
                    Log.d("kemiripan", kata+" : "+dicocokan+" = "+jwd.hitungJaroWinklerDistance(kata,dicocokan)+" : "+kemiripan);
                    kemiripan = jwd.hitungJaroWinklerDistance(kata,dicocokan);
                    kataMirip = hitungHurufMirip(dicocokan,kata);
                    kataSaran = dicocokan;
                }
          }
        }
        hasil.add(kemiripan);
        this.dataSaran.add(kataSaran);
        return kemiripan;
    }

    private static int hitungHurufMirip(String string1, String string2) {
        int count = 0;

        string1 = string1.toLowerCase();
        string2 = string2.toLowerCase();

        for (int i = 0; i < string1.length(); i++) {
            if (string2.contains(String.valueOf(string1.charAt(i)))) {
                count++;
            }
        }

        return count;
    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }
}
