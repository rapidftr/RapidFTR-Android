//package com.rapidftr.activity.pages;
//
//
//import android.app.Activity;
//import android.test.ActivityInstrumentationTestCase2;
//import android.util.Log;
//import com.jayway.android.robotium.solo.Solo;
//
//public class Base<T extends Activity> extends ActivityInstrumentationTestCase2<T>{
//     public Solo solo ;
//     public loginPage= new LoginActivityIntegrationTest();
//
//    public Base(T type) {
//
////        super(Class.forName(activity));
//        super((Class<T>) type.class);
//    }
//
//
//    @Override
//    public void setUp() throws Exception {
////        super.setUp();    //To change body of overridden com.rapidftr.activity.pages use File | Settings | File Templates.
////        super(getActivity());
//        Log.e("setup","hello set up");
//        solo = new Solo(getInstrumentation(),getActivity());
//    }
//
//    @Override
//    public void tearDown() throws Exception {
//        solo.finishOpenedActivities();
//    }
//}
