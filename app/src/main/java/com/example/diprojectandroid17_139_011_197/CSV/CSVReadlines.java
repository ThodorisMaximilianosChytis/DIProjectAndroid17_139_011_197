package com.example.diprojectandroid17_139_011_197.CSV;

import android.net.Uri;
import android.widget.Toast;
import com.opencsv.CSVReader;
import android.content.Context;



import java.io.*;
import java.util.List;

public class CSVReadlines {
 
    private List<String[]> csvBody;
    private InputStream csvInputS;
    Context context;


    public CSVReadlines(String csvURIString,Context _context) throws IOException {
        context= _context;
        Readfile(csvURIString);
    }


    private void Readfile(String csvURIString) throws IOException {

        csvInputS =  context.getContentResolver().openInputStream(Uri.parse(csvURIString));


        CSVReader reader = new CSVReader(new InputStreamReader(csvInputS));

        this.csvBody = reader.readAll();

    }


    public String[] getLine(int row){
        return csvBody.get(row);
    }

    public int getsize() {
        return  csvBody.size();
    }
}
