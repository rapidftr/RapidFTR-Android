package com.rapidftr;

import android.app.NotificationManager;
import android.content.Context;
import com.rapidftr.task.AsyncTaskWithDialog;
import com.rapidftr.task.SyncAllDataAsyncTask;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(CustomTestRunner.class)
public class RapidFtrApplicationTest {

    private RapidFtrApplication application;

    @Before
    public void setUp() {
        application = spy(new RapidFtrApplication(CustomTestRunner.INJECTOR));
    }

    @Test
    public void shouldCleanAsyncTask() {
        AsyncTaskWithDialog mockAsyncTaskWithDialog = mock(AsyncTaskWithDialog.class);
        SyncAllDataAsyncTask mockSyncTask = mock(SyncAllDataAsyncTask.class);
        NotificationManager mockNotification = mock(NotificationManager.class);
        application.setAsyncTaskWithDialog(mockAsyncTaskWithDialog);
        application.setSyncTask(mockSyncTask);
        doReturn(mockNotification).when(application).getSystemService(Context.NOTIFICATION_SERVICE);
        assertTrue(application.cleanSyncTask());
        verify(mockAsyncTaskWithDialog).cancel();
        verify(mockSyncTask).cancel(false);
        verify(mockNotification).cancel(SyncAllDataAsyncTask.NOTIFICATION_ID);
    }

//	@Test
//	public void testInitializeFormSections() throws JSONException, IOException {
//		List<FormSection> formSections = (List<FormSection>) mock(List.class);
//		RapidFtrApplication.getApplicationInstance().setFormSections(formSections);
//
//		activity.initializeData(null);
//		assertThat(activity.formSections, equalTo(formSections));
//	}


//    @Test
//    public void shouldReturnUserName() {
//        application.setPreference(RapidFtrApplication.Preference.USER_NAME, "testUser");
//        assertThat(application.getUserName(), equalTo("testUser"));
//    }
//
//    @Test
//    public void shouldReturnUserObject() throws JSONException {
//        application.setPreference(RapidFtrApplication.Preference.USER_NAME, "testUser");
//        application.setPreference("testUser", "{ 'abc' : 'xyz' }");
//        User user = application.buildUser();
//        assertThat(user.getString("abc"), equalTo("xyz"));
//    }

}
