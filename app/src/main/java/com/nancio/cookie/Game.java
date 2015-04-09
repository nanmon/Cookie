package com.nancio.cookie;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by nancio on 4/01/15.
 */
public class Game{

    private static Cookiefloat cookies = new Cookiefloat(0);
    private static Cookiefloat cps = new Cookiefloat(0);
    private static Cookiefloat multi = new Cookiefloat(1);
    private static Cookiefloat profited = new Cookiefloat(0);
    private static Cookiefloat spent = new Cookiefloat(0);
    private static Cookiefloat heavenlyChips = new Cookiefloat(0);
    private static Cookiefloat neededToRestart = new Cookiefloat("1000000000000");
    private static long bakinSince = 0;
    private static long totalBakingTime = 0;
    private static final Cookiefloat times = new Cookiefloat("0.05");
    public static boolean bselected = true;
    private static int resets = 0;

    public static void init(SharedPreferences preferences, Context context){

        cookies = new Cookiefloat(preferences.getString("cookies", "0"));
        cps = new Cookiefloat(preferences.getString("cps", "0"));
        multi = new Cookiefloat(preferences.getString("cps_multi", "1"));
        spent = new Cookiefloat(preferences.getString("spent", "0"));
        profited = new Cookiefloat(preferences.getString("profited", "0"));
        heavenlyChips = new Cookiefloat(preferences.getString("heaven_chips", "0"));
        neededToRestart = Cookiefloat.m(Cookiefloat.s(heavenlyChips, new Cookiefloat(1)), new Cookiefloat("1000000000000"));
        resets = preferences.getInt("resets", 0);

        BigCookie.cpc = new Cookiefloat(preferences.getString("cpc", "1"));
        BigCookie.multi = preferences.getInt("big_multi", 1);
        BigCookie.extra = new Cookiefloat(preferences.getString("big_extra", "0"));
        BigCookie.percent = preferences.getInt("big_percent", 0);
        BigCookie.handmade = new Cookiefloat(preferences.getString("handmade", "0"));
        BigCookie.clicks = preferences.getLong("clicks", 0);
        BigCookie.each = preferences.getFloat("big_each", 0);

        GoldenCookie.minutes = preferences.getInt("golden_time", 10);
        GoldenCookie.effectTimes = preferences.getInt("golden_multi", 1);
        GoldenCookie.showSecs = preferences.getInt("golden_showtime", 13);
        GoldenCookie.clicks = preferences.getInt("golden_clicks", 0);
        GoldenCookie.framesLeft = GoldenCookie.minutes*1200;

        for(int i=0; i<11; ++i){
            BuildingManager.n[i] = preferences.getInt("bn"+i, 0);
            BuildingManager.cps[i] = new Cookiefloat(preferences.getString("bbase" + i, BuildingManager.cps[i].get()));
            BuildingManager.costs[i] = new Cookiefloat(preferences.getString("bcost"+i, BuildingManager.costs[i].get()));
            BuildingManager.multis[i] = preferences.getInt("bmulti"+i, 1);
        }
        BuildingManager.cursorExtras = new Cookiefloat(preferences.getString("cursor_extra", "0"));

        bakinSince = preferences.getLong("since", new Date().getTime());
        totalBakingTime = preferences.getLong("total_baking_time", new Date().getTime());
        //UpgradesData.setPreferences(preferences);
        UpgradeManager.init(preferences, context);
        for(int i=0; i<11; ++i) UpgradeManager.onBuildingBought(i, BuildingManager.n[i]);
        UpgradeManager.onHandmade(BigCookie.handmade);
    }

    public static void save(SharedPreferences.Editor preferences){
        //multi, profit, hc, golden clicks
        preferences.putString("cookies", cookies.get());
        preferences.putString("cps", cps.get());
        preferences.putString("spent", spent.get());
        preferences.putString("cps_multi", multi.get());
        preferences.putString("profited", profited.get());
        preferences.putString("heaven_chips", heavenlyChips.get());
        preferences.putInt("resets", resets);

        preferences.putString("cpc", BigCookie.cpc.get());
        preferences.putString("big_extra", BigCookie.extra.get());
        preferences.putInt("big_multi", BigCookie.multi);
        preferences.putInt("big_percent", BigCookie.percent);
        preferences.putString("handmade", Game.getHandmade().get());
        preferences.putLong("clicks", Game.getCookieClicks());
        preferences.putFloat("big_each", BigCookie.each);

        preferences.putInt("golden_time", GoldenCookie.minutes);
        preferences.putInt("golden_multi", GoldenCookie.effectTimes);
        preferences.putInt("golden_showtime", GoldenCookie.showSecs);
        preferences.putInt("golden_clicks", GoldenCookie.clicks);

        for(int i=0; i<11; ++i) {
            preferences.putInt("bn" + i, BuildingManager.n[i]);
            preferences.putString("bcost" + i, BuildingManager.costs[i].get());
            preferences.putString("bbase" + i, BuildingManager.cps[i].get());
            preferences.putInt("bmulti" + i, BuildingManager.multis[i]);
        }
        preferences.putString("cursor_extra", BuildingManager.cursorExtras.get());

        preferences.putLong("since", bakinSince);
        preferences.putLong("total_baking_time", totalBakingTime);

        for(int i=0; i<UpgradeManager.ups.length; ++i){
            preferences.putInt("up"+i, UpgradeManager.ups[i].state);
        }

        preferences.apply();
    }

    public static long getBakinSince(){
        return bakinSince;
    }

    public static long getTotalBakingTime() { return totalBakingTime; }

