package com.rapidftr.task;

import com.rapidftr.model.Child;
import com.rapidftr.repository.ChildRepository;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(suppressConstructorProperties = true)
public class SaveChildAsyncTask extends AsyncTaskWithDialog<Child, Void, Boolean> {

    protected final ChildRepository repository;

    @Override
    protected Boolean doInBackground(Child... params) {
        try {
            @Cleanup ChildRepository repository = this.repository;
            repository.createOrUpdate(params[0]);
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
