/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nancio.cookie;

/**
 *
 * @author Roberto Montaño
 */
public class Cookiefloat {
    private static final int DIGITSIZE = 999999999;
    private static final int DIGITLENGTH = (DIGITSIZE+"").length();
    private static final String POSTFIX[] = {"million", "billion", "trillion", "quadrillion", "quintillion", "sextillion", "septillion", "octillion", "nonillion", "decillion", "undecillion", "duodecillion", "tredecillion", "quattuordecillion", "qunindecillion"};
    
    private long[] ent;
    private float fl;
    
    public Cookiefloat(String num){
        int pi = num.indexOf(".");
        if(pi!=-1){
            String h = num.substring(0, pi);
            String k = num.substring(pi+1);
            this.parser(h);
            
            fl = Float.parseFloat("0."+k);
        }else{
            this.parser(num);
        }
    }

    public Cookiefloat(Cookiefloat a) {
        this.ent = new long[a.ent.length];
        for(int i=0; i<ent.length; ++i) ent[i] = a.ent[i];
        this.fl = a.fl;
    }
    
    public Cookiefloat(long[] a){
        this.ent = a;
        fl = 0;
    }

    public Cookiefloat(int a){
        this.ent = new long[1];
        this.ent[0] = a;
        fl=0;
    }
    
    private void parser(String h){
        int hl = h.length();
        ent = new long[hl/DIGITLENGTH+(hl%DIGITLENGTH!=0?1:0)];
        int i=0;
        for(; i<ent.length-1; ++i){
            ent[i] = Long.parseLong(h.substring(h.length()-DIGITLENGTH));
            h = h.substring(0, h.length()-DIGITLENGTH);
        }
        ent[i] = Long.parseLong(h);
    }
    
    public String get(){//1        |    0
        String r = "";//359,018,987,000,000,000
        int i=0, l=ent.length-1;//l=1
        
        try{
            
            while(ent[l]==0)--l;//false
        }catch(ArrayIndexOutOfBoundsException ex){
            return fl+"";
        }
        
        for(; i<l; ++i){ //0
            r=(ent[i]<100000?(ent[i]<1000?(ent[i]<100?(ent[i]<10?"00000000":"0000000"):"000000"):(ent[i]<10000?"00000":"0000")):(ent[i]<10000000?(ent[i]<1000000?"000":"00"):(ent[i]<100000000?"0":"")))+ent[i]+r;
        }
        return ent[i]+r+(fl+"").substring(1);
    }

    public String getFormatted(){
        String r = "";
        int i=0, l=ent.length-1;

        try{

            while(ent[l]==0)--l;
        }catch(ArrayIndexOutOfBoundsException ex){
            return fl+"";
        }

        for(; i<l; ++i){
            r=(ent[i]<100000?(ent[i]<1000?(ent[i]<100?(ent[i]<10?"00000000":"0000000"):"000000"):(ent[i]<10000?"00000":"0000")):(ent[i]<10000000?(ent[i]<1000000?"000":"00"):(ent[i]<100000000?"0":"")))+ent[i]+r;
        }
        r= ent[i]+r;
        int rl = r.length();
        if(rl>3){
            if(rl<=6){
                r = r.substring(0, rl-3)+"," +r.substring(rl-3);
                return r+(fl+"").substring(1, 3);
            }
            int res = rl%3;
            if(res==0) res=3;
            return r.substring(0, res)+"."+r.substring(res, 3+res)+" "+POSTFIX[(rl-res)/3-2];  //"e"+(rl-res);
        }

        return r+(fl+"").substring(1, 3);
    }

    public String getRounded(){
        String r;
        if(fl<0.01)
            r = new Cookiefloat(this.ent).get();
        else r = Cookiefloat.s(new Cookiefloat(this.ent), new Cookiefloat(1)).get();
        return r.substring(0, r.length()-2);
    }

