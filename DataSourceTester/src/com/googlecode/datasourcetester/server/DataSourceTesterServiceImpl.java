/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.googlecode.datasourcetester.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.LinkedList;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.googlecode.datasourcetester.client.rpc.DataSourceTesterService;

public class DataSourceTesterServiceImpl extends RemoteServiceServlet implements
		DataSourceTesterService {
	private static final long serialVersionUID = 1L;
	private static final Log logger = LogFactory
			.getLog(DataSourceTesterServiceImpl.class);

	public String[][] queryDataSource(String dataSourceJndiName, String query) {
		Connection conn = null;
		try {
			InitialContext jndiContext = new InitialContext();
			DataSource ds = (DataSource) jndiContext.lookup(dataSourceJndiName);
			conn = ds.getConnection();
			PreparedStatement stmt = conn.prepareStatement(query);
			ResultSet rs = stmt.executeQuery();
			ResultSetMetaData resMeta = rs.getMetaData();
			LinkedList<String[]> rowList = new LinkedList<String[]>();
			String[] colLabels = new String[resMeta.getColumnCount()];
			for (int colNr = 1; colNr <= resMeta.getColumnCount(); colNr++) {
				colLabels[colNr - 1] = resMeta.getColumnName(colNr);
			}
			rowList.add(colLabels);
			while (rs.next()) {
				String[] rowData = new String[resMeta.getColumnCount()];
				for (int colNr = 1; colNr <= resMeta.getColumnCount(); colNr++) {
					rowData[colNr - 1] = rs.getString(colNr);
				}
				rowList.add(rowData);
			}
			conn.close();
			return rowList.toArray(new String[rowList.size()][]);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			try {
				if (conn != null && !conn.isClosed()) {
					conn.close();
				}
			} catch (SQLException sqlEx) {
				logger.error(sqlEx.getMessage(), sqlEx);
			}
			return null;
		}
	}
}
