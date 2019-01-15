package com.restri_tech.Worker;

import android.support.annotation.NonNull;

import java.util.List;

import androidx.work.Worker;

public class MyPeriodicWorker extends Worker {

    @NonNull
    @Override
    public Result doWork(){
        //List<Package> apps = HomeActivity.myAppDatabase.myDao().getApps();

        //for(Package p :apps){
          //  p.setRtime(p.getFtime());
            //HomeActivity.myAppDatabase.myDao().setTime(p);
        //}

        return Result.SUCCESS;
    }
}
