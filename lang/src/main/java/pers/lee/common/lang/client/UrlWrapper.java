package pers.lee.common.lang.client;

import java.util.Objects;

/**
 * Created by Passyt on 2018/5/17.
 */
public class UrlWrapper {

    private final String url;
    private final String alias;

    public UrlWrapper(String url, String alias) {
        this.url = url;
        this.alias = alias;
    }

    public String getUrl() {
        return url;
    }

    public String getAlias() {
        return alias;
    }

    public static UrlWrapper defaultUrlWrapper(String url) {
        return new UrlWrapper(url, "default");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UrlWrapper that = (UrlWrapper) o;
        return Objects.equals(url, that.url) &&
                Objects.equals(alias, that.alias);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, alias);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("UrlWrapper{");
        sb.append("url='").append(url).append('\'');
        sb.append(", alias='").append(alias).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
