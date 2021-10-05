resource aws_iam_instance_profile eks-7cbddf86-c0a6-643b-dbdd-85b97c390535 {
	name = "eks-7cbddf86-c0a6-643b-dbdd-85b97c390535"
	role = [aws_iam_role.eks-cluster-workernode-role.name]
}

resource aws_iam_instance_profile role-packer-base {
	name = "role-packer-base"
	role = [
		aws_iam_role.role-packer-base.name,
		aws_iam_role.role-packer-base2.name
	]
}

