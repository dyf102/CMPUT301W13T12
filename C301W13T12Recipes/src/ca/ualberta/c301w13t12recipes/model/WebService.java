package ca.ualberta.c301w13t12recipes.model;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
/**
 * @author YWUEI DUAN
 *	This class ia mainly responsible for the web service, 
 *	which means the upload and download the data
 */
public class WebService
{
	
	/**
	 * Used for interacting with the crowdsourcer web service.  Allows for storing/updating/listing tasks and 
	 * adding responses.
	 * 
	 * Sends information to web service through HTTP requests containing JSON content.  JSON content is 
	 * generated by serializing tasks into JSON objects.  This class also de-serializes JSON objects 
	 * from the web service into tasks.  
	 *   
	 * TODO: pictures, audio, and add a better place for uri string literal
	 */
	private HttpClient httpclient = new DefaultHttpClient();

	// JSON Utilities
	private Gson gson = new Gson();
	// need to add a resource for this
	private static String uri = StrResource.uri;

	/* public methods */
	
	/**
	 * Adds a task to the web server.  
	 * @param task to be added
	 * @return The task received from web server with id included
	 */
	public void insertRecipe(Recipe recipe) throws IllegalStateException, IOException{
		HttpPost httpPost = new HttpPost(uri+recipe.getId());
		StringEntity stringentity = null;
		try {
			stringentity = new StringEntity(gson.toJson(recipe));
		} 
		catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		httpPost.setHeader("Accept","application/json");
		httpPost.setEntity(stringentity);
		HttpResponse response = null;
		try {
			response = httpclient.execute(httpPost);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String status = response.getStatusLine().toString();
		System.out.println(status);
		HttpEntity entity = response.getEntity();
		BufferedReader br = new BufferedReader(new InputStreamReader(entity.getContent()));
		String output;
		System.err.println("Output from Server -> ");
		while ((output = br.readLine()) != null) {
			System.err.println(output);
		}
		try {
			entity.consumeContent();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//httpPost.releaseConnection();
	}

	/**
	 * Deletes a task from the web server.
	 * @param task to be deleted.
	 * @return true if deleted, false if not
	 */
	public void getRecipe(){
		try{
			HttpGet getRequest = new HttpGet("http://cmput301.softwareprocess.es:8080/testing/lab02/999?pretty=1");//S4bRPFsuSwKUDSJImbCE2g?pretty=1

			getRequest.addHeader("Accept","application/json");

			HttpResponse response = httpclient.execute(getRequest);

			String status = response.getStatusLine().toString();
			System.out.println(status);

			String json = response.getEntity().getContent().toString();

			// We have to tell GSON what type we expect
			Type elasticSearchResponseType = new TypeToken<ElasticSearchResponse<Recipe>>(){}.getType();
			// Now we expect to get a Recipe response
			ElasticSearchResponse<Recipe> esResponse = gson.fromJson(json, elasticSearchResponseType);
			// We get the recipe from it!
			Recipe recipe = esResponse.getSource();
			System.out.println(recipe.toString());
			//getRequest.releaseConnection();

		} catch (ClientProtocolException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();
		}
	}
	
}
