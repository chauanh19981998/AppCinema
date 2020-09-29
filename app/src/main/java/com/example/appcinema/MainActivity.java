package com.example.appcinema;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.appcinema.adapter.BannerMoviesPagerAdapter;
import com.example.appcinema.adapter.MainRecyclerAdapter;
import com.example.appcinema.model.AllCategory;
import com.example.appcinema.model.BannerMovies;
import com.example.appcinema.model.CategoryItemList;
import com.example.appcinema.retrofit.RetrofitClient;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    BannerMoviesPagerAdapter bannerMoviesPagerAdapter;
    TabLayout indicatorTab, categoryTab;
    ViewPager bannerMoviesViewPager;
    List<BannerMovies> homeBannerList;
    List<BannerMovies> tvShowBannerList;
    List<BannerMovies> movieBannerList;
    List<BannerMovies> kidsBannerList;
    Timer sliderTimer;

    NestedScrollView nestedScrollView;
    AppBarLayout appBarLayout;

    MainRecyclerAdapter mainRecyclerAdapter;
    RecyclerView mainRecycler;
    List<AllCategory> allCategoryList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        indicatorTab = findViewById(R.id.tab_indicator);
        categoryTab = findViewById(R.id.tabLayout);
        nestedScrollView = findViewById(R.id.nested_scroll);
        appBarLayout = findViewById(R.id.appbar);

        homeBannerList = new ArrayList<>();
        tvShowBannerList = new ArrayList<>();
        movieBannerList = new ArrayList<>();
        kidsBannerList = new ArrayList<>();

        //fetch banner data from server
        getBannerData();

        //get movies from server
        getAllmoviesData(1);

        //on tab change selected data
        categoryTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition())
                {
                    case 1:
                        setScrollDefaultState();
                        setBannerMoviesPagerAdapter(tvShowBannerList);
                        getAllmoviesData(2);
                        return;
                    case 2:
                        setScrollDefaultState();
                        setBannerMoviesPagerAdapter(movieBannerList);
                        getAllmoviesData(3);
                        return;
                    case 3:
                        setScrollDefaultState();
                        setBannerMoviesPagerAdapter(kidsBannerList);
                        getAllmoviesData(4);
                        return;
                    default:
                        setScrollDefaultState();
                        setBannerMoviesPagerAdapter(homeBannerList);
                        getAllmoviesData(1);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        //add catitem data

        allCategoryList = new ArrayList<>();

        //pass array to recycler setup method
    }

    private void setBannerMoviesPagerAdapter(List<BannerMovies> bannerMoviesList) {
        bannerMoviesViewPager = findViewById(R.id.banner_viewPager);
        bannerMoviesPagerAdapter = new BannerMoviesPagerAdapter(this,bannerMoviesList);
        bannerMoviesViewPager.setAdapter(bannerMoviesPagerAdapter);
        indicatorTab.setupWithViewPager(bannerMoviesViewPager);

        sliderTimer = new Timer();
        sliderTimer.scheduleAtFixedRate(new AutoSlider(),4000,6000);
        indicatorTab.setupWithViewPager(bannerMoviesViewPager,true);
    }

    class AutoSlider extends TimerTask {

        @Override
        public void run() {
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (bannerMoviesViewPager.getCurrentItem() < homeBannerList.size() - 1) {
                        bannerMoviesViewPager.setCurrentItem(bannerMoviesViewPager.getCurrentItem() + 1);
                    } else {
                        bannerMoviesViewPager.setCurrentItem(0);
                    }
                }
            });
        }
    }

    public void setMainRecycler(List<AllCategory> allCategoryList) {

        mainRecycler = findViewById(R.id.main_recycler);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        mainRecycler.setLayoutManager(layoutManager);
        mainRecyclerAdapter = new MainRecyclerAdapter(this,allCategoryList);
        mainRecycler.setAdapter(mainRecyclerAdapter);
    }

    private void setScrollDefaultState()
    {
        nestedScrollView.fullScroll(View.FOCUS_UP);
        nestedScrollView.scrollTo(0,0);
        appBarLayout.setExpanded(true);
    }

    private void getBannerData() {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(RetrofitClient.getRetrofitClient().getAllBanners()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeWith(new DisposableObserver<List<BannerMovies>>() {
                                @Override
                                public void onNext(List<BannerMovies> bannerMovies){

                                    for (int i = 0; i < bannerMovies.size(); i++)
                                    {
                                        if (bannerMovies.get(i).getBannerCategoryId().toString().equals("1"))
                                            homeBannerList.add(bannerMovies.get(i));
                                        else if (bannerMovies.get(i).getBannerCategoryId().toString().equals("2"))
                                            tvShowBannerList.add(bannerMovies.get(i));
                                        else if (bannerMovies.get(i).getBannerCategoryId().toString().equals("3"))
                                            movieBannerList.add(bannerMovies.get(i));
                                        else if (bannerMovies.get(i).getBannerCategoryId().toString().equals("4"))
                                            kidsBannerList.add(bannerMovies.get(i));
                                        else
                                        {

                                        }
                                    }

                                }

                                @Override
                                public void onError(Throwable e) {

                                }

                                @Override
                                public void onComplete() {
                                    //default tap selected
                                    setBannerMoviesPagerAdapter(homeBannerList);
                                }
                            }));
    }

    private void getAllmoviesData(int categoryId) {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(RetrofitClient.getRetrofitClient().getAllCategoryMovies(categoryId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<List<AllCategory>>() {
                    @Override
                    public void onNext(List<AllCategory> allCategoryList){
                        setMainRecycler(allCategoryList);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                }));
    }

}