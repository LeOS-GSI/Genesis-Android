package com.darkweb.genesissearchengine.helperManager;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Vibrator;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.ActionMenuView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ShareCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.ColorUtils;

import com.darkweb.genesissearchengine.appManager.activityContextManager;
import com.darkweb.genesissearchengine.appManager.homeManager.FakeLauncherActivity;
import com.darkweb.genesissearchengine.constants.enums;
import com.darkweb.genesissearchengine.constants.keys;
import com.darkweb.genesissearchengine.constants.status;
import com.example.myapplication.R;

import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.security.Key;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.net.ssl.HttpsURLConnection;
import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION;
import static com.darkweb.genesissearchengine.constants.constants.CONST_PLAYSTORE_URL;

public class helperMethod
{
    /*Helper Methods General*/

    public static int getResId(String resName, Class<?> c) {

        try {
            Field idField = c.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static byte[] convertToBytes(Object object) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(object);
        oos.flush();
        oos.close();
        bos.close();
        byte[] data = bos.toByteArray();
        return data;
    }

    public static Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ObjectInputStream is = new ObjectInputStream(in);
        return is.readObject();
    }

    public static String caesarCipherEncrypt(String pMessage, Key pSecretKey) {
        try{
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, pSecretKey);
            byte[] cipherText = cipher.doFinal((pMessage + "__" + createRandomID()).getBytes());
            return new String(cipherText);
        }catch (Exception ex){
            return pMessage;
        }
    }

    public static void onDelayHandler(AppCompatActivity pActivity, int pTime, Callable<Void> pMethodParam) {
        final Handler handler = new Handler();
        handler.postDelayed(() ->
        {
            try {
                pMethodParam.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, pTime);
    }

    public static void onOpenHelpExternal(AppCompatActivity context, String pURL){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(pURL));
        context.startActivity(browserIntent);
    }

    public static String completeURL(String pURL){
        if(pURL.equals("about:blank")){
            return pURL;
        }
        URL weburl;
        try
        {
            weburl = new URL(pURL);
            URLConnection result = weburl.openConnection();

            if (result instanceof HttpsURLConnection) {

            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        if(!pURL.startsWith("www.")&& !pURL.startsWith("http://")&& !pURL.startsWith("https://")){
            pURL = ""+pURL;
        }
        if(!pURL.startsWith("http://")&&!pURL.startsWith("https://")){
            if(pURL.startsWith("www")){
                pURL = "http://"+pURL;
            }else {
                pURL = "http://www."+pURL;
            }
        }else {
            pURL = pURL.replace("https://","").replace("http://","");
            if(pURL.startsWith("www")){
                pURL = "http://"+pURL;
            }else {
                pURL = "http://www."+pURL;
            }
        }
        return pURL;
    }

    public static String createRandomID(){
        return UUID.randomUUID().toString();
    }

    public static int createNotificationID(){
        Date now = new Date();
        int id = Integer.parseInt(new SimpleDateFormat("ddHHmmss",  Locale.US).format(now));
        return id;
    }

    public static int getScreenHeight(AppCompatActivity context) {
        Display display = context.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);
        return size.y;
    }

    public static int getScreenWidth(AppCompatActivity context) {
        Display display = context.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);
        return size.x;
    }

    public static SpannableString urlDesigner(String url, Context pContext, int pDefColor){

        int mColor = 0;
        if(status.sTheme == enums.Theme.THEME_DARK){
            mColor = Color.argb(255, 0, 204, 71);
        }else {
            mColor = Color.argb(255, 0, 153, 54);
        }

        if (url.contains("https://"))
        {
            SpannableString ss = new SpannableString(url);
            ss.setSpan(new ForegroundColorSpan(mColor), 0, 8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.setSpan(new ForegroundColorSpan(Color.GRAY), 5, 8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            return ss;
        } else if (url.contains("http://"))
        {
            SpannableString ss = new SpannableString(url);
            ss.setSpan(new ForegroundColorSpan(mColor), 0, 7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.setSpan(new ForegroundColorSpan(Color.GRAY), 4, 7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            return ss;
        } else
        {
            SpannableString ss = new SpannableString(url);
            return ss;
        }
    }

    public static void sendIssueEmail(Context context){
        Intent selectorIntent = new Intent(Intent.ACTION_SENDTO);
        selectorIntent.setData(Uri.parse("mailto:"));

        final Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"gamesolstudios@gmail.com"});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Issue Report");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Write Message Here....");
        emailIntent.setSelector( selectorIntent );
        context.startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }

    public static void sendCustomMail(Context context, String pURL){
        String mail = pURL.replaceFirst("mailto:", "");

        Intent selectorIntent = new Intent(Intent.ACTION_SENDTO);
        selectorIntent.setData(Uri.parse("mailto:"));

        final Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{mail});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "get transport obfs4");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "get transport obfs4");
        emailIntent.setSelector( selectorIntent );
        context.startActivity(Intent.createChooser(emailIntent, "get transport obfs4"));
    }

    public static void sendBridgeEmail(Context context){
        Intent selectorIntent = new Intent(Intent.ACTION_SENDTO);
        selectorIntent.setData(Uri.parse("mailto:"));

        final Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"bridges@torproject.org"});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "get transport obfs4");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "get transport obfs4");
        emailIntent.setSelector( selectorIntent );
        context.startActivity(Intent.createChooser(emailIntent, "get transport obfs4"));
    }

    public static void onRevealView(View pView) {
        int cx = pView.getWidth();
        int cy = pView.getHeight();
        float finalRadius = (float) Math.hypot(cx, cy);
        Animator anim = ViewAnimationUtils.createCircularReveal(pView, 100, 100, 0, finalRadius);
        pView.setVisibility(View.VISIBLE);
        anim.start();
    }
    public static void onHideView(View pView) {
        int cx = pView.getWidth();
        int cy = pView.getHeight();
        float initialRadius = (float) Math.hypot(cx, cy);
        Animator anim = ViewAnimationUtils.createCircularReveal(pView, 100, 100, initialRadius, 0);
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                pView.setVisibility(View.INVISIBLE);
            }
        });
        anim.start();
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if(drawable==null){
            return null;
        }
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static String getMimeType(Context context, Uri uri) {
        String extension;

        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            final MimeTypeMap mime = MimeTypeMap.getSingleton();
            extension = mime.getExtensionFromMimeType(context.getContentResolver().getType(uri));
        } else {
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());
        }

        return extension;
    }

    public static void hideKeyboard(AppCompatActivity context) {
        View view = context.findViewById(android.R.id.content);
        if (view != null)
        {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void shareApp(AppCompatActivity context) {
        ShareCompat.IntentBuilder.from(context)
                .setType("text/plain")
                .setChooserTitle("Hi! Check out this Awesome App")
                .setSubject("Hi! Check out this Awesome App")
                .setText("Genesis | Onion Search | " + CONST_PLAYSTORE_URL)
                .startChooser();
    }

    public static int invertedShadeColor(int pColor, float pPercent) {
        int mColor = pColor;
        if(ColorUtils.calculateLuminance(pColor) <= 0.5){
            if(ColorUtils.calculateLuminance(pColor)==1){
                return Color.DKGRAY;
            }
            int a = Color.alpha(mColor);
            int r = Math.round(Color.red(mColor) / pPercent);
            int g = Math.round(Color.green(mColor) / pPercent);
            int b = Math.round(Color.blue(mColor) / pPercent);
            return Color.argb(a,Math.min(r,255), Math.min(g,255), Math.min(b,255));
        }else{
            pPercent = pPercent + 0.05f;
            int a = (pColor >> 24) & 0xFF;
            int r = (int) (((pColor >> 16) & 0xFF) * pPercent);
            int g = (int) (((pColor >> 8) & 0xFF) * pPercent);
            int b = (int) ((pColor & 0xFF) * pPercent);

            return (a << 24) | (r << 16) | (g << 8) | b;
        }
    }

    public static int invertedGrayColor(int pColor) {
        if(ColorUtils.calculateLuminance(pColor) <= 0.5){
            return Color.WHITE;
        }else{
            return Color.BLACK;
        }
    }

    public static boolean isColorDark(int pColor) {
        if(ColorUtils.calculateLuminance(pColor) > 0.5){
            return true;
        }else{
            return false;
        }
    }

    public static double getColorDensity(int pColor) {
        return ColorUtils.calculateLuminance(pColor);
    }

    public static void shareApp(AppCompatActivity context, String p_share, String p_title) {
        ShareCompat.IntentBuilder.from(context)
                .setType("text/plain")
                .setChooserTitle("Hi! Check out this Awesome URL | " + p_title)
                .setSubject("Hi! Check out this Awesome URL | " + p_title)
                .setText("Website URL | " + p_share)
                .startChooser();
    }

    public static void vibrate(AppCompatActivity context) {
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(50);
    }

    public static void shareURL(AppCompatActivity context, String p_share) {
        ShareCompat.IntentBuilder.from(context)
                .setType("text/plain")
                .setChooserTitle("Hi! Check out these Awesome URLS")
                .setSubject("Hi! Check out these Awesome URL")
                .setText("Website URL | " + p_share)
                .startChooser();
    }

    public static void openDownloadFolder(AppCompatActivity context)
    {
        Intent intent = new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS);
        if(intent.resolveActivity(context.getPackageManager()) != null)
        {
            context.startActivity(intent);
        }else {
            helperMethod.showToastMessage("Download Folder Not Found",context);
        }
    }

    static public String getHost(String link){
        URL url;
        try
        {
            url = new URL(link);
            return url.getHost();
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
            return "";
        }

    }

    public static String langaugeWithoutTranslation(String pLangauge) {
        switch (pLangauge) {
            case "en_US":
                return "English (United States)";
            case "de_DE":
                return "German (Deutsche)";
            case "ca_ES":
                return "Catalan (Català)";
            case "zh_CN":
                return "Chinese （中文-中国）";
            case "ch_CZ":
                return "Czech （čeština）";
            case "nl_NL":
                return "Dutch （Netherland）";
            case "fr_FR":
                return "French （francaise）";
            case "el_GR":
                return "Greek （Ελληνικά）";
            case "hu_HU":
                return "Hungarian （Magyar）";
            case "in_ID":
                return "Indonesian （bahasa）";
            case "it_IT":
                return "Italian （Italiana）";
            case "ja_JP":
                return "Japanese （日本人）";
            case "ko_KR":
                return "Korean （韓国語）";
            case "pt_PT":
                return "Portuguese （Português）";
            case "ro_RO":
                return "Romanian （Română）";
            case "ru_RU":
                return "Russian （русский）";
            case "th_TH":
                return "Thai （ไทย）";
            case "tr_TR":
                return "Turkish （Türk）";
            case "uk_UA":
                return "Ukrainian （Український）";
            case "vi_VN":
                return "Vietnamese （Tiếng Việt）";
        }

        return "Not Defined";
    }
    public static Boolean isValidURL(String url)
    {
        Pattern p = Pattern.compile("((http|https)://)(www.)?[a-zA-Z0-9@:%._\\+~#?&//=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%._\\+~#?&//=]*)");
        Matcher m;
        m=p.matcher(url);
        return m.matches();
    }

    public static String capitalizeString(String string) {
        char[] chars = string.toLowerCase().toCharArray();
        boolean found = false;
        for (int i = 0; i < chars.length; i++) {
            if (!found && Character.isLetter(chars[i])) {
                chars[i] = Character.toUpperCase(chars[i]);
                found = true;
            } else if (Character.isWhitespace(chars[i]) || chars[i]=='.' || chars[i]=='\'') { // You can add other chars here
                found = false;
            }
        }
        return String.valueOf(chars);
    }

    public static String removeLastSlash(String url){
        if(url.length()>2){
            if(url.charAt(url.length()-1)=='/'){
                return url.substring(0,url.length()-1);
            }
        }
        return url;
    }

    public static String urlWithoutPrefix(String url){
        try{
            url = url.substring(url.indexOf(getHost(url))).replace("www.","").replace("m.","");
            return url;
        }catch (Exception ex){
            return url;
        }
    }

    public static Point getNavigationBarSize(Context context) {
        Point appUsableSize = getAppUsableScreenSize(context);
        Point realScreenSize = getRealScreenSize(context);

        // navigation bar on the side
        if (appUsableSize.x < realScreenSize.x) {
            return new Point(realScreenSize.x - appUsableSize.x, appUsableSize.y);
        }

        // navigation bar at the bottom
        if (appUsableSize.y < realScreenSize.y) {
            return new Point(appUsableSize.x, realScreenSize.y - appUsableSize.y);
        }

        // navigation bar is not present
        return new Point();
    }

    public static Point getAppUsableScreenSize(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    public static Point getRealScreenSize(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();

        display.getRealSize(size);

        return size;
    }

    public static void openActivity( Class<?> cls,int type,AppCompatActivity context,boolean animation){
        Intent myIntent = new Intent(context, cls);
        myIntent.putExtra(keys.PROXY_LIST_TYPE, type);
        if(!animation){
            myIntent.addFlags(FLAG_ACTIVITY_NO_ANIMATION);
        }
        context.startActivity(myIntent);
    }

    public static void openActivityReverse( Class<?> cls,int type,AppCompatActivity context,boolean animation){
        Intent myIntent = new Intent(context, cls);
        myIntent.putExtra(keys.PROXY_LIST_TYPE, type);
        if(!animation){
            myIntent.addFlags(FLAG_ACTIVITY_NO_ANIMATION);
        }

        context.startActivity(myIntent);
    }

    public static void restartActivity( Intent pIntent, AppCompatActivity pContext){
        pContext.finish();
        pContext.startActivity(pIntent);
    }

    public static void onMinimizeApp(AppCompatActivity context){
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(startMain);
    }

    public static int screenWidth()
    {
        return (Resources.getSystem().getDisplayMetrics().widthPixels);
    }

    public static RotateAnimation getRotationAnimation(){
        RotateAnimation rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,0.5f);
        rotate.setDuration(2000);
        rotate.setRepeatCount(Animation.INFINITE);
        return rotate;
    }

    public static void openNotification(AppCompatActivity pContext)
    {
        Intent intent = new Intent();
        intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
        intent.putExtra("android.provider.extra.APP_PACKAGE", pContext.getPackageName());
        pContext.startActivity(intent);
    }

    public static boolean isDayMode(AppCompatActivity pContext)
    {
        if(pContext.getResources().getString(R.string.mode).equals("Day")){
            return true;
        }else {
            return false;
        }
    }

    public static String getDomainName(String url)
    {
        try{
            URI uri = new URI(url);
            String domain = uri.getHost();
            return domain.startsWith("www.") ? domain.substring(4) : domain;
        }catch (Exception ex){
            return url;
        }
    }

    public static void openPlayStore(String packageName,AppCompatActivity context)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id="+packageName));

        if(intent.resolveActivity(context.getPackageManager()) != null)
        {
            context.startActivity(intent);
        }else {
            helperMethod.showToastMessage("Playstore Not Found",context);
        }

    }

    public static int dpFromPx(final Context context, final float px) {
        return (int)(px / context.getResources().getDisplayMetrics().density);
    }

    public static int pxFromDp(float dp){
        return   (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static void setImageDrawableWithAnimation(ImageView imageView, Drawable drawable, int duration) {
        Drawable currentDrawable = imageView.getDrawable();
        if (currentDrawable == null) {
            imageView.setImageDrawable(drawable);
            return;
        }

        TransitionDrawable transitionDrawable = new TransitionDrawable(new Drawable[] {
                currentDrawable,
                drawable
        });
        imageView.setImageDrawable(transitionDrawable);
        transitionDrawable.startTransition(duration);
    }

    public static String getMimeType(String url, Context pContext) {
        String mimeType;
        Uri myUri = Uri.parse(url);
        if (ContentResolver.SCHEME_CONTENT.equals(myUri.getScheme())) {
            ContentResolver cr = pContext.getContentResolver();
            mimeType = cr.getType(myUri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(myUri.toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.toLowerCase());
        }
        return mimeType;
    }

    public static void openFile(File url, Context context) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            try {

                Uri uri = FileProvider.getUriForFile(context, "com.darkweb.genesissearchengine.provider.dev", url);
                Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(url.toString()));
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setDataAndType(uri, Uri.parse(url.toString()).getScheme());
                context.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(context, "No application found which can open the file", Toast.LENGTH_SHORT).show();
            }
        } else{
            try {
                Uri uri = FileProvider.getUriForFile(context, "com.darkweb.genesissearchengine.provider.dev", url);
                Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(url.toString()));
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setDataAndType(Uri.fromFile(url), getMimeType(uri.toString(),context));
                context.startActivity(intent);
            } catch (ActivityNotFoundException ex) {
                Toast.makeText(context, "No application found which can open the file", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public static void copyURL(String url,Context context){

        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("link", url);
        clipboard.setPrimaryClip(clip);

        showToastMessage("Copied to Clipboard",context);

    }

    public static void showToastMessage(String message,Context context){
        Toast toast=Toast.makeText(context.getApplicationContext(),message,Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM, 0, 0);
        toast.show();
    }

    public static boolean checkPermissions(AppCompatActivity context) {
        String[] permissions = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
        };

        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(context, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(context, listPermissionsNeeded.toArray(new String[0]), 100);
            return false;
        }
        return true;
    }

    public static String getCurrentDate(){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date date = new Date();
        sdf.applyPattern("E | MMM dd,yyyy");
        return sdf.format(date);
    }

    public static Drawable getDrawableXML(Context pContext, int pSrc) throws IOException, XmlPullParserException {
        Drawable mDrawable;
        Resources res = pContext.getResources();
        mDrawable = Drawable.createFromXml(res, res.getXml(pSrc));

        return mDrawable;
    }



    public static String getCurrentTime(){
        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR);
        int month = now.get(Calendar.MONTH) + 1; // Note: zero based!
        int day = now.get(Calendar.DAY_OF_MONTH);
        int hour = now.get(Calendar.HOUR_OF_DAY);
        int minute = now.get(Calendar.MINUTE);
        int second = now.get(Calendar.SECOND);
        int millis = now.get(Calendar.MILLISECOND);

        System.out.printf("%d-%02d-%02d %02d:%02d:%02d.%03d", year, month, day, hour, minute, second, millis);
        return month + "/" + year + " | " + hour + ":" + minute + ":" + second;
    }

    public static PopupWindow onCreateMenu(View p_view, int p_layout) {
        PopupWindow popupWindow;

        LayoutInflater layoutInflater
                = (LayoutInflater) p_view.getContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") final View popupView = layoutInflater.inflate(p_layout, null);


        popupWindow = new PopupWindow(
                popupView,
                ActionMenuView.LayoutParams.WRAP_CONTENT,
                ActionMenuView.LayoutParams.WRAP_CONTENT, true);

        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setAnimationStyle(R.style.popup_window_animation);

        if(status.sSettingLanguageRegion.equals("Ur")){
            popupWindow.showAtLocation(p_view, Gravity.TOP|Gravity.START,0,0);
        }else {
            popupWindow.showAtLocation(p_view, Gravity.TOP|Gravity.END,0,0);
        }

        popupWindow.setElevation(7);

        return popupWindow;
    }

}
