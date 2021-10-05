resource aws_msk_cluster msk-dev {
	cluster_name = "msk-dev"
	kafka_version = "2.6.1"
	number_of_broker_nodes = 2
	encryption_info {
		encryption_at_rest_kms_key_arn = "arn:aws:kms:ap-northeast-2:100020003000:key/10002000-227f-4116-a5f1-12ab61377980"
		encryption_in_transit {
			in_cluster = true
		}
	}
	broker_node_group_info {
		client_subnets = [
			aws_subnet.subnet-0f58e2bf1ada4d5c0.id,
			aws_subnet.subnet-003e5f077d31b5163.id
		]
		ebs_volume_size = 10
		instance_type = "kafka.t3.small"
		security_groups = [aws_security_group.sg-010fc4d6910de29ce.id]
		tags = {
			"Name" = "msk-dev-ulsp"
		}
	}
}

