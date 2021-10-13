resource aws_s3_bucket config-bucket-235090236746 {
	bucket = "config-bucket-235090236746"
	grant {
		id = "4ddfa5cd352bef6cf39f9921b7057caa1fbe0b152f0c636a5cff842120455bd2"
		type = "CanonicalUser"
		permission = ["FULL_CONTROL"]
	}
	policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "AWSConfigBucketPermissionsCheck",
      "Effect": "Allow",
      "Principal": {
        "Service": "config.amazonaws.com"
      },
      "Action": "s3:GetBucketAcl",
      "Resource": "arn:aws:s3:::config-bucket-235090236746"
    },
    {
      "Sid": "AWSConfigBucketExistenceCheck",
      "Effect": "Allow",
      "Principal": {
        "Service": "config.amazonaws.com"
      },
      "Action": "s3:ListBucket",
      "Resource": "arn:aws:s3:::config-bucket-235090236746"
    },
    {
      "Sid": "AWSConfigBucketDelivery",
      "Effect": "Allow",
      "Principal": {
        "Service": "config.amazonaws.com"
      },
      "Action": "s3:PutObject",
      "Resource": "arn:aws:s3:::config-bucket-235090236746/AWSLogs/235090236746/Config/*",
      "Condition": {
        "StringEquals": {
          "s3:x-amz-acl": "bucket-owner-full-control"
        }
      }
    }
  ]
}
EOF
	accelerateConfiguration = null
	request_payer = "BucketOwner"
	tags = {
	}
}

