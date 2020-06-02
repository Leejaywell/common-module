package pers.lee.common.rpc.ci.status;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.stat.DruidStatManagerFacade;
import pers.lee.common.lang.json.Json;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by brookesong on 17-6-7.
 */
public class DruidSQLStatus implements StatusAware {

    public static final Logger LOGGER = LoggerFactory.getLogger(DruidStatus.class);

    public static final String NAME = "druidSql";
    private static final Set<String> KEYS = new HashSet<String>();
    private static DruidStatManagerFacade statManagerFacade = DruidStatManagerFacade.getInstance();
    private static Json JSON = Json.getDefault();

    static {
        KEYS.add("sql.ExecuteCount");
        KEYS.add("sql.TotalTime");
        KEYS.add("sql.MaxTimespan");
        KEYS.add("sql.InTransactionCount");
        KEYS.add("sql.ErrorCount");
        KEYS.add("sql.EffectedRowCount");
        KEYS.add("sql.FetchRowCount");
        KEYS.add("sql.RunningCount");
        KEYS.add("sql.ConcurrentMax");
        KEYS.add("sql.SQL");
    }

    @Override
    public String getStatusPrefix() {
        return NAME;
    }

    @Override
    public Set<String> getStatusKeys() {
        return KEYS;
    }

    @Override
    public Map<String, String> status() {
        Map<String, String> status = new LinkedHashMap<String, String>();
        List<Map<String, Object>> dataSources = statManagerFacade.getSqlStatDataList(null);
        if (dataSources != null) {
            for (Map<String, Object> dataSource : dataSources) {
                Map<String, Object> eachStatus = new LinkedHashMap<String, Object>();
                for (String key : KEYS) {
                    String subKey = key.substring("sql.".length());
                    Object value = dataSource.get(key.substring("sql.".length()));
                    if (subKey.equals("SQL")) {
                        String dbType = (String) dataSource.get("DbType");
                        String sql = (String) dataSource.get("SQL");
                        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, dbType);
                        if (!statementList.isEmpty()) {
                            SQLStatement statemen = (SQLStatement) statementList.get(0);
                            SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(statementList, dbType);
                            statemen.accept(visitor);
                            List sqlstats = Lists.newArrayList();
                            for (Map.Entry entry : visitor.getTables().entrySet()) {
                                Map<String, String> map = Maps.newHashMap();
                                String name = entry.getKey().toString();
                                String ddl = entry.getValue().toString();
                                map.put("tableName", name);
                                map.put("tableDMLs", ddl);
                                sqlstats.add(map);
                            }
                            eachStatus.put("tableStats", sqlstats);
                        }
                    }

                    eachStatus.put(subKey, String.valueOf(value));
                }
                status.put("druidSQLStats" + String.valueOf(dataSource.get("ID")), JSON.toJsonString(eachStatus));
            }
        }

        return status;
    }

    private static DruidSQLStatus druidSQLStatus = new DruidSQLStatus();

    public static DruidSQLStatus get() {
        return druidSQLStatus;
    }
}
