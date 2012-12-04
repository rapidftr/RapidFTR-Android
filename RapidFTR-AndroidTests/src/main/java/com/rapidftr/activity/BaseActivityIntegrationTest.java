package com.rapidftr.activity;

import android.test.ActivityInstrumentationTestCase2;
import com.jayway.android.robotium.solo.Solo;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.activity.pages.ChildPage;
import com.rapidftr.activity.pages.LoginPage;
import com.rapidftr.activity.pages.SearchChildrenPage;
import com.rapidftr.activity.pages.ViewAllChildrenPage;

public abstract class BaseActivityIntegrationTest extends ActivityInstrumentationTestCase2<LoginActivity> {

    public Solo solo;
    public LoginPage loginPage;
    public ViewAllChildrenPage viewAllChildrenPage;
    public ChildPage childPage;
    public SearchChildrenPage searchPage;


    final String ALPHA_NUM = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public BaseActivityIntegrationTest() {
        super(LoginActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        solo = new Solo(getInstrumentation(), getActivity());
        loginPage = new LoginPage(solo);
        viewAllChildrenPage = new ViewAllChildrenPage(solo);
        childPage = new ChildPage(solo);
        searchPage= new SearchChildrenPage(solo);
    }

    @Override
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
        RapidFtrApplication.getApplicationInstance().setLoggedIn(false);
    }

    public boolean isTextPresent(String searchText){
        return solo.searchText(searchText,true);
    }


//    Checks the text value entered in the text box and is editable
    public boolean isEditTextPresent(String editText){
        return  solo.searchEditText(editText);
    }

//   check whether edit text present
    public boolean isEditedTextPresent(String editedText){
        boolean result=false;
        int count = solo.getCurrentEditTexts().size();
        for(int i=0;i<count;i++){
           if(solo.getCurrentEditTexts().get(i).getText().toString().equals(editedText)){
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


}
