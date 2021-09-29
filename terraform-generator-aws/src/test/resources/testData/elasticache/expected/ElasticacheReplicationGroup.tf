resource aws_elasticache_replication_group redis-session {
	replication_group_id = "redis-session"
	replication_group_description = "redis cluster for session cluster"
	node_type = "cache.t3.micro"
	engine = "redis"
	engine_version = "6.0.5"
	port = 16379
	parameter_group_name = "default.redis6.x.cluster.on"
	at_rest_encryption_enabled = false
	transit_encryption_enabled = false
	auth_token = false
	auto_minor_version_upgrade = true
	automatic_failover_enabled = "enabled"
	number_cache_clusters = 3
	snapshot_retention_limit = "0"
	snapshot_window = "00:00-01:00"
	subnet_group_name = "dev-subnetgroup-session"
	security_group_ids = [aws_security_group.security_groups.sg-0eac2c2376f703c43.id]
	cluster_mode {
		num_node_groups = 1
		replicas_per_node_group = 3
	}
	tags = {
		"Name" = "redis-dev"
	}
}

