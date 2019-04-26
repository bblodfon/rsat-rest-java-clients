package input;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import java.util.ArrayList;
import java.util.List;

@Parameters(separators = "=")
public
class FetchSequencesCommandLineArgs {

    @Parameter
    private List<String> parameters = new ArrayList<>();

    @Parameter(names = { "--requestMethod", "-m" }, required = true,
            description = "The HTTP request method: GET or POST", order = 0)
    private String requestMethod;

    @Parameter(names = { "--responseContentType", "-c" },
            description = "The response content type: json or text", order = 1)
    private String responseContentType = "json";

    @Parameter(names = { "--genome", "-g" },
            description = "Genome version (e.g. mm9, hg19)", order = 2)
    private String genomeVersion = "mm9";

    @Parameter(names = { "--header", "-h" },
            description = "Format for sequence headers: UCSC or galaxy", order = 3)
    private String headerFormat = "galaxy";

    @Parameter(names = { "--resourceURL", "-u" },
            description = "URL file resource", order = 4)
    private String resourceURL;

    @Parameter(names = { "--resourceBedFile", "-f" },
            description = "File in .bed format", order = 5)
    private String resourceBedFile;

    public String getRequestMethod() {
        return requestMethod;
    }

    public String getResponseContentType() {
        return responseContentType;
    }

    public String getGenomeVersion() {
        return genomeVersion;
    }

    public String getHeaderFormat() {
        return headerFormat;
    }

    public String getResourceURL() {
        return resourceURL;
    }

    public String getResourceBedFile() {
        return resourceBedFile;
    }
}
