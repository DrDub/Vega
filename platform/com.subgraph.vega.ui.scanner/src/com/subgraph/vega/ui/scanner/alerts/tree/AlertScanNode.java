/*******************************************************************************
 * Copyright (c) 2011 Subgraph.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Subgraph - initial API and implementation
 ******************************************************************************/
package com.subgraph.vega.ui.scanner.alerts.tree;

import java.text.SimpleDateFormat;

import org.apache.http.HttpHost;

import com.subgraph.vega.api.model.IWorkspace;
import com.subgraph.vega.api.model.alerts.IScanAlert;
import com.subgraph.vega.api.model.alerts.IScanInstance;
import com.subgraph.vega.api.model.requests.IRequestLogRecord;

public class AlertScanNode extends AbstractAlertTreeNode {
	private final static String SCAN_IMAGE = "icons/scanner.png";
	private final static String PROXY_IMAGE = "icons/proxy.png";


	private final static String NO_HOSTNAME = "No Hostname";
	
	private final IWorkspace workspace;
	private final long scanId;
	private final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
	
	private IScanInstance scanInstance;
	
	AlertScanNode(long scanId, IWorkspace workspace) {
		this.workspace = workspace;
		this.scanId = scanId;
	}

	void setScanInstance(IScanInstance scanInstance) {
		this.scanInstance = scanInstance;
	}

	public IScanInstance getScanInstance() {
		return scanInstance;
	}

	@Override
	public String getLabel() {
		if(scanId == -1) {
			return "Proxy  ";
		} else if(scanInstance == null) {
			return "Scan [id: #"+ Long.toString(scanId) +"]  ";
		} else {
			return renderScanInstance();
		}
	}

	private String renderScanInstance() {
		final StringBuilder sb = new StringBuilder();
		sb.append(dateFormat.format(scanInstance.getStartTime()));

		sb.append(" [");
		switch(scanInstance.getScanStatus()) {
		case IScanInstance.SCAN_IDLE:
			sb.append("Idle");
			break;
		case IScanInstance.SCAN_STARTING:
			sb.append("Starting");
			break;
		case IScanInstance.SCAN_AUDITING:
			sb.append("Auditing");
			break;
		case IScanInstance.SCAN_CANCELLED:
			sb.append("Cancelled");
			break;
		case IScanInstance.SCAN_COMPLETED:
			sb.append("Completed");
			break;
		}
		sb.append("] ");
		return sb.toString();
	}

	@Override
	protected AbstractAlertTreeNode createNodeForAlert(IScanAlert alert) {
		return new AlertHostNode(createKeyForAlert(alert));
	}
	@Override
	public String getImage() {
		if(scanId == -1) {
			return PROXY_IMAGE;
		} else {
			return SCAN_IMAGE;
		}
	}

	@Override
	protected String createKeyForAlert(IScanAlert alert) {
		if(!alert.hasAssociatedRequest()) {
			return NO_HOSTNAME;
		}
		final IRequestLogRecord record = workspace.getRequestLog().lookupRecord(alert.getRequestId());
		if(record == null) {
			return NO_HOSTNAME;
		}
		final HttpHost host = record.getHttpHost();
		if(host == null) {
			return NO_HOSTNAME;
		}
		return host.toString();
	}

	public long getScanId() {
		return scanId;
	}
}