    public static String getSince(){
        return getSince(true);
    }

    public static String getSince(boolean thisGame){
        long time = new Date().getTime() - (thisGame ? bakinSince : totalBakingTime);
        time/=60000;
        if(time/1440>0) return time/1440 + " days";
        if(time/60>0) return time/60 + " hours";
        return time + " minutes";
    }

    public static Cookiefloat getHeavenlyChips(){
        return heavenlyChips;
    }

    public static Cookiefloat getProfited(){
        return profited;
    }

    public static int getResets(){
        return resets;
    }

    public static Cookiefloat calculateHeavenlyChips(){
        if(!(getTotalCookies().gt(neededToRestart))) return null;
        long trillions = Cookiefloat.s(profited, getTotalCookies()).getTrillions();
        long hc = (long)(Math.sqrt(8*trillions+1)-1)/2;
        Cookiefloat r = new Cookiefloat(hc+"");
        return Cookiefloat.r(r, heavenlyChips);
    }

    public static Cookiefloat getNeededToRestart(){
        return neededToRestart;
    }

    public static void restoreCookies(long time){
        float seconds = time/1000.0f;
        cookies.s(Cookiefloat.m(getCps(), new Cookiefloat(seconds+"")));
    }

    public static void lifeCicle(long nanoseconds){
        float seconds = (float)nanoseconds/1000000000.0f;
        System.out.println(seconds);
        cookies.s(Cookiefloat.m(cps, new Cookiefloat(seconds+"")));
    }

    public static int lifeCicle(){
        --GoldenCookie.frenzy;
        --GoldenCookie.clickFrenzy;
        cookies.s(Cookiefloat.m(getCps(), times));
        if(--GoldenCookie.framesLeft <= 0){
            GoldenCookie.framesLeft = GoldenCookie.minutes*1200;
            return GoldenCookie.showSecs;
        }
        return -1;
    }

    private Game() {}

    public static Cookiefloat getCookies() {
        return cookies;
    }

    public static Cookiefloat getCps() {
        Cookiefloat r = new Cookiefloat(cps);
        if(!(new Cookiefloat(1).gt(multi))) r.m(multi);
        if(GoldenCookie.frenzy>=0) r.m(new Cookiefloat(7));
        return r;
    }

    public static Cookiefloat getTotalCookies(){
        return Cookiefloat.s(cookies, spent);
    }

    public static Cookiefloat getSpent() {
        return spent;
    }

    public static Cookiefloat getCpsMulti(){
        Cookiefloat r = Cookiefloat.m(multi, new Cookiefloat(100));
        if(GoldenCookie.frenzy>=0) r.m(new Cookiefloat(7));
        return r;
    }

    /**
     * Big Cookie
     */

    private static class BigCookie {
        public static Cookiefloat cpc = new Cookiefloat(1);
        public static int multi = 1;
        public static Cookiefloat extra = new Cookiefloat(0);
        public static int percent = 0;
        public static Cookiefloat handmade = new Cookiefloat(0);
        public static long clicks = 0;
        public static float each = 0;

    }

    public static Cookiefloat click(){
        Cookiefloat made = getCpc();
        if(Math.random() == 0.24) made.m(new Cookiefloat(25000000));
        cookies.s(made);
        BigCookie.handmade.s(made);
        ++BigCookie.clicks;
        UpgradeManager.onHandmade(BigCookie.handmade);
        return cookies;
    }

    public static long getCookieClicks(){
        return BigCookie.clicks;
    }

    public static Cookiefloat getHandmade(){
        return BigCookie.handmade;
    }

    public static Cookiefloat getCpc(){
        Cookiefloat r = Cookiefloat.m(BigCookie.cpc, new Cookiefloat(BigCookie.multi));
        r.s(BigCookie.extra);
        if(BigCookie.percent!=0) r.s(Cookiefloat.m(getCps(), new Cookiefloat("0.0"+BigCookie.percent)));
        if(GoldenCookie.clickFrenzy>=0) r.m(new Cookiefloat(777));
        return r;
    }

    /**
     * Golden Cookie
     */

    private static class GoldenCookie{
        public static int minutes = 10;
        public static int framesLeft = minutes*1200;
        public static int showSecs = 13;
        public static int effectTimes = 1;
        public static int frenzy=-1;
        public static int clickFrenzy=-1;
        public static int clicks=0;
    }

    public static Object[] goldenClick(){
        UpgradeManager.onGoldenClick(++GoldenCookie.clicks);
        double prob = Math.random()*100;
        if(prob<=47){
            //Lucky! + (s || r) cookies!
            Cookiefloat s = Cookiefloat.m(cookies, new Cookiefloat("0.1"));
            Cookiefloat r = Cookiefloat.m(getCps(), new Cookiefloat(1200));
            if(s.gt(r)){
                r.s(new Cookiefloat(13));
                cookies.s(r);
                return new Object[] {0, r};
            }

            s.s(new Cookiefloat(13));
            cookies.s(s);

            return new Object[] {0, s};
        }if(prob<=94) {
            //Frenzy x7 for 77 seconds
            GoldenCookie.frenzy = 77 * GoldenCookie.effectTimes * 20;
            return new Object[] {1, GoldenCookie.frenzy/20};
        }
        //Click frenzy! clicks x777 for 13 seconds
        GoldenCookie.clickFrenzy=13*GoldenCookie.effectTimes*20;
        return new Object[] {2, GoldenCookie.clickFrenzy/20};
    }

    public static int getGoldenClicks(){
        return GoldenCookie.clicks;
    }

