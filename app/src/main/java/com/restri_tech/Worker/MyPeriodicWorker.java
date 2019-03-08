package com.restri_tech.Worker;

import android.content.Context;
import android.support.annotation.NonNull;


import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class MyPeriodicWorker extends Worker {

    public MyPeriodicWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork(){
        //List<Package> apps = HomeActivity.myAppDatabase.myDao().getApps();

        //for(Package p :apps){
          //  p.setRtime(p.getFtime());
            //HomeActivity.myAppDatabase.myDao().setTime(p);
        //}

        return Result.success();
    }
}
