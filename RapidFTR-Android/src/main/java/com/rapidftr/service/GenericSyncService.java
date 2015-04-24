package com.rapidftr.service;

import com.rapidftr.model.BaseModel;
import com.rapidftr.model.History;
import com.rapidftr.repository.Repository;
import com.rapidftr.utils.RapidFtrDateTime;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.SyncFailedException;
import java.util.HashMap;
import java.util.Map;

public class GenericSyncService<T extends BaseModel> {

    private MediaSyncHelper mediaSyncHelper;
    private EntityHttpDao<T> entityHttpDao;
    private Repository<T> repository;
    private JSONArray photoKeys;
    private Object audioAttachments;
    private static String lastUpdateAt;

    public GenericSyncService(MediaSyncHelper mediaSyncHelper, EntityHttpDao<T> entityHttpDao, Repository<T> repository) {
        this.mediaSyncHelper = mediaSyncHelper;
        this.entityHttpDao = entityHttpDao;
        this.repository = repository;
    }

    public T sync(T model, String syncPath) throws IOException, JSONException {
        try {
            Map<String, String> requestParameters = new HashMap<String, String>();
            mediaSyncHelper.addMultiMediaFilesToTheRequestParameters(model, requestParameters);
            removeUnusedParametersBeforeSync(model);

            model = model.isNew() ? entityHttpDao.create(model, syncPath, requestParameters)
                    : entityHttpDao.update(model, syncPath, requestParameters);
            setAttributes(model);
            model.remove(History.HISTORIES);
            repository.createOrUpdateWithoutHistory(model);
            setMedia(model);
            repository.close();
        } catch (Exception e) {
            model.setSynced(false);
            model.setSyncLog(e.getMessage());
            model.put("photo_keys", photoKeys);
            model.put("audio_attachments", audioAttachments);
            repository.createOrUpdateWithoutHistory(model);
            repository.close();
            throw new SyncFailedException(e.getMessage());
        }

        return model;
    }

    private void removeUnusedParametersBeforeSync(T model) {
        photoKeys = (JSONArray) model.remove("photo_keys");
        audioAttachments = model.remove("audio_attachments");
        model.remove("synced");
        model.remove("_rev");
    }

    private void setMedia(T model) throws IOException, JSONException {
        mediaSyncHelper.setPhoto(model);
        mediaSyncHelper.setAudio(model);
    }

    public static void setAttributes(BaseModel model) throws JSONException {
        model.setSynced(true);
        model.setLastSyncedAt(RapidFtrDateTime.now().defaultFormat());
        if (model.values().has("last_updated_at")) {
            lastUpdateAt = model.values().getString("last_updated_at");
        }
        model.setLastUpdatedAt(lastUpdateAt);
        model.remove("_attachments");
    }
}

