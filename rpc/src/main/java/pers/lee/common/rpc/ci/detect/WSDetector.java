package pers.lee.common.rpc.ci.detect;

import pers.lee.common.lang.client.HttpClient;
import pers.lee.common.lang.client.HttpClientBuilder;
import com.google.common.base.Throwables;

/**
 * WSDetector
 *
 * @author Drizzt Yang
 */
public class WSDetector extends DetectorBase implements Detector {
    public static final String DEFAULT_ENCODING = "UTF-8";

    private String url;
    private String request;
    private WSResponseVerifier verifier;

    public WSDetector(String detectorName, String url, String request, String successTag) {
        this(detectorName, url, request, new WSContainsVerifier(successTag));
    }

    public WSDetector(String detectorName, String url, String request, WSResponseVerifier verifier) {
        this.setDetectorName(detectorName);
        this.url = url;
        this.request = request;
        this.verifier = verifier;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public DetectResult detect() {
        try {
            HttpClient client = new HttpClientBuilder().build();
            String response = client.post(url, request);
            if (verifier.verify(response)) {
                return DetectResult.pass(this.getDetectorName(), null);
            } else {
                return DetectResult.fail(this.getDetectorName(), "response invalid by verifier: " + verifier.toString());
            }
        } catch (Exception e) {
            return DetectResult.fail(this.getDetectorName(), Throwables.getStackTraceAsString(e));
        }
    }
}