    /**
     * Building Management
     **/
    public static class BuildingManager{
        public static int selected;
        public static final Cookiefloat priceRiser = new Cookiefloat("1.15");
        public static int n[] = new int[11];
        public static Cookiefloat cps[] = new Cookiefloat[] {
                new Cookiefloat("0.1"), new Cookiefloat("0.5"), new Cookiefloat(4),
                new Cookiefloat(10), new Cookiefloat(40), new Cookiefloat(100),
                new Cookiefloat(400), new Cookiefloat(6666), new Cookiefloat(98765),
                new Cookiefloat(999999), new Cookiefloat(10000000)
        };

        public static int multis[] = {1,1,1,1,1,1,1,1,1,1,1};

        public static Cookiefloat cursorExtras = new Cookiefloat(0);

        public static Cookiefloat costs[] = new Cookiefloat[] {
                new Cookiefloat(15), new Cookiefloat(100), new Cookiefloat(500),
                new Cookiefloat(3000), new Cookiefloat(10000), new Cookiefloat(40000),
                new Cookiefloat(200000), new Cookiefloat(1666666), new Cookiefloat(123456789),
                new Cookiefloat("3999999999"), new Cookiefloat("75000000000")
        };

        public static final int names[] = {R.plurals.cursor, R.plurals.grandma,
                R.plurals.farm, R.plurals.factory, R.plurals.mine, R.plurals.ship,
                R.plurals.lab, R.plurals.portal, R.plurals.time_machine, R.plurals.antimatter, R.plurals.prism
        };

        private static final int descs[] = {R.string.cursor_desc, R.string.grandma_desc,
                R.string.farm_desc, R.string.factory_desc, R.string.mine_desc, R.string.ship_desc,
                R.string.lab_desc, R.string.portal_desc, R.string.time_machine_desc, R.string.antimatter_desc, R.string.prism_desc
        };

        private static final int draws[] = {R.drawable.ic_cursor, R.drawable.ic_grandma,
                R.drawable.ic_farm, R.drawable.ic_factory, R.drawable.ic_mine, R.drawable.ic_ship,
                R.drawable.ic_lab, R.drawable.ic_portal, R.drawable.ic_time, R.drawable.ic_antimatter, R.drawable.ic_prism
        };
    }

    public static int getBuildingName(int i){
        return BuildingManager.names[i];
    }

    public static int getBuildingDesc(int i){
        return BuildingManager.descs[i];
    }

    public static int getBuildingDraw(int i){
        return BuildingManager.draws[i];
    }

    public static void selectBuilding(int i){
        BuildingManager.selected = i;
        bselected = true;
    }

    public static int buyBuilding(){
        int i = BuildingManager.selected;
        if(cookies.gt(BuildingManager.costs[i])) {
            cookies.r(BuildingManager.costs[i]);
            spent.s(BuildingManager.costs[i]);
            ++BuildingManager.n[i];
            cps.s(getBuildingCps(i));
            BuildingManager.costs[i].m(BuildingManager.priceRiser);
            UpgradeManager.onBuildingBought(i, BuildingManager.n[i]);
            if(i!=0 && BigCookie.each!=0.0f){
                Cookiefloat s = new Cookiefloat(BigCookie.each+"");
                BigCookie.extra.s(s);
                BuildingManager.cursorExtras.s(s);
                cps.s(Cookiefloat.m(s, new Cookiefloat(BuildingManager.n[0])));
            }
            return i;
        }
        return -1;
    }

    public static Cookiefloat getBuildingCost(int i){
        return BuildingManager.costs[i];
    }

    public static int getBuildingN(int i){
        return BuildingManager.n[i];
    }

    public static Cookiefloat getBuildingCps(int i){
        Cookiefloat r = Cookiefloat.m(BuildingManager.cps[i], new Cookiefloat(BuildingManager.multis[i]));
        if(i==0)
            return Cookiefloat.s(r, BuildingManager.cursorExtras);
        return r;
    }

    public static Cookiefloat getBuildingTotalIncome(int i){
        if(new Cookiefloat(1).gt(multi)) return getBuildingCps(i);
        return Cookiefloat.m(getBuildingCps(i), multi);
    }

    public static int getTotalBuildings(){
        int n=0;
        for(int i=0; i<11; ++i){
            n+=BuildingManager.n[i];
        }
        return n;
    }

    /**
     * Upgrade Management
     */

    public static void selectUpgrade(int i){
        BuildingManager.selected = -1;
        UpgradeManager.selected = i;
        bselected = false;
    }

    public static int buyUpgrade(){
        return UpgradeManager.buy();
    }

    public static class UpgradeManager{

        private static Context c;
        public static int selected;

       public static class Upgrade {
            public String types, effects, unlocks, unlockTypes, price, desc;
            public int id, image, name, quote, state;


            public Upgrade(int n, int i, String t, String e, String u, String ut, String p){
                id = n;
                types = t;
                effects = e;
                state = 0;
                unlocks = u;
                unlockTypes = ut;
                price = p;
                image = i;
                name = c.getResources().getIdentifier("up"+n+"_name", "string", "com.nancio.cookie");
                quote = c.getResources().getIdentifier("up"+n+"_quote", "string", "com.nancio.cookie");
                switch(types.charAt(0)){
                    case '0':
                        if(types.charAt(2)=='0'){
                            if(Float.parseFloat(effects)!=0){
                                desc = String.format(c.getString(R.string.up_effect01),effects);
                            }else desc = c.getString(R.string.up_effect010);
                        }else if(Integer.parseInt(types.substring(2))==11){
                                desc = c.getString(R.string.up_effect11);
                        }else{
                            desc = String.format(c.getString(R.string.up_effect0), c.getResources().getQuantityString(BuildingManager.names[Integer.parseInt(types.substring(2))], 2));
                        }
                        break;
                    case '1':
                        if(types.charAt(2)=='0'){
                            desc = String.format(c.getString(R.string.up_effect10), (int)(Float.parseFloat(effects)*100));
                        }else if(types.charAt(2)=='2'){
                            if(effects.charAt(0)=='2'){
                                desc = c.getString(R.string.up_effect122);
                            }else{
                                desc = c.getString(R.string.up_effect121);
                            }
                        }
                        break;
                }
            }
        }

