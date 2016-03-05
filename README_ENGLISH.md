# BadgedView

[English Version](README_ENGLISH.md)

PianoView provides a ViewPager Indicator looks like piano's keyboard.It's what [ZuiMei App]() uses now.

Preview：

![](http://img.my.csdn.net/uploads/201601/22/1453434722_3349.gif)

#Usage

##Dependency

###Min SDK：2.3.3

###Add Dependency

add below code in your build.gradle:

```
dependencies {
    ...
    compile 'com.github.chaossss:PianoView:1.0.1'
}
```

##Customize PianoView

PianoView can't customize now

##Activity

```java
public class MainActivity extends FragmentActivity implements PianoItemListener {
    private PianoView pianoView;
    private List<String> iconUrlList;
    private PianoAdapter pianoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iconUrlList = new ArrayList<>();
        pianoView = (PianoView) findViewById(R.id.piano_view);
        pianoView.setScrollPianoStartDelayTime(400);
        pianoView.setPianoItemListenerListener(this);

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
```

##XML

```xml
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="fill_parent"
    android:layout_height="fill_parent"
    android:background="#000000">
    <com.github.chaossss.pianoview.PianoView
        android:id="@+id/piano_view"
        android:layout_width="fill_parent"
        android:layout_height="wrap_content"
        android:layout_alignParentBottom="true"/>
</RelativeLayout>
```

##License
Copyright (C) 2016 Cheelok

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