    public String getFormattedRounded(){
        String r;
        if(fl<0.01)
            r = new Cookiefloat(this.ent).getFormatted();
        else r = Cookiefloat.s(new Cookiefloat(this.ent), new Cookiefloat(1)).getFormatted();
        if(r.indexOf("ion")==-1) return r.substring(0, r.length()-2);
        return r;
    }

    private void addDigit(long num) {
        if(ent==null){
            ent = new long[] {num};
            return;
        }
        long niunum[] = new long[ent.length+1];
        int i=0;
        for(; i<ent.length; ++i)
            niunum[i] = ent[i];
        niunum[i]=num;
        ent = niunum;
    }

    private void removeUselessDigits(){
        int rl = ent.length-1;
        try{
            for(; ent[rl]==0; --rl);
        }catch(ArrayIndexOutOfBoundsException e){
            ent = new long[] {0};
            return;
        }
        if(rl!=ent.length-1){
            long nent[] = new long[rl+1];
            for(int i=0; i<nent.length; ++i) nent[i] = ent[i];
            ent = nent;
        }
    }
    
    public static Cookiefloat s(Cookiefloat a, Cookiefloat b){
        Cookiefloat r = new Cookiefloat(a);
        r.fl+=b.fl;
        boolean acr=false;
        if(r.fl>=1){
            acr=true;
            r.fl-=1;
        }
        int i=0;
        for(; i<r.ent.length ;++i){
            r.ent[i]+=(i<b.ent.length?b.ent[i]: 0)+(acr?1:0);
            if(r.ent[i]>DIGITSIZE){
                r.ent[i]-=DIGITSIZE+1;
                acr=true;
            }else acr=false;
        }
        for(; i<b.ent.length; ++i){
            r.addDigit(b.ent[i]+(acr?1:0));
            if(r.ent[i]>DIGITSIZE){
                r.ent[i]-=DIGITSIZE+1;
                acr=true;
            }else acr=false;
        }
        if(acr) r.addDigit(1);
        
        return r;
    }
    
    public static Cookiefloat r(Cookiefloat a, Cookiefloat b){
        if(a.ent.length<b.ent.length)//
            System.out.println("fallo en la resta");
        Cookiefloat r = new Cookiefloat(a);
        r.fl-=b.fl;
        boolean acr=false;
        if(r.fl<0){
            r.fl+=1;
            acr=true;
        }
        int i=0;
        for(; i<r.ent.length ;++i){
            r.ent[i]-=(i<b.ent.length?b.ent[i]: 0)+(acr?1:0);
            if(r.ent[i]<0){
                r.ent[i]+=DIGITSIZE+1;
                acr=true;
            }else acr=false;
        }
        if(acr) System.out.println("fallo en la resta");
        r.removeUselessDigits();
        return r;
    }
    
