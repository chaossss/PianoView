package com.github.chaossss.pianoview.demo;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.widget.Toast;

import com.github.chaossss.pianoview.Card;
import com.github.chaossss.pianoview.CardData;
import com.github.chaossss.pianoview.PianoAdapter;
import com.github.chaossss.pianoview.PianoItemListener;
import com.github.chaossss.pianoview.PianoView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity implements PianoItemListener {
    private PianoView pianoView;
    private List<String> iconUrlList;
    private PianoAdapter pianoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iconUrlList = new ArrayList<>();
        pianoView = (PianoView) findViewById(R.id.box_rhythm);
        pianoView.setScrollPianoStartDelayTime(400);
        pianoView.setPianoItepianoItemListener(this);

        fetchData();
        pianoView.showPianoAtPosition(0);
    }

    private void fetchData() {
        for (int i = 0; i < 10; i++) {
            iconUrlList.add("http://img5.duitang.com/uploads/item/201409/07/20140907080946_d4QiL.jpeg");
        }
        pianoAdapter = new PianoAdapter(this, pianoView);
        pianoView.setAdapter(pianoAdapter);
        pianoAdapter.addIconUrlList(iconUrlList);
    }

    @Override
    public void onPianoItemSelected(int itemIndex) {
        Toast.makeText(this, "item " + itemIndex + " selected", Toast.LENGTH_SHORT).show();
        pianoView.showPianoAtPosition(itemIndex);
    }

    @Override
    public void onStartSwipe() {
    }
}
