resource aws_api_gateway_rest_api PetStore {
	name = "PetStore"
	description = "Your first API with Amazon API Gateway. This is a sample API that integrates via HTTP with our demo Pet Store endpoints"
	api_key_source = "HEADER"
	disable_execute_api_endpoint = false
	endpoint_configuration {
		types = ["REGIONAL"]
		vpc_endpoint_ids = []
	}
}

resource aws_api_gateway_stage PROD {
	rest_api_id = aws_api_gateway_rest_api.PetStore.id
	deployment_id = aws_api_gateway_deployment.0t5yci.id
	stage_name = "PROD"
	xray_tracing_enabled = false
}

resource aws_api_gateway_deployment 0t5yci {
	rest_api_id = aws_api_gateway_rest_api.PetStore.id
	stage_name = "PROD"
	description = "test deploy"
}

