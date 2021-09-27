resource aws_rds_cluster_parameter_group rds-dev-paramgrp {
	name = "rds-dev-paramgrp"
	family = "aurora-mysql5.7"
	description = "Staging Aurora(Mysql 5.7) Cluster Parameter Group"
	parameter {
		name = "character_set_client"
		value = "utf8"
	}
	parameter {
		name = "character_set_connection"
		value = "utf8"
	}
}

