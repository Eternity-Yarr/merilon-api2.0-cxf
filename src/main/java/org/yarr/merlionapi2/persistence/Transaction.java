package org.yarr.merlionapi2.persistence;

import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicLong;

public class Transaction
{
    private static final Logger log = LoggerFactory.getLogger(Transaction.class);
    private static AtomicLong counter = new AtomicLong(System.currentTimeMillis());

    private final Connection connection;
    private boolean closed = false;
    private Vector<PreparedStatement> statements = new Vector<>();
    private Vector<ResultSet> resultSets = new Vector<>();
    private long transactionNo;

    public Transaction(Connection connection) throws SQLException {
        transactionNo = counter.incrementAndGet();
        this.connection = connection;
        connection.setAutoCommit(false);
        begin();
    }

    private void begin() throws SQLException {
        log.debug("Starting transaction #{} on {}", transactionNo, connection);
        try (Statement s = connection.createStatement()) {
            s.execute("BEGIN");
        }
    }

    private void commit() throws SQLException {
        log.debug("Committing transaction #{} on {}", transactionNo, connection);
        try (Statement s = connection.createStatement()) {
            s.execute("COMMIT");
        }
    }

    private void rollback() throws SQLException {
        closed = true;
        log.debug("Rolling back transaction #{} on {}", transactionNo, connection);
        try (Statement s = connection.createStatement()) {
            s.execute("ROLLBACK");
        }
    }

    public PreparedStatement ps(String sql) throws SQLException {
        String nolf = sql.replaceAll("\n", "\\n");
        String nocrlf = nolf.replaceAll("\r", "\\r");
        log.debug("New statement preparing for transaction #{} : {}", nocrlf);
        Preconditions.checkArgument(!closed, "Already closed");
        PreparedStatement ps = connection.prepareStatement(sql);
        statements.add(ps);
        return ps;
    }

    public ResultSet rs(PreparedStatement ps) throws SQLException {
        log.debug("Executing preparing statement within transaction #{}", transactionNo);
        try
        {
            ResultSet rs = ps.executeQuery();
            resultSets.add(rs);
            return rs;
        } catch (SQLException e) {
            log.error("Exception occurred ({}), rolling back transaction #{}", e.getMessage(), transactionNo, e);
            rollback();
            return null;
        }
    }

    public void commitAndCleanup() throws SQLException {
        log.debug("Commiting and cleaning up transaction #{}", transactionNo);
        Preconditions.checkArgument(!closed, "Already closed");
        resultSets.stream().forEach(rs -> {
            try
            {
                rs.close();
            } catch (SQLException e)
            {
                log.debug("Got an exception while trying to close result set, transaction #{}", transactionNo);
            }
        });
        statements.stream().forEach(ps -> {
            try
            {
                ps.close();
            } catch (SQLException e)
            {
                log.debug("Got an exception while trying to close prepared statement, transaction #{}", transactionNo);
            }
        });

        commit();
        log.debug("Releasing underlying connection, transaction #{}", transactionNo);
        connection.close();
        closed = true;
    }
}
