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

resource aws_lb a000567db2d1f4d02b7493427dc88888 {
	name = "a000567db2d1f4d02b7493427dc88888"
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

