package com.vlousada.legacylenses;


import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.view.View;

import com.github.ma1co.pmcademo.app.Logger;
import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.hardware.CameraEx.ExifInfo;
import com.sony.scalar.sysutil.ScalarInput;
import com.sony.scalar.sysutil.ScalarProperties;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;





public class LLUtils {

    private static final String SPLITTER_PERIOD = "\\Q.\\E";
    private static int sAPIversion = -1;
    private static int sHWversion = -1;
    private static String sPFVersion = null;
    protected static final int API_VERSION_SUPPORTING_EXIF = 10;

    public static final String[] SUPPORTED_MODELS = new String[]{
            "NEX-6","NEX-5T","ILCE-7","ILCE-7R","ILCE-5000","ILCE-6000","ILCE-7S","ILCE-5100",
            "ILCE-7M2","ILCE-7RM2","ILCE-7SM2","ILCE-6300","ILCE-6500"
    };


    //contants for NEX-6
    public final static String MODEL= "NEX-6"; // Device model
    public final static int KEY_FUNC_ONE = ScalarInput.ISV_KEY_SK2; //Activate PreviewMagnification
    public final static int KEY_FUNC_TWO =  ScalarInput.ISV_KEY_FN; //Set LensProfile;
    public final static int KEY_FUNC_THREE = ScalarInput.ISV_KEY_STASTOP; //Key not yet applied for some Custom action
    public final static float FEATURE_ONE = 1.5f; //Crop-factor (1.5x for APS-C and 1x for Full-frame
    public final static float FEATURE_TWO =  0f; //Touch-Screen (0 for no touch devices  &   1 for Touch devices
    public final static float FEATURE_THREE = 2f; //Number of Dials excluding PASM dial
    public final static float FEATURE_FOUR = 0f; //has wide & tele for zoom


    //MAP to Assign camera keys to specific APP funcions
    public static final Map<String, int[]> Model_FUNCTIONKEYS_Map = Collections.unmodifiableMap(
            new HashMap<String, int[]>() {{
                put(MODEL, new int[] {KEY_FUNC_ONE, KEY_FUNC_TWO, KEY_FUNC_THREE}); //Nex-6
                put("NEX-5T", new int[] {ScalarInput.ISV_KEY_SK2, ScalarInput.ISV_KEY_FN, ScalarInput.ISV_KEY_STASTOP}); //Nex-5
                put("ILCE-7", new int[] {ScalarInput.ISV_KEY_CUSTOM2, ScalarInput.ISV_KEY_FN, ScalarInput.ISV_KEY_STASTOP}); //A7
                put("ILCE-7R", new int[] {ScalarInput.ISV_KEY_CUSTOM2, ScalarInput.ISV_KEY_FN, ScalarInput.ISV_KEY_STASTOP}); //A7R
                put("ILCE-7S", new int[] {ScalarInput.ISV_KEY_CUSTOM2, ScalarInput.ISV_KEY_FN, ScalarInput.ISV_KEY_STASTOP}); //A7S
                put("ILCE-5000", new int[] {ScalarInput.ISV_KEY_FN, ScalarInput.ISV_KEY_SK2, ScalarInput.ISV_KEY_STASTOP}); //a5000
                put("ILCE-5100", new int[] {ScalarInput.ISV_KEY_FN, ScalarInput.ISV_KEY_SK2, ScalarInput.ISV_KEY_STASTOP}); //A5100
                put("ILCE-6000", new int[] {ScalarInput.ISV_KEY_CUSTOM1, ScalarInput.ISV_KEY_FN, ScalarInput.ISV_KEY_STASTOP}); //A6000
                put("ILCE-6300", new int[] {ScalarInput.ISV_KEY_AEL_AFMF, ScalarInput.ISV_KEY_FN, ScalarInput.ISV_KEY_STASTOP}); //a6300
                put("ILCE-6500", new int[] {ScalarInput.ISV_KEY_AEL, ScalarInput.ISV_KEY_FN, ScalarInput.ISV_KEY_STASTOP}); //a6500
                put("ILCE-7M2", new int[] {ScalarInput.ISV_KEY_CUSTOM3, ScalarInput.ISV_KEY_FN, ScalarInput.ISV_KEY_STASTOP}); //A7II
                put("ILCE-7RM2", new int[] {ScalarInput.ISV_KEY_CUSTOM3, ScalarInput.ISV_KEY_FN, ScalarInput.ISV_KEY_STASTOP}); //A7R2
                put("ILCE-7SM2", new int[] {ScalarInput.ISV_KEY_CUSTOM3, ScalarInput.ISV_KEY_FN, ScalarInput.ISV_KEY_STASTOP}); //A7S2
            }});

