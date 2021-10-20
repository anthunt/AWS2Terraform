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

resource aws_route_table_association igw-8ecdbbe6-rtb-d6b5fdbd {
	gateway_id = "igw-8ecdbbe6"
	route_table_id = "rtb-d6b5fdbd"
}

resource aws_route_table rtb-e6b5fdbd {
	vpc_id = "vpc-8931b212"
	tags = {
	}
	propagating_vgws = []
}

resource aws_route rtb-e6b5fdbd_172-31-0-0_16 {
	route_table_id = "rtb-e6b5fdbd"
	destination_cidr_block = "172.31.0.0/16"
	gateway_id = "local"
}

resource aws_route rtb-e6b5fdbd_0-0-0-0_0 {
	route_table_id = "rtb-e6b5fdbd"
	destination_cidr_block = "0.0.0.0/0"
	gateway_id = "igw-8ecdbbe6"
}

resource aws_route_table_association subnet-02c7511faa4344f83-rtb-e6b5fdbd {
	subnet_id = "subnet-02c7511faa4344f83"
	route_table_id = "rtb-e6b5fdbd"
}

