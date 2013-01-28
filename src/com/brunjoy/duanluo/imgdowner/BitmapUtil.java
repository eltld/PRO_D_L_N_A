package com.brunjoy.duanluo.imgdowner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Log;

public class BitmapUtil {
    final static String TAG = "BitmapUtil";

    final static String rootSavePath = Environment.getExternalStorageDirectory( ).getPath( ) + "/.DLNA/images";
    final static String rootSavePhotoPath = Environment.getExternalStorageDirectory( ).getPath( ) + "/.DLNA/photo";
    final static String rootSaveMyImagePath = Environment.getExternalStorageDirectory( ).getPath( ) + "/DLNA/userSavePhoto";
    public final static String rootSaveCachePath = Environment.getExternalStorageDirectory( ).getPath( ) + "/.DLNA/cache";
    public final static String rootSaveFilePath = Environment.getExternalStorageDirectory( ).getPath( ) + "/DLNA/Files";

    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable == null || drawable.getIntrinsicWidth( ) < 0 || drawable.getIntrinsicHeight( ) < 0)
            return null;
        try {
            Bitmap bitmap = Bitmap.createBitmap( drawable.getIntrinsicWidth( ), drawable.getIntrinsicHeight( ),
                    drawable.getOpacity( ) != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565 );

            Canvas canvas = new Canvas( bitmap );
            drawable.setBounds( 0, 0, drawable.getIntrinsicWidth( ), drawable.getIntrinsicHeight( ) );
            drawable.draw( canvas );
            return bitmap;
        } catch (OutOfMemoryError e) {
            return null;
        }

    }

    public static Bitmap formatBitmap(Bitmap bitmap, int width, int height) {

        int wh = Math.max( bitmap.getWidth( ), bitmap.getHeight( ) );
        Bitmap mBitmap = Bitmap.createBitmap( wh, wh, Bitmap.Config.ARGB_8888 );
        Canvas canvas = new Canvas( mBitmap );
        canvas.drawBitmap( bitmap, -(bitmap.getWidth( ) - wh) / 2, -(bitmap.getHeight( ) - wh) / 2, null );
        bitmap = Bitmap.createScaledBitmap( mBitmap, width, height, true );
        return bitmap;
    }

    // 放大缩小图片
    public static Bitmap zoomBitmap(Bitmap bitmap, int w, int h) {
        int width = bitmap.getWidth( );
        int height = bitmap.getHeight( );
        Matrix matrix = new Matrix( );
        float scaleWidht = ((float) w / width);
        float scaleHeight = ((float) h / height);
        matrix.postScale( scaleWidht, scaleHeight );
        Bitmap newbmp = Bitmap.createBitmap( bitmap, 0, 0, width, height, matrix, true );
        return newbmp;
    }

    public static Bitmap formatBitmap(Bitmap bitmap, int size) {

        float width = bitmap.getWidth( );
        float height = bitmap.getHeight( );
        float rota = size / height;

        int widthnew = (int) ((size * width) / height);
        // int wh = Math.max(bitmap.getWidth(), bitmap.getHeight());
        Bitmap mBitmap = Bitmap.createBitmap( widthnew, size, Bitmap.Config.ARGB_8888 );
        Matrix matrix = new Matrix( );
        matrix.postScale( rota, rota );
        Canvas canvas = new Canvas( mBitmap );
        canvas.drawBitmap( bitmap, matrix, null );

        // bitmap = Bitmap.createScaledBitmap(mBitmap, width, height, true);
        return mBitmap;
    }

    public static Bitmap formatScaleBitmap(Bitmap bitmap, int width, int height) {

        int bmpWidth = bitmap.getWidth( );

        int bmpHeight = bitmap.getHeight( );

        float scaleHeight = (float) height / bmpHeight; //

        // float scaleWidth = (float) width / bmpWidth;
        Matrix matrix = new Matrix( );

        matrix.postScale( scaleHeight, scaleHeight );

        Bitmap resizeBitmap = Bitmap.createBitmap(

        bitmap, 0, 0, bmpWidth, bmpHeight, matrix, true );

        bitmap.recycle( );

        return resizeBitmap;
    }

    public static Bitmap getBitmap(String fileName) {
        if (fileName == null)
            return null;

        File file = new File( getFileName( fileName ) );
        // MyLog.D(TAG, file.getPath());
        if (file.exists( )) {
            //
            // MyLog.D(TAG, "file exites");
            try {
                return BitmapFactory.decodeFile( file.getPath( ) );
            } catch (OutOfMemoryError e) {
                MyLog.E( TAG, "OutOfMemoryError  " + e.getMessage( ) + "   " + file.getPath( ) );
            }

        }
        // MyLog.D(TAG, "file!!!!!!!! exites");
        return null;
    }

    public static void deleteBitmap(String fileName) {
        String status = Environment.getExternalStorageState( );
        if (!status.equals( Environment.MEDIA_MOUNTED )) {// 判断是否有SD卡
            return;
        }

        File file = new File( getFileName( fileName ) );
        if (file.exists( ))
            file.delete( );
    }

    /**
     * 
     * @param mBitmap
     *            要保存的bitmap对象
     * @param fileName
     *            要保存到 /mnt/sdcard/DuanLuo/images 里面的文件名，但不包括后缀名
     * @return
     */
    public static boolean savaBitmap(Bitmap mBitmap, String fileName) {

        return savaBitmap( mBitmap, fileName, Bitmap.CompressFormat.PNG );
    }

    private static String getFormat(Bitmap.CompressFormat format) {
        if (format == Bitmap.CompressFormat.JPEG) {
            return ".jpg";
        } else {
            return ".png";
        }
    }

    public static boolean savaBitmap(Bitmap mBitmap, String fileName, Bitmap.CompressFormat format) {
        if (mBitmap == null) {
            return false;
        }
        String status = Environment.getExternalStorageState( );
        if (!status.equals( Environment.MEDIA_MOUNTED )) {// 判断是否有SD卡
            return false;
        }

        File file = new File( getFileName( fileName, format ) );
        if (file.exists( )) {

            try {
                file.delete( );
                file.createNewFile( );
            } catch (IOException e) {
                // e.printStackTrace();
                return false;
            }
        } else {
            File parentFile = file.getParentFile( );
            parentFile.mkdirs( );
            try {
                file.createNewFile( );
            } catch (IOException e) {
                // e.printStackTrace();
                return false;
            }

        }
        try {
            FileOutputStream fos = new FileOutputStream( file );
            mBitmap.compress( format, 100, fos );
            fos.flush( );
            fos.close( );
        } catch (FileNotFoundException e) {
            // e.printStackTrace();
            return false;
        } catch (IOException e) {
            // e.printStackTrace();
            return false;
        }
        // MyLog.I(TAG, "capture success!" + file.getPath());
        return true;
    }

    public static String getFileName(String fileName) {

        fileName = getSonFileName( fileName );
        fileName = rootSavePath + "/" + fileName + ".png";
        MyLog.D( TAG, fileName );
        return fileName;
    }

    public static String getFileName(String fileName, Bitmap.CompressFormat format) {

        fileName = getSonFileName( fileName );
        fileName = rootSavePath + "/" + fileName + getFormat( format );
        MyLog.D( TAG, fileName );
        return fileName;
    }

    private static String getSonFileName(String fileName) {
        // int indexStart = fileName.lastIndexOf('/');
        // indexStart = indexStart >= 0 ? indexStart + 1 : 0;
        // int indexLast = fileName.lastIndexOf('.');
        // indexLast = indexLast != -1 ? indexLast : fileName.length();
        // // if(indexLast+5<fileName.length())
        // // {
        // // indexLast= fileName.length();
        // // }
        //
        // int index2 = fileName.lastIndexOf('/', indexStart - 2);
        // if (index2 > 0) {
        // indexStart = index2;
        // }
        // MyLog.D(TAG, "fileName=" + fileName + "  indexStart=" + indexStart
        // + "  indexLast=" + indexLast + "  lg=" + fileName.length());
        //
        // if(indexLast<indexStart)
        // {
        // indexLast= fileName.length();
        // }
        // if(indexLast<indexStart)
        // {
        // return getMD5Str(fileName);
        // }
        // fileName = fileName.substring(indexStart, indexLast);
        // fileName = fileName.replace('/', '_');
        return getMD5Str( fileName );
    }

    public static String getMD5Str(String str) {
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance( "MD5" );

            messageDigest.reset( );

            messageDigest.update( str.getBytes( "UTF-8" ) );
        } catch (NoSuchAlgorithmException e) {
            System.out.println( "NoSuchAlgorithmException caught!" );
        } catch (UnsupportedEncodingException e) {
        }

        byte[] byteArray = messageDigest.digest( );

        StringBuffer md5StrBuff = new StringBuffer( );

        for (int i = 0; i < byteArray.length; i++) {
            if (Integer.toHexString( 0xFF & byteArray[i] ).length( ) == 1)
                md5StrBuff.append( "0" ).append( Integer.toHexString( 0xFF & byteArray[i] ) );
            else
                md5StrBuff.append( Integer.toHexString( 0xFF & byteArray[i] ) );
        }
        return md5StrBuff.substring( 0, 24 ).toString( ).toUpperCase( );
    }

    public static String getPhotoName(String fileName) {
        fileName = getSonFileName( fileName );
        fileName = rootSavePhotoPath + "/" + fileName + ".png";
        return fileName;
    }

    public static Bitmap transform(Matrix scaler, Bitmap source, int targetWidth, int targetHeight, boolean scaleUp) {
        int deltaX = source.getWidth( ) - targetWidth;
        int deltaY = source.getHeight( ) - targetHeight;
        if (!scaleUp && (deltaX < 0 || deltaY < 0)) {
            /*
             * In this case the bitmap is smaller, at least in one dimension, than the target. Transform it by placing as much of the image as possible into the target and leaving
             * the top/bottom or left/right (or both) black.
             */
            Bitmap b2 = Bitmap.createBitmap( targetWidth, targetHeight, Bitmap.Config.ARGB_8888 );
            Canvas c = new Canvas( b2 );

            int deltaXHalf = Math.max( 0, deltaX / 2 );
            int deltaYHalf = Math.max( 0, deltaY / 2 );
            Rect src = new Rect( deltaXHalf, deltaYHalf, deltaXHalf + Math.min( targetWidth, source.getWidth( ) ), deltaYHalf + Math.min( targetHeight, source.getHeight( ) ) );
            int dstX = (targetWidth - src.width( )) / 2;
            int dstY = (targetHeight - src.height( )) / 2;
            Rect dst = new Rect( dstX, dstY, targetWidth - dstX, targetHeight - dstY );
            c.drawBitmap( source, src, dst, null );
            return b2;
        }
        float bitmapWidthF = source.getWidth( );
        float bitmapHeightF = source.getHeight( );

        float bitmapAspect = bitmapWidthF / bitmapHeightF;
        float viewAspect = (float) targetWidth / targetHeight;

        if (bitmapAspect > viewAspect) {
            float scale = targetHeight / bitmapHeightF;
            if (scale < .9F || scale > 1F) {
                scaler.setScale( scale, scale );
            } else {
                scaler = null;
            }
        } else {
            float scale = targetWidth / bitmapWidthF;
            if (scale < .9F || scale > 1F) {
                scaler.setScale( scale, scale );
            } else {
                scaler = null;
            }
        }

        Bitmap b1;
        if (scaler != null) {
            // this is used for minithumb and crop, so we want to filter here.
            b1 = Bitmap.createBitmap( source, 0, 0, source.getWidth( ), source.getHeight( ), scaler, true );
        } else {
            b1 = source;
        }

        int dx1 = Math.max( 0, b1.getWidth( ) - targetWidth );
        int dy1 = Math.max( 0, b1.getHeight( ) - targetHeight );

        Bitmap b2 = Bitmap.createBitmap( b1, dx1 / 2, dy1 / 2, targetWidth, targetHeight );

        if (b1 != source) {
            b1.recycle( );
        }

        return b2;
    }

    public static Bitmap makeBitmap(String filePath, int maxNumOfPixels) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options( );
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile( filePath, options );
            if (options.mCancel || options.outWidth == -1 || options.outHeight == -1) {
                return null;
            }
            options.inSampleSize = computeSampleSize( options, -1, maxNumOfPixels );
            options.inJustDecodeBounds = false;
            options.inDither = false;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap b = BitmapFactory.decodeFile( filePath, options );
            b = formatBitmap( b, Math.min( options.outHeight, options.outWidth ) );
            return b;
        } catch (OutOfMemoryError ex) {
            Log.e( TAG, "Got oom exception ", ex );
            return null;
        }
    }

    public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap( bitmap.getWidth( ), bitmap.getHeight( ), Config.ARGB_8888 );
        Canvas canvas = new Canvas( output );
        final int color = 0xffffffff;
        final Paint paint = new Paint( );
        final Rect rect = new Rect( 0, 0, bitmap.getWidth( ), bitmap.getHeight( ) );
        final RectF rectF = new RectF( rect );
        final float roundPx = pixels;
        paint.setAntiAlias( true );
        canvas.drawARGB( 0, 255, 255, 255 );
        paint.setColor( color );
        canvas.drawRoundRect( rectF, roundPx, roundPx, paint );
        paint.setXfermode( new PorterDuffXfermode( PorterDuff.Mode.SRC_IN ) );
        canvas.drawBitmap( bitmap, rect, rect, paint );
        bitmap.recycle( );
        return output;
    }

    public static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize( options, minSideLength, maxNumOfPixels );

        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }

        return roundedSize;
    }

    public static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;

        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil( Math.sqrt( w * h / maxNumOfPixels ) );
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min( Math.floor( w / minSideLength ), Math.floor( h / minSideLength ) );

        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }

        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    /**
     * 压缩文件
     * 
     * @param filePath
     */
    public static String compressPhotoFile(String filePath) {
        BitmapFactory.Options opts = new BitmapFactory.Options( );
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile( filePath, opts );
        opts.inSampleSize = BitmapUtil.computeSampleSize( opts, -1, 640 * 640 );
        opts.inJustDecodeBounds = false;
        try {
            Bitmap bmp = BitmapFactory.decodeFile( filePath, opts );

            if (savaBitmap( bmp, filePath )) {
                bmp.recycle( );
                return getFileName( filePath );
            }

        } catch (OutOfMemoryError err) {

        }
        return null;
    }

    public static Bitmap compressPhotoFileToBitmap(String filePath) {
        BitmapFactory.Options opts = new BitmapFactory.Options( );
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile( filePath, opts );
        opts.inSampleSize = BitmapUtil.computeSampleSize( opts, -1, 640 * 640 );
        opts.inJustDecodeBounds = false;
        try {
            return BitmapFactory.decodeFile( filePath, opts );
        } catch (OutOfMemoryError err) {
            MyLog.D( TAG, err.getMessage( ) );
            // err.printStackTrace();
        }
        return null;
    }

    public static void clearLoacalCache() {
        MyLog.D( TAG, "clearLoacalCache" );
        new Thread( new Runnable( ) {
            @Override
            public void run() {
                long start = System.currentTimeMillis( );
                MyLog.D( TAG, "start= " + start );
                File file = new File( rootSavePath );
                if (!file.exists( )) {
                    MyLog.D( TAG, "file.exists( )除了 " + file.getPath( ) );
                    return;
                }
                // long duration = 24 * 60 * 60 * 1000;// 检查图片保存时间
                for (File f : file.listFiles( )) {
                    // if ((start - f.lastModified( )) > duration) {
                    MyLog.D( TAG, "删除了 " + f.getPath( ) );
                    f.delete( );
                    // } else {
                    // MyLog.D( TAG, "保留了 " + f.getPath( ) );
                    // }
                }
                MyLog.D( TAG, "耗时：" + (System.currentTimeMillis( ) - start) );

                start = System.currentTimeMillis( );
                MyLog.D( TAG, "start= " + start );
                file = new File( rootSaveCachePath );
                if (!file.exists( )) {
                    MyLog.D( TAG, "file.exists( )除了 " + file.getPath( ) );
                    return;
                }
                long duration = 24 * 60 * 60 * 1000;// 检查图片保存时间
                for (File f : file.listFiles( )) {
                    if ((start - f.lastModified( )) > duration) {
                        MyLog.D( TAG, "删除了 " + f.getPath( ) );
                        f.delete( );
                    } else {
                        MyLog.D( TAG, "保留了 " + f.getPath( ) );
                    }
                }
                MyLog.D( TAG, "耗时：" + (System.currentTimeMillis( ) - start) );
            }
        } ).start( );

    }

    public static boolean isExistCache(String fileName) {
        fileName = rootSaveCachePath + "/" + getMD5Str( fileName ) + ".png";
        return new File( fileName ).exists( );

    }

    public static String getCachePath(String fileName) {
        fileName = rootSaveCachePath + "/" + getMD5Str( fileName ) + ".png";
        return fileName;

    }

    public static String saveCacheName(Bitmap mBitmap, String fileName) {
        if (mBitmap == null) {
            return null;
        }
        String status = Environment.getExternalStorageState( );
        if (!status.equals( Environment.MEDIA_MOUNTED )) {// 判断是否有SD卡
            return null;
        }

        // String fileName = System.currentTimeMillis() + "";
        fileName = rootSaveCachePath + "/" + getMD5Str( fileName ) + ".png";

        File file = new File( fileName );
        if (!file.exists( )) {
            File parentFile = file.getParentFile( );
            parentFile.mkdirs( );
            try {
                file.createNewFile( );
            } catch (IOException e) {
                // e.printStackTrace();
                return null;
            }

        } else {
            return file.getPath( );
        }
        try {
            FileOutputStream fos = new FileOutputStream( file );
            mBitmap.compress( Bitmap.CompressFormat.PNG, 100, fos );
            fos.flush( );
            fos.close( );
            return file.getPath( );
        } catch (FileNotFoundException e) {
            // e.printStackTrace();
            return null;
        } catch (IOException e) {
            // e.printStackTrace();
            return null;
        }
        // MyLog.I(TAG, "capture success!" + file.getPath());
    }

    public static String saveCacheName(Bitmap mBitmap) {
        if (mBitmap == null) {
            return null;
        }
        String status = Environment.getExternalStorageState( );
        if (!status.equals( Environment.MEDIA_MOUNTED )) {// 判断是否有SD卡
            return null;
        }

        String fileName = System.currentTimeMillis( ) + "";
        fileName = rootSaveCachePath + "/" + fileName + ".png";

        File file = new File( fileName );
        if (!file.exists( )) {
            File parentFile = file.getParentFile( );
            parentFile.mkdirs( );
            try {
                file.createNewFile( );
            } catch (IOException e) {
                // e.printStackTrace();
                return null;
            }

        }
        try {
            FileOutputStream fos = new FileOutputStream( file );
            mBitmap.compress( Bitmap.CompressFormat.PNG, 100, fos );
            fos.flush( );
            fos.close( );
            return file.getPath( );
        } catch (FileNotFoundException e) {
            // e.printStackTrace();
            return null;
        } catch (IOException e) {
            // e.printStackTrace();
            return null;
        }
        // MyLog.I(TAG, "capture success!" + file.getPath());
    }

    public static String saveUserName(Bitmap mBitmap) {
        if (mBitmap == null) {
            return null;
        }
        String status = Environment.getExternalStorageState( );
        if (!status.equals( Environment.MEDIA_MOUNTED )) {// 判断是否有SD卡
            return null;
        }

        String fileName = System.currentTimeMillis( ) + "";
        fileName = rootSaveMyImagePath + "/" + fileName + ".png";

        File file = new File( fileName );
        if (!file.exists( )) {
            File parentFile = file.getParentFile( );
            parentFile.mkdirs( );
            try {
                file.createNewFile( );
            } catch (IOException e) {
                // e.printStackTrace();
                return null;
            }

        }
        try {
            FileOutputStream fos = new FileOutputStream( file );
            mBitmap.compress( Bitmap.CompressFormat.PNG, 100, fos );
            fos.flush( );
            fos.close( );
            return file.getPath( );
        } catch (FileNotFoundException e) {
            // e.printStackTrace();
            return null;
        } catch (IOException e) {
            // e.printStackTrace();
            return null;
        }
        // MyLog.I(TAG, "capture success!" + file.getPath());
    }
}
