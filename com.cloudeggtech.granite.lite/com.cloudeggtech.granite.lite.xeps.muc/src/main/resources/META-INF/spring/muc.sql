CREATE TABLE ROOM(id VARCHAR(64) PRIMARY KEY, room_jid VARCHAR(2047) NOT NULL, creator VARCHAR(2047) NOT NULL, create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP, locked BOOLEAN DEFAULT TRUE);
CREATE UNIQUE INDEX UNIQUE_INDEX_ROOM_ROOM_JID ON ROOM (room_jid);

CREATE TABLE ROOM_CONFIG(id VARCHAR(64) PRIMARY KEY, room_id VARCHAR(64) NOT NULL, room_name VARCHAR(1023), room_desc VARCHAR(2048), lang VARCHAR(5), enable_logging BOOLEAN,
	change_subject BOOLEAN, allow_invites BOOLEAN, allow_pm VARCHAR(12), max_users INTEGER, public_room BOOLEAN, persistent_room BOOLEAN, moderated_room BOOLEAN,
	members_only BOOLEAN, password_protected_room BOOLEAN, room_secret VARCHAR(256), who_is VARCHAR(10), max_history_fetch INTEGER, pub_sub VARCHAR(2056));
CREATE UNIQUE INDEX UNIQUE_INDEX_ROOM_CONFIG_ROOM_ID ON ROOM_CONFIG (room_id);
CREATE INDEX INDEX_ROOM_CONFIG_ROOM_NAME ON ROOM_CONFIG (room_name);

CREATE TABLE ROOM_CONFIG_PRESENCE_BROADCAST(id VARCHAR(64) PRIMARY KEY, room_config_id VARCHAR(64), moderator BOOLEAN, participant BOOLEAN, visitor BOOLEAN);
CREATE UNIQUE INDEX UNIQUE_INDEX_ROOM_CONFIG_PRESENCE_BROADCAST_ROOM_CONFIG_ID ON ROOM_CONFIG_PRESENCE_BROADCAST (room_config_id);

CREATE TABLE ROOM_CONFIG_GET_MEMBER_LIST(id VARCHAR(64) PRIMARY KEY, room_config_id VARCHAR(64), moderator BOOLEAN, participant BOOLEAN, visitor BOOLEAN);
CREATE UNIQUE INDEX UNIQUE_INDEX_ROOM_CONFIG_GET_MEMBER_LIST_ROOM_CONFIG_ID ON ROOM_CONFIG_GET_MEMBER_LIST (room_config_id);

CREATE TABLE ROOM_AFFILIATED_USER(id VARCHAR(64) PRIMARY KEY, room_id VARCHAR(64), jid VARCHAR(2047), affiliation VARCHAR(7), role VARCHAR(11), nick VARCHAR(1023), CONSTRAINT UNIQUE_ROOM_ID_JID UNIQUE (room_id, jid));
CREATE UNIQUE INDEX UNIQUE_INDEX_ROOM_AFFILIATED_USER_ROOM_ID_JID ON ROOM_AFFILIATED_USER (room_id, jid);
CREATE INDEX INDEX_ROOM_AFFILIATED_USER_ROOM_ID_AFFILIATION ON ROOM_AFFILIATED_USER (room_id, affiliation);

CREATE TABLE ROOM_SUBJECT_HISTORY(id VARCHAR(64) PRIMARY KEY, room_id VARCHAR(64), subject VARCHAR(512), create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP);
CREATE INDEX INDEX_ROOM_SUBJECT_HISTORY_ROOM_ID ON ROOM_SUBJECT_HISTORY (room_id);