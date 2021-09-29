resource aws_elasticache_subnet_group redis-dev-subnetgrp {
	name = "redis-dev-subnetgrp"
	subnet_ids = [aws_subnet.subnet-000140c12f7a1ca6e.id, aws_subnet.subnet-000240c12f7a1ca6e.id]
	tags = {
		"Name" = "redis-dev-subnetgrp"
	}
}

