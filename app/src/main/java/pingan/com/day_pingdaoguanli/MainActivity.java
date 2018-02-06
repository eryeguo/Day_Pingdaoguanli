package pingan.com.day_pingdaoguanli;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.trs.channellib.channel.channel.helper.ChannelDataHelepr;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import pingan.com.day_pingdaoguanli.adapter.TitleFragmentAdapter;
import pingan.com.day_pingdaoguanli.bean.MyChannel;
import pingan.com.day_pingdaoguanli.util.GsonUtil;

public class MainActivity extends AppCompatActivity implements ChannelDataHelepr.ChannelDataRefreshListenter {
    View switch_view;
    ViewPager viewPager;
    TitleFragmentAdapter adapter;
    List<MyChannel> myChannels;
    ChannelDataHelepr<MyChannel> dataHelepr;
    TabLayout tab;
    private int needShowPosition=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tab = (TabLayout) findViewById(R.id.tab);
        switch_view = findViewById(R.id.iv_subscibe);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        dataHelepr = new ChannelDataHelepr(this, this, findViewById(R.id.top_bar));
        dataHelepr.setSwitchView(switch_view);
        myChannels = new ArrayList<>();
        adapter = new TitleFragmentAdapter(getSupportFragmentManager(), myChannels);
        viewPager.setAdapter(adapter);
        tab.setupWithViewPager(viewPager);
        loadData();
    }

    @Override
    public void updateData() {
        loadData();
    }

    @Override
    public void onChannelSeleted(boolean update,final int posisiton) {
        if(!update) {
            viewPager.setCurrentItem(posisiton);
        }else {
            needShowPosition=posisiton;
        }

    }


    private void loadData() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                String data = getFromRaw();
                List<MyChannel> alldata = GsonUtil.jsonToBeanList(data, MyChannel.class);
                final List<MyChannel> showChannels = dataHelepr.getShowChannels(alldata);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        myChannels.clear();
                        myChannels.addAll(showChannels);
                        adapter.notifyDataSetChanged();
                        if(needShowPosition!=-1){
                            viewPager.setCurrentItem(needShowPosition);
                            needShowPosition=-1;
                        }
                    }
                });

            }
        }).start();
    }

    private String getFromRaw() {
        String result = "";
        try {
            InputStream input = getResources().openRawResource(R.raw.news_list);
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length = 0;
            while ((length = input.read(buffer)) != -1) {
                output.write(buffer, 0, length);
            }
            output.close();
            input.close();

            return output.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
