package com.hiddenservices.onionservices.appManager.externalCommandManager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.hiddenservices.onionservices.appManager.activityContextManager;
import com.hiddenservices.onionservices.appManager.homeManager.homeController.homeController;
import com.hiddenservices.onionservices.constants.constants;
import com.hiddenservices.onionservices.constants.status;
import com.hiddenservices.onionservices.constants.strings;
import com.hiddenservices.onionservices.dataManager.dataController;
import com.hiddenservices.onionservices.helperManager.helperMethod;
import com.example.myapplication.R;
import org.torproject.android.service.wrapper.orbotLocalConstants;
import static com.hiddenservices.onionservices.constants.constants.CONST_EXTERNAL_SHORTCUT_COMMAND_ERASE;
import static com.hiddenservices.onionservices.constants.keys.EXTERNAL_SHORTCUT_COMMAND;

public class externalShortcutController extends AppCompatActivity {

    /* Initialize */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        status.sExternalWebsite = strings.GENERIC_EMPTY_STR;
        orbotLocalConstants.mIsTorInitialized = false;
        Intent mIntent = new Intent(this, homeController.class);

        if(getIntent() != null){
            String mShortcutCommands = getIntent().getStringExtra(EXTERNAL_SHORTCUT_COMMAND);
            status.sSettingIsAppStarted = false;
            if(mShortcutCommands!=null){
                mIntent.putExtra(EXTERNAL_SHORTCUT_COMMAND, mShortcutCommands);
                switch (mShortcutCommands) {
                    case CONST_EXTERNAL_SHORTCUT_COMMAND_ERASE:
                        setContentView(R.layout.popup_data_cleared_shortcut);
                        panicExitInvoked();
                        helperMethod.onDelayHandler(this, 3000, () -> {
                            finishAndRemoveTask();
                            return null;
                        });

                        return;
                    case constants.CONST_EXTERNAL_SHORTCUT_COMMAND_ERASE_OPEN:
                        panicExitInvoked();
                        helperMethod.restartAndOpen(true);

                        break;
                    case constants.CONST_EXTERNAL_SHORTCUT_COMMAND_RESTART:
                        helperMethod.restartAndOpen(false);
                        break;
                }
            }
        }

    }

    /* UI TRIGGERS */

    public void onUITrigger(View view){
        if(view.getId() == R.id.pDataClearedDismiss){
            finishAndRemoveTask();
        }
    }

    /* Helper Methods */

    public void panicExitInvoked() {
        dataController.getInstance().clearData(this);
    }
}