package com.poisondemo.mycurrencies;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Uri;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.os.Bundle;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Properties;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private Button mCalcButton;
    private Button mInvertButton;
    private TextView mConvertedTextView;
    private EditText mAmountEditText;
    private Spinner mForSpinner,mHomSpinner;
    private String[] mCurrencies;
    public static final String FOR = "FOR_CURRENCY";
    public static final String HOM = "HOM_CURRENCY";

    private String mKey;
    public static final String RATES = "rates";
    public static final String URL_BASE = "https://openexchangerates.org/api/latest.json?app_id=";
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#,##0.00000");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCalcButton = (Button)findViewById(R.id.btn_calc);
        mInvertButton =(Button)findViewById(R.id.btn_invert);
        mForSpinner = (Spinner)findViewById(R.id.spn_for);
        mHomSpinner = (Spinner)findViewById(R.id.spn_hom);
        mAmountEditText = (EditText)findViewById(R.id.edt_amount);
        mConvertedTextView = (TextView)findViewById(R.id.txt_converted);
        ArrayList<String> arrayList = ((ArrayList<String>)
                getIntent().getSerializableExtra(SplashActivity.KEY_ARRAYLIST));
        Collections.sort(arrayList);
        mCurrencies = arrayList.toArray(new String[arrayList.size()]);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,R.layout.spinner_closed,mCurrencies);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mHomSpinner.setAdapter(arrayAdapter);
        mForSpinner.setAdapter(arrayAdapter);
        mForSpinner.setOnItemSelectedListener(this);
        mHomSpinner.setOnItemSelectedListener(this);

        if(savedInstanceState == null
                && (PrefsMgr.getString(this, FOR) == null &&
                PrefsMgr.getString(this,HOM) == null)){

            mForSpinner.setSelection(findPositionGivenCode("CNY", mCurrencies));
            mHomSpinner.setSelection(findPositionGivenCode("USD", mCurrencies));

            PrefsMgr.setString(this, FOR, "CNY");
            PrefsMgr.setString(this, HOM, "USD");

        }else {
            mForSpinner.setSelection(findPositionGivenCode(PrefsMgr.getString(this, FOR), mCurrencies));
            mHomSpinner.setSelection(findPositionGivenCode(PrefsMgr.getString(this, HOM), mCurrencies));

        }
        mCalcButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CurrencyConverterTask().execute(URL_BASE+mKey);
            }
        });

        mInvertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                invertCurrencies();
            }
        });

        mKey = getKey("open_key");
    }

    /**
     * 重写activity 中创建菜单选项
     *
     * @return 返回真假决定是否显示
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        switch(id)
        {
            case R.id.mnu_codes:
                launchBrowser(SplashActivity.URL_CODES);
                break;
            case R.id.mnu_note:
                Intent intent1 = new Intent(this,NoteActivity.class);
                startActivity(intent1);
                break;
            case R.id.mnu_record:
                Intent intent = new Intent(this,RecordActivity.class);
                startActivity(intent);
                break;
            case R.id.mnu_exit:
                finish();
                break;
        }
        return true;
    }

    public boolean isOnline()
    {
        ConnectivityManager cm =
                (ConnectivityManager)
                          getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnectedOrConnecting())
        {
            return true;
        }
        return false;
    }
    private void launchBrowser(String strUri)
    {
        if(isOnline())
        {
            Uri uri = Uri.parse(strUri);
            Intent intent = new Intent(Intent.ACTION_VIEW,uri);
            startActivity(intent);
        }
    }

    private void invertCurrencies(){
        int nFor = mForSpinner.getSelectedItemPosition();
        int nHom = mHomSpinner.getSelectedItemPosition();

        mForSpinner.setSelection(nHom);
        mHomSpinner.setSelection(nFor);

        mConvertedTextView.setText("");

        PrefsMgr.setString(this, FOR, extractCodeFromCurrency((String)mForSpinner.getSelectedItem()));
        PrefsMgr.setString(this, HOM, extractCodeFromCurrency((String)mHomSpinner.getSelectedItem()));

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch(parent.getId()){
            case R.id.spn_for:
                PrefsMgr.setString(this, FOR, extractCodeFromCurrency((String)mForSpinner.getSelectedItem()));
                break;
            case R.id.spn_hom:
                PrefsMgr.setString(this, HOM, extractCodeFromCurrency((String)mHomSpinner.getSelectedItem()));
                break;
            default:
                break;
        }
        mConvertedTextView.setText("");
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public int findPositionGivenCode(String code,String[] currencies)
    {
        for(int i=0;i< currencies.length;i++ )
        {
            if(extractCodeFromCurrency(mCurrencies[i]).equalsIgnoreCase(code))
            {
                return i;
            }
        }
        return 0;
    }

    private String extractCodeFromCurrency(String mCurrency) {
        return (mCurrency).substring(0,3);
    }

    //获取密钥
    private String getKey(String keyName){
        AssetManager assetManager = this.getResources().getAssets();
        Properties properties = new Properties();
        try {
            InputStream inputStream = assetManager.open("keys.properties");
            properties.load(inputStream);
        }catch (IOException e){
            e.printStackTrace();
        }
        return properties.getProperty(keyName);
    }

    private class CurrencyConverterTask extends AsyncTask<String,Void, JSONObject>
    {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {

            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setTitle("计算结果中...");
            progressDialog.setMessage("请等待...");
            progressDialog.setCancelable(true);

            progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            CurrencyConverterTask.this.cancel(true);
                            progressDialog.dismiss();
                        }
                    });
            progressDialog.show();
        }


        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            double dCalculated = 0.0;
            String strForCode = extractCodeFromCurrency(mCurrencies[mForSpinner.getSelectedItemPosition()]);
            String strHomCode = extractCodeFromCurrency(mCurrencies[mHomSpinner.getSelectedItemPosition()]);
            String strAmount = mAmountEditText.getText().toString();

            try {
                if(jsonObject == null){
                    throw new JSONException("no data available.");
                }
                JSONObject jsonRates = jsonObject.getJSONObject(RATES);
                if(strHomCode.equalsIgnoreCase("USD")){
                    dCalculated = Double.parseDouble(strAmount) / jsonRates.getDouble(strForCode);
                }
                else if(strForCode.equalsIgnoreCase("USD")){
                    dCalculated = Double.parseDouble(strAmount) * jsonRates.getDouble(strHomCode);
                }
                else {
                    dCalculated = Double.parseDouble(strAmount) * jsonRates.getDouble(strHomCode)/jsonRates.getDouble(strForCode);
                }
            }catch (JSONException e){
                Toast.makeText(MainActivity.this,
                        "There's been a JSON exception: " + e.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
                mConvertedTextView.setText("");
                e.printStackTrace();
            }
            mConvertedTextView.setText(DECIMAL_FORMAT.format(dCalculated) + " " + strHomCode);

            //把数据存入数据库
            BeanRecord data = new BeanRecord();
            data.setForCode(strForCode);
            data.setForAmount(strAmount);
            data.setHomCode(strHomCode);
            data.setHomAmount(new DecimalFormat("0.00").format(dCalculated));
            SimpleDateFormat formatter = new SimpleDateFormat("yy/MM/dd HH:mm");
            Date curDate = new Date(System.currentTimeMillis());
            data.setTime(formatter.format(curDate));
            MyDatabaseManager dbManager = new MyDatabaseManager(getBaseContext());
            dbManager.addData(data);



            progressDialog.dismiss();

            //for testing
         //   if(mCurrencyTaskCallback != null){
         //       mCurrencyTaskCallback.executionDone();
         //   }
        }


        @Override
        protected JSONObject doInBackground(String... params) {
            return new JSONParser().getJSONFromUrl(params[0]);
        }
    }
}
