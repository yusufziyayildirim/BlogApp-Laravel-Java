package com.blog.blogapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blog.blogapp.Adapters.ViewPagerAdapter;

public class OnBoardActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private Button btnLeft,btnRight;
    private ViewPagerAdapter adapter;
    private LinearLayout dotsLayout;
    private TextView[] dots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_board);
        init();
    }

    private void init() {
        viewPager = findViewById(R.id.view_pager);
        btnLeft = findViewById(R.id.btnLeft);
        btnRight = findViewById(R.id.btnRight);
        dotsLayout = findViewById(R.id.dotsLayout);
        adapter = new ViewPagerAdapter(this);
        addDots(0);
        viewPager.addOnPageChangeListener(listener); // listener oluştur
        viewPager.setAdapter(adapter);

        btnRight.setOnClickListener(v->{
            //sonraki düğme metni ise, görüntüleme sayfasının sonraki sayfasına gideceğiz
            if (btnRight.getText().toString().equals("Next")){
                viewPager.setCurrentItem(viewPager.getCurrentItem()+1);
            }
            else{
                //aksi takdirde bitişi, auth activity başlatacağız
                startActivity(new Intent(OnBoardActivity.this,AuthActivity.class));
                finish();
            }
        });

        btnLeft.setOnClickListener(v->{
            // btn atla tıklanırsa 3. sayfaya gideriz
            viewPager.setCurrentItem(viewPager.getCurrentItem()+2);
        });


    }

    //html kodundan noktalar oluşturma yöntemi
    private void addDots(int position){
        dotsLayout.removeAllViews();
        dots = new TextView[3];
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            //bu html kodu nokta oluşturur
            dots[i].setText(Html.fromHtml("&#8226"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(getResources().getColor(R.color.colorLightGrey));
            dotsLayout.addView(dots[i]);
        }

        // seçilen nokta rengini değiştirme
        if(dots.length>0){
            dots[position].setTextColor(getResources().getColor(R.color.colorGrey));
        }
    }



    private ViewPager.OnPageChangeListener listener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDots(position);
            //Şimdi 3. sayfaya ulaştıysak İleri düğmesinin metnini Bitir olarak değiştirmemiz gerekiyor
            //ve 1. sayfada değilsek Atla düğmesini gizleme

            if(position==0){
                btnLeft.setVisibility(View.VISIBLE);
                btnLeft.setEnabled(true);
                btnRight.setText("Next");
            }
            else if(position==1){
                btnLeft.setVisibility(View.GONE);
                btnLeft.setEnabled(false);
                btnRight.setText("Next");
            }
            else{
                btnLeft.setVisibility(View.GONE);
                btnLeft.setEnabled(false);
                btnRight.setText("Finish");
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };











}
