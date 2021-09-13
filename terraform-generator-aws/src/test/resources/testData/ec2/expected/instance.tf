resource aws_instance instance0 {
	ami = "ami-0685efd12a23690f5"
	placement_group = null
	tenancy = "default"
	host_id = null
	cpu_core_count = 1
	cpu_threads_per_core = 2
	ebs_optimized = true
	disable_api_termination = true
	instance_initiated_shutdown_behavior = "stop"
	instance_type = "c5.large"
	key_name = "sec-key"
	monitoring = false
	vpc_security_group_ids = ["sg-032bd64bb8901f233"]
	subnet_id = "subnet-45e0000a"
	private_ip = "172.31.1.1"
	secondary_private_ips = []
	source_dest_check = true
	user_data = null
	iam_instance_profile = null
	tags = {
		"Name" = "windows-desktop"
	}
	hibernation = null

	metadata_options {
		http_endpoint = "enabled"
		http_tokens = "optional"
		http_put_response_hop_limit = 1
	}

}

