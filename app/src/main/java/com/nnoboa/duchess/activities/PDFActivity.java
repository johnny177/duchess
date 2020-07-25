package com.nnoboa.duchess.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import com.nnoboa.duchess.R;
import com.nnoboa.duchess.controllers.adapters.PDFAdapter;

import java.io.File;
import java.util.ArrayList;

public class PDFActivity extends AppCompatActivity {
    ListView lv_pdf;
    public static ArrayList<File> fileList = new ArrayList<File>();
    PDFAdapter obj_adapter;
    public static int REQUEST_PERMISSIONS = 1;
    boolean boolean_permission;
    File dir;String type = "";
    String type1 = "";
    String type2 = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.activity_pdf);
        init();
        fn_permission();
        Intent intent = getIntent();
        type = intent.getStringExtra("type").toUpperCase();
        getSupportActionBar().setTitle(type+" List of Files");
        lv_pdf.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if(scrollY<oldScrollY){
                    if(getSupportActionBar().isShowing()){
                        getSupportActionBar().hide();
                    }
                }else {
                    getSupportActionBar().show();
                }
            }
        });

    }

    private void init() {

        lv_pdf = findViewById(R.id.lv_pdf);
        dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
        fn_permission();
        lv_pdf.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                open_File(String.valueOf(fileList.get(i).getAbsoluteFile()));
                Log.e("Position", i + "");
            }
        });
        lv_pdf.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        lv_pdf.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                final int checkedCount = lv_pdf.getCheckedItemCount();
                mode.setTitle(checkedCount+" Selected");
                obj_adapter.toggleSelection(position);
                lv_pdf.setBackgroundColor(R.color.clean_color);
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {

                mode.getMenuInflater().inflate(R.menu.schedule_menu,menu);
                return true;

            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()){
                    case R.id.delete:
                        SparseBooleanArray sparseBooleanArray = obj_adapter.getSelectedIds();
                        for(int i =(sparseBooleanArray.size()-1); i >0; i--){
                            if(sparseBooleanArray.valueAt(i)){
                                File selectedFiles = obj_adapter.getItem(sparseBooleanArray.keyAt(i));
                                if ((ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {

                                    if ((ActivityCompat.shouldShowRequestPermissionRationale(PDFActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE))) {
                                    } else {
                                        ActivityCompat.requestPermissions(PDFActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                REQUEST_PERMISSIONS);

                                    }
                                }else {
                                obj_adapter.remove(selectedFiles);}
                            }
                            mode.finish();
                            return true;
                        }
                        default:
                            return false;

                }

            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                obj_adapter.removeSelection();

            }
        });
    }

    public ArrayList<File> getfile(File dir) {
        File[] listFile = dir.listFiles();
        Intent intent = getIntent();
        type = intent.getStringExtra("type");
        type1 = intent.getStringExtra("type1");
        type2 = intent.getStringExtra("type2");
        if (listFile != null && listFile.length > 0) {
            for (int i = 0; i < listFile.length; i++) {


                if (listFile[i].isDirectory()) {
                    getfile(listFile[i]);

                } else {

                    boolean booleanpdf = false;

                    if(listFile[i].getName().endsWith(type)||listFile[i].getName().endsWith(type1)
                            ||listFile[i].getName().endsWith(type2)){
                        lv_pdf.scrollTo(0,fileList.size());
                        for (int j = 0; j < fileList.size(); j++) {
                            if (fileList.get(j).getName().equals(listFile[i].getName())) {
                                booleanpdf = true;
                            } else {

                            }
                        }

                        if (booleanpdf) {
                            booleanpdf = false;
                        } else {
                            fileList.add(listFile[i]);

                        }
                    }
                    }
            }
        }
        return fileList;
    }
    private void fn_permission() {
        if ((ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {

            if ((ActivityCompat.shouldShowRequestPermissionRationale(PDFActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE))) {
            } else {
                ActivityCompat.requestPermissions(PDFActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_PERMISSIONS);

            }
        } else {
            boolean_permission = true;

            getfile(dir);

            obj_adapter = new PDFAdapter(getApplicationContext(), fileList);
            lv_pdf.setAdapter(obj_adapter);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                boolean_permission = true;
                getfile(dir);

                obj_adapter = new PDFAdapter(getApplicationContext(), fileList);
                lv_pdf.setAdapter(obj_adapter);

            } else {
                Toast.makeText(getApplicationContext(), "Please allow the permission", Toast.LENGTH_LONG).show();

            }
        }
    }

    public void open_File(String path){
        File file = new File(path);
        Uri uri = FileProvider.getUriForFile(PDFActivity.this,getApplicationContext().getPackageName()+".provider",file);
        Intent intent2 = new Intent(Intent.ACTION_VIEW);
        if(file.getName().endsWith(".pdf")){
            intent2.setDataAndType(uri,"application/pdf");
        }
        else if(file.getName().endsWith(".docx")|file.getName().endsWith(".doc")){
        intent2.setDataAndType(uri, "application/msword");}
        else  if(file.getName().endsWith(".pptx")|file.getName().endsWith(".ppt")){
            intent2.setDataAndType(uri, "application/vnd.ms-powerpoint");}
        else  if(file.getName().endsWith(".xlsx")|file.getName().endsWith(".xls")|file.getName().endsWith(".csv")){
            intent2.setDataAndType(uri, "application/vnd.ms-excel");}
        else{
            intent2.setDataAndType(uri,"*/*");
        }
        intent2.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent2.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Intent intent1 = Intent.createChooser(intent2, "Open With");
        try {
            startActivity(intent1);
        } catch (ActivityNotFoundException e) {
        }

}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fileList = new ArrayList<>();
    }
}