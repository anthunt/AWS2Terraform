resource aws_security_group sg_test {
	name = "sg_test"
	description = "test description"
	vpc_id = null
	tags = {
	}
	ingress = [
		{
			description = ""
			from_port = 3306
			to_port = 3306
			protocal = "tcp"
			cidr_blocks = []
			ipv6_cidr_blocks = []
			security_groups = ["sg-002efaf20710623d5"]
		},
		{
			description = ""
			from_port = 3306
			to_port = 3306
			protocal = "tcp"
			cidr_blocks = []
			ipv6_cidr_blocks = []
			security_groups = ["sg-05465424de0e9d80b"]
		},
		{
			description = "ssh hub to aurora service"
			from_port = 3306
			to_port = 3306
			protocal = "tcp"
			cidr_blocks = []
			ipv6_cidr_blocks = []
			security_groups = ["sg-0575ae95fd6c58c75"]
		}
	]
	egress = [
		{
			description = ""
			from_port = 3306
			to_port = 3306
			protocal = "tcp"
			cidr_blocks = []
			ipv6_cidr_blocks = []
			security_groups = ["sg-002efaf20710623d5"]
		},
		{
			description = ""
			from_port = 0
			to_port = 0
			protocal = "-1"
			cidr_blocks = ["0.0.0.0/0"]
			ipv6_cidr_blocks = []
			security_groups = []
		},
		{
			description = ""
			from_port = 0
			to_port = 0
			protocal = "-1"
			cidr_blocks = []
			ipv6_cidr_blocks = ["::/0"]
			security_groups = []
		}
	]
}

