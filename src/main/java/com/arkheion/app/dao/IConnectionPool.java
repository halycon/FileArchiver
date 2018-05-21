package com.arkheion.app.dao;

import java.sql.Connection;

public interface IConnectionPool {
	
	public Connection getConnection();
	public boolean closeConnection(Connection conn);
}
