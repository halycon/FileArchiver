package com.arkheion.app.dao.impl.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arkheion.app.dao.IConnectionPool;

public class JdbcConnectionPool implements IConnectionPool {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	private DataSource dataSource;

	public Connection getConnection() {
		
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
		} catch (SQLException e) {
			logger.error("getConnection :: {}",e);
		}
		return conn;
	}

	public boolean closeConnection(Connection conn) {
		try {
			conn.close();
			return conn.isClosed();
		} catch (SQLException e) {
			logger.error("closeConnection :: {}",e);
		}
		return false;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}


}
