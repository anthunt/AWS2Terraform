resource aws_cloudwatch_log_resource_policy es_log_resource_policy {
	policy_name = "es_log_resource_policy"
	policy_document = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {
        "Service": "es.amazonaws.com"
      },
      "Action": [
        "logs:PutLogEvents",
        "logs:PutLogEventsBatch",
        "logs:CreateLogStream"
      ],
      "Resource": "${aws_cloudwatch_log_group.aws-containerinsights-EKS-CLS-SAMPLE-host.arn}"
    }
  ]
}
EOF
}

