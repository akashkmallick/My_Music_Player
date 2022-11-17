package com.example.musicplayer;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Build.VERSION.SDK_INT;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    String[] items;
    ArrayList<File> mySongs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listViewSong);
        //runtimePermission();

        if(!checkPermission()){
            showPermissionDialog();
        }
        displaySongs();
    }

    private void showPermissionDialog() {
        if (SDK_INT >= Build.VERSION_CODES.R) {

            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse(String.format("package:%s", new Object[]{getApplicationContext().getPackageName()})));
                startActivityForResult(intent, 2000);
            } catch (Exception e) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivityForResult(intent, 2000);

            }

        } else
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, 333);
    }

    private boolean checkPermission() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        } else {
            int write = ContextCompat.checkSelfPermission(getApplicationContext(),
                    WRITE_EXTERNAL_STORAGE);
            int read = ContextCompat.checkSelfPermission(getApplicationContext(),
                    READ_EXTERNAL_STORAGE);

            return write == PackageManager.PERMISSION_GRANTED &&
                    read == PackageManager.PERMISSION_GRANTED;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 333) {
            if (grantResults.length > 0) {
                boolean write = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean read = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                if (read && write){

                }else {

                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2000) {
            if (SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {

                } else {

                }
            }
        }
    }

    /*private void runtimePermission() {
        Dexter.withContext(this).withPermissions(READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        displaySongs();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }*/




    public ArrayList<File> findSong(File file) {
        ArrayList<File> arrayList = new ArrayList<>();
        File[] files = file.listFiles();
        for (File singlefile : files) {
            if (singlefile.isDirectory() && !singlefile.isHidden()) {
                arrayList.addAll(findSong(singlefile));
            } else {
                if (singlefile.getName().endsWith(".mp3") || singlefile.getName().endsWith(".wav")) {
                    arrayList.add(singlefile);
                }
            }
        }
        return arrayList;
    }


    public void displaySongs() {
        //final ArrayList<File> mySongs = findSong(Environment.getExternalStorageDirectory());

        if(SDK_INT >= Build.VERSION_CODES.R){
            if(Environment.isExternalStorageManager()){
                mySongs = findSong(new File("storage/emulated/0/Download"));

            }else{
                Log.e("msg","don't have permission");
            }
        }else{
            mySongs = findSong(Environment.getExternalStorageDirectory());
        }

        items = new String[mySongs.size()];
        for (int i = 0; i < mySongs.size(); i++) {
            items[i] = mySongs.get(i).getName().replace(".mp3", "").replace(".wav", "");

        }

        ArrayAdapter<String> myAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        listView.setAdapter(myAdapter);


        CustomAdapter customAdapter = new CustomAdapter();

        listView.setAdapter(customAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String songName = (String) listView.getItemAtPosition(i);
                startActivity(new Intent(getApplicationContext(), playerActivity.class)
                        .putExtra("songs",mySongs)
                        .putExtra("songsname",songName)
                        .putExtra("pos",i)

                );
            }
        });
    }

    class CustomAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return items.length;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View myView = getLayoutInflater().inflate(R.layout.list_item,null);
            TextView textSong = myView.findViewById(R.id.txtsongname);
            textSong.setSelected(true);
            textSong.setText(items[i]);
            return myView;
        }
    }
}