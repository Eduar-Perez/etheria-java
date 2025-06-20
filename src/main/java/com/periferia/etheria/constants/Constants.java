package com.periferia.etheria.constants;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

@Getter
public class Constants {

	private Constants() {}

	//Security
	public static final String JWT_SECRET = "JWT_SECRET";
	public static final String ERROR_CREDENCIALES = "La contraseña y el correo son obligatorios ";
	public static final String DATA_OBLIGATORIA = "Los datos son obligatorios "; 
	public static final String ALGORITMO = "AES";
	public static final String ENDPOINT_AGENTIA = "ENDPOINT_AGENTIA";
	public static final String API_KEY = "API_KEY";

	//Exceptions
	public static final String ERROR_REQUEST = "Error procesando la peticion ";
	public static final String ERROR_ALGORITMO = "Error: Algoritmo no encontrado o no soportado: ";
	public static final String ERROR_INVALID_KEY = "Error: Clave inválida utilizada para la desencriptación: ";
	public static final String ERROR_BAD_PADDING = "Error: Relleno incorrecto durante la desencriptación: ";
	public static final String ERROR_ILLEGAL_BLOCK_SIZE = "Error: Tamaño de bloque ilegal durante la desencriptación: ";
	public static final String DECRYPT_PASSWORD = "Error: Ocurrió un error inesperado durante la desencriptación: ";
	public static final String TOKEN_EXPIRATED = "Error: El token ha expirado: ";
	public static final String ERROR_CONTRASENA = "Se requiere la contraseña para la consulta ";
	
	public static final String ERROR_SQL_GET_USER_EMAIL = "Error SQL: Al obtener el usuario por correo electrónico: ";
	public static final String ERROR_SQL_USER_EXIST = "Error SQL: Al verificar si el usuario existe por cédula: ";
	public static final String ERROR_SQL_SAVE_USER = "Error SQL: Al guardar el usuario en la base de datos: ";
	public static final String ERROR_SQL_GET_RECORD = "Error SQL: Durante la consulta del historial: ";
	public static final String ERROR_SQL_SAVE_RECORD = "Error SQL: Durante la creación de la relación del usuario con el historial: ";
	public static final String ERROR_SQL_UPDATE_RECORD = "Error SQL: Durante la actualización de historial: ";
	public static final String ERROR_SQL_GET_UPDATE = "Error SQL: Durante la consulta de las instrucciones: ";
	public static final String ERROR_SQL_DELETE_RECORDS = "Error SQL: Durante la eliminación del historial: ";
	public static final String ERROR_GET_RECORDS = "Error: Durante la consulta del historial: ";
	public static final String ERROR_SAVE_RECORDS = "Error: Guardando el historial: ";
	public static final String ERROR_DELETE_RECORDS = "Error: Eliminando el historial: ";
	public static final String ERROR_UPDATE_RECORDS = "Error: Actualizando el historial: ";
	public static final String ERROR_QUERY_AGENT = "Error: Durante la consulta con el agente IA: ";
	public static final String ERROR_SQL_DELETE_INSTRUCTION = "Error SQL: Durante la eliminación de la instrucción: ";
	public static final String ERROR_SQL_SAVE_INSTRUCTION = "Error SQL: Durante la creación de la instrucción: ";

	//Logs
	public static final String CONECTION_OK = "Conexión con la BD establecida. ";
	public static final String CONECTION_ERROR = "Error conectando con la BD: ";
	public static final String INTINIALIZER_KEY = "Inicializando Jwt Service clave secreta. ";
	public static final String GENERATE_TOKEN = "Se genera el token con exito. ";
	public static final String ERROR_REGISTER = "Error registrando al usuario: ";
	public static final String ERROR_LOGIN = "Error en login: ";

	//Object-user
	public static final String QUESTION = "question";
	public static final String MODEL = "model";
	public static final String AGENT = "agent_id";
	public static final String UUID = "uuid";
	public static final String CC = "cedula";
	public static final String FIRST_NAME = "firstName";
	public static final String LAST_NAME = "lastName";
	public static final String EMAIL = "email";
	public static final String PASSWORD = "password";
	public static final Object FILE = "files";
	public static final Object ROLE = "role";

	//Object-instruction
	public static final Object ID = "id";
	public static final Object NAME = "name";
	public static final Object INSTRUCTION = "instruction";
	public static final Object DESCRIPTION = "description";
	public static final Object GENERAL = "general";
	public static final Object ID_USER = "idUser";
	public static final Object ACTION = "action";

	//Logs-Service
	public static final String LOGIN_SERVICE = "Ingresa al servicio de: {}";
	public static final String LOGIN_SQL = "Ingresa a la consulta de: {}";

	//Response-Service	
	public static final String RESPONSE_GENERIC = "Error: Datos incompletos ...";
	public static final String RESPONSE_REGISTER = "Todos los datos son obligatorios. ";  
	public static final String RESPONSE_LOGIN = "El correo y la contraseña son obligatorios. ";  
	public static final String RESPONSE_LOGOUT = "El token es obligatorio. ";  
	public static final String RESPONSE_QUERYAGENT = "La pregunta, la cedula y el token son obligatorios. ";  
	public static final String RESPONSE_DELETE = "El id es obligatorio. ";	

	//Headers-Response
	public static final String CONTENT_TYPE = "Content-Type";
	public static final String RESPONSE_CONTENT_TYPE = "application/json";
	public static final String ACCES_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
	public static final String RESPONSE_ACCES_CONTROL_ALLOW_ORIGIN = "*";
	public static final String ACCES_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";
	public static final String RESPONSE_CONTROL_ALLOW_METHODS = "OPTIONS,POST";
	public static final String ACCES_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";
	public static final String RESPONSE_CONTROL_ALLOW_HEADERS = "Content-Type,Authorization,X-Amz-Date,X-Api-Key,X-Amz-Security-Token";

	//Model_agent 
	protected static final Map<String, String> modelsAgents = new HashMap<>();
	static {
		modelsAgents.put("claud-agent", "us.anthropic.claude-3-7-sonnet-20250219-v1:0");
		modelsAgents.put("web-agent", "us.anthropic.claude-3-7-sonnet-20250219-v1:0");
		modelsAgents.put("agno-assist", "us.anthropic.claude-3-7-sonnet-20250219-v1:0");
		modelsAgents.put("finance-agent", "us.anthropic.claude-3-7-sonnet-20250219-v1:0");
	}

	public static final Map<String, String> getModelsAgents() {
		return modelsAgents;
	}

}
