package com.rapidftr.task;

import android.os.AsyncTask;
import com.google.inject.Inject;
import com.rapidftr.model.Child;
import com.rapidftr.repository.ChildRepository;
import com.rapidftr.service.ChildService;
import com.rapidftr.service.FormService;
import org.json.JSONException;

import java.io.IOException;
import java.util.List;

public class SyncAllDataAsyncTask extends AsyncTask<Void, Void, Boolean> {

    private FormService formService;
    private ChildService childService;
    private ChildRepository childRepository;

    @Inject
    public SyncAllDataAsyncTask(FormService formService, ChildService childService, ChildRepository childRepository) {
        this.formService = formService;
        this.childService = childService;
        this.childRepository = childRepository;
    }

    @Override
    protected Boolean doInBackground(Void... notRelevant) {
        try {
            List<Child> childrenToSyncWithServer = childRepository.toBeSynced();
            formService.getPublishedFormSections();
            sendChildrenToServer(childrenToSyncWithServer);
            saveIncomingChildren();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private void sendChildrenToServer(List<Child> childrenToSyncWithServer) throws IOException, JSONException {
        for (Child child : childrenToSyncWithServer) {
            childService.sync(child);
        }
    }

    private void saveIncomingChildren() throws IOException, JSONException {
        for (Child incomingChild : childService.getAllChildren()) {
            incomingChild.setSynced(true);
            if(childRepository.exists(incomingChild.getId())){
                childRepository.update(incomingChild);
            }else{
                childRepository.create(incomingChild);
            }
        }
    }
}



jmured@gmail.com ,austiine04@gmail.com ,aziizziana@gmail.com ,oquidave@gmail.com ,onyangojamesoloo@gmail.com ,starnapho@gmail.com ,nkugwa.mahad@gmail.com ,taremwaabraham@gmail.com ,alvinkatojr@gmail.com ,grkbeth@gmail.com ,victorolweny@gmail.com ,kmosesisaac@gmail.com ,salymsash@gmail.com ,lucygiggs11@gmail.com ,kiwendujoseph@gmail.com ,tamalejob995@gmail.com ,alemitdk@gmail.com ,jodongo@cit.mak.ac.ug ,richardzulu@gmail.com ,osmumos@gmail.com ,joekisirinya@gmail.com ,btushabe7@gmail.com ,azizkrmr@gmail.com ,enemilyn@gmail.com ,snantagya@grameenfoundation.org ,eowacha@gmail.com ,robsebunya@gmail.com ,tonyzake@gmail.com ,jamescellini@gmail.com ,williamluyima@gmail.com ,mbambulukoo@gmail.com ,dolel@cit.mak.ac.ug ,nkugwa.mahad@gmail.com ,dxdydzee@gmail.com ,joeseggie@gmail.com ,kbonky@gmail.com ,kebirungi.rita@gmail.com ,mbvicktor@gmail.com ,latimerscope@gmail.com ,alvinkatojr@gmail.com ,ampstine@gmail.com ,murungichristine763@gmail.com ,gracenansamba@gmail.com ,ekar45@gmail.com ,joliemirembe@yahoo.com
