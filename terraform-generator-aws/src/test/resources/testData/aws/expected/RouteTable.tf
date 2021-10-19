resource aws_route_table rtb-d6b5fdbd {
	vpc_id = "vpc-7931b212"
	tags = {
	}
	propagating_vgws = []
}

resource aws_route rtb-d6b5fdbd_172-31-0-0_16 {
	route_table_id = "rtb-d6b5fdbd"
	destination_cidr_block = "172.31.0.0/16"
	gateway_id = "local"
}

resource aws_route rtb-d6b5fdbd_0-0-0-0_0 {
	route_table_id = "rtb-d6b5fdbd"
	destination_cidr_block = "0.0.0.0/0"
	gateway_id = "igw-8ecdbbe6"
}

