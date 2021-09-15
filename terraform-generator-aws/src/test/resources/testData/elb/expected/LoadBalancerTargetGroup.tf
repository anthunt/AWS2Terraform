resource aws_lb_target_group k8s-ingressn-ingressn-1dab2d3f88 {
	name = "k8s-ingressn-ingressn-1dab2d3f88"
	port = 30832
	protocol = "TCP"
	vpc_id = aws_vpc.vpc-00015ad4b3a1ecefb.id
	target_type = "instance"
	deregistration_delay = 300

	health_check {
		enabled = true
		port = 30035
		protocol = "TCP"
		path = "/healthz"
		healthy_threshold = 2
		unhealthy_threshold = 2
		interval = 10
	}

}

resource aws_lb_target_group tg-dev-service-was {
	name = "tg-dev-service-was"
	port = 8080
	protocol = "HTTP"
	vpc_id = aws_vpc.vpc-00015ad4b3a1ecefb.id
	target_type = "instance"
	deregistration_delay = 300

	health_check {
		enabled = true
		port = traffic-port
		protocol = "HTTP"
		path = "/health"
		healthy_threshold = 5
		unhealthy_threshold = 2
		interval = 30
	}

}

