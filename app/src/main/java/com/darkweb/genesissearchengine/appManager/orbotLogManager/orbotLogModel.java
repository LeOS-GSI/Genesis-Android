package com.darkweb.genesissearchengine.appManager.orbotLogManager;

import com.darkweb.genesissearchengine.constants.constants;
import com.darkweb.genesissearchengine.helperManager.helperMethod;
import org.torproject.android.service.wrapper.logRowModel;
import java.util.ArrayList;
import java.util.Collections;

class orbotLogModel
{
    /*Private Variables*/

    private ArrayList<logRowModel> mModelList = new ArrayList<>();

    /*Helper Methods*/

    void setList(ArrayList<logRowModel> model)
    {
        if(model.size()>0){
            mModelList.clear();
            mModelList.addAll(model);
            Collections.reverse(mModelList);
        }
        else {
            mModelList.add(0, new logRowModel(constants.LOGS_DEFAULT_MESSAGE, helperMethod.getCurrentTime()));
        }
    }

    ArrayList<logRowModel> getList()
    {
        return mModelList;
    }
}