        private static Upgrade ups[];
        private static ArrayList bought, unlocked, bLock[] = new ArrayList[11], uLock, cLock, hLock, gLock;

        public static void init(SharedPreferences preferences, Context context){
            UpgradeManager.setContext(context);
            bought = new ArrayList();
            unlocked = new ArrayList();
            //uLock = new ArrayList();
            cLock = new ArrayList();
            hLock = new ArrayList();
            gLock = new ArrayList();
            for(int i=0; i<bLock.length; ++i) bLock[i] = new ArrayList();

            for(int i=0; i<ups.length; ++i){
                if(preferences.getInt("up"+i, 0)==2){
                    bought.add(i);
                    ups[i].state = 2;
                }else{
                    int needs[] = {
                            Integer.parseInt(ups[i].unlockTypes.split(",")[0]),
                            Integer.parseInt(ups[i].unlockTypes.split(",")[1])
                    };

                    switch(needs[0]){
                        case 0:
                            bLock[needs[1]].add(i);
                            break;
                        case 1:
                            switch(needs[1]){
                                case 0:
                                    cLock.add(i);
                                    break;
                                case 1:
                                    hLock.add(i);
                                    break;
                                case 2:
                                    gLock.add(i);
                                    break;
                            }
                            break;
                    }

                }
            }
        }


        public static void onBuildingBought(int which, int much){
            for(int i=0; i<bLock[which].size(); ++i){
                if(Integer.parseInt(ups[(int)bLock[which].get(i)].unlocks)<=much){
                    int u = (int)bLock[which].remove(i);
                    --i;
                    if(unlocked.size()==0) unlocked.add(u);
                    else {
                        int j=0;
                        for (; j < unlocked.size(); ++j) {
                            if (new Cookiefloat(ups[(int) unlocked.get(j)].price).gt(new Cookiefloat(ups[u].price))) {
                                unlocked.add(j, u);
                                break;
                            }
                        }
                        if(j==unlocked.size()) unlocked.add(u);
                    }
                }
            }
        }

        public static void onHandmade(Cookiefloat made){
            for(int i=0; i<hLock.size(); ++i){
                if(made.gt(new Cookiefloat(ups[(int)hLock.get(i)].unlocks))){
                    int u = (int)hLock.remove(i);
                    --i;
                    if(unlocked.size()==0) unlocked.add(u);
                    else {
                        int j=0;
                        for (; j < unlocked.size(); ++j) {
                            if (new Cookiefloat(ups[(int) unlocked.get(j)].price).gt(new Cookiefloat(ups[u].price))) {
                                unlocked.add(j, u);
                                j = unlocked.size();
                            }
                        }
                        if (j == unlocked.size()) unlocked.add(u);
                    }
                }
            }
        }

        public static void onGoldenClick(int clicks){
            for(int i=0; i<gLock.size(); ++i){
                if(clicks >= Integer.parseInt(ups[(int)gLock.get(i)].unlocks)){
                    int u = (int)gLock.remove(i);
                    --i;
                    if(unlocked.size()==0) unlocked.add(u);
                    else {
                        int j=0;
                        for (; j < unlocked.size(); ++j) {
                            if (new Cookiefloat(ups[(int) unlocked.get(j)].price).gt(new Cookiefloat(ups[u].price))) {
                                unlocked.add(j, u);
                                j = unlocked.size();
                            }
                        }
                        if (j == unlocked.size()) unlocked.add(u);
                    }
                }
            }
        }

        public static void onCookieGet(Cookiefloat alltime){
            for(int i=0; i<cLock.size(); ++i){
                if(new Cookiefloat(ups[(int)cLock.get(i)].unlocks).gt(alltime)) return;
                    int u = (int)cLock.remove(i);
                    --i;
                    if(unlocked.size()==0) unlocked.add(u);
                    else {
                        int j=0;
                        for (; j < unlocked.size(); ++j) {
                            if (new Cookiefloat(ups[(int) unlocked.get(j)].price).gt(new Cookiefloat(ups[u].price))) {
                                unlocked.add(j, u);
                                j = unlocked.size();
                            }
                        }
                        if (j == unlocked.size()) unlocked.add(u);
                    }
            }
        }

        public static Upgrade[] getUnlocked(){
            onCookieGet(Cookiefloat.s(cookies, spent));
            Upgrade r[] = new Upgrade[unlocked.size()];
            for(int i=0; i<r.length; ++i){
                r[i] = ups[(int)unlocked.get(i)];
            }
            return r;
        }

        public static Upgrade[] getBought(){
            Upgrade r[] = new Upgrade[bought.size()];
            for(int i=0; i<r.length; ++i){
                r[i] = ups[(int)bought.get(i)];
            }
            return r;
        }

        public static int getBoughtLength(){
            return bought.size();
        }

        public static int getLength(){
            return ups.length;
        }