    //MAP FEATURES from camera models
    public static final Map<String, float[]> Model_FEATURES_Map = Collections.unmodifiableMap(
            new HashMap<String, float[]>() {{
                put(MODEL, new float[] {FEATURE_ONE, FEATURE_TWO, FEATURE_THREE, FEATURE_FOUR}); //Nex-6
                put("NEX-5T", new float[] {1.5f, 1f, 1f, 0f}); //Nex-5
                put("ILCE-7", new float[] {1.0f, 0f, 3f, 0f}); //A7
                put("ILCE-7R", new float[] {1.0f, 0f, 3f, 0f}); //A7R
                put("ILCE-7S", new float[] {1.0f, 0f, 3f, 0f}); //A7S
                put("ILCE-5000", new float[] {1.5f, 1f, 1f, 1f}); //A5000
                put("ILCE-5100", new float[] {1.5f, 1f, 1f, 1f}); //A5100
                put("ILCE-6000", new float[] {1.5f, 0f, 2f, 0f}); //A6000
                put("ILCE-6300", new float[] {1.5f, 0f, 2f, 0f}); //A6300
                put("ILCE-6500", new float[] {1.5f, 0f, 2f, 0f}); //A6500
                put("ILCE-7M2", new float[] {1.0f, 0f, 3f, 0f}); //A7II
                put("ILCE-7RM2", new float[] {1.0f, 0f, 3f, 0f}); //A7R2
                put("ILCE-7SM2", new float[] {1.0f, 0f, 3f, 0f}); //A7S2
            }});

    /*      ********EXAMPLE to get KEY_FUNC from Model ILCE-7 **********
      int Key_one = Model_FUNCTIONKEYS_Map.get("ILCE-7")[0];
      int Key_two = Model_FUNCTIONKEYS_Map.get("ILCE-7")[1];
      int Key_three = Model_FUNCTIONKEYS_Map.get("ILCE-7")[2];
    */






    public static boolean isOldLensAttached(CameraEx my_camera) {
        boolean bool = false;
        if (my_camera != null) {
            CameraEx.LensInfo localLensInfo = my_camera.getLensInfo();
            bool = false;
            if (localLensInfo == null) {
                bool = true;
            }
        }
        return bool;
    }

    public static boolean isSDCARDPresent() {
        return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
    }


    /**
     * Synchronize LLEGACY/PROFILES.XML into app-private storage, then open it.
     * Content comparison handles cards with stale or unreliable timestamps.
     * Priority: updated SD -> last valid private copy -> APK assets.
     * Both profile tabs call this method from their background thread.
     */
    public static synchronized InputStream openProfilesXml(Context context) throws IOException {
        File dir = context.getFilesDir();
        File cache = new File(dir, "profiles.xml");
        File backup = new File(dir, "profiles.xml.bak");
        File staging = new File(dir, "profiles.xml.tmp");
        File sd = new File(Environment.getExternalStorageDirectory(), "LLEGACY/PROFILES.XML");

        // Recover from an interrupted cache replacement.
        if (backup.exists()) {
            if (!cache.exists() || !isValidProfilesXml(cache)) {
                if (cache.exists() && !cache.delete()) {
                    Logger.error("Profiles: cannot remove invalid internal cache");
                } else if (!backup.renameTo(cache)) {
                    Logger.error("Profiles: cannot restore internal backup");
                }
            } else if (!backup.delete()) {
                Logger.error("Profiles: cannot remove old backup");
            }
        }

        if (sd.isFile()) {
            try {
                if (!cache.isFile() || !sameFileContents(sd, cache)) {
                    copyFile(sd, staging);
                    if (!isValidProfilesXml(staging)) {
                        throw new IOException("SD profiles XML is invalid");
                    }
                    // Move the existing cache aside before installing the new one.
                    if (cache.exists()) {
                        if (backup.exists() && !backup.delete()) {
                            throw new IOException("Cannot remove stale profiles backup");
                        }
                        if (!cache.renameTo(backup)) {
                            throw new IOException("Cannot back up internal profiles");
                        }
                    }
                    if (!staging.renameTo(cache)) {
                        if (backup.exists() && !backup.renameTo(cache)) {
                            Logger.error("Profiles: failed to restore backup");
                        }
                        throw new IOException("Cannot install internal profiles");
                    }
                    if (backup.exists() && !backup.delete()) {
                        Logger.error("Profiles: old backup not removed");
                    }
                    Logger.info("Profiles: internal cache updated from SD, modified="
                            + sd.lastModified() + ", size=" + sd.length());
                }
            } catch (Exception e) {
                Logger.error("Profiles: SD import failed: " + e.toString());
            } finally {
                // A failed import must not leave a partial staging file.
                if (staging.exists() && !staging.delete()) {
                    Logger.error("Profiles: cannot remove staging file");
                }
            }
        }

        if (cache.isFile() && isValidProfilesXml(cache)) {
            return new FileInputStream(cache);
        }
        Logger.info("Profiles: using default assets (no valid internal copy)");
        return context.getAssets().open("profiles.xml");
    }

