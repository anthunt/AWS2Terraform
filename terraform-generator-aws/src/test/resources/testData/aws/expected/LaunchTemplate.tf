resource aws_launch_template LT-DEV-DLS {
	name = "LT-DEV-DLS"
	disable_api_termination = true
	ebs_optimized = false
	image_id = "ami-08c64544f5cfcddd0"
	instance_initiated_shutdown_behavior = "stop"
	instance_type = "m5.xlarge"
	kernel_id = null
	key_name = "DEV-KEYPAIR"
	ram_disk_id = null
	vpc_security_group_ids = []
	elastic_gpu_specifications {
		type  = null
	}
	elastic_inference_accelerator {
		type  = null
	}
	iam_instance_profile {
		name  = null
	}
	license_specification {
		market_type = null
	}
	network_interfaces {
		associate_public_ip_address = false
	}
	user_data = base64encode(
<<EOF
#cloud-config
package_update: true
package_upgrade: true
runcmd:
EOF
)
}

