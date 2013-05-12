package com.rapidftr.activity;

import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.EditText;
import com.jayway.android.robotium.solo.Solo;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.activity.pages.*;

import java.io.File;

public abstract class BaseActivityIntegrationTest extends ActivityInstrumentationTestCase2<LoginActivity> {

    public Solo solo;
    public LoginPage loginPage;
    public ViewAllChildrenPage viewAllChildrenPage;
    public ChildPage childPage;
    public SearchChildrenPage searchPage;
	public RapidFtrApplication application;
    public ChangePasswordPage changePasswordPage;
    public UnverifiedUserPage unverifiedUserPage;


//    RapidFtrApplication context = RapidFtrApplication.getApplicationInstance() ;
//    ChildRepository repository=context.getInjector().getInstance(ChildRepository.class);

    final String ALPHA_NUM = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public BaseActivityIntegrationTest() {
        super(LoginActivity.class);


    }

    @Override
    public void setUp() throws Exception {
//        SharedPreferences defaultPreferences = PreferenceManager.getDefaultSharedPreferences(getInstrumentation().getTargetContext());
//        defaultPreferences.edit().clear().commit();
//        deleteDir(new File(getInstrumentation().getTargetContext().getApplicationInfo().dataDir));

        solo = new Solo(getInstrumentation(), getActivity());
        loginPage = new LoginPage(solo);
        viewAllChildrenPage = new ViewAllChildrenPage(solo);
        childPage = new ChildPage(solo);
        searchPage= new SearchChildrenPage(solo);
        changePasswordPage = new ChangePasswordPage(solo);
        unverifiedUserPage=new UnverifiedUserPage(solo);
	    application = RapidFtrApplication.getApplicationInstance();

	    if (application.isLoggedIn()) {
		    loginPage.logout();
	    }

        clearApplicationData();
    }

    @Override
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
        super.tearDown();
    }

    public boolean isTextPresent(String searchText){
        return solo.searchText(searchText, true);
    }


//    Checks the text value entered in the text box and is editable
    public boolean isEditTextPresent(String editText){
        return  solo.searchEditText(editText);
    }

//   check whether edit text present
    public boolean isEditedTextPresent(String editedText){
        boolean result=false;
        int count = solo.getCurrentViews(EditText.class).size();
        for(int i=0;i<count;i++){
           if(solo.getCurrentViews(EditText.class).get(i).getText().toString().equals(editedText)){
                    result=true;
               break;
           }
        }
       return result;
    }


    public String getAlphaNumeric(int len) {
        StringBuffer sb = new StringBuffer(len);
        for (int i=0;  i<len;  i++) {
            int ndx = (int)(Math.random()*ALPHA_NUM.length());
            sb.append(ALPHA_NUM.charAt(ndx));
        }
        return sb.toString();
    }

   public void waitUntilTextDisappears(String text) throws Exception{
       assertTrue(solo.searchText(text,true));
      for(int i=0;i<10;i++){
          if(solo.searchText(text,true)){
              solo.sleep(50);
          }else{
              break;
          }

      }
   }

    public void clearApplicationData() {
        Context context = application;
        File cache = context.getCacheDir();
        File appDir = new File(cache.getParent());
        if (appDir.exists()) {
            String[] children = appDir.list();
            for (String s : children) {
                if (!s.equals("lib")) {
                    deleteDir(new File(appDir, s));
                    Log.i("TAG", "**************** File /data/data/APP_PACKAGE/" + s + " DELETED *******************");
                }
            }
        }
    }



    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
               boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();

    }

    public void waitUntilSyncCompletion() {

        for(int i=0;i<10;i++){
            solo.sendKey(KeyEvent.KEYCODE_MENU);
            if(solo.searchText("Synchronize All",false)){
                solo.sleep(10);
            }else{
            break;
        }
        }
//        solo.waitForText("Synchronize All");

    }

}