    private static boolean sameFileContents(File first, File second) throws IOException {
        if (first.length() != second.length()) return false;
        InputStream a = null;
        InputStream b = null;
        try {
            a = new FileInputStream(first);
            b = new FileInputStream(second);
            int x;
            while ((x = a.read()) != -1) {
                if (x != b.read()) return false;
            }
            return b.read() == -1;
        } finally {
            if (a != null) a.close();
            if (b != null) b.close();
        }
    }

    private static void copyFile(File from, File to) throws IOException {
        FileInputStream in = null;
        FileOutputStream out = null;
        try {
            in = new FileInputStream(from);
            out = new FileOutputStream(to);
            byte[] buffer = new byte[4096];
            int count;
            while ((count = in.read(buffer)) != -1) {
                out.write(buffer, 0, count);
            }
            out.flush();
            out.getFD().sync();
        } finally {
            if (in != null) in.close();
            if (out != null) out.close();
        }
    }

    /** Check XML well-formedness and recognize at least one lens/special entry. */
    private static boolean isValidProfilesXml(File file) {
        if (!file.isFile() || file.length() == 0) return false;
        try {
            final boolean[] hasRoot = { false };
            final boolean[] hasEntry = { false };
            SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
            parser.parse(file, new DefaultHandler() {
                @Override
                public void startElement(String uri, String localName, String qName,
                                         Attributes attributes) {
                    String tag = localName.length() == 0 ? qName : localName;
                    if ("Contents".equalsIgnoreCase(tag)) hasRoot[0] = true;
                    if ("Lens".equalsIgnoreCase(tag) || "Special".equalsIgnoreCase(tag)) {
                        hasEntry[0] = true;
                    }
                }
            });
            return hasRoot[0] && hasEntry[0];
        } catch (Exception e) {
            Logger.error("Profiles: XML validation failed: " + e.toString());
            return false;
        }
    }

    //log in TEXTVIEW located at MainActivity
    public static void log(final String str)
    {
        if (MainActivity.LOGGING_ENABLED)
            MainActivity.m_tvLog.append(str);
    }


    // for print-screen
    public static void saveBitmap(Bitmap bitmap, Integer i)
    {
        File imagePath = new File(Environment.getExternalStorageDirectory() + "/screen" + Integer.toString(i) + ".png");
        LLUtils.log("direction: " + imagePath.toString());
        FileOutputStream fos;
        try
        {
            fos = new FileOutputStream(imagePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        }
        catch (FileNotFoundException e)
        {
            Logger.error(e.getMessage());
        }
        catch (IOException e)
        {
            Logger.error(e.getMessage());
        }
    }

    public static Bitmap takeScreenshot(View view)
    {
        //View rootView = findViewById(android.R.id.content).getRootView();
        view.setDrawingCacheEnabled(true);
        return view.getDrawingCache();
    }





    public static int getAPIVersion() {
        if(-1 == sAPIversion) {
            if(sPFVersion == null) {
                sPFVersion = ScalarProperties.getString("version.platform");
            }

            if(sPFVersion != null) {
                String[] var0 = sPFVersion.split("\\Q.\\E");
                if(var0 != null && var0.length >= 2) {
                    sAPIversion = Integer.parseInt(var0[1]);
                }
            }
        }

        return sAPIversion;
    }

    public static boolean isEXIFSupported() {
        boolean var0;
        if(API_VERSION_SUPPORTING_EXIF <= getAPIVersion()) {
            var0 = true;
        } else {
            var0 = false;
        }

        return var0;
    }


    //FUNCTIONS TO READ FROM SurfaceView Textboxes
    public static String getFocalFromSV(String input) {
        String tmp = input.replaceAll("[^0-9]", ""); // removes substring "mm"
        return tmp;
    }

    public static String getApertureFromSV(String input) {
        String tmp = input.replaceAll("[^0-9\\.]",""); // removes "F/"
        return tmp;
    }

    public static String getIsoFromSV(String input) {
        String tmp = input.replaceAll("[^0-9]", ""); // removes "ISO "
        return tmp;
    }


    public static String getFocalFromInfo(String input) {
        String tmp = input.replaceAll("[^0-9]", ""); // removes substring "mm"
        return tmp;
    }

}

