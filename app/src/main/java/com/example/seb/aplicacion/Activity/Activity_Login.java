package com.example.seb.aplicacion.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.EditText;

import com.example.seb.aplicacion.R;

public class Activity_Login extends AppCompatActivity {

    private EditText intxtBaseNombre;
    private EditText intxtBaseClave;
    private EditText intxtNombre;
    private EditText intxtClave;

    private Button btnProcesar;
    private TextView txtResultado;

    private Button btnStepOver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        intxtBaseNombre = (EditText) findViewById(R.id.intxtBaseNombre);
        intxtBaseClave  = (EditText) findViewById(R.id.intxtBaseClave);
        intxtNombre     = (EditText) findViewById(R.id.intxtNombre);
        intxtClave      = (EditText) findViewById(R.id.intxtClave);

        btnProcesar     = (Button) findViewById(R.id.btnProcesar);
        txtResultado    = (TextView) findViewById(R.id.txtResultado);

        btnProcesar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                String base_nombre = intxtBaseNombre.getText().toString();
                String base_clave = intxtBaseClave.getText().toString();
                String nombre = intxtNombre.getText().toString();
                String clave = intxtClave.getText().toString();

                if (base_nombre.isEmpty()) {
                    txtResultado.setText(":( Base Nombre Vacio");
                    return;
                }

                if (base_clave.isEmpty()) {
                    txtResultado.setText(":( Base Clave Vacio");
                    return;
                }

                if (nombre.isEmpty()) {
                    txtResultado.setText(":( Nombre Vacio");
                    return;
                }

                if (clave.isEmpty()) {
                    txtResultado.setText(":( Clave Vacio");
                    return;
                }

                if (nombre.equals(base_nombre))
                {
                    if (clave.equals(base_clave))
                    {
                        txtResultado.setText(":)");
                    }
                    else
                    {
                        txtResultado.setText(":( Clave no válida");
                    }
                }
                else
                {
                    txtResultado.setText(":( Nombre no válido");
                }
            }
        });

        btnStepOver     = (Button) findViewById(R.id.btnStepOver);
        btnStepOver.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Log.i("setOnClickListener", "btnStepOver");
                Intent intent = new Intent(Activity_Login.this, Activity_Main.class);
                startActivity(intent);
            }
        });

    }
}
