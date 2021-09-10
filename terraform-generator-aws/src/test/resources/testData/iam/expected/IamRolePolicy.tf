resource aws_iam_role_policy policy-eks-describe {
	name = "policy-eks-describe"
	role = aws_iam_role.role-packer-base.id
	policy = <<EOF
{
  "RoleName": "role-packer-base",
  "PolicyName": "policy-eks-describe",
  "PolicyDocument": {
    "Version": "2012-10-17",
    "Statement": [
      {
        "Effect": "Allow",
        "Action": [
          "eks:*"
        ],
        "Resource": "*"
      },
      {
        "Effect": "Allow",
        "Action": "iam:PassRole",
        "Resource": "*",
        "Condition": {
          "StringEquals": {
            "iam:PassedToService": "eks.amazonaws.com"
          }
        }
      }
    ]
  }
}
EOF
}

