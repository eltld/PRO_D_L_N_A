package com.wireme.util;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.brunjoy.duanluo.imgdowner.MyLog;

public class JsonAnalysis {
    public JsonAnalysis() {

    }

    public ArrayList<RecommendHolder> analySisDownJson(String jsonStr) {
        try {
            if (jsonStr == null)
                return null;
            JSONObject obj = new JSONObject( jsonStr );

            String op_status = obj.getString( "op_status" );
            if ("OK".equalsIgnoreCase( op_status )) {
                JSONArray arrays = obj.getJSONArray( "data" );
                if (arrays != null) {
                    ArrayList<RecommendHolder> list = new ArrayList<RecommendHolder>( );
                    for (int i = 0; i < arrays.length( ); i++) {
                        JSONObject item = arrays.getJSONObject( i );
                        String name = item.getString( "name" );
                        String version = item.getString( "version" );
                        String desc = item.getString( "desc" );
                        String downUrl = item.getString( "downUrl" );
                        String icon = item.getString( "icon" );
                        RecommendHolder holder = new RecommendHolder( name, version, desc, downUrl, icon );
                        list.add( holder );
                        MyLog.d( "jsonAnaly  json", "RecommendHolder="+holder.toString( ) );
                        
                    }
                    
                    return list;
                }
            } else {

            }
        } catch (JSONException e) {
            e.printStackTrace( );
        }
        return null;
    }
}
