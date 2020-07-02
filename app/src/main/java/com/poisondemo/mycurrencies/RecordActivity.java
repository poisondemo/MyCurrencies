package com.poisondemo.mycurrencies;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class RecordActivity extends Activity{

    private MyDatabaseManager dbManager;
    private ListView listview;
    private List<BeanRecord> datas = new ArrayList<>();
    EditText edtSearch;
    ImageView ivDeleteText;
    private RecordAdapter adapter;
    ArrayList<String> forCode = new ArrayList<String>();
    ArrayList<String> forAmount = new ArrayList<String>();
    ArrayList<String> homCode = new ArrayList<String>();
    ArrayList<String> homAmount = new ArrayList<String>();
    ArrayList<String> time = new ArrayList<String>();
    Handler myhandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        dbManager = new MyDatabaseManager(getBaseContext());
        setContentView(R.layout.activity_record);
        initDatas();
        set_edtSearch_TextChanged();

        set_ivDeleteText_OnClick();

        adapter = new RecordAdapter(this,R.layout.record_list,datas);
        adapter.notifyDataSetChanged();
        listview = (ListView) findViewById(R.id.listview);
        listview.setAdapter(adapter);
        listview.setTextFilterEnabled(true);


    }




    private void set_edtSearch_TextChanged()
    {
        edtSearch = (EditText) findViewById(R.id.etSearch);

        edtSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() == 0){
                    ivDeleteText.setVisibility(View.GONE);
                }
                else{
                    ivDeleteText.setVisibility(View.VISIBLE);
                }

                myhandler.post(eChanged);
            }
        });
    }

    Runnable eChanged = new Runnable() {
        @Override
        public void run() {
            String data = edtSearch.getText().toString();

            datas.clear();

            getmDataSub(datas, data);

            adapter.notifyDataSetChanged();
        }
    };

    private void getmDataSub(List<BeanRecord> datas,String data)
    {
        int length = homCode.size();
        for(int i = 0;i < length;++i){
            if(time.get(i).contains(data)||forCode.get(i).contains(data)||forAmount.get(i).contains(data)||homCode.get(i).contains(data)||homAmount.get(i).contains(data)){
                BeanRecord item = new BeanRecord();
                item.setTime(time.get(i));
                item.setForCode(forCode.get(i));
                item.setForAmount(forAmount.get(i));
                item.setHomCode(homCode.get(i));
                item.setHomAmount(homAmount.get(i));
                datas.add(item);
            }
        }
    }

    private void set_ivDeleteText_OnClick() {
        ivDeleteText = (ImageView) findViewById(R.id.ivDeleteText);
        ivDeleteText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                edtSearch.setText("");
            }
        });
    }


    private void initDatas()
    {
        datas = dbManager.queryAllContent();
        for (BeanRecord d:datas)
        {
            if(d!=null){
                time.add(d.getTime());
                forCode.add(d.getForCode());
                forAmount.add(d.getForAmount());
                homCode.add(d.getHomCode());
                homAmount.add(d.getHomAmount());
            }
        }
    }
}
