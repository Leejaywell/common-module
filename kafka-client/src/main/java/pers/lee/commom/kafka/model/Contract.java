package pers.lee.commom.kafka.model;

import java.util.Objects;

/**
 * @author: Jay
 * @date: 2018/1/19
 */
public class Contract {
    public static final String SEPARATOR = "|";
    public static final String SEPARATOR_PATTERN = "\\|";

    private String sourceId;
    private String distributorId;
    private String hotelId;

    public Contract(String sourceId, String distributorId, String hotelId) {
        this.sourceId = sourceId;
        this.distributorId = distributorId;
        this.hotelId = hotelId;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getHotelId() {
        return hotelId;
    }

    public void setHotelId(String hotelId) {
        this.hotelId = hotelId;
    }

    public String getDistributorId() {
        return distributorId;
    }

    public void setDistributorId(String distributorId) {
        this.distributorId = distributorId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Contract contract = (Contract) o;
        return Objects.equals(sourceId, contract.sourceId) &&
                Objects.equals(distributorId, contract.distributorId) &&
                Objects.equals(hotelId, contract.hotelId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sourceId, distributorId, hotelId);
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append(sourceId).append(SEPARATOR).append(distributorId).append(SEPARATOR).append(hotelId);
        return sb.toString();
    }

    public static Contract buildContract(String key) {
        String[] splits = key.split(SEPARATOR_PATTERN);
        return new Contract(splits[0], splits[1], splits[2]);
    }
}
