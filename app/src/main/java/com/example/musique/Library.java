package com.example.musique;

import android.os.Bundle;

import com.example.musique.utility.Functions;
import com.example.musique.utility.SongsHandler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.musique.ui.main.SectionsPagerAdapter;

public class Library extends AppCompatActivity implements View.OnClickListener {

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
        btnSearch.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        titleText = findViewById(R.id.title);
        searchBar = findViewById(R.id.search_bar);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_back:
                onBackPressed();
                break;

            case R.id.btn_search:
                openSearchBar();
                break;
        }
    }

    private void openSearchBar(){
        if (searchBar.getVisibility() == View.VISIBLE){
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
}