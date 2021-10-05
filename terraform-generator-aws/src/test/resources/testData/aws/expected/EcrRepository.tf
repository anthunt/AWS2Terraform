resource aws_ecr_repository envoyproxy-envoy {
	name = "envoyproxy/envoy"
	encryption_configuration = {
		"encryption_type" = "AES256"
		"kms_key" = null
	}
	image_tag_mutability = "MUTABLE"
	image_scanning_configuration = {
		"scan_on_push" = false
	}
}

resource aws_ecr_repository openjdk {
	name = "openjdk"
	encryption_configuration = {
		"encryption_type" = "AES256"
		"kms_key" = null
	}
	image_tag_mutability = "MUTABLE"
	image_scanning_configuration = {
		"scan_on_push" = false
	}
}

