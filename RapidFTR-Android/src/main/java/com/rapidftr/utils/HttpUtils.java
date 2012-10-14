package com.rapidftr.utils;


import android.content.Context;
import com.rapidftr.R;

public class HttpUtils {

    public static String getToastMessage(int statusCode, Context context){
      if(statusCode == 200 || statusCode == 201){
          return context.getString(R.string.login_successful);
      }else if(statusCode == 401){
          return context.getString(R.string.unauthorized);
      }else if(statusCode == 404){
          return context.getString(R.string.server_not_reachable);
      }
        return "";
    }

}
