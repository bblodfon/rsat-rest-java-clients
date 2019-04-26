package clients;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import input.FetchSequencesCommandLineArgs;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.*;
import java.net.URLEncoder;

public class FetchSequencesClient {

    private static final String SERVER_REST_URL = "http://rsat-tagc.univ-mrs.fr/rest";
    private static final String FETCH_SEQ_ENDPOINT = "/fetch-sequences/";

    public static void main(String[] args) {
        FetchSequencesClient client = new FetchSequencesClient();
        client.start(args);
    }

    private void start(String[] args) {
        try {
            FetchSequencesCommandLineArgs arguments = new FetchSequencesCommandLineArgs();
            JCommander.newBuilder().addObject(arguments).build().parse(args);

            String requestMethod = arguments.getRequestMethod();
            String genomeVersion = arguments.getGenomeVersion();
            String responseContentType = arguments.getResponseContentType();
            String headerFormat = arguments.getHeaderFormat();
            String resourceURL = arguments.getResourceURL();
            String resourceBedFile = arguments.getResourceBedFile();

            checkArguments(requestMethod, resourceURL, resourceBedFile, responseContentType);

            String responseContentTypeStr =
                    responseContentType.equals("json")
                            ? "application/json"
                            : "text/plain";

            HttpResponse response;
            HttpClient client = HttpClientBuilder.create().build();

            if (requestMethod.equals("GET")) {
                String stringURL = SERVER_REST_URL + FETCH_SEQ_ENDPOINT
                        + "?genome=" + genomeVersion
                        + "&u=" + URLEncoder.encode(resourceURL, "UTF-8")
                        + "&header_format=" + headerFormat;

                HttpGet request = new HttpGet(stringURL);
                request.addHeader(HttpHeaders.ACCEPT, responseContentTypeStr);

                response = client.execute(request);
            } else if (requestMethod.equals("POST")) {
                String stringURL = SERVER_REST_URL + FETCH_SEQ_ENDPOINT;

                HttpPost request = new HttpPost(stringURL);
                request.addHeader(HttpHeaders.ACCEPT, responseContentTypeStr);

                HttpEntity httpEntity;
                if (isNullOrEmpty(resourceBedFile)) {
                    httpEntity = MultipartEntityBuilder.create()
                            .addPart("genome", new StringBody(genomeVersion, ContentType.TEXT_PLAIN))
                            .addPart("u", new StringBody(resourceURL, ContentType.TEXT_PLAIN))
                            .addPart("header_format", new StringBody(headerFormat, ContentType.TEXT_PLAIN))
                            .build();
                } else {
                    httpEntity = MultipartEntityBuilder.create()
                            .addPart("genome", new StringBody(genomeVersion, ContentType.TEXT_PLAIN))
                            .addPart("i", new FileBody(new File(resourceBedFile)))
                            .addPart("header_format", new StringBody(headerFormat, ContentType.TEXT_PLAIN))
                            .build();
                }

                request.setEntity(httpEntity);
                response = client.execute(request);
            } else {
                throw new IllegalArgumentException("Request method should be either GET or POST");
            }

            int responseCode = response.getStatusLine().getStatusCode();
            if (responseCode != 200) {
                throw new RuntimeException("Response code was not 200. "
                        + "Detected response code was: " + responseCode
                        + "\nReason: " + response.getStatusLine().getReasonPhrase());
            }

            System.out.println(getOutput(response));

        } catch (ParameterException parEx) {
            System.out.println("\nOptions preceded by an asterisk are required.");
            parEx.getJCommander().setProgramName("clients.FetchSequencesClient");
            parEx.usage();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void checkArguments(String requestMethod, String resourceURL, String resourceBedFile, String contentType) {
        if (!(requestMethod.equals("GET") || requestMethod.equals("POST"))) {
            throw new IllegalArgumentException("Request method should be either GET or POST");
        }

        if (requestMethod.equals("GET") && isNullOrEmpty(resourceURL)) {
            throw new IllegalArgumentException("Request method is GET and no resourceURL was " +
                    "provided");
        }

        if (requestMethod.equals("POST") && isNullOrEmpty(resourceBedFile) && isNullOrEmpty(resourceURL)) {
            throw new IllegalArgumentException("Request method is POST and no resourceURL or " +
                    "resourceBedFile was provided");
        }

        if (requestMethod.equals("POST") && !isNullOrEmpty(resourceBedFile) && !isNullOrEmpty(resourceURL)) {
            throw new IllegalArgumentException("Request method is POST and both resourceURL and " +
                    "resourceBedFile were provided (choose only one)");
        }

        if (!(contentType.equals("text") || contentType.equals("json"))) {
            throw new IllegalArgumentException("Content type should be either json or text");
        }
    }

    private boolean isNullOrEmpty(String str) {
        return (str == null || str.isEmpty());
    }

    private String getOutput(HttpResponse response) throws IOException {
        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));

        StringBuilder result = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }

        return result.toString();
    }
}
