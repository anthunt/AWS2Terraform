resource aws_vpc_endpoint com-amazonaws-ap-northeast-2-s3 {
	vpc_id = aws_vpc.vpc-a01106c2.id
	service_name = "com.amazonaws.ap-northeast-2.s3"
	tags = {
	}
}

