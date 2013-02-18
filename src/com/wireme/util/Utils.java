package com.wireme.util;

import java.text.DecimalFormat;

public class Utils {

    /**
     * ==========================================<BR>
     * 功能：将byte转换成相应单位的数值 <BR>
     * 时间：2013-1-30 下午6:52:45 <BR>
     * ========================================== <BR>
     * 参数：
     * 
     * @param size
     * @return
     */
    public static String converSizeToString(long size) {
        String ext[] = { "B", "KB", "MB", "G", "T", "P" };
        int i = 0;
        long duration = size;
        while (duration >= 1024) {
            System.out.println( "duration=" + duration + "  i=" + i );
            duration = duration >> 10;
            i++;
            System.out.println( "duration=" + duration + "  i=" + i );
        }
        double d = Math.pow( 2, 10 * (i) );
        java.text.DecimalFormat df = new DecimalFormat( );
        df.setMaximumFractionDigits( 2 );
        df.setMinimumFractionDigits( 2 );
        return df.format( size / d ) + ext[i];
    }
}
