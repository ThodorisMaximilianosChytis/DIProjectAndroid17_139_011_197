package com.example.diprojectandroid17_139_011_197.CSV;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.diprojectandroid17_139_011_197.R;
import com.opencsv.CSVReader;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class CSVgetFile extends AppCompatActivity {

    private List<String[]> csvBody;
    private File csvfile;
    String csvFilePath;
//    private int nofrows;
//    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_csv);

        csvFilePath=null;



        Intent chooseintent = new Intent(Intent.ACTION_GET_CONTENT);
        chooseintent.setType("text/csv/*");
        startActivityForResult(chooseintent,1);


//        Intent intent = new Intent();
//        intent.putExtra("csvFilepath","/storage/emulated/0/documents/vehicle_26.csv");
//        setResult(1, intent);
//
//        //finish();


    }



//    private void Readfile(){
//
//        //na dialegei o xrhsths
//
////        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
////        intent.setType("documents/*");
////        startActivityForResult(intent,1);
//
//
//
//        try {
//            String csvFilepath = "/storage/emulated/0/documents/vehicle_26.csv";
////            File csvfile = new File(Environment.getExternalStorageState() + "/documents/vehicle_26.csv");
//            csvfile = new File("/storage/emulated/0/documents/vehicle_26.csv");
//            CSVReader reader = new CSVReader(new FileReader(csvfile.getAbsolutePath()));
//
//            this.csvBody = reader.readAll();
//            for (int row=1; row< csvBody.size(); row++){
//                Log.d("epitelouskaloneo", csvBody.get(row)[2]);
//
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            Toast.makeText(this, "The specified file was not found", Toast.LENGTH_SHORT).show();
//        }
//    }



//na dilagei o xrhsths

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == 1) {
            // Make sure the request was successful

            if (resultCode == RESULT_OK) {
                Uri FileUri = data.getData();
//                ing with the contact here (bigger example below)
                Uri selectedFile = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };
                Cursor cursor = getContentResolver().query(selectedFile,filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                csvFilePath = cursor.getString(columnIndex);
                Log.d("path",csvFilePath);
                cursor.close();

            }
            if (csvFilePath!=null){
                verifyStoragePermissions();
            }else{
                Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
                this.finish();
            }
        }
    }


    public void verifyStoragePermissions() {
        // Check if we have write permission

        int Permission = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
        if (Permission != PackageManager.PERMISSION_GRANTED) {
//            if (!shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
//                showMessageOKCancel("You need to allow access to FILES",
//                        new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_PERMISSIONS);
//
//                            }
//                        });
//                return;
//            }
            requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_CODE_ASK_PERMISSIONS);
//            Return();
        }else{
            Return(1);
        }


    }


    private void Return(int resultcode){
        Intent intent = new Intent();
        if (csvFilePath==null){
            setResult(0,intent);
        }else {
            intent.putExtra("csvFilepath", csvFilePath);
            setResult(resultcode, intent);
        }
        finish();
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
//                    Readfile();
                    Log.d("please se parakalw na emafnisteis sto log", "amananamanaaman");
                    Return(1);


                } else {
                    // Permission Denied
                    Toast.makeText(this, "READ_EXTERNAL_MEMORY Denied", Toast.LENGTH_SHORT)
                            .show();
                    Return(0);

                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


//    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
//        new AlertDialog.Builder(this)
//                .setMessage(message)
//                .setCancelable(false)
//                .setPositiveButton("OK", okListener)
//                .setNegativeButton("Cancel", null)
//                .create()
//                .show();
//    }


}