package com.rapidftr.repository;

import com.rapidftr.CustomTestRunner;
import com.rapidftr.database.DatabaseSession;
import com.rapidftr.database.ShadowSQLiteHelper;
import com.rapidftr.forms.FormField;
import com.rapidftr.forms.FormSection;
import com.rapidftr.forms.FormSectionTest;
import com.rapidftr.model.Child;
import com.rapidftr.model.PotentialMatch;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(CustomTestRunner.class)
public class PotentialMatchRepositoryTest {

    public DatabaseSession session;
    public PotentialMatchRepository repository;

    @Before
    public void setupSession() throws IOException {
        session = new ShadowSQLiteHelper("test_database").getSession();
        repository = new PotentialMatchRepository("user1", session);
    }

    @Test
    public void shouldReturnFalseWhenAChildWithTheGivenIdDoesNotExistsInTheDatabase() throws JSONException, SQLException {
        assertThat(repository.exists("1234"), is(false));
    }

    @Test
    public void shouldReturnTrueWhenAChildWithTheGivenIdExistsInTheDatabase() throws JSONException, SQLException {
        PotentialMatch match = new PotentialMatch("enquiry", "child", "unique_identifier");
        repository.createOrUpdate(match);
        assertThat(repository.exists("unique_identifier"), is(true));
    }

}
