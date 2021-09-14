resource aws_efs_file_system efs-test-app {
	encrypted = true
	kms_key_id = "arn:aws:kms:ap-northeast-2:100020003000:key/1000ffff-2210-472f-ad29-2c2f4ef0c4e2"
	performance_mode = "generalPurpose"
	throughput_mode = "bursting"
	provisioned_throughput_in_mibps = null
	tags = {
		"aws:elasticfilesystem:default-backup" = "enabled"
		"Name" = "efs-test-app"
	}
}

resource aws_efs_file_system_policy efs-test-app-policy {
	file_system_id = aws_efs_file_system.efs-test-app.id
	policy = <<EOF
{
  "Version": "2012-10-17",
  "Id": "ExamplePolicy01",
  "Statement": [
    {
      "Sid": "ExampleStatement01",
      "Effect": "Allow",
      "Principal": {
        "AWS": "*"
      },
      "Resource": "${aws_efs_file_system.test.arn}",
      "Action": [
        "elasticfilesystem:ClientMount",
        "elasticfilesystem:ClientWrite"
      ],
      "Condition": {
        "Bool": {
          "aws:SecureTransport": "true"
        }
      }
    }
  ]
}
EOF
}

resource aws_efs_backup_policy efs-test-app {
	file_system_id = aws_efs_file_system.efs-test-app.id
	backup_policy = {
		"status" = "ENABLED"
	}
}

resource aws_efs_file_system aws_efs_file_system-1 {
	encrypted = false
	kms_key_id = null
	performance_mode = "generalPurpose"
	throughput_mode = "bursting"
	provisioned_throughput_in_mibps = null
	tags = {
	}
}

