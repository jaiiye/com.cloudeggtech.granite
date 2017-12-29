package com.cloudeggtech.granite.xeps.component.processing;

import java.util.List;

import com.cloudeggtech.basalt.protocol.core.JabberId;

public interface IReliableDeliveryStorage {
	String save(ReliableDeliveryMessage message);
	void remove(String id);
	List<ReliableDeliveryMessage> get(JabberId user);
}
