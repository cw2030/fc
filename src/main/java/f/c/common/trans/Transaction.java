package f.c.common.trans;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import javax.sql.DataSource;

public class Transaction {
    private int level;

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        if (this.level <= 0)
            this.level = level;
    }
    
    private static AtomicLong TransIdMaker = new AtomicLong();

    private List<ConnInfo> list;
    
    private long id;

    private static class ConnInfo {
        ConnInfo(DataSource ds, Connection conn, int level) throws SQLException {
            this.ds = ds;
            this.conn = conn;
            this.oldLevel = conn.getTransactionIsolation();
            if (this.oldLevel != level)
                conn.setTransactionIsolation(level);
        }

        DataSource ds;
        Connection conn;
        int oldLevel;
    }

    public Transaction() {
        list = new ArrayList<ConnInfo>();
        id = TransIdMaker.getAndIncrement();
    }

    protected void commit() {
//        ComboException ce = new ComboException();
        for (ConnInfo cInfo : list) {
            try {
                // 提交事务
                cInfo.conn.commit();
                // 恢复旧的事务级别
                if (cInfo.conn.getTransactionIsolation() != cInfo.oldLevel)
                    cInfo.conn.setTransactionIsolation(cInfo.oldLevel);
            }
            catch (SQLException e) {
//                ce.add(e);
            }
        }
        // 如果有一个数据源提交时发生异常，抛出
//        if (null != ce.getCause()) {
//            throw ce;
//        }
    }

    public Connection getConnection(DataSource dataSource) throws SQLException {
        for (ConnInfo p : list)
            if (p.ds == dataSource)
                return p.conn;
        Connection conn = dataSource.getConnection();
        // System.out.printf("=> %s\n", conn.toString());
        if (conn.getAutoCommit())
            conn.setAutoCommit(false);
        // Store conn, it will set the trans level
        list.add(new ConnInfo(dataSource, conn, getLevel()));
        return conn;
    }

    public long getId() {
        return id;
    }

    public void close() {
//        ComboException ce = new ComboException();
        for (ConnInfo cInfo : list) {
            try {
                // 试图恢复旧的事务级别
                if (!cInfo.conn.isClosed()) {
                    if (cInfo.conn.getTransactionIsolation() != cInfo.oldLevel)
                        cInfo.conn.setTransactionIsolation(cInfo.oldLevel);
                }
            }
            catch (Throwable e) {}
            finally {
                try {
                    cInfo.conn.close();
                }
                catch (Exception e) {
//                    ce.add(e);
                }
            }
        }
        // 清除数据源记录
        list.clear();
    }

    protected void rollback() {
        for (ConnInfo cInfo : list) {
            try {
                cInfo.conn.rollback();
            }
            catch (Throwable e) {}
        }
    }

}
