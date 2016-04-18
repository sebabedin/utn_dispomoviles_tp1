package com.example.seb.aplicacion.Activity;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

// agregadas
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.seb.aplicacion.R;

import uIO.uMenu;

public class Activity_Main extends AppCompatActivity {

    private Button btnBotones;
    private Button btnLogin;
    private Button btnToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnBotones = (Button)findViewById(R.id.btnBotones);
        btnBotones.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent intent = new Intent(Activity_Main.this, Activity_Botones.class);
                startActivity(intent);
            }
        });

        btnLogin = (Button)findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0)
            {
                Intent intent = new Intent(Activity_Main.this, Activity_Login.class);
                startActivity(intent);
            }
        });
    }

    /**
    MENU TOOLBAR
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        uMenu menu;
//        menu = new uMenu();
//        if(!menu.uMenuToolBar_onOptionsItemSelected(item))
//        {
//            return super.onOptionsItemSelected(item);
//        }
//
//
//
//        return true;

        Intent i;

        switch (item.getItemId()) {
            case R.id.itmTakePhoto:
                Log.i("onOptionsItemSelected", "itmTakePhoto");
                return true;
            case R.id.itmGallery:
                Log.i("onOptionsItemSelected", "itmGallery");
                return true;
            case R.id.itmSettings:
                Log.i("onOptionsItemSelected", "itmSettings");
                i = new Intent(Activity_Main.this, Activity_Settings.class);
                startActivity(i);
                return true;
            case R.id.itmHelp:
                Log.i("onOptionsItemSelected", "itmHelp");
                i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://google.com/"));
                startActivity(i);
                return true;
            case R.id.itmAbout:
                Log.i("onOptionsItemSelected", "itmAbout");
                i = new Intent(Activity_Main.this, Activity_About.class);
                startActivity(i);
                return true;
            case R.id.itmExit:
                Log.i("onOptionsItemSelected", "itmExit");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
