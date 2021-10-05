resource aws_lb_listener 0111567db2d1f4d02b7493427dc824d8-80-TCP {
	load_balancer_arn = aws_lb.0111567db2d1f4d02b7493427dc824d8.arn
	port = 80
	protocol = "TCP"
	default_action {
		target_group_arn = aws_lb_target_group.k8s-ingressn-ingressn-1dab2d3f88.arn
		type = "forward"
	}
}

