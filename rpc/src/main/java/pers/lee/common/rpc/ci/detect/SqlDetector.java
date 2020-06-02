package pers.lee.common.rpc.ci.detect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * MySQLDetector
 *
 * @author Drizzt Yang
 */
public class SqlDetector extends DetectorBase implements Detector {
    private static final Logger logger = LoggerFactory.getLogger(SqlDetector.class);

    private DataSource datasource;
    private String sql;

    public SqlDetector(String detectorName, DataSource datasource, String sql) {
        this.datasource = datasource;
        this.setDetectorName(detectorName);
        this.sql = sql;
    }

    public DataSource getDatasource() {
        return datasource;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    @Override
    public DetectResult detect() {
        if(executeSQL(sql)) {
            return DetectResult.pass(this.getDetectorName(), null);
        } else {
            return DetectResult.fail(this.getDetectorName(), null);
        }
    }


    protected Boolean executeSQL(String sql) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = getDatasource().getConnection();
            preparedStatement = connection.prepareStatement(sql);

            preparedStatement.executeQuery();
            return true;
        } catch (SQLException e) {
            logger.warn("execute sql for DatabaseConfiguration failed, sql [" + sql + "]", e);
            return false;
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (SQLException ignored) {
            }
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ignored) {
            }
        }
    }
}
