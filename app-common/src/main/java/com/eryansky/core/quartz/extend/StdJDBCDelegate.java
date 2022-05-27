package com.eryansky.core.quartz.extend;

import org.quartz.TriggerKey;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;
/**
 * quartz.properties中配置：
 * org.quartz.jobStore.driverDelegateClass= com.eryansky.core.quartz.extend.StdJDBCDelegate
 */
import static org.quartz.TriggerKey.triggerKey;

public class StdJDBCDelegate extends org.quartz.impl.jdbcjobstore.StdJDBCDelegate {

    String INSTANCE_NAME_VALUE = "{2}";

    String SELECT_NEXT_TRIGGER_TO_ACQUIRE_EXT = "SELECT "
            + COL_TRIGGER_NAME + ", " + COL_TRIGGER_GROUP + ", "
            + COL_NEXT_FIRE_TIME + ", " + COL_PRIORITY + " FROM "
            + TABLE_PREFIX_SUBST + TABLE_TRIGGERS + " WHERE "
            + COL_SCHEDULER_NAME + " = " + SCHED_NAME_SUBST
            + " AND " + COL_TRIGGER_STATE + " = ? AND " + COL_NEXT_FIRE_TIME + " <= ? "
            + " AND (" + COL_INSTANCE_NAME + " IS NULL OR " + COL_INSTANCE_NAME + " = " + INSTANCE_NAME_VALUE + ")"
            + "AND (" + COL_MISFIRE_INSTRUCTION + " = -1 OR (" + COL_MISFIRE_INSTRUCTION + " != -1 AND "
            + COL_NEXT_FIRE_TIME + " >= ?)) "
            + "ORDER BY " + COL_NEXT_FIRE_TIME + " ASC, " + COL_PRIORITY + " DESC";

    /**
     * 允许设置trigger 指定一台机器进行任务调度执行；
     *
     * @param conn
     * @param noLaterThan
     * @param noEarlierThan
     * @param maxCount
     * @return
     * @throws SQLException
     */
    @Override
    public List<TriggerKey> selectTriggerToAcquire(Connection conn, long noLaterThan, long noEarlierThan, int maxCount)
            throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<TriggerKey> nextTriggers = new LinkedList<>();
        try {
            ps = conn.prepareStatement(rtp(SELECT_NEXT_TRIGGER_TO_ACQUIRE_EXT, instanceId));

            // Set max rows to retrieve
            if (maxCount < 1) {
                maxCount = 1;
            }
            ps.setMaxRows(maxCount);

            // Try to give jdbc driver a hint to hopefully not pull over more than the few rows we actually need.
            // Note: in some jdbc drivers, such as MySQL, you must set maxRows before fetchSize, or you get exception!
            ps.setFetchSize(maxCount);

            ps.setString(1, STATE_WAITING);ps.setBigDecimal(2, new BigDecimal(String.valueOf(noLaterThan)));
            ps.setBigDecimal(3, new BigDecimal(String.valueOf(noEarlierThan)));
            rs = ps.executeQuery();

            while (rs.next() && nextTriggers.size() <= maxCount) {
                nextTriggers.add(triggerKey(
                        rs.getString(COL_TRIGGER_NAME),
                        rs.getString(COL_TRIGGER_GROUP)));
            }

            return nextTriggers;
        } finally {
            closeResultSet(rs);
            closeStatement(ps);
        }
    }

    protected final String rtp(String query, String instanceName) {
        return MessageFormat.format(query,
                new Object[]{tablePrefix, getSchedulerNameLiteral(), "'" + instanceName + "'"});
    }
}