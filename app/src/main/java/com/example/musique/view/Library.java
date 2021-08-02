package com.example.musique.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.musique.R;
import com.example.musique.services.PlayerService;
import com.example.musique.ui.main.SectionsPagerAdapter;
import com.example.musique.utils.Functions;
import com.google.android.material.tabs.TabLayout;

import static com.example.musique.services.PlayerService.currentSong;
import static com.example.musique.services.PlayerService.mediaPlayer;

public class Library extends AppCompatActivity {

    private static final String TAG = "Library";
    ImageView btnBack, btnSearch;
    EditText searchBar;
    TextView titleText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        btnBack = findViewById(R.id.btn_back);
        btnSearch = findViewById(R.id.btn_search);
        titleText = findViewById(R.id.title);
        searchBar = findViewById(R.id.search_bar);
    }

    private void openSearchBar() {
        if (searchBar.getVisibility() == View.VISIBLE) {
            Functions.closeKeyboard(this);
            titleText.setVisibility(View.VISIBLE);
            searchBar.setVisibility(View.GONE);
            btnSearch.setImageDrawable(getDrawable(R.drawable.ic_baseline_search_24));
        } else {
            titleText.setVisibility(View.GONE);
            searchBar.setVisibility(View.VISIBLE);
            btnSearch.setImageDrawable(getDrawable(R.drawable.ic_baseline_close_24));
        }
    }

    TextWatcher searchBarWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                onBackPressed();
                break;

            case R.id.btn_search:
                openSearchBar();
                break;
        }
    }
}