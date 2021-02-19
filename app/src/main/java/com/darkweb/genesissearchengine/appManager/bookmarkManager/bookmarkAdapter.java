package com.darkweb.genesissearchengine.appManager.bookmarkManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.darkweb.genesissearchengine.constants.enums;
import com.darkweb.genesissearchengine.constants.strings;
import com.darkweb.genesissearchengine.helperManager.eventObserver;
import com.darkweb.genesissearchengine.helperManager.helperMethod;
import com.example.myapplication.R;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import systems.intelligo.slight.ImageLoader;
import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class bookmarkAdapter extends RecyclerView.Adapter<bookmarkAdapter.listViewHolder>
{
    /*Private Variables*/

    private ArrayList<bookmarkRowModel> mModelList = new ArrayList<>();
    private ArrayList<bookmarkRowModel> mCurrentList;
    private ArrayList<bookmarkRowModel> mPassedList;
    private ArrayList<Integer> mRealID = new ArrayList<>();
    private ArrayList<Integer> mRealIndex = new ArrayList<>();
    private ArrayList<Date> mLongSelectedDate = new ArrayList<>();
    private ArrayList<String> mLongSelectedIndex = new ArrayList<>();
    private ArrayList<Integer> mLongSelectedID = new ArrayList<>();

    private ImageLoader imageLoader;
    private AppCompatActivity mContext;
    private bookmarkAdapterView mHistroyAdapterView;
    private Context mListHolderContext;
    private PopupWindow mPopupWindow = null;
    private eventObserver.eventListener mEvent;
    private String mFilter = strings.GENERIC_EMPTY_STR;
    private boolean mLongPressedMenuActive = false;

    /*Local Variables*/

    private boolean mDisableCallable = false;
    private boolean mSearchEnabled = false;

    bookmarkAdapter(ArrayList<bookmarkRowModel> pModelList, eventObserver.eventListener mEvent, AppCompatActivity pMainContext) {
        this.mEvent = mEvent;
        this.mCurrentList = new ArrayList<>();
        this.mPassedList = pModelList;
        this.mContext = pMainContext;
        this.mHistroyAdapterView = new bookmarkAdapterView(mContext);
        this.imageLoader = new ImageLoader(mContext);

        initializeModelWithDate(false);
    }

    public void onLoadMore(ArrayList<bookmarkRowModel> pModelList){
        notifyDataSetChanged();
        initializeModelWithDate(false);
    }


    private void initializeModelWithDate(boolean pFilterEnabled){
        int m_real_counter=0;

        mRealID.clear();
        mRealIndex.clear();
        mCurrentList.clear();
        this.mModelList.clear();
        onVerifyLongSelectedURL();

        ArrayList<bookmarkRowModel> p_model_list = mPassedList;
        int m_date_state = -1;
        int m_last_day = -1;
        for(int counter = 0; counter< p_model_list.size(); counter++){

            if(pFilterEnabled){
                if(!p_model_list.get(counter).getHeader().toLowerCase().contains(this.mFilter.toLowerCase()) && !p_model_list.get(counter).getDescription().toLowerCase().contains(this.mFilter)){
                    continue;
                }
            }

            Calendar cal = Calendar.getInstance();
            cal.setTime(p_model_list.get(counter).getDate());

            int m_date_1 = cal.get(Calendar.DAY_OF_YEAR);
            cal.setTime(Calendar.getInstance().getTime());
            int m_date_2 = cal.get(Calendar.DAY_OF_YEAR);

            float diff = m_date_2-m_date_1;

            if(diff==0){
                if(m_date_state!=1 && p_model_list.get(counter).getID()!=-2){
                    this.mModelList.add(new bookmarkRowModel("Today ",null,-1));
                    mRealID.add(m_real_counter);
                    mRealIndex.add(m_real_counter);
                    m_date_state = 1;
                }
            }else if (diff>=1){

                if(m_date_state!=2 || m_last_day!=(int)(Math.ceil(diff/7)*7)){
                    m_last_day = (int)(Math.ceil(diff/7)*7);
                    this.mModelList.add(new bookmarkRowModel("Last " + m_last_day + " Days",null,-1));
                    mRealID.add(m_real_counter);
                    mRealIndex.add(m_real_counter);
                    m_date_state = 2;
                }
            }else {
                if(m_date_state!=3){
                    this.mModelList.add(new bookmarkRowModel("Older ",null,-1));
                    mRealID.add(m_real_counter);
                    mRealIndex.add(m_real_counter);
                    m_date_state = 3;
                }
            }

            mRealID.add(p_model_list.get(counter).getID());
            mRealIndex.add(m_real_counter);
            this.mModelList.add(p_model_list.get(counter));
            m_real_counter+=1;
        }
        mCurrentList.addAll(this.mModelList);
    }

    /*Initializations*/

    private ArrayList<String> getLongSelectedleURL(){
        return mLongSelectedIndex;
    }

    public void onDeleteSelected(){
        for(int m_counter = 0; m_counter< mLongSelectedIndex.size(); m_counter++){
            for(int m_counter_inner = 0; m_counter_inner< mCurrentList.size(); m_counter_inner++){
                if(mCurrentList.get(m_counter_inner).getDate() == mLongSelectedDate.get(m_counter) && mLongSelectedIndex.get(m_counter).equals("https://"+ mCurrentList.get(m_counter_inner).getDescription())){
                    mEvent.invokeObserver(Collections.singletonList(mRealIndex.get(m_counter_inner)),enums.etype.url_clear);
                    mEvent.invokeObserver(Collections.singletonList(mLongSelectedID.get(m_counter)),enums.etype.url_clear_at);
                    invokeFilter(false);
                    mEvent.invokeObserver(Collections.singletonList(m_counter_inner),enums.etype.is_empty);

                    boolean mDateVerify = false;
                    if(mCurrentList.size()>0 && mCurrentList.get(m_counter_inner-1).getDescription()==null && (mCurrentList.size()>m_counter_inner+1 && mCurrentList.get(m_counter_inner+1).getDescription()==null || mCurrentList.size()==m_counter_inner+1)){
                        mDateVerify = true;
                    }

                    if(m_counter_inner==0){
                        notifyDataSetChanged();
                    }else {

                        if(mDateVerify){
                            notifyItemRemoved(m_counter_inner-1);
                            mCurrentList.remove(m_counter_inner-1);
                            //notifyItemRemoved(m_counter_inner-1);
                            //mCurrentList.remove(m_counter_inner-1);
                            notifyItemRangeChanged(m_counter_inner-1, mCurrentList.size());
                        }else {
                            notifyItemRemoved(m_counter_inner);
                            mCurrentList.remove(m_counter_inner);
                            notifyItemRangeChanged(m_counter_inner, mCurrentList.size());
                        }
                    }
                    break;
                }
            }
        }
        clearLongSelectedURL();
    }

    private void clearLongSelectedURL(){
        mLongSelectedDate.clear();
        mLongSelectedIndex.clear();
        mLongSelectedID.clear();
        notifyDataSetChanged();
    }

    private String getSelectedURL(){
        String m_joined_url = "\n";
        for(int m_counter = 0; m_counter< mLongSelectedIndex.size(); m_counter++){
            m_joined_url = m_joined_url.concat("\n"+ mLongSelectedIndex.get(m_counter));
        }
        return m_joined_url;
    }

    @NonNull
    @Override
    public listViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mListHolderContext = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_bookmark_row_view, parent, false);
        return new listViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull bookmarkAdapter.listViewHolder holder, int position)
    {
        holder.bindListView(mCurrentList.get(position), position);
    }

    public int getItem(){
        return mCurrentList.size();
    }

    @Override
    public int getItemCount() {
        return mCurrentList.size();
    }

    /*Listeners*/
    public void onUpdateSearchStatus(boolean p_is_searched){
        mSearchEnabled = p_is_searched;
    }

    public void onSelectView(View pItemView, String pUrl, View pMenuItem, ImageView pLogoImage, boolean pIsForced, int pId, Date pDate){
        if(!mSearchEnabled){
            try {
                mPopupWindow = (PopupWindow) mHistroyAdapterView.onTrigger(bookmarkEnums.eBookmarkViewAdapterCommands.M_SELECT_VIEW, Arrays.asList(pItemView, pMenuItem, pLogoImage, pIsForced, true));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(!pIsForced){
                if(mLongSelectedID.size()==0){
                    notifyDataSetChanged();
                }
                mLongSelectedDate.add(pDate);
                mLongSelectedIndex.add(pUrl);
                mLongSelectedID.add(pId);

            }
            onVerifyLongSelectedURL();
        }
    }

    public void onVerifyLongSelectedURL(){
        if(mLongSelectedIndex.size()>0){
            mEvent.invokeObserver(Collections.singletonList(false),enums.etype.on_verify_selected_url_menu);
        }else {
            mEvent.invokeObserver(Collections.singletonList(true),enums.etype.on_verify_selected_url_menu);
        }
    }

    public void onClearHighlight(View pItemView, String pUrl, View pMenuItem, ImageView pLogoImage, boolean pIsForced, int pId, Date pDate)
    {
        try {
            mPopupWindow = (PopupWindow) mHistroyAdapterView.onTrigger(bookmarkEnums.eBookmarkViewAdapterCommands.M_CLEAR_HIGHLIGHT, Arrays.asList(pItemView, pMenuItem, pLogoImage, pIsForced));
            mLongSelectedDate.remove(pDate);
            mLongSelectedIndex.remove(pUrl);
            mLongSelectedID.remove((Integer) pId);
            if(mLongSelectedID.size()==0){
                notifyDataSetChanged();
            }
            onVerifyLongSelectedURL();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    public void onSwipe(View pItemView, String pUrl, View pMenuItem, ImageView pLogoImage, int pId, Date pDate){

        Handler handler = new Handler();

        Runnable mLongPressed = () -> {
            if(!mDisableCallable){
                if(!mLongSelectedIndex.contains(pUrl) || !mLongSelectedID.contains(pId)) {
                    mLongPressedMenuActive = true;
                    onSelectView(pItemView, pUrl,pMenuItem, pLogoImage, false, pId, pDate);
                }else {
                    onClearHighlight(pItemView, pUrl,pMenuItem, pLogoImage, false, pId, pDate);
                    mLongPressedMenuActive = true;
                }
            }else {
                pItemView.setPressed(false);
                pItemView.clearFocus();
            }
        };

        pItemView.setOnTouchListener((v, event) -> {

            if (event.getAction() == MotionEvent.ACTION_UP) {

                if (mLongSelectedIndex.size() > 0) {
                    if (!mLongPressedMenuActive) {
                        if (mLongSelectedIndex.contains(pUrl) && mLongSelectedID.contains(pId)) {
                            handler.removeCallbacks(mLongPressed);
                            bookmarkAdapter.this.onClearHighlight(pItemView, pUrl, pMenuItem, pLogoImage, false, pId, pDate);
                        } else{
                            handler.removeCallbacks(mLongPressed);
                            bookmarkAdapter.this.onSelectView(pItemView, pUrl, pMenuItem, pLogoImage, false, pId, pDate);
                        }
                    }
                    return false;
                }

                v.setPressed(false);
                handler.removeCallbacks(mLongPressed);
                mEvent.invokeObserver(Collections.singletonList(pUrl), enums.etype.url_triggered);
                return true;

            } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                mDisableCallable = false;
                mLongPressedMenuActive = false;
                v.setPressed(true);
                handler.postDelayed(mLongPressed, ViewConfiguration.getLongPressTimeout());
                return true;
            } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                handler.removeCallbacks(mLongPressed);
                mDisableCallable = true;
                if (!mLongSelectedIndex.contains(pUrl) || !mLongSelectedID.contains(pId)) {
                    v.setPressed(false);
                }
                handler.removeCallbacks(mLongPressed);

                return true;
            }
            return false;
        });
    }

    void onOpenMenu(View pView, String pUrl, int pPosition, String pTitle){
        LayoutInflater layoutInflater = (LayoutInflater) pView.getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") final View mPopupView = layoutInflater.inflate(R.layout.history_bookmark__row_menu, null);
        mPopupWindow = (PopupWindow) mHistroyAdapterView.onTrigger(bookmarkEnums.eBookmarkViewAdapterCommands.M_OPEN_MENU, Arrays.asList(mPopupWindow, pView, mPopupView));

        setPopupWindowEvents(mPopupView.findViewById(R.id.pMenuCopy), pUrl, pPosition, pTitle);
        setPopupWindowEvents(mPopupView.findViewById(R.id.pMenuShare), pUrl, pPosition, pTitle);
        setPopupWindowEvents(mPopupView.findViewById(R.id.pMenuOpenCurrentTab), pUrl, pPosition, pTitle);
        setPopupWindowEvents(mPopupView.findViewById(R.id.pMenuOpenNewTab), pUrl, pPosition, pTitle);
        setPopupWindowEvents(mPopupView.findViewById(R.id.pMenuDelete), pUrl, pPosition, pTitle);
    }

    public void setPopupWindowEvents(View pView, String pUrl, int pPosition, String pTitle){
        pView.setOnClickListener(v -> {
            if(v.getId() == R.id.pMenuCopy){
                helperMethod.copyURL(pUrl, mListHolderContext);
                mPopupWindow.dismiss();
            }
            else if(v.getId() == R.id.pMenuShare){
                helperMethod.shareApp((AppCompatActivity)mListHolderContext, pUrl, pTitle);
                mPopupWindow.dismiss();
            }
            else if(v.getId() == R.id.pMenuOpenCurrentTab){
                mEvent.invokeObserver(Collections.singletonList(pUrl),enums.etype.url_triggered);
                mPopupWindow.dismiss();
            }
            else if(v.getId() == R.id.pMenuOpenNewTab){
                mEvent.invokeObserver(Collections.singletonList(pUrl),enums.etype.url_triggered_new_tab);
                mPopupWindow.dismiss();
            }
            else if(v.getId() == R.id.pMenuDelete){
                initializeModelWithDate(false);
                onClose(pPosition);
                mPopupWindow.dismiss();
            }
        });
    }

    private void setItemViewOnClickListener(View pItemView, View pItemMenu, String pUrl, int pPosition, String pTitle, View pMenuItem, ImageView pLogoImage, int pId, Date pDate)
    {
        pItemMenu.setOnClickListener((View v) -> onOpenMenu(v, pUrl, pPosition, pTitle));
        onSwipe(pItemView, pUrl,pMenuItem, pLogoImage, pId, pDate);
    }

    private void onClose(int pIndex){
        mEvent.invokeObserver(Collections.singletonList(mRealIndex.get(pIndex)),enums.etype.url_clear);
        mEvent.invokeObserver(Collections.singletonList(mRealID.get(pIndex)),enums.etype.url_clear_at);
        invokeFilter(false);
        mEvent.invokeObserver(Collections.singletonList(mRealID.get(pIndex)),enums.etype.is_empty);
        boolean mDateVerify = false;
        if(mPassedList.size()>0){
            if(mCurrentList.size()>0 && mCurrentList.get(pIndex-1).getDescription()==null && (mCurrentList.size()>pIndex+1 && mCurrentList.get(pIndex+1).getDescription()==null || mCurrentList.size()==pIndex+1)){
                mDateVerify = true;
            }
        }else {
            mCurrentList.clear();
            notifyDataSetChanged();
            return;
        }
        int size = mCurrentList.size();

        if(mDateVerify){
            notifyItemRemoved(pIndex-1);
            mCurrentList.remove(pIndex-1);
            notifyItemRemoved(pIndex-1);
            mCurrentList.remove(pIndex-1);
            notifyItemRangeChanged(pIndex-1, mCurrentList.size());
        }else {
            mCurrentList.remove(pIndex);

            notifyItemRemoved(pIndex);
            notifyItemRangeChanged(pIndex, mCurrentList.size());
            notifyItemChanged(mCurrentList.size()-1);
        }
    }

    /*View Holder Extensions*/
    class listViewHolder extends RecyclerView.ViewHolder
    {
        TextView mHeader;
        TextView mDescription;
        TextView mDate;
        TextView mWebLogo;
        ImageButton mRowMenu;
        ImageView mLogoImage;
        ImageView mFaviconLogo;
        LinearLayout mRowContainer;
        LinearLayout mDateContainer;
        LinearLayout mLoadingContainer;
        ImageView mHindTypeIconTemp;

        listViewHolder(View itemView) {
            super(itemView);
        }

        void bindListView(bookmarkRowModel model, int p_position) {
            mDateContainer = itemView.findViewById(R.id.pDateContainer);
            mHeader = itemView.findViewById(R.id.pHeader);
            mDescription = itemView.findViewById(R.id.pDescription);
            mRowContainer = itemView.findViewById(R.id.pRowContainer);
            mRowMenu = itemView.findViewById(R.id.pRowMenu);
            mDate = itemView.findViewById(R.id.pDate);
            mLogoImage = itemView.findViewById(R.id.pLogoImage);
            mWebLogo = itemView.findViewById(R.id.pWebLogo);
            mLoadingContainer = itemView.findViewById(R.id.pLoadingContainer);
            mFaviconLogo = itemView.findViewById(R.id.pFaviconLogo);
            mHindTypeIconTemp = new ImageView(mContext);

            if(model.getID() == -1){
                mDate.setText(model.getHeader());
                mDateContainer.setVisibility(View.VISIBLE);
                mRowContainer.setVisibility(View.GONE);
                mRowMenu.setVisibility(View.INVISIBLE);
                mRowMenu.setClickable(false);
                mWebLogo.setVisibility(View.GONE);
                mLoadingContainer.setVisibility(View.GONE);
            }
            else if(model.getID() == -2){
                mDate.setText(model.getHeader());
                mDateContainer.setVisibility(View.GONE);
                mRowContainer.setVisibility(View.GONE);
                mRowMenu.setVisibility(View.INVISIBLE);
                mRowMenu.setClickable(false);
                mWebLogo.setVisibility(View.GONE);
                mLoadingContainer.setVisibility(View.VISIBLE);
                return;
            }
            else {
                mDateContainer.setVisibility(View.GONE);
                mLoadingContainer.setVisibility(View.GONE);
                mRowContainer.setVisibility(View.VISIBLE);
                mRowMenu.setVisibility(View.VISIBLE);
                mRowMenu.setClickable(true);
                mWebLogo.setVisibility(View.VISIBLE);
                mHeader.setText(model.getHeader());
                mWebLogo.setText((helperMethod.getDomainName(model.getHeader()).toUpperCase().charAt(0)+""));
                String header = model.getHeader();
                mDescription.setText(("https://"+model.getDescription()));

                new Thread(){
                    public void run(){
                        try {
                            mHindTypeIconTemp.setImageDrawable(null);
                            mEvent.invokeObserver(Arrays.asList(mHindTypeIconTemp, "https://" + helperMethod.getDomainName(model.getDescription())), enums.etype.fetch_favicon);
                            while (true){
                                int mCounter=0;
                                if(mHindTypeIconTemp.isAttachedToWindow() || mHindTypeIconTemp.getDrawable()==null){
                                    sleep(50);
                                    mCounter+=1;
                                }else {
                                    break;
                                }
                                if(mCounter>6){
                                    break;
                                }
                            }
                            mContext.runOnUiThread(() -> mFaviconLogo.setImageDrawable(mHindTypeIconTemp.getDrawable()));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();

                setItemViewOnClickListener(mRowContainer, mRowMenu, mDescription.getText().toString(), p_position, header, mRowMenu, mLogoImage, model.getID(), model.getDate());
            }

            if(mLongSelectedID.size()>0){
                mRowMenu.setVisibility(View.INVISIBLE);
                mRowMenu.setClickable(false);
            }else {
                mRowMenu.setVisibility(View.VISIBLE);
                mRowMenu.setClickable(true);
            }

            if(mLongSelectedIndex.contains("https://" + model.getDescription()) && mLongSelectedID.contains(model.getID())){
                mPopupWindow = (PopupWindow) mHistroyAdapterView.onTrigger(bookmarkEnums.eBookmarkViewAdapterCommands.M_SELECT_VIEW, Arrays.asList(mRowContainer, mRowMenu, mLogoImage, true, false));
            }else if(mLogoImage.getAlpha()>0){
                mPopupWindow = (PopupWindow) mHistroyAdapterView.onTrigger(bookmarkEnums.eBookmarkViewAdapterCommands.M_CLEAR_HIGHLIGHT, Arrays.asList(mRowContainer, mRowMenu, mLogoImage, true, false));
            }
        }
    }

    void setFilter(String pFilter){
        this.mFilter = pFilter.toLowerCase();
    }

    void invokeFilter(boolean notify){
        if(notify){
            if(mFilter.length()>0){
                initializeModelWithDate(true);
            }else {
                initializeModelWithDate(false);
            }
            notifyDataSetChanged();
        }
    }

    private boolean isLongPressMenuActive(){
        return mLongSelectedIndex.size()>0;
    }

    public Object onTrigger(bookmarkEnums.eBookmarkAdapterCommands pCommands, List<Object> pData){
        if(pCommands == bookmarkEnums.eBookmarkAdapterCommands.GET_SELECTED_URL){
            return getSelectedURL();
        }
        else if(pCommands == bookmarkEnums.eBookmarkAdapterCommands.M_CLEAR_LONG_SELECTED_URL){
            clearLongSelectedURL();
        }
        else if(pCommands == bookmarkEnums.eBookmarkAdapterCommands.GET_LONG_SELECTED_URL){
            return getLongSelectedleURL();
        }
        else if(pCommands == bookmarkEnums.eBookmarkAdapterCommands.GET_LONG_SELECTED_STATUS){
            return isLongPressMenuActive();
        }
        else if(pCommands == bookmarkEnums.eBookmarkAdapterCommands.ON_CLOSE){
            onClose((int)pData.get(0));
        }
        return null;
    }

}
