package com.example.diprojectandroid17_139_011_197.CSV;

import android.widget.Toast;
import com.opencsv.CSVReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class CSVReadlines {
    private File csvfile;
    private List<String[]> csvBody;

    public CSVReadlines(String csvFilepath) throws IOException {
        Readfile(csvFilepath);
    }


    private void Readfile(String csvfilepath) throws IOException {

        csvfile = new File(csvfilepath);

        CSVReader reader = new CSVReader(new FileReader(csvfile.getAbsolutePath()));
        this.csvBody = reader.readAll();

    }


    public String[] getLine(int row){
        return csvBody.get(row);
    }

    public int getsize() {
        return  csvBody.size();
    }
}
