resource aws_rds_cluster rds-dev-cluster {
	cluster_identifier = "rds-dev-cluster"
	engine = "aurora-postgresql"
	engine_version = "11.9"
	engine_mode = "provisioned"
	availability_zones = [
		"ap-northeast-2a",
		"ap-northeast-2c",
		"ap-northeast-2d"
	]
	database_name = "rds-dev"
	master_username = "admin"
	db_cluster_parameter_group_name = "default.aurora-postgresql11"
	db_subnet_group_name = "rdsgrp-dev"
	port = "5432"
	storage_encrypted = true
	kms_key_id = "arn:aws:kms:ap-northeast-2:100020003000:key/c1000fcd-2000-3000-4100-500096006000"
	vpc_security_group_ids = [aws_security_group.security_groups.sg-1000200079284a471.id]
	backtrack_window = null
	backup_retention_period = 7
	copy_tags_to_snapshot = true
	deletion_protection = true
	tags = {
		"Name" = "rds-dev"
	}
}

resource aws_rds_cluster_instance rds-dev-cluster-instance-1 {
	identifier = "rds-dev-cluster-instance-1"
	cluster_identifier = "rds-dev-cluster"
	availability_zone = "ap-northeast-2c"
	instance_class = "db.t3.medium"
	engine = "aurora-postgresql"
	engine_version = "11.9"
	db_subnet_group_name = "rdsgrp-dev"
	monitoring_interval = 60
	monitoring_role_arn = "arn:aws:iam::100020003000:role/rds-monitoring-role"
	performance_insights_enabled = true
	tags = {
		"Name" = "rds-dev"
	}
	depends_on = [aws_rds_cluster.rds-dev-cluster]
}

