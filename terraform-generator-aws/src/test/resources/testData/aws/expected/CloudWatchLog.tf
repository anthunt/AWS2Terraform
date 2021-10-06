resource aws_cloudwatch_log_group aws-containerinsights-EKS-CLS-SAMPLE-application {
	name = "/aws/containerinsights/EKS-CLS-SAMPLE/application"
	retention_in_days = null
	kms_key_id = null
	tags = {
		"Name" = "test log group"
	}
}

resource aws_cloudwatch_log_group aws-containerinsights-EKS-CLS-SAMPLE-dataplane {
	name = "/aws/containerinsights/EKS-CLS-SAMPLE/dataplane"
	retention_in_days = null
	kms_key_id = null
	tags = {
	}
}

resource aws_cloudwatch_log_group aws-containerinsights-EKS-CLS-SAMPLE-host {
	name = "/aws/containerinsights/EKS-CLS-SAMPLE/host"
	retention_in_days = null
	kms_key_id = null
	tags = {
	}
}

