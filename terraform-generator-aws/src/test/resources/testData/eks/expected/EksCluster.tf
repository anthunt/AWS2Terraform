resource aws_eks_cluster eks-dev-app-cluster {
	name = "eks-dev-app-cluster"
	role_arn = "arn:aws:iam::100020003000:role/eks-cluster-role"
	vpc_config = {
		"endpoint_private_access" = false
		"endpoint_public_access" = true
		"public_access_cidrs" = ["0.0.0.0/0"]
		"security_group_ids" = [aws_security_group.sg-010fc4d6910de29ce.id]
		"subnet_ids" = [
			aws_subnet.subnet-0f58e2bf1ada4d5c0.id,
			aws_subnet.subnet-003e5f077d31b5163.id
		]
	}
	kubernetes_network_config = {
		"service_ipv4_cidr" = "10.100.0.0/16"
	}
	version = "1.20"
	enabled_cluster_log_types = [
		"api",
		"audit",
		"authenticator",
		"controllerManager",
		"scheduler"
	]
	encryption_config = {
		"provider" = {
			"key_arn" = "arn:aws:kms:ap-northeast-2:100020003000:key/10002000-aba9-49ae-8121-2f9411bfa69f"
		}
		"resources" = ["secrets"]
	}
	tags = {
		"Name" = "eks-dev-app-cluster"
	}
}