        public static int buy(){
            //int buyed = (int)unlocked.get(selected);
            if(ups[selected].state==2) return -1;
            Cookiefloat price = new Cookiefloat(ups[selected].price);
            if(!cookies.gt(price)) return -1;
            cookies.r(price);
            spent.s(price);

            ups[selected].state=2;
            for(int i=0; i<unlocked.size(); ++i)
                if((int)unlocked.get(i)==selected){ ///////////FIX!
                    unlocked.remove(i);
                    break;
                }
            Upgrade up = ups[selected];

            bought.add(selected);
            String t[] = up.types.split(",");
            int types[] = {
                    Integer.parseInt(t[0]), Integer.parseInt(t[1])
            };

            switch(types[0]){
                case 0:
                    if(types[1] == 0) {
                        if (Float.parseFloat(up.effects) == 0) {
                            cps.s(Cookiefloat.m(new Cookiefloat(BuildingManager.n[0]), getBuildingCps(0)));
                            BuildingManager.multis[0] *= 2;
                            BigCookie.multi *= 2;
                        } else {
                            float n = 0;
                            for (int i = 1; i < 11; ++i) {
                                n += BuildingManager.n[i];
                            }
                            n *= Float.parseFloat(up.effects);
                            cps.s(Cookiefloat.m(new Cookiefloat(BuildingManager.n[0]), new Cookiefloat(n + "")));
                            BuildingManager.cursorExtras.s(new Cookiefloat(n + ""));
                            BigCookie.extra.s(new Cookiefloat(n + ""));
                            BigCookie.each += Float.parseFloat(up.effects);
                        }
                    }else if(types[1] ==11) ++BigCookie.percent;
                    else{
                            cps.s(Cookiefloat.m(getBuildingCps(types[1]), new Cookiefloat(BuildingManager.n[types[1]])));
                            BuildingManager.multis[types[1]]*=2;
                    }
                    break;
                case 1:
                    switch(types[1]){
                        case 0:
                            multi.s(new Cookiefloat(up.effects));
                            break;
                        case 2:
                            if(up.effects.equals("2")){
                                GoldenCookie.minutes/=2;
                                GoldenCookie.showSecs*=2;
                            }else {
                                GoldenCookie.effectTimes *= 2;
                            }
                            break;
                    }
                    break;
            }
            return selected;
        }

        /*
        Add a upgrade:
        -new Upgrade(...),
        -if(new unlockType)
            -new ArrayList()
            -public void Listener()
            -setListener()
            -onBuy()
        -strings.xml
         */

