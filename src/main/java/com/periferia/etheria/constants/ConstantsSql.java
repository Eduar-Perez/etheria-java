package com.periferia.etheria.constants;

import lombok.Getter;

@Getter
public enum ConstantsSql {
	VAR_HOST("host"),
	VAR_PORT("port"),
	VAR_DBNAME("dbname"),
	VAR_USERNAME("username"),
	VAR_PASSWORD("password"),
	VAR_SENTENCIA_SQL_EMAIL("SELECT u.cedula, u.first_name, u.last_name, u.email, u.password, u.token, u.role FROM etheria.users u WHERE email = ?"),
	VAR_SENTENCIA_SQL_EXIST_BY_ID_USER("SELECT 1 FROM etheria.users WHERE cedula = ?"),
	VAR_SENTENCIA_SQL_EXIST_BY_MODULE("SELECT 1 FROM etheria.record WHERE uuid = ?"),
	VAR_SENTENCIA_SQL_EXIST_BY_ID_RECORD_USER("SELECT 1 FROM etheria.record_users WHERE cedula = ?"),
	VAR_SENTENCIA_SQL_SAVE_USER("INSERT INTO etheria.users (cedula, first_name, last_name, email, password, role) VALUES (?, ?, ?, ?, ?, ?)"),
	VAR_SENTENCIA_SQL_GET_CHAT("SELECT r.id, r.question, r.response, r.date_create, r.uuid FROM etheria.record r INNER JOIN etheria.record_users ru on ru.id_record = r.id INNER JOIN etheria.users u on u.cedula = ru.cedula where u.cedula = ?"),
	VAR_SENTENCIA_SQL_SAVE_CHAT("INSERT INTO etheria.record (question, response, date_create, uuid) VALUES (?, ?, ?, ?)"),
	VAR_SENTENCIA_SQL_SAVE_RECORD_USER("INSERT INTO etheria.record_users (id_record, cedula) VALUES (?, ?)"),
	VAR_SENTENCIA_SQL_DELETE_BY_MODULE("DELETE FROM etheria.record r WHERE r.uuid = ?"),
	VAR_SENTENCIA_SQL_FIND_BY_ID_RECORD("SELECT r.id, r.question, r.response, r.date_create, r.uuid FROM etheria.record r WHERE id = ?"),
	VAR_SENTENCIA_SQL_UPDATE_RECORD("UPDATE etheria.record SET response = ? WHERE id = ?"),
	VAR_SENTENCIA_SQL_DELETE_INSTRUCTION("DELETE FROM etheria.instruction i WHERE i.id = ? AND i.id_user = ?"),
	VAR_SENTENCIA_SQL_CREATE_INSTRUCTION("INSERT INTO etheria.instruction (name, instruction, description, general, id_user) VALUES (?,?,?,?,?)"),
	VAR_SENTENCIA_SQL_UPDATE_INSTRUCTION("UPDATE etheria.instruction SET name = ?, instruction = ?, description = ?, general = ?, id_user = ? WHERE id = ?"),
	VAR_SENTENCIA_SQL_GET_INSTRUCTION("SELECT i.id, i.instruction, i.description, i.id_user, i.general, i.name FROM etheria.instruction i WHERE i.id_user = ?");

	private String value;

	ConstantsSql(String value) {
		this.value = value;
	}

}

