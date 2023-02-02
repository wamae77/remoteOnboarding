package com.cll.FingerPrintModule.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

public class MyExceptionHandler implements
        Thread.UncaughtExceptionHandler {
    private final Context myContext;
    // private final Class<?> myActivityClass;

    public MyExceptionHandler(Context context) {
        myContext = context;

    }

    public void uncaughtException(Thread thread, Throwable exception) {
        StringWriter stackTrace = new StringWriter();
        exception.printStackTrace(new PrintWriter(stackTrace));


        sendUserCrashedLogs(stackTrace);
        System.exit(0);
    }


    private void sendUserCrashedLogs(StringWriter stackTrace) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        //add email your email id here send crash logs
        String[] emailAdrs = new String[]{""};

        emailIntent.putExtra(Intent.EXTRA_EMAIL, emailAdrs);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Crash Logs");
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_TEXT, stackTrace.toString());
        final PackageManager pm = myContext.getPackageManager();
        final List<ResolveInfo> matches = pm.queryIntentActivities(emailIntent, 0);
        ResolveInfo best = null;
        for (final ResolveInfo info : matches)
            if (info.activityInfo.packageName.endsWith(".gm") || info.activityInfo.name.toLowerCase().contains("gmail"))
                best = info;
        if (best != null)
            emailIntent.setClassName(best.activityInfo.packageName, best.activityInfo.name);
        myContext.startActivity(emailIntent);
    }

}
