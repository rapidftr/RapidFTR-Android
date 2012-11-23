package com.rapidftr.utils.http;


import com.rapidftr.R;

public class HttpUtils {

    public static int getToastMessage(int statusCode) {
      if(statusCode == 200 || statusCode == 201){
          return R.string.login_successful;
      } else if(statusCode == 401){
          return R.string.unauthorized;
      } else if(statusCode == 404){
          return R.string.server_not_reachable;
      } else {
          return R.string.internal_error;
      }
    }

}