        private static void setContext(Context c){
            UpgradeManager.c = c;
            ups = new Upgrade[] {
                    new Upgrade(0, R.drawable.cursor1, "0,0", "0", "1", "0,0", "100"),
                    new Upgrade(1,R.drawable.cursor1, "0,0", "0", "1", "0,0", "400"),
                    new Upgrade(2,R.drawable.cursor10, "0,0", "0", "10", "0,0", "10000"),
                    new Upgrade(3,R.drawable.cursor20, "0,0", "0.1", "20", "0,0", "500000"),
                    new Upgrade(4,R.drawable.cursor40, "0,0", "0.5", "40", "0,0", "50000000"),
                    new Upgrade(5,R.drawable.cursor40, "0,0", "2", "80", "0,0", "500000000"),
                    new Upgrade(6,R.drawable.cursor120, "0,0", "10", "120", "0,0", "5000000000"),
                    new Upgrade(7,R.drawable.cursor120, "0,0", "20", "160", "0,0", "50000000000"),
                    new Upgrade(8,R.drawable.cursor200, "0,0", "100", "200", "0,0", "50000000000000"),
                    new Upgrade(9,R.drawable.cursor200, "0,0", "200", "240", "0,0", "500000000000000"),
                    new Upgrade(10,R.drawable.cursor280, "0,0", "400", "280", "0,0", "5000000000000000"),
                    new Upgrade(11,R.drawable.cursor280, "0,0", "800", "320", "0,0", "50000000000000000"),
                    new Upgrade(12,R.drawable.grandma1, "0,1", "0", "1", "0,1", "1000"),
                    new Upgrade(13,R.drawable.grandma1, "0,1", "0", "1", "0,1", "10000"),
                    new Upgrade(14,R.drawable.grandma10, "0,1", "0", "10", "0,1", "10000"),
                    new Upgrade(15,R.drawable.grandma50, "0,1", "0", "50", "0,1", "5000000"),
                    new Upgrade(16,R.drawable.grandma100, "0,1", "0", "100", "0,1", "100000000"),
                    new Upgrade(17,R.drawable.grandma200, "0,1", "0", "200", "0,1", "800000000000"),
                    new Upgrade(18,R.drawable.grandma15, "0,1", "0", "15", "0,2", "50000"),
                    new Upgrade(19,R.drawable.grandma15, "0,1", "0", "15", "0,3", "300000"),
                    new Upgrade(20,R.drawable.grandma15, "0,1", "0", "15", "0,4", "1000000"),
                    new Upgrade(21,R.drawable.grandma15, "0,1", "0", "15", "0,5", "4000000"),
                    new Upgrade(22,R.drawable.grandma15, "0,1", "0", "15", "0,6", "20000000"),
                    new Upgrade(23,R.drawable.grandma15, "0,1", "0", "15", "0,7", "166666600"),
                    new Upgrade(24,R.drawable.grandma15, "0,1", "0", "15", "0,8", "12345678900"),
                    new Upgrade(25,R.drawable.grandma15, "0,1", "0", "15", "0,9", "399999999900"),
                    new Upgrade(26,R.drawable.grandma15, "0,1", "0", "15", "0,10", "7500000000000"),
                    new Upgrade(27,R.drawable.farm1, "0,2", "0", "1", "0,2", "5000"),
                    new Upgrade(28,R.drawable.farm1, "0,2", "0", "1", "0,2", "50000"),
                    new Upgrade(29,R.drawable.farm10, "0,2", "0", "10", "0,2", "500000"),
                    new Upgrade(30,R.drawable.farm50, "0,2", "0", "50", "0,2", "25000000"),
                    new Upgrade(31,R.drawable.farm100, "0,2", "0", "100", "0,2", "500000000"),
                    new Upgrade(32,R.drawable.farm200, "0,2", "0", "200", "0,2", "4000000000000"),
                    new Upgrade(33,R.drawable.factory1, "0,3", "0", "1", "0,3", "30000"),
                    new Upgrade(34,R.drawable.factory1, "0,3", "0", "1", "0,3", "300000"),
                    new Upgrade(35,R.drawable.factory10, "0,3", "0", "10", "0,3", "3000000"),
                    new Upgrade(36,R.drawable.factory50, "0,3", "0", "50", "0,3", "150000000"),
                    new Upgrade(37,R.drawable.factory100, "0,3", "0", "100", "0,3", "3000000000"),
                    new Upgrade(38,R.drawable.factory200, "0,3", "0", "200", "0,3", "24000000000000"),
                    new Upgrade(39,R.drawable.mine1, "0,4", "0", "1", "0,4", "100000"),
                    new Upgrade(40,R.drawable.mine1, "0,4", "0", "1", "0,4", "1000000"),
                    new Upgrade(41,R.drawable.mine10, "0,4", "0", "10", "0,4", "10000000"),
                    new Upgrade(42,R.drawable.mine50, "0,4", "0", "50", "0,4", "500000000"),
                    new Upgrade(43,R.drawable.mine100, "0,4", "0", "100", "0,4", "10000000000"),
                    new Upgrade(44,R.drawable.mine200, "0,4", "0", "200", "0,4", "80000000000000"),
                    new Upgrade(45,R.drawable.ship1, "0,5", "0", "1", "0,5", "400000"),
                    new Upgrade(46,R.drawable.ship1, "0,5", "0", "1", "0,5", "4000000"),
                    new Upgrade(47,R.drawable.ship10, "0,5", "0", "10", "0,5", "40000000"),
                    new Upgrade(48,R.drawable.ship50, "0,5", "0", "50", "0,5", "2000000000"),
                    new Upgrade(49,R.drawable.ship100, "0,5", "0", "100", "0,5", "40000000000"),
                    new Upgrade(50,R.drawable.ship200, "0,5", "0", "200", "0,5", "320000000000000"),
                    new Upgrade(51,R.drawable.lab1, "0,6", "0", "1", "0,6", "2000000"),
                    new Upgrade(52,R.drawable.lab1, "0,6", "0", "1", "0,6", "20000000"),
                    new Upgrade(53,R.drawable.lab10, "0,6", "0", "10", "0,6", "200000000"),
                    new Upgrade(54,R.drawable.lab50, "0,6", "0", "50", "0,6", "10000000000"),
                    new Upgrade(55,R.drawable.lab100, "0,6", "0", "100", "0,6", "200000000000"),
                    new Upgrade(56,R.drawable.lab200, "0,6", "0", "200", "0,6", "1600000000000000"),
                    new Upgrade(57,R.drawable.portal1, "0,7", "0", "1", "0,7", "16666660"),
                    new Upgrade(58,R.drawable.portal1, "0,7", "0", "1", "0,7", "166666600"),
                    new Upgrade(59,R.drawable.portal10, "0,7", "0", "10", "0,7", "1666666000"),
                    new Upgrade(60,R.drawable.portal50, "0,7", "0", "50", "0,7", "83333300000"),
                    new Upgrade(61,R.drawable.portal100, "0,7", "0", "100", "0,7", "1666666000000"),
                    new Upgrade(62,R.drawable.portal200, "0,7", "0", "200", "0,7", "13333328000000000"),
                    new Upgrade(63,R.drawable.time1, "0,8", "0", "1", "0,8", "1234567890"),
                    new Upgrade(64,R.drawable.time1, "0,8", "0", "1", "0,8", "9876543210"),
                    new Upgrade(65,R.drawable.time10, "0,8", "0", "10", "0,8", "98765456789"),
                    new Upgrade(66,R.drawable.time50, "0,8", "0", "50", "0,8", "1234567890000"),
                    new Upgrade(67,R.drawable.time100, "0,8", "0", "100", "0,8", "123456789000000"),
                    new Upgrade(68,R.drawable.time200, "0,8", "0", "200", "0,8", "987654321000000000"),
                    new Upgrade(69,R.drawable.antimatter1, "0,9", "0", "1", "0,9", "39999999990"),
                    new Upgrade(70,R.drawable.antimatter1, "0,9", "0", "1", "0,9", "399999999900"),
                    new Upgrade(71,R.drawable.antimatter10, "0,9", "0", "10", "0,9", "3999999999000"),
                    new Upgrade(72,R.drawable.antimatter50, "0,9", "0", "50", "0,9", "199999999950000"),
                    new Upgrade(73,R.drawable.antimatter100, "0,9", "0", "100", "0,9", "3999999999000000"),
                    new Upgrade(74,R.drawable.antimatter200, "0,9", "0", "200", "0,9", "31999999992000000000"),
                    new Upgrade(75,R.drawable.prism1, "0,10", "0", "1", "0,10", "750000000000"),
                    new Upgrade(76,R.drawable.prism1, "0,10", "0", "1", "0,10", "7500000000000"),
                    new Upgrade(77,R.drawable.prism10, "0,10", "0", "10", "0,10", "75000000000000"),
                    new Upgrade(78,R.drawable.prism50, "0,10", "0", "50", "0,10", "3750000000000000"),
                    new Upgrade(79,R.drawable.prism100, "0,10", "0", "100", "0,10", "75000000000000000"),
                    new Upgrade(80,R.drawable.prism200, "0,10", "0", "200", "0,10", "600000000000000000000"),
                    new Upgrade(81,R.drawable.click4, "0,11", "1", "1000", "1,1", "50000"),
                    new Upgrade(82,R.drawable.click4, "0,11", "1", "100000", "1,1", "5000000"),
                    new Upgrade(83,R.drawable.click8, "0,11", "1", "10000000", "1,1", "500000000"),
                    new Upgrade(84,R.drawable.click10, "0,11", "1", "1000000000", "1,1", "50000000000"),
                    new Upgrade(85,R.drawable.click12, "0,11", "1", "100000000000", "1,1", "5000000000000"),
                    new Upgrade(86,R.drawable.click14, "0,11", "1", "10000000000000", "1,1", "500000000000000"),
                    new Upgrade(87,R.drawable.click14, "0,11", "1", "1000000000000000", "1,1", "50000000000000000"),
                    new Upgrade(88,R.drawable.golden_upgrade, "1,2", "2", "7", "1,2", "777777777"),
                    new Upgrade(89,R.drawable.golden_upgrade, "1,2", "2", "27", "1,2", "77777777777"),
                    new Upgrade(90,R.drawable.golden_upgrade, "1,2", "1", "77", "1,2", "77777777777777"),
                    ///Oatmeal raisin - sugar
                    new Upgrade(91,R.drawable.oatmeal_raisin, "1,0", "0.05", "9999999", "1,0", "99999999"),
                    new Upgrade(92,R.drawable.peanut_butter, "1,0", "0.05", "9999999", "1,0", "99999999"),
                    new Upgrade(93,R.drawable.plain, "1,0", "0.05", "9999999", "1,0", "99999999"),
                    new Upgrade(94,R.drawable.sugar, "1,0", "0.05", "9999999", "1,0", "99999999"),
                    ///Coconut - Macadamia nut
                    new Upgrade(95,R.drawable.coconut, "1,0", "0.05", "99999999", "1,0", "999999999"),
                    new Upgrade(96,R.drawable.white_chocolate, "1,0", "0.05", "99999999", "1,0", "999999999"),
                    new Upgrade(97,R.drawable.macadamia_nut, "1,0", "0.05", "99999999", "1,0", "999999999"),
                    ///Double chip - All chocolate
                    new Upgrade(98,R.drawable.double_chip, "1,0", "0.1", "999999999", "1,0", "99999999999"),
                    new Upgrade(99,R.drawable.white_chocolate_macadamia_nut, "1,0", "0.1", "999999999", "1,0", "99999999999"),
                    new Upgrade(100,R.drawable.all_chocolate, "1,0", "0.1", "999999999", "1,0", "99999999999"),
                    //Dark chocolate coated - white chocolate coated
                    new Upgrade(101,R.drawable.dark_chocolate_coated, "1,0", "0.15", "9999999999", "1,0", "999999999999"),
                    new Upgrade(102,R.drawable.white_chocolate_coated, "1,0", "0.15", "9999999999", "1,0", "999999999999"),
                    //Eclipse - Zebra
                    new Upgrade(103,R.drawable.eclipse, "1,0", "0.15", "99999999999", "1,0", "9999999999999"),
                    new Upgrade(104,R.drawable.zebra, "1,0", "0.15", "99999999999", "1,0", "9999999999999"),
                    //Snickedoodles - Macaroons
                    new Upgrade(105,R.drawable.snickerdoodle, "1,0", "0.15", "99999999999", "1,0", "9999999999999"),
                    new Upgrade(106,R.drawable.stroopwafel, "1,0", "0.15", "99999999999", "1,0", "9999999999999"),
                    new Upgrade(107,R.drawable.macaroons, "1,0", "0.15", "99999999999", "1,0", "9999999999999"),
                    //Madeleines - Sablés
                    new Upgrade(108,R.drawable.madeleines, "1,0", "0.20", "9999999999999", "1,0", "199999999999999"),
                    new Upgrade(109,R.drawable.palmiers, "1,0", "0.20", "9999999999999", "1,0", "199999999999999"),
                    new Upgrade(110,R.drawable.palets, "1,0", "0.20", "9999999999999", "1,0", "199999999999999"),
                    new Upgrade(111,R.drawable.sables, "1,0", "0.20", "9999999999999", "1,0", "199999999999999"),
            };
        }
    }

