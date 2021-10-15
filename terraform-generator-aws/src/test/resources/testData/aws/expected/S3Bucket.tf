resource aws_s3_bucket config-bucket-235090236746 {
	bucket = "config-bucket-235090236746"
	grant {
		id = "4ddfa5cd352bef6cf39f9921b7057caa1fbe0b152f0c636a5cff842120455bd2"
		type = "CanonicalUser"
		permission = ["FULL_CONTROL"]
	}
	grant {
		type = "Group"
		permission = ["FULL_CONTROL"]
		uri = "http://acs.amazonaws.com/groups/s3/LogDelivery"
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
      "Resource": "arn:aws:s3:::config-bucket-100020003000"
    },
    {
      "Sid": "AWSConfigBucketExistenceCheck",
      "Effect": "Allow",
      "Principal": {
        "Service": "config.amazonaws.com"
      },
      "Action": "s3:ListBucket",
      "Resource": "arn:aws:s3:::config-bucket-100020003000"
    },
    {
      "Sid": "AWSConfigBucketDelivery",
      "Effect": "Allow",
      "Principal": {
        "Service": "config.amazonaws.com"
      },
      "Action": "s3:PutObject",
      "Resource": "arn:aws:s3:::config-bucket-100020003000/AWSLogs/100020003000/Config/*",
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

resource aws_s3_bucket aws-cloudtrail-logs {
	bucket = "aws-cloudtrail-logs"
	grant {
		id = "fb11ff2b1ee34087a15526f5f078675c7bd5cf8c773b3e4797104c91791bb25f"
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
      "Resource": "arn:aws:s3:::config-bucket-100020003000"
    },
    {
      "Sid": "AWSConfigBucketExistenceCheck",
      "Effect": "Allow",
      "Principal": {
        "Service": "config.amazonaws.com"
      },
      "Action": "s3:ListBucket",
      "Resource": "arn:aws:s3:::config-bucket-100020003000"
    },
    {
      "Sid": "AWSConfigBucketDelivery",
      "Effect": "Allow",
      "Principal": {
        "Service": "config.amazonaws.com"
      },
      "Action": "s3:PutObject",
      "Resource": "arn:aws:s3:::config-bucket-100020003000/AWSLogs/100020003000/Config/*",
      "Condition": {
        "StringEquals": {
          "s3:x-amz-acl": "bucket-owner-full-control"
        }
      }
    }
  ]
}
EOF
	versioning {
		enabled = true
	}
	logging = {
		target_bucket = "s3-dev-aws-console-log"
		target_prefix = "s3-access-log"
	}	lifecycle_rule {
		id = "ctail-dev-aws-log-retention-cycle"
		prefix = "ctrail"
		tags = {
		}
		enabled = true
	}
	accelerateConfiguration = null
	request_payer = "BucketOwner"
	server_side_encryption_configuration {
		rule {
			apply_server_side_encryption_by_default {
				kms_master_key_id = "arn:aws:kms:ap-northeast-2:100020003000:alias/aws/s3"
				sse_algorithm = "aws:kms"
			}
		}
	}
	tags = {
	}
}

