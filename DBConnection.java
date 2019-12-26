package com.billpugh;

/***
 * 
 * @author Niroshan Sandeepa
 */
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DBConnection implements Serializable {
	private static final long serialVersionUID = 2584114777537893105L;
	private Connection connection;

	private DBConnection() {
		// throw an error for reflection attack
		if (DBConnectionHolder.INSTANCE != null) {
			throw new IllegalAccessError("Instance already created");
		}
	}

	// Bill Pugh way to create a singleton (Thread safe)
	private static class DBConnectionHolder {
		private static DBConnection INSTANCE = new DBConnection();
	}

	public static DBConnection getInstance() {
		return DBConnectionHolder.INSTANCE;
	}
	
	public Connection getConnection() {
		try {
			if (connection == null || connection.isClosed()) {
				synchronized (DBConnection.class) {
					if ((connection == null) || (connection.isClosed())) {
						Class.forName("com.mysql.jdbc.Driver");
						connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/hibernate", "root", "1234");
					}
				}
			}
			return connection;
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @return INSTANCE for guaranteed de-serialization won't create a new DBConnection object
	 */
	protected Object readResolve() {
		return getInstance();
	}

	/**
	 * 
	 * @throws exception for when trying to clone the object
	 */
	@Override
	protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException("Can't create a clone from Singleton class");
	}
}
