package com.rapidftr.model;

import android.content.SharedPreferences;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.utils.EncryptionUtil;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(of = {"userName", "verified"})
@AllArgsConstructor(suppressConstructorProperties = true)
public class User {

	public static final ObjectMapper JSON_MAPPER = new ObjectMapper();
	public static final String UNAUTHENTICATED_DB_KEY = "UNAUTHENTICATED_DB_KEY";
	public static final String UNAUTHENTICATED_DB_NAME = "DB-" + UNAUTHENTICATED_DB_KEY.hashCode();

	@Getter @JsonProperty("user_name")
	protected String userName;

	@Getter @Setter @JsonIgnore
	protected String password;

    @Getter @Setter @JsonProperty("verified")
	protected boolean verified;

	@Getter @Setter @JsonProperty("server_url")
	protected String serverUrl;

	@Getter @Setter @JsonProperty("db_key")
	protected String dbKey;

	@Getter @Setter @JsonProperty("organisation")
	protected String organisation;

	@Getter @Setter @JsonProperty("full_name")
	protected String fullName;

	@Getter @Setter @JsonProperty("unauthenticated_password")
	protected String unauthenticatedPassword;

	@Getter @Setter @JsonProperty("language")
	protected String language;

	protected User() {
	}

	public User(String userName) {
		this(userName, null, false, null, null, null, null, null, null);
	}

	public User(String userName, String password) {
		this(userName, password, false, null, null, null, null, null, null);
	}

	public User(String userName, String password, boolean authenticated) {
		this(userName, password, authenticated, null, null, null, null, null, null);
	}

	public User(String userName, String password, boolean authenticated, String serverUrl) {
		this(userName, password, authenticated, serverUrl, null, null, null, null, null);
	}

	public String getDbName() {
		return getDbKey() == null ? null : ("DB-" + getDbKey().hashCode());
	}

	public String asJSON() throws IOException {
		return getJsonMapper().writeValueAsString(this);
	}

	public String asEncryptedJSON() throws IOException, GeneralSecurityException {
		if (this.password == null)
			throw new GeneralSecurityException("User password not available to encrypt");
		else
			return EncryptionUtil.encrypt(this.password, this.asJSON());
	}

	public User read(String json) throws IOException {
		getJsonMapper().readerForUpdating(this).readValue(json);
		return this;
	}

	public User load() throws GeneralSecurityException, IOException {
		String encryptedJson = getSharedPreferences().getString(prefKey(userName), null);
		String json = EncryptionUtil.decrypt(password, encryptedJson);
		return read(json);
	}

	public void save() throws IOException, GeneralSecurityException {
		if (!this.isVerified()) {
			this.setUnauthenticatedPassword(this.getPassword());
			this.setDbKey(getUnauthenticatedDbKey());
		}

		getSharedPreferences().edit().putString(prefKey(userName), asEncryptedJSON()).commit();
	}

	public boolean exists() {
		return getSharedPreferences().contains(prefKey(userName));
	}

	protected ObjectMapper getJsonMapper() {
		return JSON_MAPPER;
	}

	public static User readFromJSON(String json) throws IOException {
		return new User().read(json);
	}

	protected static SharedPreferences getSharedPreferences() {
		return RapidFtrApplication.getApplicationInstance().getSharedPreferences();
	}

	protected static String getUnauthenticatedDbKey() {
		String dbKey = getSharedPreferences().getString(UNAUTHENTICATED_DB_KEY, null);
		return dbKey != null ? dbKey : createUnauthenticatedDbKey();
	}

	protected static String createUnauthenticatedDbKey() {
		String dbKey = UUID.randomUUID().toString();
		getSharedPreferences().edit().putString(UNAUTHENTICATED_DB_KEY, dbKey).commit();
		return dbKey;
	}

	protected static String prefKey(String userName) {
		return "user_" + userName;
	}

}
