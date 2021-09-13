resource aws_subnet subnet0 {
	availability_zone_id = "apne2-az2"
	cidr_block = "172.31.16.0/20"
	map_public_ip_on_launch = true
	assign_ipv6_address_on_creation = false
	vpc_id = "vpc-7931b212"
	tags = {
	}
}

resource aws_subnet subnet1 {
	availability_zone_id = "apne2-az1"
	cidr_block = "172.31.0.0/20"
	map_public_ip_on_launch = true
	assign_ipv6_address_on_creation = false
	vpc_id = "vpc-7931b212"
	tags = {
	}
}

resource aws_subnet subnet2 {
	availability_zone_id = "apne2-az3"
	cidr_block = "172.31.32.0/20"
	map_public_ip_on_launch = true
	assign_ipv6_address_on_creation = false
	vpc_id = "vpc-7931b212"
	tags = {
	}
}