    /*public static class AchievementManager{
        public static class Achievement{
            public String triggerType, trigger;
            int img, desc;
        }
    }*/

    public static boolean softReset(){

        profited.s(getTotalCookies());
        long trillones = profited.getTrillions();
        long hc = (long)(Math.sqrt(trillones*8+1)-1)/2;
        heavenlyChips = new Cookiefloat(hc+"");
        neededToRestart = Cookiefloat.m(Cookiefloat.s(heavenlyChips, new Cookiefloat(1)), new Cookiefloat("1000000000000"));

        cps = new Cookiefloat(0);
        cookies = new Cookiefloat(0);
        multi = Cookiefloat.s(new Cookiefloat(1), Cookiefloat.m(heavenlyChips, new Cookiefloat("0.02")));
        spent = new Cookiefloat(0);
        ++resets;
        totalBakingTime = new Date().getTime();

        BigCookie.each = 0;
        BigCookie.extra = new Cookiefloat(0);
        BigCookie.percent = 0;
        BigCookie.cpc = new Cookiefloat(1);
        BigCookie.clicks = 0;
        BigCookie.handmade = new Cookiefloat(0);
        BigCookie.multi = 1;

        GoldenCookie.showSecs = 13;
        GoldenCookie.effectTimes = 1;
        GoldenCookie.minutes = 10;
        GoldenCookie.framesLeft = 10*1200;
        GoldenCookie.clicks = 0;
        GoldenCookie.frenzy = 0;
        GoldenCookie.clickFrenzy = 0;

        for(int i=0; i<11; ++i){
            BuildingManager.n[i] = 0;
            BuildingManager.multis[i] = 1;
        }
        BuildingManager.cursorExtras = new Cookiefloat(0);

        BuildingManager.cps = new Cookiefloat[] {
                new Cookiefloat("0.1"), new Cookiefloat("0.5"), new Cookiefloat(4),
                new Cookiefloat(10), new Cookiefloat(40), new Cookiefloat(100),
                new Cookiefloat(400), new Cookiefloat(6666), new Cookiefloat(98765),
                new Cookiefloat(999999), new Cookiefloat(10000000)
        };


        BuildingManager.costs = new Cookiefloat[] {
                new Cookiefloat(15), new Cookiefloat(100), new Cookiefloat(500),
                new Cookiefloat(3000), new Cookiefloat(10000), new Cookiefloat(40000),
                new Cookiefloat(200000), new Cookiefloat(1666666), new Cookiefloat(123456789),
                new Cookiefloat("3999999999"), new Cookiefloat("75000000000")
        };

        UpgradeManager.bought = new ArrayList();
        UpgradeManager.unlocked = new ArrayList();
        //uLock = new ArrayList();
        UpgradeManager.cLock = new ArrayList();
        UpgradeManager.hLock = new ArrayList();
        UpgradeManager.gLock = new ArrayList();
        for(int i=0; i<UpgradeManager.bLock.length; ++i) UpgradeManager.bLock[i] = new ArrayList();

        for(int i=0; i<UpgradeManager.ups.length; ++i){
            UpgradeManager.ups[i].state = 0;
                int needs[] = {
                        Integer.parseInt(UpgradeManager.ups[i].unlockTypes.split(",")[0]),
                        Integer.parseInt(UpgradeManager.ups[i].unlockTypes.split(",")[1])
                };

                switch(needs[0]){
                    case 0:
                        UpgradeManager.bLock[needs[1]].add(i);
                        break;
                    case 1:
                        switch(needs[1]){
                            case 0:
                                UpgradeManager.cLock.add(i);
                                break;
                            case 1:
                                UpgradeManager.hLock.add(i);
                                break;
                            case 2:
                                UpgradeManager.gLock.add(i);
                                break;
                        }
                        break;
                }

            }

        return true;
    }

