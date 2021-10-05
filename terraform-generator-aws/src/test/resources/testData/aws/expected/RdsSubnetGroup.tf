resource aws_db_subnet_group rds-dev-subnetgrp {
	name = "rds-dev-subnetgrp"
	subnet_ids = [aws_subnet.subnet-000140c12f7a1ca6e.id, aws_subnet.subnet-000240c12f7a1ca6e.id]
	tags = {
		"Name" = "rds-dev-subnetgrp"
	}
}

