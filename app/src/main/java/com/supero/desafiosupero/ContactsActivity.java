package com.supero.desafiosupero;
/**
 * Created by Rodrigo Fortes
 */

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.supero.desfiosupero.util.Mask;
import com.supero.desfiosupero.util.Validator;
import com.supero.desfiosupero.util.ViaCEP;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ContactsActivity extends AppCompatActivity {

    public final static String EXTRA_MESSAGE = "com.supero.contact";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        final EditText etCep = (EditText)findViewById(R.id.etCep);
        etCep.addTextChangedListener(Mask.insert("#####-###", etCep));
        etCep.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    String cep = etCep.getText().toString();
                    ConnectivityManager connMgr = (ConnectivityManager)
                            getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                    if (networkInfo != null && networkInfo.isConnected()) {
                        Pattern pattern = Pattern.compile("^[0-9]{5}-[0-9]{3}$");
                        Matcher matcher = pattern.matcher(cep);
                        if (matcher.find()) {
                            new DownloadCEPTask().execute(cep);
                        } else {
                            etCep.setError("CEP inválido");
                            etCep.setFocusable(true);
                            etCep.requestFocus();
                        }
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.save:
                if(validaCampos()) {
                    Intent intent = new Intent(this, DisplayMessageActivity.class);
                    EditText editText = (EditText) findViewById(R.id.etNome);
                    String message = editText.getText().toString();
                    intent.putExtra(EXTRA_MESSAGE, message);
                    startActivity(intent);
                }
        }
        return true;
    }

    private class DownloadCEPTask extends AsyncTask<String, Void, ViaCEP> {
        @Override
        protected ViaCEP doInBackground(String ... ceps) {
            ViaCEP vCep = null;
            try {
                vCep = new ViaCEP(ceps[0]);
            } finally {
                return vCep;
            }
        }

        @Override
        protected void onPostExecute(ViaCEP result) {
            if (result != null) {
                EditText etLogradouro = (EditText) findViewById(R.id.etLogradouro);
                EditText etBairro = (EditText) findViewById(R.id.etBairro);
                EditText etCidade = (EditText) findViewById(R.id.etCidade);
                EditText etUF = (EditText) findViewById(R.id.etUf);
                etLogradouro.setFocusable(false);
                etBairro.setFocusable(false);
                etCidade.setFocusable(false);
                etUF.setFocusable(false);
                etLogradouro.setText(result.getLogradouro());
                etBairro.setText(result.getBairro());
                etCidade.setText(result.getLocalidade());
                etUF.setText(result.getUf());
            }else{
                EditText etCep = (EditText) findViewById(R.id.etCep);
                etCep.setError("CEP inválido");
                etCep.setFocusable(true);
                etCep.requestFocus();
            }
        }
    }


    private boolean validaCampos(){
        final EditText etNome = (EditText) findViewById(R.id.etNome);
        final EditText etLogradouro = (EditText) findViewById(R.id.etLogradouro);
        final EditText etCep = (EditText) findViewById(R.id.etCep);
        final EditText etBairro = (EditText) findViewById(R.id.etBairro);
        final EditText etCidade = (EditText) findViewById(R.id.etCidade);
        final EditText etUf = (EditText) findViewById(R.id.etUf);
        final EditText etNumero = (EditText) findViewById(R.id.etNumero);
        final EditText etEmail = (EditText) findViewById(R.id.etEmail);

        if(!Validator.validateNotNull(etNome,"Preencha o campo Nome")){
            return false;
        }
        if(!Validator.validateNotNull(etCep,"Preencha o campo CEP")){
            return false;
        }
        if(!Validator.validateNotNull(etNumero,"Preencha o campo Número")){
            return false;
        }
        if(!Validator.validateNotNull(etLogradouro,"Preencha o campo Logradouro")){
            return false;
        }
        if(!Validator.validateNotNull(etBairro,"Preencha o campo Cidade")){
            return false;
        }
        if(!Validator.validateNotNull(etCidade,"Preencha o campo UF")){
            return false;
        }
        if(!Validator.validateNotNull(etUf,"Preencha o campo UF")){
            return false;
        }

        boolean email_valido = Validator.validateEmail(etEmail.getText().toString());
        if(!email_valido){
            etEmail.setError("Email inválido");
            etEmail.setFocusable(true);
            etEmail.requestFocus();
            return false;
        }
        return true;
    }
}
