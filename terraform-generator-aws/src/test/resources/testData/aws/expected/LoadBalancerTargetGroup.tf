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
		proxy_protocol_v2 = false
		stickiness = false
		path = "/healthz"
		healthy_threshold = 2
		unhealthy_threshold = 2
		interval = 10
		tags = {
		}
	}
}

resource aws_lb_target_group_attachment k8s-ingressn-ingressn-1dab2d3f88-i-00015ef3e99e66157 {
	target_group_arn  = aws_lb_target_group.k8s-ingressn-ingressn-1dab2d3f88.arn
	target_id = aws_instance.i-00015ef3e99e66157.id
	port = 30832
}

resource aws_lb_target_group_attachment k8s-ingressn-ingressn-1dab2d3f88-i-00025ef3e99e66157 {
	target_group_arn  = aws_lb_target_group.k8s-ingressn-ingressn-1dab2d3f88.arn
	target_id = aws_instance.i-00025ef3e99e66157.id
	port = 30832
}

resource aws_lb_target_group tg-dev-service-was {
	name = "tg-dev-service-was"
	port = 8080
	protocol = "HTTP"
	vpc_id = aws_vpc.vpc-00015ad4b3a1ecefb.id
	target_type = "ip"
	deregistration_delay = 300
	health_check {
		enabled = true
		port = traffic-port
		protocol = "HTTP"
		proxy_protocol_v2 = false
		stickiness = false
		path = "/health"
		healthy_threshold = 5
		unhealthy_threshold = 2
		interval = 30
		tags = {
		}
	}
}

resource aws_lb_target_group_attachment tg-dev-service-was-10-100-1-10 {
	target_group_arn  = aws_lb_target_group.tg-dev-service-was.arn
	target_id = "10.100.1.10"
	port = 8080
}

