package com.nancio.cookie;

import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nancio.cookie.views.BuildingView;

/*
    Todo:
    - change upgrade's GridView for LinearLayout(horizontal)
    - settings
 */
public class MainActivity extends ActionBarActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    boolean visible, running;
    //static long timeGone = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("onCreate");


        //Game.resetUpgrades(getSharedPreferences("game", 0).edit());

        //RestorePreferences
        Game.init(getSharedPreferences("game", 0), this);
        //Game.save(getSharedPreferences("game", 0).edit());
        //Game.recalcCps();
        /*if(timeGone!=0){
            timeGone=new Date().getTime() - timeGone;
            Game.restoreCookies(timeGone);
            timeGone = 0;
        }*/

        setContentView(R.layout.activity_main);


        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        //Starts on big cookie fragment
        mViewPager.setCurrentItem(1);

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            int pos;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                pos = position;
                switch(position) {
                    case 2:
                        //Fill the upgrade grid
                        GridView grid = (GridView) findViewById(R.id.upgrade_box);
                        grid.setAdapter(new ImageAdapter(MainActivity.this, 1));
                        break;
                    case 0:
                        //update stats
                        ((TextView) findViewById(R.id.cpc_label))
                                .setText(Game.getCpc().getFormatted() + " cpc");
                        ((TextView) findViewById(R.id.spent_cookies_label))
                                .setText(Game.getSpent().getFormattedRounded() + " cookies");
                        ((TextView) findViewById(R.id.cookie_clicks_label))
                                .setText(Game.getCookieClicks() + " clicks");
                        ((TextView) findViewById(R.id.handmade_label))
                                .setText(Game.getHandmade().getFormattedRounded() + " cookies");
                        ((TextView) findViewById(R.id.buildings_label))
                                .setText(Game.getTotalBuildings() + "");
                        ((TextView) findViewById(R.id.goldclicks_label))
                                .setText(Game.getGoldenClicks() + " clicked");
                        ((TextView) findViewById(R.id.cps_multi_label))
                                .setText(Game.getCpsMulti().getFormatted() + " percent");
                        ((TextView) findViewById(R.id.profited_cookies_label))
                                .setText(Game.getProfited().getFormatted() + " cookies");
                        ((TextView) findViewById(R.id.game_resets_label))
                                .setText(Game.getResets() + "");

                        Cookiefloat hc = Game.getHeavenlyChips(), m = Cookiefloat.m(hc, new Cookiefloat("2"));
                        ((TextView) findViewById(R.id.heavenly_chips_label))
                                .setText(hc.getFormattedRounded() + " chips (+" + m.getFormatted() + "% multiplier)");

                        GridView gridBought = (GridView) findViewById(R.id.bought_upgrades);
                        gridBought.setAdapter(new ImageAdapter(MainActivity.this, 2));

                        ((TextView)findViewById(R.id.upgrades_num)).setText(Game.UpgradeManager.getBoughtLength()+"/"+Game.UpgradeManager.getLength());

                        //stats changing over time
                        Thread stats = new Thread(){
                            public void run(){
                                while(pos == 0){
                                    runOnUiThread(new Runnable(){

                                        @Override
                                        public void run() {
                                            ((TextView) findViewById(R.id.alltime_cookies_label))
                                                    .setText(Game.getTotalCookies().getFormattedRounded() + " cookies");
                                            ((TextView) findViewById(R.id.bakin_since_label))
                                                    .setText(Game.getSince(false));
                                            ((TextView) findViewById(R.id.total_time_label))
                                                    .setText(Game.getSince());
                                        }
                                    });
                                    try {
                                        Thread.sleep(50);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    pos = mViewPager.getCurrentItem();
                                }
                            }
                        };

                        stats.start();


                        break;

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        running = true;
        final Timer timer = new Timer();
        TimerTask timerTask= new TimerTask(){
            @Override
            public void run() {
                final int secs = Game.lifeCicle();
                if(secs!=-1){//Golden Cookie
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final ImageView gold = ((ImageView)findViewById(R.id.golden_cookie_view));
                            Point d = new Point();
                            d.y = ((RelativeLayout)gold.getParent()).getHeight();
                            d.x = ((RelativeLayout)gold.getParent()).getWidth();
                            ((RelativeLayout.LayoutParams)gold.getLayoutParams()).setMargins(
                                    (int) (Math.random() * (d.x - gold.getWidth())), (int) (Math.random() * (d.y - gold.getHeight())), 0, 0
                            );
                            AnimationSet set = new AnimationSet(true);
                            AlphaAnimation alpha = new AlphaAnimation(0.0f, 1.0f);
                            alpha.setDuration(secs*1000/3);
                            gold.setAlpha(1.0f);
                            set.addAnimation(alpha);
                            alpha = new AlphaAnimation(1.0f, 0.0f);
                            alpha.setDuration(secs*1000/3);
                            alpha.setStartOffset(2*secs*1000/3);
                            alpha.setAnimationListener(new Animation.AnimationListener() {
                                @Override
                                public void onAnimationStart(Animation animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animation animation) {
                                    gold.setAlpha(0.0f);
                                    ((RelativeLayout.LayoutParams)gold.getLayoutParams()).setMargins(0, 0, 0, 0);
                                }

                                @Override
                                public void onAnimationRepeat(Animation animation) {

                                }
                            });
                            set.addAnimation(alpha);
                            gold.startAnimation(set);
                        }
                    });
                }
                if(!running) timer.cancel();
            }
        };
        timer.scheduleAtFixedRate(timerTask, 0, 50);

        Thread thread = new Thread(){
            public void run(){
                while(running) {
                    if (visible) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                repaintTitle();
                            }
                        });
                    }
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

        };

        thread.start();

    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        System.out.println("onSaveInstanceState");

        SharedPreferences.Editor preferences = getSharedPreferences("game", 0).edit();
        Game.save(preferences);

        state.putLong("longTime", new Date().getTime());
    }

    @Override
    protected void onStart() {
        super.onStart();
        running = true;
        setTitle(Game.getCookies().getFormattedRounded());
        getSupportActionBar().setSubtitle(Game.getCps().getFormatted()+" cps");

        System.out.println("onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        visible = true;

        System.out.println("onResume");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        running = false;
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        System.out.println("onRestoreInstanceState");
        long restore = new Date().getTime();
        restore -= savedInstanceState.getLong("longTime", restore);
        Game.restoreCookies(restore);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_legacy){
                Cookiefloat plus = Game.calculateHeavenlyChips();
                if(plus == null){
                    String msg = String.format(getString(R.string.noreset_dialog_text), Game.getNeededToRestart().getFormatted());
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage(msg)
                            .setTitle(R.string.reset_dialog_title).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.create().show();
                }else {
                    String msg = String.format(getString(R.string.reset_dialog_text),plus.getFormattedRounded());
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(msg).setTitle(R.string.reset_dialog_title)
                            .setPositiveButton(R.string.reset_dialog_accept, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    Game.softReset();
                                    mViewPager.setCurrentItem(1, true);
                                    getSupportActionBar().setSubtitle("0.0 cps");

                                }
                            }).setNegativeButton(R.string.reset_dialog_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                        }
                    });
                    builder.create().show();
                }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public final void repaintTitle(){
        this.setTitle(Game.getCookies().getFormattedRounded() + " cookies");
    }

    public void bigClick(View view){
        try {
            Game.click();
            //
            final TextView t = new TextView(this);
            t.setText("+" + Game.getCpc().getFormatted());
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.CENTER_HORIZONTAL);
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            t.setLayoutParams(params);
            t.setPadding(10, 32, 10, 16);
            //((RelativeLayout)mSectionsPagerAdapter.getItem(1).getView()).addView(t);
            final RelativeLayout rl = (RelativeLayout) findViewById(R.id.bigCookie_layout);
            rl.addView(t);

            TranslateAnimation transl = new TranslateAnimation(0, 0, 0, -16);
            AlphaAnimation alpha = new AlphaAnimation(1, 0);
            transl.setDuration(1000);
            alpha.setDuration(1000);

            AnimationSet set = new AnimationSet(false);
            set.addAnimation(transl);
            set.addAnimation(alpha);


            //Animation set = AnimationUtils.loadAnimation(this, R.anim.upvote);
           transl.setAnimationListener(new AnimationSet.AnimationListener() {
               @Override
               public void onAnimationStart(Animation animation) {
               }

               @Override
               public void onAnimationEnd(Animation animation) {
                   rl.removeView(t);
               }

               @Override
               public void onAnimationRepeat(Animation animation) {
                   animation.cancel();
               }
           });
            t.startAnimation(transl);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void goldenClick(View view){
        if(view.getAlpha()==0) return;
        String desc="";
        final Object goldClick[] = Game.goldenClick();
        switch((int)goldClick[0]){
            case 0:
                desc = "Lucky! +"+((Cookiefloat)goldClick[1]).getFormattedRounded()+" cookies";
                break;
            case 1:
                desc = "Frenzy! cps x7 for "+(int)goldClick[1]+" seconds";
                final Cookiefloat cps7 = new Cookiefloat(Game.getCps());
                getSupportActionBar().setSubtitle(cps7.getFormatted() + " cps");
                Thread goldenThread = new Thread(){
                    public void run(){
                        try {
                            Thread.sleep(((int)goldClick[1])*1000);
                            while(Game.getCps().gt(cps7)) Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                getSupportActionBar().setSubtitle(Game.getCps().getFormatted() + " cps");
                                if(mViewPager.getCurrentItem()==0){
                                    ((TextView) findViewById(R.id.cpc_label))
                                            .setText(Game.getCpc().getFormatted() + " cpc");
                                    ((TextView) findViewById(R.id.cps_multi_label))
                                            .setText(Game.getCpsMulti().getFormatted() + " percent");
                                }
                            }
                        });
                    }
                };
                goldenThread.start();
                break;
            case 2:
                desc = "Click Frenzy! clicks x777 for "+ (int)goldClick[1]+ " seconds";
        }
        final TextView gd = (TextView)findViewById(R.id.golden_desc);
        gd.setText(desc);
        gd.setAlpha(1.0f);
        AnimationSet set = new AnimationSet(true);
        set.setFillEnabled(true);
        AlphaAnimation alpha = new AlphaAnimation(0, 1);
        alpha.setDuration(1000);
        set.addAnimation(alpha);
        alpha = new AlphaAnimation(1.0f, 0.0f);
        alpha.setDuration(1000);
        alpha.setStartOffset(3000);
        alpha.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                gd.setAlpha(0);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        set.addAnimation(alpha);
        gd.startAnimation(set);
        view.setAlpha(0);
        ((RelativeLayout.LayoutParams)view.getLayoutParams()).setMargins(0,0,0,0);
    }

    public void buildingClick(View view){
        String bi = ((BuildingView)view).getBuildingId();
        int i;
        for(i=0; i<11; ++i){
            if(bi.equals(getResources().getQuantityString(Game.getBuildingName(i), 1))){
                Game.selectBuilding(i);
                break;
            }
        }

        ((TextView)findViewById(R.id.check_title)).setText(getResources().getQuantityString(Game.getBuildingName(i), 1));
        ((TextView)findViewById(R.id.check_description)).setText(getString(Game.getBuildingDesc(i)));
        ((TextView)findViewById(R.id.check_cookies)).setText("+"+Game.getBuildingTotalIncome(i).getFormatted()+" cps");
        ((ImageView)findViewById(R.id.check_image)).setImageResource(Game.getBuildingDraw(i));
        findViewById(R.id.check_quote).setAlpha(0);

        View checkout = findViewById(R.id.checkout);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)checkout.getLayoutParams();
        ExpandAnimation ex = new ExpandAnimation(lp.weight, 1, checkout);
        ex.setDuration(300);
        checkout.startAnimation(ex);
    }

    public void upgradeClick(Game.UpgradeManager.Upgrade up){

        Game.selectBuilding(-1);
        Game.selectUpgrade(up.id);

        ((TextView)findViewById(R.id.check_title)).setText(up.name);
        ((TextView)findViewById(R.id.check_description)).setText(up.desc);
        ((TextView)findViewById(R.id.check_cookies)).setText(new Cookiefloat(up.price).getFormattedRounded());
        ((ImageView)findViewById(R.id.check_image)).setImageResource(up.image);
        ((TextView)findViewById(R.id.check_quote)).setText(up.quote);
        findViewById(R.id.check_quote).setAlpha(1);

        View checkout = findViewById(R.id.checkout);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)checkout.getLayoutParams();
        ExpandAnimation ex = new ExpandAnimation(lp.weight, 1, checkout);
        ex.setDuration(300);
        checkout.startAnimation(ex);

    }

    public void buy(View viaw){
        if(Game.bselected) {
            int i = Game.buyBuilding();
            if (i != -1) {
                LinearLayout layout = (LinearLayout) findViewById(R.id.buildings_layout);
                BuildingView view = (BuildingView) layout.getChildAt(i);
                view.setCost(Game.getBuildingCost(i).getFormattedRounded());
                view.setN(Game.getBuildingN(i));

                GridView grid = (GridView)findViewById(R.id.upgrade_box);
                grid.setAdapter(new ImageAdapter(this, 1));

                getSupportActionBar().setSubtitle(Game.getCps().getFormatted() + " cps");
            }
        }else{
            int i = Game.buyUpgrade();
            if(i!=-1){
                GridView grid = (GridView)findViewById(R.id.upgrade_box);
                grid.setAdapter(new ImageAdapter(this, 1));

                getSupportActionBar().setSubtitle(Game.getCps().getFormatted() + " cps");

                View checkout = findViewById(R.id.checkout);
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)checkout.getLayoutParams();
                ExpandAnimation ex = new ExpandAnimation(lp.weight, 0, checkout);
                ex.setDuration(300);
                checkout.startAnimation(ex);
            }
        }
    }

    public void closeCheckout(View view){
        View checkout = findViewById(R.id.checkout);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)checkout.getLayoutParams();
        ExpandAnimation ex = new ExpandAnimation(lp.weight, 0, checkout);
        ex.setDuration(300);
        checkout.startAnimation(ex);
    }

    @Override
    public void onBackPressed() {
        if(mViewPager.getCurrentItem()==0){
            mViewPager.setCurrentItem(1, true);
        }else if(mViewPager.getCurrentItem()==2){
            View checkout = findViewById(R.id.checkout);
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)checkout.getLayoutParams();
            if(lp.weight>0) {
                ExpandAnimation ex = new ExpandAnimation(lp.weight, 0, checkout);
                ex.setDuration(300);
                checkout.startAnimation(ex);
            }else  mViewPager.setCurrentItem(1, true);
        }else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.exit_dialog_text).setTitle(R.string.exit_dialog_title)
                    .setPositiveButton(R.string.exit_dialog_stay,new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            }).setNegativeButton(R.string.exit_dialog_title, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    Game.save(getSharedPreferences("game", 0).edit());
                    finish();
                }
            });
            builder.create().show();
            //super.onBackPressed();
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);

            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            int section;
            switch(this.getArguments().getInt(ARG_SECTION_NUMBER)){
                case 1: section = R.layout.stats_fragment; break;
                case 2: section = R.layout.big_cookie_fragment; break;
                default: section = R.layout.upgrades_fragment;

            }
            return inflater.inflate(section, container, false);
        }
    }

    public class ExpandAnimation extends Animation {

        private final float mStartWeight;
        private final float mDeltaWeight;
        private final View mContent;

        public ExpandAnimation(float start, float end, View target){
            mStartWeight = start;
            mDeltaWeight = end - start;
            mContent = target;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mContent.getLayoutParams();
            lp.weight = (mStartWeight+(mDeltaWeight*interpolatedTime));
            mContent.setLayoutParams(lp);
        }

        @Override
        public boolean willChangeBounds() {
            return true;
        }
    }

}
