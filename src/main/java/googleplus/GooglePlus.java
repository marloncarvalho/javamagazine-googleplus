package googleplus;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.plus.Plus;
import com.google.api.services.plus.model.Activity;
import com.google.api.services.plus.model.ActivityFeed;
import com.google.api.services.plus.model.Person;

/**
 * Exemplo de uso do Google+ em uma aplicação.
 * 
 * @author Marlon Silva Carvalho
 * @version 0.0.1
 */
public class GooglePlus {
	private final String APPLICATION_NAME = "JavaMagazine-GooglePlus/1.0.0";
	private final java.io.File DATA_STORE_DIR = new java.io.File(System.getProperty("user.home"), ".store/plus");
	private FileDataStoreFactory dataStoreFactory;
	private HttpTransport httpTransport;
	private final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	private Plus plus;

	/**
	 * Solicitar a autorização do usuário para obter suas informações privadas
	 * no Google+.
	 * 
	 * @return Credenciais que permitem o acesso a suas informações privadas.
	 * @throws Exception Caso ocorra algum erro na autorização.
	 */
	private Credential authorize() throws Exception {
		InputStream inputStream = getClass().getResourceAsStream("/client_secrets.json");
		InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, inputStreamReader);

		List<String> scope = Arrays.asList(
			    "https://www.googleapis.com/auth/plus.me",
			    "https://www.googleapis.com/auth/plus.circles.read",
			    "https://www.googleapis.com/auth/plus.stream.write");

		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.
				Builder(httpTransport, JSON_FACTORY,clientSecrets, scope).
				setDataStoreFactory(dataStoreFactory).
				build();

		return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
	}

	/**
	 * Conectar no Google+.
	 */
	public void connect() {
		try {
			httpTransport = GoogleNetHttpTransport.newTrustedTransport();
			dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);

			Credential credential = authorize();
						
			plus = new Plus.Builder(httpTransport, JSON_FACTORY, credential).
					setApplicationName(APPLICATION_NAME).
					build();			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Obter uma lista contendo as atividades de um usuário no Google+.
	 * 
	 * @param maxResults Quantidade máxima de resultados a serem retornados.
	 * @return Lista de atividades do usuário.
	 */
	public List<Activity> getActivities(long maxResults) {
		Plus.Activities.List listActivities;
		try {
			listActivities = plus.activities().list("me", "public");
			listActivities.setMaxResults(maxResults);
			ActivityFeed feed = listActivities.execute();
			return feed.getItems();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Obter uma atividade específica.
	 * 
	 * @param id Identificador da atividade.
	 * @return Atividade encontrada.
	 */
	public Activity getActivity(String id) {
		try {
			return plus.activities().get(id).execute();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Obter o Profile do usuário no Google+.
	 * 
	 * @return Profile.
	 */
	public Person getProfile() {
		try {
			return plus.people().get("me").execute();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Testando...
	 * 
	 * @param args Argumentos da linha de comando.
	 */
	public static void main(String...args) {
		GooglePlus p = new GooglePlus();
		p.connect();
		
//		for(Circle circle : p.getCircles()) {
//			System.out.println(circle.getDisplayName());
//		}
		
//		for(Activity activity : p.getActivities(5)) {
//			System.out.println(activity.getObject().getContent());
//		}
	}
	
}
