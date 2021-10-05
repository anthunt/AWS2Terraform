resource aws_elasticache_cluster redis-dev-cluster {
	cluster_id = "redis-dev-cluster"
	node_type = "cache.t3.micro"
	num_cache_nodes = 1
	engine = "redis"
	engine_version = "6.0.5"
	port = 6379
	parameter_group_name = "default.redis6.x"
	snapshot_retention_limit = "0"
	snapshot_window = "00:00-01:00"
	subnet_group_name = "dev-subnetgroup-session"
	security_group_ids = [aws_security_group.security_groups.sg-0eac2c2376f703c43.id]
	tags = {
		"Name" = "redis-dev"
	}
}