    //*****************************DEBUG*************************//
    public static void resetUpgrades(SharedPreferences.Editor preferences){
        /*BigCookie.cpc = new Cookiefloat(1);
        BigCookie.multi = 1;
        BigCookie.extra = new Cookiefloat(0);
        BigCookie.percent = 0;

        BuildingManager.cps = new Cookiefloat[] {
                new Cookiefloat("0.1"), new Cookiefloat("0.5"), new Cookiefloat(4),
                new Cookiefloat(10), new Cookiefloat(40), new Cookiefloat(100),
                new Cookiefloat(400), new Cookiefloat(6666), new Cookiefloat(98765),
                new Cookiefloat(999999), new Cookiefloat(10000000)
        };

        BuildingManager.multis = new int[] {1,1,1,1,1,1,1,1,1,1,1};

        BuildingManager.cursorExtras = new Cookiefloat(0);

        for(int i=0, j; i<UpgradeManager.bought.size(); ++i){
            for(j=0; j<UpgradeManager.unlocked.size(); ++j){
                if(new Cookiefloat(UpgradeManager.ups[(int)UpgradeManager.bought.get(i)].price).gt(new Cookiefloat(UpgradeManager.ups[(int)UpgradeManager.unlocked.get(j)].price))){
                    UpgradeManager.unlocked.add(j,UpgradeManager.bought.get(i));
                    break;
                }
            }
            if(j==UpgradeManager.unlocked.size()) UpgradeManager.unlocked.add(UpgradeManager.bought.get(i));
        }
        UpgradeManager.bought = new ArrayList();*/

        preferences.putString("cpc", "1");
        preferences.putString("big_extra", "0");
        preferences.putInt("big_multi", 1);
        preferences.putInt("big_percent", 0);


        for(int i=0; i<11; ++i) {
            preferences.putString("bbase" + i, BuildingManager.cps[i].get());
            preferences.putInt("bmulti" + i, 1);
        }
        preferences.putString("cursor_extra", "0");

        for(int i=0; i<88; ++i){
            preferences.putInt("up"+i, 0);
        }

        preferences.apply();

    }

    public static void recalcCps(){
        cps = new Cookiefloat(0);
        for(int i=0; i<11; ++i){
            cps.s(Cookiefloat.m(getBuildingCps(i), new Cookiefloat(BuildingManager.n[i])));
        }
    }

    public static void fix(SharedPreferences.Editor pr){
        pr.putInt("up0", 2);
        pr.putInt("up1", 2);
        pr.putInt("up0", 2);

        pr.apply();
    }
}
