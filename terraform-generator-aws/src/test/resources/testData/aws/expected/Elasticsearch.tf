resource aws_elasticsearch_domain test-domain {
	domain_name = "test-domain"
	elasticsearch_version = "OpenSearch_1.0"
	cluster_config {
		instance_type = "t3.small.elasticsearch"
		instance_count = 1
	}
	vpc_options {
		subnet_ids = ["subnet-09cb7762"]
		security_group_ids = ["sg-23321f59"]
	}
	advanced_options = {
		"override_main_response_version" = "false"
		"rest.action.multi.allow_explicit_index" = "true"
	}
	ebs_options {
		ebs_enabled = true
		volume_size = 10
		volume_type = "gp2"
	}
	tags = {
		"Name" = "Test-ES"
	}
}

