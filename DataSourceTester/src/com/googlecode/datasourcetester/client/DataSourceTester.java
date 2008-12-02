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
package com.googlecode.datasourcetester.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.datasourcetester.client.rpc.DataSourceTesterService;
import com.googlecode.datasourcetester.client.rpc.DataSourceTesterServiceAsync;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class DataSourceTester implements EntryPoint {
	private FlexTable resultTable = new FlexTable();

	private DialogBox dialogBox = new DialogBox();

	private TextBox txtDataSourceName = new TextBox();

	private TextBox txtQueryText = new TextBox();

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		Label lblDataSourceName = new Label("DataSource JNDI name:");
		txtDataSourceName.setText("java:/DefaultDS");
		Label lblQueryText = new Label("Test query text:");
		txtQueryText.setText("SELECT * FROM INFORMATION_SCHEMA.SYSTEM_TABLES");
		txtQueryText.setWidth("32em");
		Button button = new Button("Test DataSource");
		button.addStyleName("btn");
		
		resultTable.addStyleName("resultTbl");

		VerticalPanel vPanel = new VerticalPanel();
		vPanel.setWidth("100%");
		vPanel.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
		
		HorizontalPanel hPanel = new HorizontalPanel();
		hPanel.add(lblDataSourceName);
		hPanel.add(txtDataSourceName);
		hPanel.add(lblQueryText);
		hPanel.add(txtQueryText);
		vPanel.add(hPanel);
		vPanel.add(new HTML("<p>Examples of other queries:</p>"+
				"<p><code>SELECT USERID FROM JMS_USERS</code> (JBoss's default HSQLDB)</p>"+
				"<p><code>SELECT 1 FROM DUAL</code> (Oracle)</p>"
				));
		
		vPanel.add(button);
		vPanel.add(resultTable);

		// Add image and button to the RootPanel
		RootPanel.get().add(vPanel);

		Button closeButton = new Button("close");
		VerticalPanel dialogVPanel = new VerticalPanel();
		dialogVPanel.setWidth("100%");
		dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
		dialogVPanel.add(closeButton);

		closeButton.addClickListener(new ClickListener() {
			public void onClick(Widget sender) {
				dialogBox.hide();
			}
		});

		// Set the contents of the Widget
		dialogBox.setWidget(dialogVPanel);

		button.addClickListener(new ClickListener() {
			public void onClick(Widget sender) {
				queryDataSource();
			}
		});
	}

	public void queryDataSource() {
		DataSourceTesterServiceAsync dsTesterService = (DataSourceTesterServiceAsync) GWT
				.create(DataSourceTesterService.class);
		AsyncCallback<String[][]> callback = new AsyncCallback<String[][]>() {
			public void onSuccess(String[][] result) {
				if (result == null) {
					showMsg("ERROR: Got empty response.");
				}
				for (int i = 0; i < result.length; i++) {
					for (int j = 0; j < result[i].length; j++) {
						resultTable.setText(i, j, result[i][j]);
					}
				}
			}

			public void onFailure(Throwable caught) {
				showMsg("ERROR: " + caught.getMessage());
			}
		};
		dsTesterService.queryDataSource(txtDataSourceName.getText(),
				txtQueryText.getText(), callback);
	}

	private void showMsg(String msg) {
		dialogBox.setText(msg);
		dialogBox.center();
		dialogBox.show();
	}
}
