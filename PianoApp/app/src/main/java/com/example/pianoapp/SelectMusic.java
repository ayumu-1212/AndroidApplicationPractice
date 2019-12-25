package com.example.pianoapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class SelectMusic extends AppCompatActivity {

    private ArrayList<ListItem> listItems;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_music);

        Button returnToTop2Button = findViewById(R.id.return_to_top2);
        returnToTop2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), Top.class);
                startActivity(intent);
            }
        });

//        レイアウトからビューを取得
        ListView listView = (ListView)findViewById(R.id.listview);

//        リストビューに表示する要素を設定
        listItems = new ArrayList<>();
        addItems("ぶんぶんぶん", R.drawable.bunbunbun);
        addItems("いぬのおまわりさん", R.drawable.dog_police_officer);
        addItems("チューリップ", R.drawable.tulips);
        addItems("紅", R.drawable.deep_red);



//        出力結果をリストビューに表示
        ListAdapter adapter = new ListAdapter(this, R.layout.music_list, listItems);
        listView.setAdapter(adapter);
    }


    private void addItems(String imageName, int id) {
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), id);
        ListItem item = new ListItem(bmp, imageName);
        listItems.add(item);
    }

}