    public static Cookiefloat m(Cookiefloat a, Cookiefloat b){//[418236871, 488982873, 1] * [0].000000000001
        /*
        int l = a.ent.length+b.ent.length - (a.ent[a.ent.length-1]*b.ent[b.ent.length-1]>DIGITSIZE? 0 : 1);//3+1-1
        Cookiefloat r = new Cookiefloat(new long[l]); //[3]
        r.fl = a.fl*b.fl; //~=0
        double d = a.fl*b.ent[0]; // 0
        r.ent[0] = (long)d; //HUEHUEHUEHUEHUH // [0, 0, 0];
        r.fl+=d-r.ent[0]; // [0, 0, 0] .0
        if(r.fl>1){//false
            r.ent[0]+=r.fl;
            r.fl-=(int)r.fl;
        }
        for(int j=1; j<b.ent.length; ++j){//false
            d=b.ent[j]*a.fl;
            r.ent[j]=(long)d; ///////////////////////////
            r.ent[j-1]+=(d-r.ent[j])*(DIGITSIZE+1);
            r.ent[j]+=r.ent[j-1]/(DIGITSIZE+1);
            r.ent[j-1]%=DIGITSIZE+1;
        }
        d = a.ent[0]*b.fl; // 0.000418236871
        r.ent[0]+=d; //[0, 0, 0]
        if(r.ent[0]>DIGITSIZE){ //false
            r.ent[1]+=r.ent[0]/(DIGITSIZE+1);
            r.ent[0]%=DIGITSIZE+1;
        }
        r.fl+=d-(long)d; //0.000418236871
        if(r.fl>1){ //false
            r.ent[0]+=r.fl;
            r.fl-=(int)r.fl;
        }
        for(int i=1; i<a.ent.length; ++i){ //i=2, r.ent[488982, 0, 0] .000418236871
            d=a.ent[i]*b.fl; //d = 0.000000000001
            /////////kansdkan otro for
            r.ent[i]+=(long)d; /////////////////////////// r.ent[2] = 0
            r.ent[i-1]+=(d-(long)d)*(DIGITSIZE+1); //r.ent[1]+= 0.001
            r.ent[i]+=r.ent[i-1]/(DIGITSIZE+1); //r[1] = 0;
            r.ent[i-1]%=DIGITSIZE+1; // ==
        }
        try{
            for(int i=0; i<a.ent.length; ++i){
                for(int j=0; j<b.ent.length; ++j){
                    d=a.ent[i]*b.ent[j];
                    r.ent[i+j]+=d;
                    r.ent[i+j+1]+=r.ent[i+j]/(DIGITSIZE+1);
                    r.ent[i+j]%=DIGITSIZE+1;
                }
            }
        }catch(ArrayIndexOutOfBoundsException ex){}
        if(b.ent.length==1 && b.ent[0]==0) r.removeUselessDigits();
        else if(a.ent.length==1 && a.ent[0]==0) r.removeUselessDigits();
        return r;
        */

        int l = a.ent.length+b.ent.length - (a.ent[a.ent.length-1]*b.ent[b.ent.length-1]>DIGITSIZE? 0 : 1);//3+1-1
        double ne[] = new double[l];
        ne[0] = a.fl*b.fl;
        for(int j=0; j<b.ent.length; ++j) ne[j] += a.fl*b.ent[j];
        for(int i=1; i<a.ent.length; ++i) ne[i] += a.ent[i]*b.fl;
        try{
            for(int i=0; i<a.ent.length; ++i){
                for(int j=0; j<b.ent.length; ++j) ne[i+j] += a.ent[i]*b.ent[j];
            }
        }catch(ArrayIndexOutOfBoundsException ex){}
        for(int i=Math.max(a.ent.length, b.ent.length)-1; i>0; --i){
            if(ne[i]!=Math.floor(ne[i])){
                ne[i-1] = //recorrer decimales y num>digitsize
            }
        }
        if(b.ent.length==1 && b.ent[0]==0) r.removeUselessDigits();
        else if(a.ent.length==1 && a.ent[0]==0) r.removeUselessDigits();
        return r;

    }

    public long getTrillions(){
        if(ent.length<2) return 0;
        if(ent[1]<1000) return 0;
        String num = getRounded();
        return Long.parseLong(num.substring(0, num.length()-12));

    }

    public String s(Cookiefloat b){
        Cookiefloat a = Cookiefloat.s(this, b);
        this.ent = a.ent;
        this.fl = a.fl;
        return this.get();
    }

    public String r(Cookiefloat b){
        Cookiefloat a = Cookiefloat.r(this, b);
        this.ent = a.ent;
        this.fl = a.fl;
        return this.get();
    }

    public String m(Cookiefloat b){
        Cookiefloat a = Cookiefloat.m(this, b);
        this.ent = a.ent;
        this.fl = a.fl;
        return this.get();
    }

    public boolean gt(Cookiefloat b){
        if(this.ent.length>b.ent.length) return true;
        if(this.ent.length<b.ent.length) return false;
        for(int i=this.ent.length-1; i>=0; --i){
            if(this.ent[i]>b.ent[i]) return true;
            if(this.ent[i]<b.ent[i]) return false;
        }
        if(this.fl>b.fl) return true;
        if(this.fl<b.fl) return false;
        return true;
    }
}
