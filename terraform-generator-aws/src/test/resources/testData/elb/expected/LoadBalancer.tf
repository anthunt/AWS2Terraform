resource aws_lb a000567db2d1f4d02b7493427dc88888 {
	name = "a000567db2d1f4d02b7493427dc88888"
	internal = false
	load_balancer_type = "network"
	subnets = [
		aws_subnet.subnet-05837fce269e60001.id,
		aws_subnet.subnet-05837fce269e60002.id
	]
	ip_address_type = "ipv4"
	enable_deletion_protection = false
	enable_cross_zone_load_balancing = true
	tags = {
		"kubernetes.io/service-name" = "ingress-nginx/ingress-nginx-controller"
		"kubernetes.io/cluster/eks-dev-app-cluster" = "owned"
	}
}

resource aws_lb b000567db2d1f4d02b7493427dc88888 {
	name = "b000567db2d1f4d02b7493427dc88888"
	internal = false
	load_balancer_type = "application"
	subnets = [
		aws_subnet.subnet-05837fce269e60003.id,
		aws_subnet.subnet-05837fce269e60004.id
	]
	ip_address_type = "ipv4"
	enable_deletion_protection = false
	enable_cross_zone_load_balancing = false
	tags = {
	}
}

resource aws_lb c000567db2d1f4d02b7493427dc88888 {
	name = "c000567db2d1f4d02b7493427dc88888"
	internal = false
	load_balancer_type = "application"

	subnet_mapping {
		subnet_id = aws_subnet.subnet-05837fce269e60003.id
		allocation_id  = "allication-01"
	}


	subnet_mapping {
		subnet_id = aws_subnet.subnet-05837fce269e60004.id
		allocation_id  = "allication-02"
	}

	ip_address_type = "ipv4"
	enable_deletion_protection = false
	enable_cross_zone_load_balancing = false
	tags = {
	}
}

resource aws_lb d000567db2d1f4d02b7493427dc88888 {
	name = "d000567db2d1f4d02b7493427dc88888"
	internal = false
	load_balancer_type = "application"

	subnet_mapping {
		subnet_id = aws_subnet.subnet-05837fce269e60003.id
		private_ipv4_address  = "10.0.1.15"
	}


	subnet_mapping {
		subnet_id = aws_subnet.subnet-05837fce269e60004.id
		private_ipv4_address  = "10.0.2.15"
	}

	ip_address_type = "ipv4"
	enable_deletion_protection = false
	enable_cross_zone_load_balancing = false
	tags = {
	}
}

