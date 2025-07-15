package com.periferia.etheria.constants;

import lombok.Getter;

@Getter
public enum ConstantsSql {
	VAR_HOST("host"),
	VAR_PORT("port"),
	VAR_DBNAME("dbname"),
	VAR_USERNAME("username"),
	VAR_PASSWORD("password"),
	VAR_SENTENCIA_SQL_EMAIL("SELECT u.cedula, u.first_name, u.last_name, u.email, u.password, u.token, u.role, u.image FROM etheria.users u WHERE email = ?"),
	VAR_SENTENCIA_SQL_EXIST_BY_ID_USER("SELECT 1 FROM etheria.users WHERE cedula = ?"),
	VAR_SENTENCIA_SQL_EXIST_BY_MODULE("SELECT 1 FROM etheria.title_record WHERE uuid = ?"),
	VAR_SENTENCIA_SQL_GET_TITLE_RECORD("SELECT tr.* FROM etheria.title_record tr WHERE uuid = ? AND id_user = ?"),
	VAR_SENTENCIA_SQL_SAVE_USER("INSERT INTO etheria.users (cedula, first_name, last_name, email, password, role, image) VALUES (?, ?, ?, ?, ?, ?, ?)"),
	VAR_SENTENCIA_SQL_GET_CHAT("SELECT tr.id AS id_title, tr.title, tr.date_create, tr.id_user, tr.uuid, r.id AS id_record, r.question, r.response FROM etheria.record_users ru "
			+ "INNER JOIN etheria.record r on ru.id_record = r.id "
			+ "INNER JOIN etheria.title_record tr on ru.id_title_record = tr.id "
			+ "WHERE tr.id_user = ?"),
	VAR_SENTENCIA_SQL_SAVE_CHAT("INSERT INTO etheria.record (question, response) VALUES (?, ?)"),
	VAR_SENTENCIA_SQL_SAVE_TITLE_RECORD("INSERT INTO etheria.title_record (title, date_create, id_user, uuid) VALUES (?, ?, ?, ?)"),
	VAR_SENTENCIA_SQL_SAVE_RECORD_USER("INSERT INTO etheria.record_users (id_record, id_title_record) VALUES (?, ?)"),
	VAR_SENTENCIA_SQL_DELETE_BY_MODULE("DELETE FROM etheria.title_record r WHERE r.uuid = ?"),
	VAR_SENTENCIA_SQL_FIND_BY_ID_RECORD("SELECT r.id, r.question, r.response, r.date_create, r.uuid FROM etheria.record r WHERE id = ?"),
	VAR_SENTENCIA_SQL_UPDATE_RECORD("UPDATE etheria.title_record SET title = ? WHERE id = ? RETURNING id, title, date_create, id_user, uuid"),
	VAR_SENTENCIA_SQL_DELETE_INSTRUCTION("DELETE FROM etheria.instruction WHERE id = ? AND id_user = ?"),
	VAR_SENTENCIA_SQL_CREATE_INSTRUCTION("INSERT INTO etheria.instruction (name, instruction, description, general, id_user) VALUES (?,?,?,?,?)"),
	VAR_SENTENCIA_SQL_UPDATE_INSTRUCTION("UPDATE etheria.instruction SET name = ?, instruction = ?, description = ?, general = ?, id_user = ? WHERE id = ?"),
	VAR_SENTENCIA_SQL_GET_INSTRUCTION("SELECT i.id, i.instruction, i.description, i.id_user, i.general, i.name FROM etheria.instruction i WHERE i.id_user = ?"),
	VAR_SENTENCIA_SQL_GET_INSTRUCTION_GENERAL("SELECT i.id, i.instruction, i.description, i.id_user, i.general, i.name FROM etheria.instruction i WHERE i.general = ?");

	private String value;

	ConstantsSql(String value) {
		this.value = value;
	}

}

