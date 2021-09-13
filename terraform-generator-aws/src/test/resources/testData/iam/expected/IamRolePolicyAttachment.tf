resource aws_iam_role_policy_attachment role-packer-base-attach-policy-eks-describe {
	role = aws_iam_role.role-packer-base.name
	policy_arn = aws_iam_policy.policy-eks-describe.arn
}

resource aws_iam_role_policy_attachment role-packer-base1-attach-policy-eks-describe1 {
	role = aws_iam_role.role-packer-base1.name
	policy_arn = aws_iam_policy.policy-eks-describe1.arn
}

