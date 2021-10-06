resource aws_cloudwatch_log_group aws-containerinsights-EKS-CLS-SAMPLE-application {
	name = "/aws/containerinsights/EKS-CLS-SAMPLE/application"
	tags = {
		"Name" = "test log group"
	}
}

resource aws_cloudwatch_log_group aws-containerinsights-EKS-CLS-SAMPLE-dataplane {
	name = "/aws/containerinsights/EKS-CLS-SAMPLE/dataplane"
	tags = {
	}
}

resource aws_cloudwatch_log_group aws-containerinsights-EKS-CLS-SAMPLE-host {
	name = "/aws/containerinsights/EKS-CLS-SAMPLE/host"
	tags = {
	}
}

