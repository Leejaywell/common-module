package pers.lee.common.rpc.ci.detect;

import pers.lee.common.lang.client.HttpClient;
import pers.lee.common.lang.client.HttpClientBuilder;
import com.google.common.base.Throwables;

/**
 * HttpDetector
 *
 * @author Drizzt Yang
 */
public class HttpDetector extends DetectorBase implements Detector {

    private String url;

    public HttpDetector(String detectorName, String url) {
        this.setDetectorName(detectorName);
        this.url = url;
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
            client.get(url);
            return DetectResult.pass(this.getDetectorName(), null);
        } catch (Exception e) {
            return DetectResult.fail(this.getDetectorName(), Throwables.getStackTraceAsString(e));
        }
    }
}
