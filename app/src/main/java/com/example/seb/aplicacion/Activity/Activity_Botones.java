package com.example.seb.aplicacion.Activity;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.RadioGroup;
import android.widget.RadioButton;
import android.widget.CheckBox;
import android.widget.Switch;
import android.widget.ToggleButton;

import com.example.seb.aplicacion.R;

public class Activity_Botones extends AppCompatActivity {

    /*
    Boton normal
     */
    private TextView txtBoton;
    private Button btnBoton;

    /*
    Boton Small
     */
    private TextView txtBotonSmall;
    private Button btnBotonSmall;

    /*
    Radio Botones
     */
    private TextView txtRadio;
    private RadioButton rbtnRadio_R, rbtnRadio_G, rbtnRadio_B;
    private RadioGroup rgrpRadio;

    /*
    CheckBox
     */
    private TextView txtCheck;
    private CheckBox chcCheck;

    /*
    Switch
     */
    private TextView txtSwitch;
    private Switch swtSwitch;

    /*
    Switch
     */
    private TextView txtToggle;
    private ToggleButton tglToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_botones);

        /*
        Boton normal
         */
        txtBoton = (TextView) findViewById(R.id.txtBoton);
        btnBoton = (Button) findViewById(R.id.btnBoton);
        btnBoton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                txtBoton.setText("Tocado!");
            }
        });

        /*
        Boton Small
         */
        txtBotonSmall = (TextView) findViewById(R.id.txtBotonSmall);
        btnBotonSmall = (Button) findViewById(R.id.btnBotonSmall);
        btnBotonSmall.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {txtBotonSmall.setText("Tocado!");
            }
        });


        /*
        Radio Botones
         */
        rgrpRadio = (RadioGroup) findViewById(R.id.rgrpRadio);
        txtRadio = (TextView) findViewById(R.id.txtRadio);

        rbtnRadio_R = (RadioButton) findViewById(R.id.rbtnRadio_R);
        rbtnRadio_R.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                onRadioButtonClicked(arg0);
            }
        });

        rbtnRadio_G = (RadioButton) findViewById(R.id.rbtnRadio_G);
        rbtnRadio_G.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                onRadioButtonClicked(arg0);
            }
        });

        rbtnRadio_B = (RadioButton) findViewById(R.id.rbtnRadio_B);
        rbtnRadio_B.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                onRadioButtonClicked(arg0);
            }
        });

        /*
        CheckBox
         */
        txtCheck = (TextView) findViewById(R.id.txtCheck);
        chcCheck = (CheckBox) findViewById(R.id.chcCheck);
        chcCheck.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                boolean checked = chcCheck.isChecked();
                if (checked)
                    txtCheck.setText(":)");
                else
                    txtCheck.setText(":(");
            }
        });

        /*
        Switch
         */
        txtSwitch = (TextView) findViewById(R.id.txtSwitch);
        swtSwitch = (Switch) findViewById(R.id.swtSwitch);
        swtSwitch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                boolean checked = swtSwitch.isChecked();
                if (checked)
                    txtSwitch.setText(":)");
                else
                    txtSwitch.setText(":(");
            }
        });

        /*
        Toggle
         */
        txtToggle = (TextView) findViewById(R.id.txtToggle);
        tglToggle = (ToggleButton) findViewById(R.id.tglToggle);
        tglToggle.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                boolean checked = tglToggle.isChecked();
                if (checked)
                    txtToggle.setText(":)");
                else
                    txtToggle.setText(":(");
            }
        });

    }

    /*
    Radio Botones
     */
    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        switch (view.getId()) {
            case R.id.rbtnRadio_R:
                if (checked)
                    txtRadio.setText("Rojo!");
                break;
            case R.id.rbtnRadio_G:
                if (checked)
                    txtRadio.setText("Verde!");
                break;
            case R.id.rbtnRadio_B:
                if (checked)
                    txtRadio.setText("Azul!");
                break;
        }
    }
}
