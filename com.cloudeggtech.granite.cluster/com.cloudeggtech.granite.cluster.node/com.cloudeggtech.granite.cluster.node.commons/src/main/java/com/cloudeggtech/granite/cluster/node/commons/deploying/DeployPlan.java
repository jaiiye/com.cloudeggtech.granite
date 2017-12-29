package com.cloudeggtech.granite.cluster.node.commons.deploying;

import java.util.HashMap;
import java.util.Map;

import com.cloudeggtech.granite.cluster.node.commons.utils.StringUtils;

public class DeployPlan {
	private Cluster cluster;
	private Map<String, NodeType> nodeTypes;
	private Global global;
	private Db db;
	
	public Cluster getCluster() {
		return cluster;
	}
	
	public void setCluster(Cluster cluster) {
		this.cluster = cluster;
	}
	
	public Map<String, NodeType> getNodeTypes() {
		if (nodeTypes == null) {
			nodeTypes = new HashMap<>();
		}
		
		return nodeTypes;
	}
	
	public void setNodeTypes(Map<String, NodeType> nodeTypes) {
		this.nodeTypes = nodeTypes;
	}
	
	public Global getGlobal() {
		return global;
	}

	public void setGlobal(Global global) {
		this.global = global;
	}

	public Db getDb() {
		return db;
	}
	
	public void setDb(Db db) {
		this.db = db;
	}
	
	public String getChecksum() {
		String[] nodeTypeChecksums = new String[nodeTypes.size()];
		
		int i = 0;
		for (NodeType nodeType : nodeTypes.values()) {
			nodeTypeChecksums[i++] = nodeType.getChecksum();
		}
		
		String[] sortedNodeTypeChecksums = StringUtils.sort(nodeTypeChecksums);
		
		StringBuilder nodeTypesStringBuilder = new StringBuilder();
		for (String checksum : sortedNodeTypeChecksums) {
			nodeTypesStringBuilder.append(checksum).append('|');
		}
		nodeTypesStringBuilder.deleteCharAt(nodeTypesStringBuilder.length() - 1);
		
		if (db != null && db.getChecksum() != null) {
			return StringUtils.getChecksum(nodeTypesStringBuilder.toString()) + db.getChecksum();
		}
		
		return StringUtils.getChecksum(nodeTypesStringBuilder.toString());
	}
	
	public String getAppnodeRuntimeChecksum(String nodeTypeName) {
		NodeType nodeType = getNodeTypes().get(nodeTypeName);
		if (nodeType == null) {
			throw new IllegalArgumentException(String.format("Illegal node type: %s", nodeTypeName));
		}
		
		String dbChecksum = (db == null) ? null : db.getChecksum();
		
		if (dbChecksum != null && !dbChecksum.isEmpty())
			return nodeType.getChecksum() + dbChecksum;
		
		return nodeType.getChecksum();
	}
	
}
