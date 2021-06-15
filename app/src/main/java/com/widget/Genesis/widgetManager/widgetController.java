package com.widget.Genesis.widgetManager;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;
import com.darkweb.genesissearchengine.eventObserver;
import com.example.myapplication.R;
import com.widget.Genesis.helperMethod.helperMethod;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class widgetController extends AppWidgetProvider {

    /*Model Declaration*/
    private widgetViewController mWidgetViewController;
    private widgetModelController mWidgetModelController;

    /* Local Variables */

    private int mCurrentWidth;
    private RemoteViews mViews;

    /* Navigator Initializations */

    private void initializeAppWidget(Context pContext, AppWidgetManager pAppWidgetManager, int pAppWidgetId) {
        initializeModel(pContext);
        initializeLocalEventHandler(pContext, pAppWidgetId);
        pAppWidgetManager.updateAppWidget(pAppWidgetId, mViews);
    }

    private void initializeLocalEventHandler(Context pContext, int pAppWidgetId){
        mViews.setOnClickPendingIntent(R.id.pTextInvoker, helperMethod.onCreatePendingIntent(pContext, pAppWidgetId, PendingIntent.FLAG_UPDATE_CURRENT, "mOpenApplication"));
        mViews.setOnClickPendingIntent(R.id.pVoiceInvoker, helperMethod.onCreatePendingIntent(pContext, pAppWidgetId, PendingIntent.FLAG_UPDATE_CURRENT, "mOpenVoice"));
    }

    private void initializeModel(Context pContext){
        mViews = new RemoteViews(pContext.getPackageName(), R.layout.widget_search_controller);
        mWidgetViewController = new widgetViewController(pContext, new widgetViewCallback(), mViews);
        mWidgetModelController = new widgetModelController(new widgetModelCallback());
    }

    public void onReceive(Context context, Intent intent) {
        initializeModel(context);
        mWidgetModelController.onTrigger(widgetEnums.eModelViewController.M_ON_RECIEVE, Arrays.asList(context, intent));
    }

    /* Local Overrides */

    @Override
    public void onAppWidgetOptionsChanged (Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle widgetInfo) {
        mCurrentWidth = widgetInfo.getInt (AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH);
        onUpdate(context, appWidgetManager, new int[]{appWidgetId});
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        initializeModel(context);
        if(mCurrentWidth!=0){
            int size = (int) Math.floor ((mCurrentWidth - 30) / 70);
            mWidgetViewController.onTrigger(widgetEnums.eWidgetViewController.M_INIT, Collections.singletonList(size));
            appWidgetManager.updateAppWidget(appWidgetIds, mViews);
        }else {
            initializeAppWidget(context, appWidgetManager, appWidgetIds[0]);
        }
    }

    /* Callbacks */

    public class widgetViewCallback implements eventObserver.eventListener {
        @Override
        public Object invokeObserver(List<Object> data, Object e_type) {
            return null;
        }
    }

    public class widgetModelCallback implements eventObserver.eventListener {
        @Override
        public Object invokeObserver(List<Object> data, Object e_type) {
            if(e_type.equals(widgetEnums.eWidgetControllerCallback.M_UPDATE)){
                onUpdate((Context)data.get(0), (AppWidgetManager)data.get(1), (int[])data.get(2));
            }
            else if(e_type.equals(widgetEnums.eWidgetControllerCallback.M_OPTION_CHANGE)){
                onAppWidgetOptionsChanged((Context)data.get(0), (AppWidgetManager)data.get(1), (int)data.get(2), (Bundle)data.get(3));
            }
            else if(e_type.equals(widgetEnums.eWidgetControllerCallback.M_RESTORE)){
                onRestored((Context) data.get(0), (int[]) data.get(1), (int[]) data.get(2));
            }
            else if(e_type.equals(widgetEnums.eWidgetControllerCallback.M_DELETE)){
                onDeleted((Context) data.get(0), (int[]) data.get(1));
            }
            return null;
        }
    }
}