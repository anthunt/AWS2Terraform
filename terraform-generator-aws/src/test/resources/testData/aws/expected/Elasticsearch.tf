resource aws_elasticsearch_domain test-domain {
	domain_name = "test-domain"
	elasticsearch_version = "OpenSearch_1.0"
	cluster_config {
		dedicated_master_enabled = false
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
		iops = null
		volume_size = 10
		volume_type = "gp2"
	}
	node_to_node_encryption {
		enabled = true
	}
	encrypt_at_rest {
		enabled = true
	}
	advanced_security_options {
		enabled = true
		internal_user_database_enabled = true
	}
	cognito_options {
		enabled = false
		identity_pool_id = null
		role_arn = null
		user_pool_id = null
	}
	domain_endpoint_options {
		enforce_https = true
		tls_security_policy = "Policy-Min-TLS-1-0-2019-07"
	}
	tags = {
		"Name" = "Test-ES"
	}
}

resource aws_elasticsearch_domain_policy test-domain {
	domain_name = "test-domain"
	access_policies = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {
        "AWS": "*"
      },
      "Action": "es:*",
      "Resource": "arn:aws:es:ap-northeast-2:100020003000:domain/test-domain/*"
    }
  ]
}
EOF
}

