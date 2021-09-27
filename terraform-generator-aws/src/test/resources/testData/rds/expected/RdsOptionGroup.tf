resource aws_db_option_group test {
	name = "test"
	engine_name = "mysql"
	major_engine_version = "5.6"
	option_group_description = "test desc"
	option {
		option_name = "MARIADB_AUDIT_PLUGIN"
		option_settings  {
			name = "SERVER_AUDIT_EXCL_USERS"
			value = null
		}
		option_settings  {
			name = "SERVER_AUDIT_EVENTS"
			value = "CONNECT,QUERY"
		}
		option_settings  {
			name = "SERVER_AUDIT_FILE_PATH"
			value = "/rdsdbdata/log/audit/"
		}
	}
}

