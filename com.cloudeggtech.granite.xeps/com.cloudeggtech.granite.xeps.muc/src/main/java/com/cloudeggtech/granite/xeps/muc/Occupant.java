package com.cloudeggtech.granite.xeps.muc;

import java.util.ArrayList;
import java.util.List;

import com.cloudeggtech.basalt.protocol.core.JabberId;
import com.cloudeggtech.basalt.xeps.muc.Role;

public class Occupant {
	private List<JabberId> jids;
	private Role role;	
	private String nick;
	
	public Occupant() {
		jids = new ArrayList<>();
		role = Role.NONE;
	}
	
	public JabberId[] getJids() {
		return jids.toArray(new JabberId[jids.size()]);
	}
	
	public void addJid(JabberId jid) {
		if (!jids.contains(jid)) {
			jids.add(jid);
		}
	}
	
	public void removeJid(JabberId jid) {
		jids.remove(jid);
	}
	
	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}
	
	public Role getRole() {
		return role;
	}
	
	public void setRole(Role role) {
		this.role = role;
	}
	
}
